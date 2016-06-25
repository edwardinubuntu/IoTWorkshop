package tw.soleil.pwrs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import tw.soleil.pwrs.object.DataStream;
import tw.soleil.pwrs.object.Feed;

public class GraphsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_graphs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                new AlertDialog.Builder(GraphsActivity.this)
                        .setTitle("Power switch")
                        .setMessage("Do you want to turn on or off?")
                        .setIcon(R.drawable.ic_action_image_flash_on)
                        .setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                JsonParser parser = new JsonParser();

                                String jsonText = toJSONPowerSwitch(true);

                                Log.i(XivelySettings.TAG, "JSON Text: " + jsonText);

                                final ProgressDialog loadingDialog = new ProgressDialog(GraphsActivity.this);
                                loadingDialog.setMessage("Updating");
                                loadingDialog.show();

                                Ion.with(GraphsActivity.this)
                                        .load("PUT", "https://api.xively.com/v2/feeds/"+ XivelySettings.FEED_ID)
                                        .setHeader("X-ApiKey", XivelySettings.API_KEY)
                                        .setJsonObjectBody(parser.parse(jsonText).getAsJsonObject())
                                        .asString()
                                        .setCallback(new FutureCallback<String>() {
                                            @Override
                                            public void onCompleted(Exception e, String result) {

                                                loadingDialog.dismiss();
                                                if (e == null) {

                                                    Log.i(XivelySettings.TAG, "Result: " + result);

                                                    Snackbar.make(view, "Power has been turned on.", Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                    loadGraphics();
                                                } else {
                                                    Log.e(XivelySettings.TAG, "Put error: " + e.getLocalizedMessage());
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Turn off", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                JsonParser parser = new JsonParser();

                                String jsonText = toJSONPowerSwitch(false);

                                Log.i(XivelySettings.TAG, "JSON Text: " + jsonText);

                                final ProgressDialog loadingDialog = new ProgressDialog(GraphsActivity.this);
                                loadingDialog.setMessage("Updating");
                                loadingDialog.show();

                                Ion.with(GraphsActivity.this)
                                        .load("PUT", "https://api.xively.com/v2/feeds/"+ XivelySettings.FEED_ID)
                                        .setHeader("X-ApiKey", XivelySettings.API_KEY)
                                        .setJsonObjectBody(parser.parse(jsonText).getAsJsonObject())
                                        .asString()
                                        .setCallback(new FutureCallback<String>() {
                                            @Override
                                            public void onCompleted(Exception e, String result) {
                                                loadingDialog.dismiss();
                                                if (e == null) {

                                                    Log.i(XivelySettings.TAG, "Result: " + result);

                                                    Snackbar.make(view, "Power has been turned off.", Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                    loadGraphics();
                                                } else {
                                                    Log.e(XivelySettings.TAG, "Put error: " + e.getLocalizedMessage());
                                                }
                                            }
                                        });
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();


            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadGraphics();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graphs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_refresh) {
            this.loadGraphics();
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadGraphics() {

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        final TextView kwTextView = (TextView)findViewById(R.id.kw_text_view);
        final TextView kwhTextView = (TextView)findViewById(R.id.kwh_text_view);
        final TextView paTextView = (TextView)findViewById(R.id.pa_text_view);
        final TextView pvTextView = (TextView)findViewById(R.id.pv_text_view);

        final ProgressDialog loadingDialog = new ProgressDialog(GraphsActivity.this);
        loadingDialog.setMessage("Loading");
        loadingDialog.show();

        // Load all data from feeds
        Ion.with(GraphsActivity.this)
                .load("GET", "https://api.xively.com/v2/feeds/" + XivelySettings.FEED_ID +".json")
                .setHeader("X-ApiKey", XivelySettings.API_KEY)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {

                            loadingDialog.dismiss();

                            Gson gson = new Gson();

                            // Put Json Object into Feed Object
                            Feed feed = gson.fromJson(result, Feed.class);

                            Log.d("XivelyTestDrive", "Feed result: " + feed.toString());

                            if (feed.getDatastreams() != null) {
                                for (DataStream eachData : feed.getDatastreams()) {
                                    if (eachData.getId().equalsIgnoreCase("KW")) {
                                        String symbol = eachData.getUnit().get("symbol");
                                        kwTextView.setText("Current value: " + eachData.getCurrent_value() + " " +symbol);
                                    }
                                    if (eachData.getId().equalsIgnoreCase("KWH")) {
                                        String symbol = eachData.getUnit().get("symbol");
                                        kwhTextView.setText("Current value: " + eachData.getCurrent_value() + " " +symbol);
                                    }
                                    if (eachData.getId().equalsIgnoreCase("PA")) {
                                        String symbol = eachData.getUnit().get("symbol");
                                        paTextView.setText("Current value: " + eachData.getCurrent_value() + " " +symbol);
                                    }
                                    if (eachData.getId().equalsIgnoreCase("PV")) {
                                        String symbol = eachData.getUnit().get("symbol");
                                        pvTextView.setText("Current value: " + eachData.getCurrent_value() + " " +symbol);
                                    }

                                    // Check whether plug is on or not
                                    if (eachData.getId().equalsIgnoreCase("PSW")) {
                                        if (eachData.getCurrent_value().equalsIgnoreCase("ON")) {
                                            fab.setImageResource(R.drawable.ic_action_on);
                                        } else if (eachData.getCurrent_value().equalsIgnoreCase("OFF")) {
                                            fab.setImageResource(R.drawable.ic_action_off);
                                        }
                                    }


                                }
                            }



//                            Toast.makeText(GraphsActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Load KW image view
        final ImageView kwImageView = (ImageView)findViewById(R.id.kw_image_view);
        Ion.with(GraphsActivity.this)
                .load("https://api.xively.com/v2/feeds/"+ XivelySettings.FEED_ID +"/datastreams/KW.png?b=true&g=true&t=KW&timezone=Taipei")
                .setHeader("X-ApiKey", XivelySettings.API_KEY)
                .noCache()
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (result != null) {
                            kwImageView.setImageBitmap(result);
                        }

                        if (e != null) {
                            Log.e(XivelySettings.TAG, "Error image: " + e.getLocalizedMessage());
                        }
                    }
                });

        // Load KWH image view
        final ImageView kwhImageView = (ImageView)findViewById(R.id.kwh_image_view);
        Ion.with(GraphsActivity.this)
                .load("https://api.xively.com/v2/feeds/"+ XivelySettings.FEED_ID +"/datastreams/KWH.png?b=true&g=true&t=KWH&timezone=Taipei")
                .setHeader("X-ApiKey", XivelySettings.API_KEY)
                .noCache()
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (result != null) {
                            kwhImageView.setImageBitmap(result);
                        }

                        if (e != null) {
                            Log.e(XivelySettings.TAG, "Error image: " + e.getLocalizedMessage());
                        }
                    }
                });

        // Load PA image view
        final ImageView paImageView = (ImageView)findViewById(R.id.pa_image_view);
        Ion.with(GraphsActivity.this)
                .load("https://api.xively.com/v2/feeds/"+ XivelySettings.FEED_ID +"/datastreams/PA.png?b=true&g=true&t=PA&timezone=Taipei")
                .setHeader("X-ApiKey", XivelySettings.API_KEY)
                .noCache()
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (result != null) {
                            paImageView.setImageBitmap(result);
                        }

                        if (e != null) {
                            Log.e(XivelySettings.TAG, "Error image: " + e.getLocalizedMessage());
                        }
                    }
                });

        // Load PV image view
        final ImageView pvImageView = (ImageView)findViewById(R.id.pv_image_view);
        Ion.with(GraphsActivity.this)
                .load("https://api.xively.com/v2/feeds/"+ XivelySettings.FEED_ID +"/datastreams/PV.png?b=true&g=true&t=PV&timezone=Taipei")
                .setHeader("X-ApiKey", XivelySettings.API_KEY)
                .noCache()
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (result != null) {
                            pvImageView.setImageBitmap(result);
                        }

                        if (e != null) {
                            Log.e(XivelySettings.TAG, "Error image: " + e.getLocalizedMessage());
                        }
                    }
                });
    }

    private String toJSONPowerSwitch(boolean turnOn) {

        String turnOnOff = new String();
        if (turnOn) {
            turnOnOff = "ON";
        } else {
            turnOnOff = "OFF";
        }

        Gson gson = new Gson();

        Feed feed = new Feed();
        feed.setVersion("1.0.0");

        DataStream dataStream = new DataStream();
        dataStream.setId("PSW");

        HashMap<String, String> value = new HashMap<String, String>();
        value.put("value", turnOnOff);

        // yyyy-MM-dd'T'HH:mm:ss'Z'
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        value.put("at", simpleDateFormat.format(new Date()));

        List<HashMap<String, String>> datapoints = new ArrayList<>();
        datapoints.add(value);

        dataStream.setDatapoints(datapoints);
        dataStream.setCurrent_value(turnOnOff);

        List<DataStream> dataStreams = new ArrayList<DataStream>();
        dataStreams.add(dataStream);

        feed.setDatastreams(dataStreams);

        JsonParser parser = new JsonParser();

        String jsonText = gson.toJson(feed);

        return jsonText;
    }
}

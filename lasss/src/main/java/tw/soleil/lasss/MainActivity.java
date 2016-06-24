package tw.soleil.lasss;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.soleil.lasss.object.DataStream;
import tw.soleil.lasss.object.Feed;

public class MainActivity extends AppCompatActivity {

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = (LineChart)findViewById(R.id.line_chart);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        this.loadData();
    }

    private void loadData() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        String url = "https://api.xively.com/v2/feeds/" + XivelySettings.FEED_ID +".json?datastreams=Humi,Temp"
                + "&start=" + simpleDateFormat.format(yesterday.getTime()) + "&end=" + simpleDateFormat.format(Calendar.getInstance().getTime());

        Ion.with(this)
                .load("GET", url)
                .setHeader("X-ApiKey", XivelySettings.API_KEY)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        if (result != null) {
                            Gson gson = new Gson();

                            // Put Json Object into Feed Object
                            Feed feed = gson.fromJson(result, Feed.class);

                            Log.d(XivelySettings.TAG, "Feed result: " + feed.toString());

                            List<HashMap<String, String>> tempData = new ArrayList<HashMap<String, String>>();
                            List<HashMap<String, String>> humiData = new ArrayList<HashMap<String, String>>();

                            for (DataStream eachData : feed.getDatastreams()) {
                                if (eachData.getId().equalsIgnoreCase("Temp")) {
                                    tempData = eachData.getDatapoints();
                                }
                                if (eachData.getId().equalsIgnoreCase("Humi")) {
                                    humiData = eachData.getDatapoints();
                                }
                            }

                            setupLineChart(tempData, humiData);
                        }

                        if (e != null) {
                            Log.e(XivelySettings.TAG, "Error :" + e.getLocalizedMessage());
                        }
                    }
                });
    }

    private void setupLineChart(List<HashMap<String, String>> tempData, List<HashMap<String, String>> humiData) {

        lineChart.setDrawGridBackground(false);

        lineChart.setDescription("Temperature / Humidity");
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        lineChart.getAxisRight().setEnabled(false);

        setLineChartData(tempData, humiData);

        lineChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setLineChartData(List<HashMap<String, String>> tempData, List<HashMap<String, String>> humiData) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < tempData.size(); i++) {

            String dateText = tempData.get(i).get("at");

            // To Short Format
            SimpleDateFormat shortDateFormat = new SimpleDateFormat("HH:mm");

            try {
                String shortText = shortDateFormat.format(simpleDateFormat.parse(dateText));
                xVals.add(shortText);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e(XivelySettings.TAG, "Error: "+e.getLocalizedMessage());

                xVals.add(i + "");
            }
        }

        ArrayList<Entry> yTempVals = new ArrayList<Entry>();
        for (int i = 0; i < tempData.size(); i++) {
            yTempVals.add(new Entry(Float.parseFloat(tempData.get(i).get("value")), i));
        }

        ArrayList<Entry> yHumiVals = new ArrayList<Entry>();
        for (int i = 0; i < humiData.size(); i++) {
            yHumiVals.add(new Entry(Float.parseFloat(humiData.get(i).get("value")), i));
        }

        LineDataSet tempSet;
        LineDataSet humiSet;

        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {

            // Temp
            tempSet = (LineDataSet)lineChart.getData().getDataSetByIndex(0);
            tempSet.setYVals(yTempVals);
            lineChart.getData().setXVals(xVals);

            // Humi
            humiSet = (LineDataSet)lineChart.getData().getDataSetByIndex(1);
            humiSet.setYVals(yHumiVals);
            lineChart.getData().setXVals(xVals);

            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            tempSet = new LineDataSet(yTempVals, "Temperature");
            tempSet.setColor(getResources().getColor(R.color.colorPrimary));
            tempSet.setLineWidth(2.5f);
            tempSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
            tempSet.setCircleRadius(0f);
            tempSet.setFillColor(getResources().getColor(R.color.colorPrimary));
            tempSet.setDrawCubic(true);
            tempSet.setDrawValues(false);
            tempSet.setValueTextSize(5f);
            tempSet.setValueTextColor(getResources().getColor(R.color.colorPrimary));
            tempSet.setCircleColorHole(getResources().getColor(R.color.colorPrimary));

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(tempSet); // add the datasets

            humiSet = new LineDataSet(yHumiVals, "Humidity");
            humiSet.setColor(getResources().getColor(android.R.color.holo_blue_light));
            humiSet.setLineWidth(2.5f);
            humiSet.setCircleColor(getResources().getColor(android.R.color.holo_blue_light));
            humiSet.setCircleRadius(0f);
            humiSet.setFillColor(getResources().getColor(android.R.color.holo_blue_light));
            humiSet.setDrawCubic(true);
            humiSet.setDrawValues(false);
            humiSet.setValueTextSize(5f);
            humiSet.setValueTextColor(getResources().getColor(android.R.color.holo_blue_light));
            humiSet.setCircleColorHole(getResources().getColor(android.R.color.holo_blue_light));
            dataSets.add(humiSet);

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);

            // set data
            lineChart.setData(data);
        }

        lineChart.invalidate();
    }
}

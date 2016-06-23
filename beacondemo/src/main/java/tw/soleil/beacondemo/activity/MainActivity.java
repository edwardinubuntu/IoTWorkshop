package tw.soleil.beacondemo.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import tw.soleil.beacondemo.R;
import tw.soleil.beacondemo.service.BLEScannerService;
import tw.soleil.beacondemo.service.BLEService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {

            /*
                Check current os version so we can bring up new service
             */
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Intent serviceIntent = new Intent(this, BLEScannerService.class);
                startService(serviceIntent);
            } else {
                Intent serviceIntent = new Intent(this, BLEService.class);
                startService(serviceIntent);
            }
        }
    }

}

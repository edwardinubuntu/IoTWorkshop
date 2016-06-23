package tw.soleil.beacondemo.service;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by edward_chiang on 15/5/23.
 */
public class BLEReceiver extends BroadcastReceiver {
    private Intent beaconServiceIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_OFF:
                    if (beaconServiceIntent != null) {
                        context.stopService(beaconServiceIntent);
                        beaconServiceIntent = null;
                    }
                    break;
                case BluetoothAdapter.STATE_ON:
                    if (beaconServiceIntent == null) {

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            beaconServiceIntent = new Intent(context, BLEScannerService.class);
                            context.startService(beaconServiceIntent);
                        } else {
                            beaconServiceIntent = new Intent(context, BLEService.class);
                            context.startService(beaconServiceIntent);
                        }
                    }
                    break;
            }
        }
    }
}

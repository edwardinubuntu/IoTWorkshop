package tw.soleil.beacondemo.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tw.soleil.beacondemo.BeaconDemo;
import tw.soleil.beacondemo.object.ScanRecord;

/**
 * Created by edward_chiang on 15/5/23.
 */
public class BLEService extends Service {


    private List<ScanRecord> recordedList;

    private static final long SCAN_PERIOD = 10000;

    private Handler mHandler;

    private BluetoothAdapter mBluetoothAdapter;

    private Intent intent;

    public static String PREFERENCES_KEY_BEACON_UUID = "PREFERENCES_KEY_BEACON_UUID";
    public static String PREFERENCES_KEY_BEACON_MAJOR = "PREFERENCES_KEY_BEACON_MAJOR";
    public static String PREFERENCES_KEY_BEACON_MINOR = "PREFERENCES_KEY_BEACON_MINOR";


    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        recordedList = new ArrayList<>();

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mHandler = new Handler();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        this.intent = intent;
        recordedList.clear();
        scanLeDevice(true);
        return START_STICKY;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(leScanCallback);

                    scanLeDevice(true);
                }
            }, SCAN_PERIOD);

            mBluetoothAdapter.startLeScan(leScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(leScanCallback);
        }
    }


    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {


        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            ScanRecord scanRecordObject = ScanRecord.parseScanRecord(device, scanRecord);
            scanRecordObject.setRssi(rssi);
            boolean found = false;
            for (ScanRecord scanRecordStore  : recordedList) {
                if (scanRecordStore.isSameBeacon(scanRecordObject)) {
                    found = true;
                }
            }
            if (!found && scanRecordObject.getUUID() != null && scanRecordObject.getUUID().length() > 0) {
                scanRecordObject.setScannedDate(Calendar.getInstance().getTime());
                Log.i(BeaconDemo.TAG, "Scanned Object: " + scanRecordObject);
                recordedList.add(scanRecordObject);
            }

        }
    };
}

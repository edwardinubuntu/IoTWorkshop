package tw.soleil.indoorlocation.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tw.soleil.indoorlocation.InDoorDemo;
import tw.soleil.indoorlocation.object.IndoorObject;
import tw.soleil.indoorlocation.object.NameMapping;
import tw.soleil.indoorlocation.object.ScanRecord;
import tw.soleil.indoorlocation.util.LocationCalculator;

/**
 * Created by edward_chiang on 6/15/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BLEScannerService extends Service {

    private List<ScanRecord> recordedList;

    private ScanSettings settings;

    private static final long SCAN_PERIOD = 10000;

    private BluetoothLeScanner bluetoothLeScanner;

    private Handler mHandler;

    private List<ScanFilter> scanFilterList;

    private Intent intent;

    private LocationCalculator locationCalculator = new LocationCalculator();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();

        recordedList = new ArrayList<>();

        settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build();

        scanFilterList = new ArrayList<>();
//        scanFilterList.add(new ScanFilter.Builder().setDeviceName("HERE_Beacon").build());
        scanFilterList.add(new ScanFilter.Builder().setDeviceName("estimote").build());
        // EST
        scanFilterList.add(new ScanFilter.Builder().setDeviceName("EST").build());

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        mHandler = new Handler();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        this.intent = intent;

        recordedList.clear();
        scanDeviceList(true);
        return START_STICKY;
    }

    /**
     * Added in API level 21
     * @param enable
     */
    private void scanDeviceList(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothLeScanner.stopScan(scanCallback);
                    scanDeviceList(true);
                }
            }, SCAN_PERIOD);

            bluetoothLeScanner.startScan(scanFilterList, settings, scanCallback);
        } else {
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            Log.d(InDoorDemo.TAG, "Scanned Callback Type: " + String.valueOf(callbackType));
            Log.d(InDoorDemo.TAG, "Scanned result: " + result.toString());

            ScanRecord scanRecordObject = ScanRecord.parseScanRecord(result.getDevice(), result.getScanRecord().getBytes());
            scanRecordObject.setRssi(result.getRssi());

            /**
             *
             *  RSSI stands for Received Signal Strength Indicator.
             *  It is the strength of the beacon's signal as seen on the receiving device, e.g. a smartphone.
             *  The signal strength depends on distance and Broadcasting Power value.
             *  At maximum Broadcasting Power (+4 dBm) the RSSI ranges from -26 (a few inches) to -100 (40-50 m distance).
             *
             *  https://community.estimote.com/hc/en-us/articles/201636913-What-are-Broadcasting-Power-RSSI-and-other-characteristics-of-beacon-s-signal-
             */
            Log.d(InDoorDemo.TAG, "Beacon info: " + scanRecordObject.toString());
            Log.i(InDoorDemo.TAG, "Get relative distance:" + scanRecordObject.getRelativeDistance(-26) + ", beacon nickname: " + new NameMapping(scanRecordObject).getRegisteredDeviceName());

            NameMapping nameMapping = new NameMapping(scanRecordObject);

            IndoorObject indoorObject = new IndoorObject(nameMapping.getRegisteredDeviceName(), scanRecordObject);
            indoorObject.setRelativeDistance(scanRecordObject.getRelativeDistance(-26));
            indoorObject.setPosition(nameMapping.getPosition());

            locationCalculator.getPositions().add(indoorObject);

            if (locationCalculator.getPositions().size() >= 3) {
                double[] position = locationCalculator.calculateCentroid();
                Log.i(InDoorDemo.TAG, "Device position is at (" + position[0] +", " + position[1] + ") base on " + locationCalculator.getPositions().size() + " points.");
            }

//            if (locationCalculator.getPositions().size() >= 20) {
//                locationCalculator.getPositions().clear();
//            }

            boolean found = false;
            for (ScanRecord scanRecordStore  : recordedList) {
                if (scanRecordStore.isSameBeacon(scanRecordObject)) {
                    found = true;
                }
            }
            if (!found && scanRecordObject.getUUID() != null && scanRecordObject.getUUID().length() > 0) {
                scanRecordObject.setScannedDate(Calendar.getInstance().getTime());
                Log.d(InDoorDemo.TAG, "Scanned parse Object: " + scanRecordObject);
                recordedList.add(scanRecordObject);

            }
        }
    };
}

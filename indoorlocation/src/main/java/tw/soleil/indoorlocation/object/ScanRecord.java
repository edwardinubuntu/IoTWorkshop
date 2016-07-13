package tw.soleil.indoorlocation.object;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.Date;

/**
 * Created by edward_chiang on 15/5/23.
 */
public class ScanRecord {
    private String UUID;
    private String deviceName;
    private int rssi;
    private int major;
    private int minor;
    private Date scannedDate;

    public double getRelativeDistance(double accordingNearest) {
        return accordingNearest - rssi;
    }

    public boolean isSameBeacon(ScanRecord compareRecord) {
        return
                compareRecord.getUUID() != null
                        &&  this.getUUID() != null
                        && this.getUUID().equals(compareRecord.getUUID())
                        && this.getMajor() == compareRecord.getMajor()
                        && this.getMinor() == compareRecord.getMinor();

    }

    public Date getScannedDate() {
        return scannedDate;
    }

    public void setScannedDate(Date scannedDate) {
        this.scannedDate = scannedDate;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "ScanRecord{" +
                "UUID='" + UUID + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", rssi=" + rssi +
                ", major=" + major +
                ", minor=" + minor +
                ", scannedDate=" + scannedDate +
                '}';
    }

    /**
     * bytesToHex method
     * Found on the internet
     * http://stackoverflow.com/a/9855338
     */
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static ScanRecord parseScanRecord(BluetoothDevice device, byte[] scanRecord) {

        ScanRecord scanRecordObject = new ScanRecord();

        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound) {
            //Convert to hex String
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte+4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);

            //Here is your UUID
            String uuid =  hexString.substring(0,8) + "-" +
                    hexString.substring(8,12) + "-" +
                    hexString.substring(12,16) + "-" +
                    hexString.substring(16,20) + "-" +
                    hexString.substring(20,32);

            scanRecordObject.setUUID(uuid);

            //Here is your Major value
            int major = (scanRecord[startByte+20] & 0xff) * 0x100 + (scanRecord[startByte+21] & 0xff);
            scanRecordObject.setMajor(major);

            //Here is your Minor value
            int minor = (scanRecord[startByte+22] & 0xff) * 0x100 + (scanRecord[startByte+23] & 0xff);
            scanRecordObject.setMinor(minor);
        }
        if  (device.getName() != null) {
            scanRecordObject.setDeviceName(device.getName().trim());
        }
        return scanRecordObject;
    }

    /**
     * https://gist.github.com/eklimcz/446b56c0cb9cfe61d575
     * @param rssi
     * @return
     */
    public double calculateDistance(double rssi) {
        int fixedPower = -59;

        if (rssi == 0) {
            return -1;
        }

        double ratio = rssi * 1.0 / fixedPower;

        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double distance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return  distance;
        }
    }
}

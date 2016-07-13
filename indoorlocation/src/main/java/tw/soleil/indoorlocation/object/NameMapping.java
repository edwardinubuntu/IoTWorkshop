package tw.soleil.indoorlocation.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by edward_chiang on 7/13/16.
 */
public class NameMapping {
    private ScanRecord scanRecord;
    private List<IndoorObject> registeredBeaconList;

    public NameMapping(ScanRecord scanRecord) {
        this.scanRecord = scanRecord;

        registeredBeaconList = new ArrayList<>();

        // Blueberry
        ScanRecord blueberryRecord = new ScanRecord();
        blueberryRecord.setUUID("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
        blueberryRecord.setMajor(56206);
        blueberryRecord.setMinor(57673);

        IndoorObject blueberryObject = new IndoorObject("Blueberry", blueberryRecord);
        blueberryObject.setPosition(new double[]{0.0, 2.0});

        registeredBeaconList.add(blueberryObject);

        // Ice
        ScanRecord iceRecord = new ScanRecord();
        iceRecord.setUUID("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
        iceRecord.setMajor(24735);
        iceRecord.setMinor(29036);

        IndoorObject iceObject = new IndoorObject("Ice", iceRecord);
        iceObject.setPosition(new double[]{6.0, 1.0});
        registeredBeaconList.add(iceObject);

        // Mint
        ScanRecord mintRecord = new ScanRecord();
        mintRecord.setUUID("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
        mintRecord.setMajor(26196);
        mintRecord.setMinor(58929);

        IndoorObject mintObject = new IndoorObject("Mint", mintRecord);
        mintObject.setPosition(new double[]{8.0, 0.0});
        registeredBeaconList.add(mintObject);

        // No. 87
        ScanRecord here87Record = new ScanRecord();
        here87Record.setUUID("15345164-67AB-3E49-F9D6-E29000000008");
        here87Record.setMajor(10);
        here87Record.setMinor(87);

        IndoorObject here87Object = new IndoorObject("No. 87", here87Record);
        here87Object.setPosition(new double[]{0.0, 0.0});
        registeredBeaconList.add(here87Object);

        // No. 121
        ScanRecord here121Record = new ScanRecord();
        here121Record.setUUID("15345164-67AB-3E49-F9D6-E29000000008");
        here121Record.setMajor(10);
        here121Record.setMinor(121);

        IndoorObject here121Object = new IndoorObject("No. 121", here121Record);
        here121Object.setPosition(new double[]{8.0, 2.0});
        registeredBeaconList.add(here121Object);
    }

    public String getRegisteredDeviceName() {

        for (IndoorObject eachBeacon: registeredBeaconList) {
            if (eachBeacon.getScanRecord().isSameBeacon(scanRecord)) {
                return eachBeacon.getNickName();
            }
        }

        return "Unknown";
    }

    public double[] getPosition() {
        for (IndoorObject eachBeacon: registeredBeaconList) {
            if (eachBeacon.getScanRecord().isSameBeacon(scanRecord)) {
                return eachBeacon.getPosition();
            }
        }

        return new double[]{0.0, 0.0};
    }

}

package tw.soleil.indoorlocation.object;

/**
 * Created by edward_chiang on 7/13/16.
 */
public class IndoorObject {

    private String nickName;
    private ScanRecord scanRecord;
    private double[] position;
    private double relativeDistance;

    public IndoorObject(String nickName, ScanRecord scanRecord) {
        this.nickName = nickName;
        this.scanRecord = scanRecord;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public ScanRecord getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(ScanRecord scanRecord) {
        this.scanRecord = scanRecord;
    }

    public double getRelativeDistance() {
        return relativeDistance;
    }

    public void setRelativeDistance(double relativeDistance) {
        this.relativeDistance = relativeDistance;
    }
}

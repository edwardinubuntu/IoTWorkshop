package tw.soleil.xivelydatastreampnggraphs.object;

import java.util.List;

/**
 * Created by edward_chiang on 6/17/16.
 */
public class Feed {

    private String id;
    private String title;
    private String feed;
    private String auto_feed_url;
    private String status;
    private String updated;
    private String created;
    private String creator;
    private String version;

    private List<DataStream> datastreams;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getAuto_feed_url() {
        return auto_feed_url;
    }

    public void setAuto_feed_url(String auto_feed_url) {
        this.auto_feed_url = auto_feed_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<DataStream> getDatastreams() {
        return datastreams;
    }

    public void setDatastreams(List<DataStream> datastreams) {
        this.datastreams = datastreams;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", feed='" + feed + '\'' +
                ", auto_feed_url='" + auto_feed_url + '\'' +
                ", status='" + status + '\'' +
                ", updated='" + updated + '\'' +
                ", created='" + created + '\'' +
                ", creator='" + creator + '\'' +
                ", version='" + version + '\'' +
                ", datastreams=" + datastreams +
                '}';
    }
}

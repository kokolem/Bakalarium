package cz.vitek.bakalarium.pojos;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.vitek.bakalarium.utils.Converters;

@Entity(indices = @Index(value = {"id"}, unique = true))
@TypeConverters(Converters.class)
@Root(strict = false)
public class Homework {

    @PrimaryKey
    @NonNull
    @Element
    private String id = "";

    @Element(name = "predmet", required = false)
    private String title;

    @Element(name = "popis", required = false)
    private String description;

    @Element(name = "zkratka", required = false)
    private String subject;

    @Element(required = false)
    private String status;

    @ColumnInfo(name = "is_done")
    private Boolean isDone;

    @ColumnInfo(name = "is_archived")
    private Boolean isArchived;

    @Ignore
    @Element(name = "nakdy", required = false)
    private String timeStampHandIn;

    @Ignore
    @Element(name = "zadano", required = false)
    private String timeStampAssigned;

    @ColumnInfo(name = "hand_in")
    private Date handIn;

    private Date assigned;

    @ColumnInfo(name = "attachment_list")
    @ElementList(name = "attachments", entry = "attachment", required = false)
    private List<Attachment> attachmentList;

    public Homework(@NonNull String id, String title, String description, String subject, String status, Boolean isDone, Boolean isArchived, Date handIn, Date assigned, List<Attachment> attachmentList) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.status = status;
        this.isDone = isDone;
        this.isArchived = isArchived;
        this.handIn = handIn;
        this.assigned = assigned;
        this.attachmentList = attachmentList;
    }

    @Ignore
    public Homework() {
    }

    @Commit
    public void init() {

        // convert the timestamps provided by Bakaláři server into Date objects
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmm", Locale.getDefault());
        try {
            handIn = formatter.parse(timeStampHandIn);
            assigned = formatter.parse(timeStampAssigned);
        } catch (ParseException ignored) {
        }

        // TODO: "probehlo" and "aktivni" are not the only possibilities
        isArchived = status.equals("probehlo");

        // replace html break tags with \n (yes, we are getting html from the server, perhaps xss would be possible?)
        if (description != null) description = description.replace("<br />", "\n");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public String getTimeStampHandIn() {
        return timeStampHandIn;
    }

    public void setTimeStampHandIn(String timeStampHandIn) {
        this.timeStampHandIn = timeStampHandIn;
    }

    public String getTimeStampAssigned() {
        return timeStampAssigned;
    }

    public void setTimeStampAssigned(String timeStampAssigned) {
        this.timeStampAssigned = timeStampAssigned;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getHandIn() {
        return handIn;
    }

    public void setHandIn(Date handIn) {
        this.handIn = handIn;
    }

    public Date getAssigned() {
        return assigned;
    }

    public void setAssigned(Date assigned) {
        this.assigned = assigned;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    // only return id for brevity
    @NonNull
    @Override
    public String toString() {
        return id + "\n";
    }
}

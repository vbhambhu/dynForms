package kennedy.ox.ac.uk.Models;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;


@Document(collection = "forms")
public class Form {

    @Id
    private String id;
    private String title;
    private String description;
    private List<Field> fields = new ArrayList<>();
    private List<Setting> settings = new ArrayList<>();

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date createdAt;

    private Boolean isPublished;
    private Boolean isLocked;
    private String ownerId;

    private String mode = Modes.unpaged;
    private Integer questionsPerPage = 0;
    private Integer randomQuestions = 0;

    public Form() {}

    public Form(String title, String description, Date createdAt) {
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static class Modes {
        public final static String unpaged = "unpaged";
        public final static String paged = "paged";
        public final static String random = "random";
        public final static String adaptive = "adaptive";
    }


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Setting> getSettings() { return settings; }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }

    public Boolean getPublished() {
        return isPublished;
    }

    public void setPublished(Boolean published) {
        isPublished = published;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public String getOwnerId() { return ownerId; }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getMode() { return mode; }

    public void setMode(String mode) { this.mode = mode; }

    public Integer getQuestionsPerPage() { return questionsPerPage; }

    public void setQuestionsPerPage(Integer questionsPerPage) { this.questionsPerPage = questionsPerPage; }

    public Integer getRandomQuestions() { return randomQuestions; }

    public void setRandomQuestions(Integer randomQuestions) { this.randomQuestions = randomQuestions; }
}

package kennedy.ox.ac.uk.Models;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.validation.constraints.Size;


@Document(collection = "forms")
public class Form {

    @Id
    private String id;

    @NotBlank(message = "Form name field is required.")
    private String name;

    @NotBlank(message = "Form description field is required.")
    private String description;

    private List<Field> fields = new ArrayList<>();
    private String owner;


    @NotEmpty(message = "Project field is required.")
    private List<String> projectIds = new ArrayList<>();

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date createdAt;

    private Boolean isPublished;
    private Boolean isLocked;
    private String ownerId;

    private String mode = Modes.unpaged;
    private int questionsPerPage = 0;
    private int randomQuestions = 0;

    public Form() {}

    public Form(String title, String description, Date createdAt) {
        this.name = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static class Modes {
        public final static String unpaged = "unpaged";
        public final static String paged = "paged";
        public final static String random = "random";
        public final static String adaptive = "adaptive";
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<String> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<String> projectIds) {
        this.projectIds = projectIds;
    }

    public void setQuestionsPerPage(int questionsPerPage) {
        this.questionsPerPage = questionsPerPage;
    }

    public void setRandomQuestions(int randomQuestions) {
        this.randomQuestions = randomQuestions;
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

    public boolean isPaged() { return this.mode.equals(Modes.paged); }

    public boolean isRandom() { return this.mode.equals(Modes.random); }

    public boolean isAdaptive() { return this.mode.equals(Modes.adaptive); }
}

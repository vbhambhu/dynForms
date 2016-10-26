package kennedy.ox.ac.uk.Models;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vinodkumar on 10/10/2016.
 */

@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    @Size(min=1,max=20, message="Project name field must be between 1 and 20 characters in length.")
    private String name;
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt;

    private String owner;
    private boolean isDeleted = false;

    private int formCount;

    private boolean isArchived = false;

    private List<Team> teams = new ArrayList<>();


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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getFormCount() {
        return formCount;
    }

    public void setFormCount(int formCount) {
        this.formCount = formCount;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }


    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public void setTeam(Team team) {
        teams.add(team);
    }

}

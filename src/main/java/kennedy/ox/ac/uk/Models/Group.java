package kennedy.ox.ac.uk.Models;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.util.Date;

@Document(collection = "groups")
public class Group {

    @Id
    private String id;

    @Size(min=3,max=30, message="Group name field must be between 3 and 30 characters in length.")
    @Indexed(unique = true)
    private String name;

    private String description;

    private int membersCount = 0;
    private String owner;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt = new Date();


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

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

package kennedy.ox.ac.uk.Models;

import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotNull
    @Size(min=2, max=30)
    @Indexed(unique = true)
    private String username;

    @NotNull
    @Size(min=2, max=30)
    private String password;

    @NotNull
    @Size(min=2, max=30)
    private String firstName;

    @NotNull
    @Size(min=2, max=30)
    private String lastName;

    @Email
    @NotNull
    @Size(min=2, max=30)
    private String email;

    private boolean isLDAPAccount = false;

    private boolean firstTimePasswordChange = true;
    private int loginCount = 0;

    //Indicates whether the user's account is deleted or not.
    private boolean isDeleted;
    //Indicates whether the user is locked or unlocked.
    private boolean isLocked;

    private List<String> roles = new ArrayList<String>();
    private List<String> groups = new ArrayList<String>();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date acconutExpireDate;


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date updatedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt = new Date();


    public void setRole(String role) {
        roles.add(role);
    }

    public void setGroup(String group) {
        groups.add(group);
    }

    public String getFullName() {
        return firstName + ' ' + lastName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLDAPAccount() {
        return isLDAPAccount;
    }

    public void setLDAPAccount(boolean LDAPAccount) {
        isLDAPAccount = LDAPAccount;
    }

    public boolean isFirstTimePasswordChange() {
        return firstTimePasswordChange;
    }

    public void setFirstTimePasswordChange(boolean firstTimePasswordChange) {
        this.firstTimePasswordChange = firstTimePasswordChange;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public Date getAcconutExpireDate() {
        return acconutExpireDate;
    }

    public void setAcconutExpireDate(Date acconutExpireDate) {
        this.acconutExpireDate = acconutExpireDate;
    }
}
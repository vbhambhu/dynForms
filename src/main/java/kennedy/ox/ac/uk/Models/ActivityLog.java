package kennedy.ox.ac.uk.Models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Document(collection = "logs")
public class ActivityLog {

    @Id
    private String id;

    private String user;

    private String event;

    private String ip;

    private String userAgent;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt = new Date();

    public ActivityLog(){}

    public ActivityLog(String user, String event){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        this.user = user;
        this.event = event;
        this.ip = request.getRemoteAddr();
        this.userAgent = request.getHeader("user-agent");
        this.createdAt = new Date();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

package kennedy.ox.ac.uk.Models;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by vinodkumar on 10/10/2016.
 */
public class Project {

    @Id
    private String id;

    private String name;
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt;

}

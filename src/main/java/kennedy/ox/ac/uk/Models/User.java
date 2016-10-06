package kennedy.ox.ac.uk.Models;

/**
 * Created by vinod on 06/10/2016.
 */

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    String username;

    String password;

    //getter, setter, toString, Constructors

}
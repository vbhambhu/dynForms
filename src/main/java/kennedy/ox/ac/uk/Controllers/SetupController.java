package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.Group;
import kennedy.ox.ac.uk.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SetupController {

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(value="/install", method= RequestMethod.GET)
    public String setup() {

        //Delete collections
        mongoOperation.getCollection("users").drop();
        mongoOperation.getCollection("groups").drop();
        mongoOperation.getCollection("projects").drop();
        mongoOperation.getCollection("forms").drop();

        User user = new User();
        user.setFirstName("Vinod");
        user.setLastName("Kumar");
        user.setEmail("vinod@test.com");
        user.setUsername("spadmin");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode("admin"));

        user.setRole("ROLE_USER");
        user.setRole("ROLE_ADMIN");
        user.setGroup("ALL_USERS");
        mongoOperation.save(user);

        //Create default group
        Group group = new Group();
        group.setIdentifier("ALL_USERS");
        group.setName("All Users");
        group.setOwner("spadmin");
        group.setDefault(true);
        group.setDescription("This group is a special user group that contains all users in the system.");
        group.setMembersCount(1);
        mongoOperation.save(group);


/*
        for(int i=1; i<10; i++){
            String username = this.randString();

            String fname = this.randString();
            String lname = this.randString();

            passwordEncoder = new BCryptPasswordEncoder();
            user = new User();
            user.setFirstName(fname);
            user.setLastName(lname);
            user.setEmail(username+"@test.com");
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode("user"));
            user.addRole("ROLE_USER");
            mongoOperation.save(user);

        }

*/
        return "redirect:/";



    }

}
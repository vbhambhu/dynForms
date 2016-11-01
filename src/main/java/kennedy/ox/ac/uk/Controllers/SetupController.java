package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.Group;
import kennedy.ox.ac.uk.Models.Project;
import kennedy.ox.ac.uk.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.Random;

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



        for(int i=1; i<100000; i++){
            String username = randString(10);

            String fname = randString(5);
            String lname = this.randString(5);

            passwordEncoder = new BCryptPasswordEncoder();
            user = new User();
            user.setFirstName(fname);
            user.setLastName(lname);
            user.setEmail(username+"@test.com");
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode("user"));
            mongoOperation.save(user);
        }


        return "redirect:/";



    }

    @RequestMapping(value="/pro", method= RequestMethod.GET)
    public String blukpro() {

        Project project;


        for(int i=1; i<200; i++){

            project = new Project();
            String username = randString(10);

            project.setName("project " + i);

            project.setDescription("project " + i + " description");

            project.setCreatedAt(new Date());
            project.setOwner(username);
            project.setUser(username);
            mongoOperation.save(project);
        }



        return "redirect:/";

    }

    public  String randString(int strLength){

        char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < strLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }


}
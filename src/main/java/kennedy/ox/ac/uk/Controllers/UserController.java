package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Models.User;
import kennedy.ox.ac.uk.Repositories.UserRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by vinod on 07/10/2016.
 */
@Controller
public class UserController {

    @Autowired
    MongoOperations mongoOperation;


    @RequestMapping(value="/users", method= RequestMethod.GET)
    public String listUsers(Model model) {

        List<User> users = mongoOperation.findAll(User.class);
        model.addAttribute("users", users);
        return "users/list";
    }

    @RequestMapping(value="/users/new", method= RequestMethod.GET)
    public String newUser(User user) {
        return "users/create";
    }


    @RequestMapping(value="/users/new", method= RequestMethod.POST)
    public String createUser(@Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/create";
        }

        //insert user to db here

        mongoOperation.save(user);
        return "redirect:/users";
    }


    @RequestMapping(value="/groups", method= RequestMethod.GET)
    public String listGruops(Model model) {

        List<User> users = mongoOperation.findAll(User.class);
        model.addAttribute("users", users);
        return "groups/list";
    }


    @RequestMapping(value="/user-create", method= RequestMethod.GET)
    public void create() {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setFirstName("Vinod");
        user.setLastName("Kumar");
        user.setEmail("vinod@test.com");
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setEnabled(true);
        user.setUpdatedAt(new Date());
        user.setCreatedAt(new Date());
        user.addRole("ROLE_USER");
        user.addRole("ROLE_ADMIN");
        mongoOperation.save(user);



        for(int i=1; i<100; i++){



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
            user.setEnabled(true);
            user.setUpdatedAt(new Date());
            user.setCreatedAt(new Date());
            user.addRole("ROLE_USER");
            mongoOperation.save(user);

        }






    }



    public  String randString(){

        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output;
    }
}

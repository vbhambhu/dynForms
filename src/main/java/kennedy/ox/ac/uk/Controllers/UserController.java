package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Models.User;
import kennedy.ox.ac.uk.Repositories.UserRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vinod on 07/10/2016.
 */
@Controller
public class UserController {

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(value="/user", method= RequestMethod.GET)
    public String showProfile(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username


//        BasicQuery query = new BasicQuery("{ age : { $lt : 40 }, name : 'cat' }");
//        User userTest1 = mongoOperation.findOne(query1, User.class);
//
//
//        System.out.print(auth.getPrincipal());
//
//        mongoOperation.


        return "users/profile";
    }


    @RequestMapping(value="/users/invite", method = { RequestMethod.GET, RequestMethod.POST })
    public String inviteUser(Model model, @RequestParam(value = "emails", required = false) String emails) {


        if(emails != null){

            List<String> items = Arrays.asList(emails.split(","));
            model.addAttribute("postEmails", emails);

            String err = null;

            //validate
            for (String item: items){
                item = item.trim();
                if(!isValidEmail(item)){
                    err = "Invalid emails in list.";
                    break;
                } else if(isAlreadyEmail(item)){
                    err = "Email address '"+item+"' already exists.";
                    break;
                }
            }

           if(err != null){
               model.addAttribute("err", err);
           } else{

               createUsers(items);
               return "redirect:/users/";

           }
        }
        return "users/invite";
    }


    @RequestMapping(value="/users/{id}", method= RequestMethod.GET)
    public String singleUser(Model model, @PathVariable String id) {

        User user = mongoOperation.findById(new ObjectId(id), User.class);

        System.out.print(user.getRoles());
        model.addAttribute("user", user);
        return "users/details";

    }


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
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setGroup("ALL_USERS");
        mongoOperation.save(user);
        return "redirect:/users/"+user.getId();
    }




    @RequestMapping(value="/install", method= RequestMethod.GET)
    public String setup() {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setFirstName("Vinod");
        user.setLastName("Kumar");
        user.setEmail("vinod@test.com");
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));

        user.setRole("ROLE_USER");
        user.setRole("ROLE_ADMIN");
        user.setGroup("ALL_USERS");
        mongoOperation.save(user);


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

    private Boolean isValidEmail(String text) {
        try {
            String regex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    private Boolean isAlreadyEmail(String email){
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        User user = mongoOperation.findOne(query, User.class);
        return (user == null) ? false : true;
    }

    private Boolean isUsernameExists(String username){
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        User user = mongoOperation.findOne(query, User.class);
        return (user == null) ? false : true;
    }



    private void createUsers(List<String> emails){

        User user = new User();

        for (String email: emails) {
            email = email.trim();
            String firstName = email.substring(0,email.trim().indexOf("@"));
            String username = (isUsernameExists(firstName)) ? randString(10) : firstName;
            user = new User();
            user.setFirstName(firstName);
            user.setEmail(email);
            user.setUsername(username);
            user.setRole("USER");
            mongoOperation.save(user);

            //send email invitation here
        }

    }


}

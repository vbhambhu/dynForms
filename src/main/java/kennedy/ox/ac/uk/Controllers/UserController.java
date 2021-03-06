package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Models.Group;
import kennedy.ox.ac.uk.Models.User;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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


    @RequestMapping(value="/users", method= RequestMethod.GET)
    public String listUsers(Model model) {

        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        List<User> users = mongoOperation.find(query, User.class);
        model.addAttribute("users", users);

        return "users/list";
    }

    @RequestMapping(value="/users/{id}", method= RequestMethod.GET)
    public String editUser(Model model, @PathVariable String id) {

        User user = mongoOperation.findById(new ObjectId(id), User.class);

        if(user.getAcconutExpireDate() != null){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            user.setReadableAcconutExpireDate(formatter.format(user.getAcconutExpireDate()));
        }

        model.addAttribute("user", user);
        return "users/details";
    }

    @RequestMapping(value="/users/{id}", method= RequestMethod.POST)
    public String updateUser(@Valid User user, BindingResult bindingResult, Model model, @PathVariable String id) {

        //validate email
        if(isEmailExists(user.getEmail(), null)){
            bindingResult.rejectValue("email","user.email", "An account with provided email is already exists.");
        }

        //validation username
        if(isUsernameExists(user.getUsername(),null)){
            bindingResult.rejectValue("username","user.username", "Username is already exists. Please choose unique username.");
        }

        //validate expiry date
        if(user.getReadableAcconutExpireDate() != null || !user.getReadableAcconutExpireDate().isEmpty() ){
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date today = removeTime(new Date());
            Date expDate = null;
            try {
                expDate = formatter.parse(user.getReadableAcconutExpireDate());
                System.out.println(expDate);
            } catch (ParseException e) {
                e.getMessage();
            }

            if(expDate != null && expDate.before(today)  ){
                bindingResult.rejectValue("acconutExpireDate","user.acconutExpireDate", "Expire date must not be a past date.");
            }

            user.setAcconutExpireDate(expDate);
        }


        if (bindingResult.hasErrors()) {
            return "users/details";
        }

        return "users/desstails";

    }












    @RequestMapping(value="/user", method= RequestMethod.GET)
    public String showProfile(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username
        return "users/profile";
    }



    @RequestMapping(value="/users/new", method= RequestMethod.GET)
    public String newUser(User user) {
        return "users/create";
    }


    @RequestMapping(value="/users/new", method= RequestMethod.POST)
    public String createUser(@Valid User user, BindingResult bindingResult) {



        //validate email
        if(isEmailExists(user.getEmail(),null)){
            bindingResult.rejectValue("email","user.email", "An account with provided email is already exists.");
        }

        //validation username
        if(isUsernameExists(user.getUsername(),null)){
            bindingResult.rejectValue("username","user.username", "Username is already exists. Please choose unique username.");
        }

        //validate expiry date
        if(user.getReadableAcconutExpireDate() != null || !user.getReadableAcconutExpireDate().isEmpty() ){
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date today = removeTime(new Date());
            Date expDate = null;
            try {
                expDate = formatter.parse(user.getReadableAcconutExpireDate());
                System.out.println(expDate);
            } catch (ParseException e) {
                e.getMessage();
            }

            if(expDate != null && expDate.before(today)  ){
                bindingResult.rejectValue("acconutExpireDate","user.acconutExpireDate", "Expire date must not be a past date.");
            }

            user.setAcconutExpireDate(expDate);
        }

        //check if has errors
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
                } else if(isEmailExists(item, null)){
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

    private Boolean isEmailExists(String email, String userid){

        Query query = new Query();
        if(userid == null){
            query.addCriteria(Criteria.where("email").is(email));
        } else{
            query.addCriteria(Criteria.where("email").is(email).and("_id").not().is(new ObjectId(userid)));
        }

        User user = mongoOperation.findOne(query, User.class);
        return (user == null) ? false : true;
    }

    private Boolean isUsernameExists(String username, String userid){
        Query query = new Query();

        if(userid == null){
            query.addCriteria(Criteria.where("username").is(username));
        } else{
            query.addCriteria(Criteria.where("username").is(username).and("_id").not().is(new ObjectId(userid)));
        }

        User user = mongoOperation.findOne(query, User.class);
        return (user == null) ? false : true;
    }




    private void createUsers(List<String> emails){

        User user = new User();

        for (String email: emails) {
            email = email.trim();
            String firstName = email.substring(0,email.trim().indexOf("@"));
            String username = (isUsernameExists(firstName, null)) ? randString(10) : firstName;
            user = new User();
            user.setFirstName(firstName);
            user.setEmail(email);
            user.setUsername(username);
            user.setRole("USER");
            mongoOperation.save(user);

            //send email invitation here
        }

    }



    public Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


}

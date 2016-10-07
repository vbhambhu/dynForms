package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.User;
import kennedy.ox.ac.uk.Repositories.UserRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by vinod on 07/10/2016.
 */
@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value="/user/create", method= RequestMethod.GET)
    public void create() {


        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();



        User user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.addRole("ROLE_USER");
        user.addRole("ROLE_ADMIN");

        userRepository.save(user);
    }
}

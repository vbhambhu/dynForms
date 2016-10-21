package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.Project;
import kennedy.ox.ac.uk.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by vinod on 07/10/2016.
 */
@Controller
public class EmailController {

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(value="/emails", method= RequestMethod.GET)
    public String projectPage(Model model) {
        List<Project> projects = mongoOperation.findAll(Project.class);
        model.addAttribute("projects", projects);
        return "project/list";
    }


}

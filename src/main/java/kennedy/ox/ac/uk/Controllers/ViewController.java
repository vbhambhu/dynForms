package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by vinod on 07/10/2016.
 */
@Controller
public class ViewController {

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(value="/views", method= RequestMethod.GET)
    public String projectPage(Model model) {
        List<Project> projects = mongoOperation.findAll(Project.class);
        model.addAttribute("projects", projects);
        return "project/list";
    }


}

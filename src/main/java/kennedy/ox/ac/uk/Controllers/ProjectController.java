package kennedy.ox.ac.uk.Controllers;


import kennedy.ox.ac.uk.Models.Project;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * Created by vinod on 08/09/2016.
 */
@Controller
public class ProjectController {

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(value="/project/{id}", method= RequestMethod.GET)
    public String singleProjectPage(@PathVariable String id, Model model) {
        Project project = mongoOperation.findById(new ObjectId(id), Project.class);
        model.addAttribute("project", project);
        return "project/single";
    }

    @RequestMapping(value="/projects", method= RequestMethod.GET)
    public String projectPage(Model model) {
        List<Project> projects = mongoOperation.findAll(Project.class);
        model.addAttribute("projects", projects);
        return "project/list";
    }

    @RequestMapping(value="/projects/new", method= RequestMethod.GET)
    public String createProjectPage(Project project) {

        return "project/create";
    }

    @RequestMapping(value="/projects/new", method= RequestMethod.POST)
    public String createProjectPage(@Valid Project project,  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "project/create";
        }

        project.setCreatedAt(new Date());
        //Save to DB
        mongoOperation.save(project);

        return "redirect:/projects";
    }


}
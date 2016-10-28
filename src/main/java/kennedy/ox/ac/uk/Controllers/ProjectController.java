package kennedy.ox.ac.uk.Controllers;


import kennedy.ox.ac.uk.Models.Project;
import kennedy.ox.ac.uk.Models.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        List<User> users = mongoOperation.find(query, User.class);
        model.addAttribute("users", users);


//        Team team = new Team();
//
//        //
//        //team.add();
//
//        model.addAttribute("team", team);


        return "project/single";
    }

    @RequestMapping(value="/project/edit/{id}", method= RequestMethod.GET)
    public String editroject(@PathVariable String id, Model model) {
        Project project = mongoOperation.findById(new ObjectId(id), Project.class);
        model.addAttribute("project", project);
        return "project/single";
    }

    @RequestMapping(value="/projects", method= RequestMethod.GET)
    public String projectPage(Model model) {

        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        List<Project> projects = mongoOperation.find(query, Project.class);
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

        //Save object to DB
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        User user = mongoOperation.findOne(query,User.class);
        project.setCreatedAt(new Date());
        project.setOwner(user.getUsername());
        project.setUser(user.getUsername());
        mongoOperation.save(project);


        return "redirect:/projects";
    }


    @RequestMapping(value="/projects/archive", method= RequestMethod.GET)
    public String archiveProject(@RequestParam(value = "pid", required=true) String projectId) {

        //check if valid project id

        // check if user has right permissions

        //update object to DB

        //Create log audit
        Project project = mongoOperation.findById(new ObjectId(projectId), Project.class);
        project.setArchived(true);
        mongoOperation.save(project);
        return "redirect:/project/"+ projectId;
    }

    @RequestMapping(value="/projects/unarchive", method= RequestMethod.GET)
    public String unarchiveProject(@RequestParam(value = "pid", required=true) String projectId) {

        //check if valid project id

        // check if user has right permissions

        //update object to DB

        //Create log audit


        Project project = mongoOperation.findById(new ObjectId(projectId), Project.class);
        project.setArchived(false);
        mongoOperation.save(project);
        return "redirect:/project/"+ projectId;
    }


    @RequestMapping(value="/projects/delete", method= RequestMethod.GET)
    public String deleteProject(@RequestParam(value = "pid", required=true) String projectId) {

        //check if valid project id

        // check if user has right permissions

        //update object to DB
        //also delete all forms belong to project

        //Create log audit

        Project project = mongoOperation.findById(new ObjectId(projectId), Project.class);
        project.setDeleted(true);
        mongoOperation.save(project);
        return "redirect:/projects";
    }










}
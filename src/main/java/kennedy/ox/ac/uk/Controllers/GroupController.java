package kennedy.ox.ac.uk.Controllers;


import kennedy.ox.ac.uk.Models.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

@Controller
public class GroupController {

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(value="/groups", method= RequestMethod.GET)
    public String listGroups(Model model) {

        List<Group> groups =  mongoOperation.findAll(Group.class);
        model.addAttribute("groups", groups);
        return "groups/list";
    }


    @RequestMapping(value="/groups/new", method= RequestMethod.GET)
    public String addGroups(Group group) {
        return "groups/create";
    }


    @RequestMapping(value="/groups/new", method= RequestMethod.POST)
    public String addGroups(@Valid Group group, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "groups/create";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        group.setOwner(auth.getName());
        mongoOperation.save(group);

        return "redirect:/groups/"+group.getId();
    }






}

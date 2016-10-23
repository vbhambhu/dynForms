package kennedy.ox.ac.uk.Controllers;


import kennedy.ox.ac.uk.Models.Group;
import kennedy.ox.ac.uk.Models.Role;
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
public class RoleController {

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(value="/roles", method= RequestMethod.GET)
    public String listRoles(Model model) {

        List<Role> roles =  mongoOperation.findAll(Role.class);
        model.addAttribute("roles", roles);
        return "roles/list";
    }


    @RequestMapping(value="/roles/new", method= RequestMethod.GET)
    public String createRole(Role role) {
        return "roles/create";
    }


    @RequestMapping(value="/roles/new", method= RequestMethod.POST)
    public String createRole(@Valid Role role, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "roles/create";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        role.setOwner(auth.getName());
        mongoOperation.save(role);

        return "redirect:/roles/"+role.getId();
    }






}

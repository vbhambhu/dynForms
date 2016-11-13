package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.Field;
import kennedy.ox.ac.uk.Models.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kennedy.ox.ac.uk.Repositories.FormRepository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.security.Principal;

/**
 * Created by vinod on 08/09/2016.
 */
@Controller
public class HomeController {

    @RequestMapping(value={"", "/", "dashboard"}, method= RequestMethod.GET)
    public String showDashboard(Principal principal, Model model) {
        model.addAttribute("metaTitle", "daddas ada dasda dada");

        //String[] cssFiles = {"style1", "style2", "style3"};

        //String[] jsFiles = {"js1", "js2", "js.js"};

       // model.addAttribute("cssFiles", cssFiles);
        //model.addAttribute("jsFiles", jsFiles);


        return "account/dashboard";
    }



}
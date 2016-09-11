package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Repositories.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by vinodkumar on 10/09/2016.
 */
@Controller
public class FormController {

    @Autowired
    private FormRepository formRepository;


    @RequestMapping(value="/forms", method= RequestMethod.GET)
    public String showHome(Model model) {

        List<Form> forms = formRepository.findAll();
        model.addAttribute("forms", forms);
        return "form/list";

    }


}

package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Form;
import kennedy.ox.ac.uk.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by vinodkumar on 11/09/2016.
 */
@Controller
public class ImportController {

    @Autowired
    private FormRepository formRepository;

    @RequestMapping(value="/import/{id}", method= RequestMethod.GET)
    public String showHome(Model model, @PathVariable String id) {

        List<Form> forms = formRepository.findAll();
        model.addAttribute("forms", forms);
        return "import/index";

    }

}

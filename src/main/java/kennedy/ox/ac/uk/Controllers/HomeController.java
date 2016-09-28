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

/**
 * Created by vinod on 08/09/2016.
 */
@Controller
public class HomeController {

    @Autowired
    private FormRepository formRepository;
    

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String showHome() {
        return "home";
    }


    @RequestMapping(value="/capture/{id}", method= RequestMethod.GET)
    public String capture(Model model, @PathVariable String id) {

        Form form = formRepository.findById(id);
        model.addAttribute("form", form);
        return "capture";

    }


    @RequestMapping(value="/capture/{id}", method= RequestMethod.POST)
    public String capturePost(Model model, @RequestParam("formId") String formId, MultipartHttpServletRequest mrequest, @PathVariable String id) {

        Form form = formRepository.findById(formId);
        //FormValidator fv = new FormValidator(form);
        model.addAttribute("form",  form);
        //model.addAttribute("fv",  fv);

        for (Field field : form.getFields()) {

            String fieldValue = mrequest.getParameter(field.getName());
            field.setValue(fieldValue);  // for redisplay on error

            if(field.getRequired() != null && field.getRequired()){

                // if required
                if(fieldValue == null || fieldValue.trim().isEmpty() ){
                    field.setHasError(true);
                    field.setErrMsg(String.format(" The %s field is required." , field.getName()));
                }

            } else {
                //System.out.println("NULL" + field.getReqired());
            }
            //field.getValidations().validate(field, fieldValue, mrequest);

        }

        return "capture";
    }
}
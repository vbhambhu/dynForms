package kennedy.ox.ac.uk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kennedy.ox.ac.uk.FormRepository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

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


    @RequestMapping(value="/capture", method= RequestMethod.GET)
    public String capture(Model model) {

        Form f = formRepository.findById("57d15a45c13eb148b44e3075");
        model.addAttribute("form",  f);

//        System.out.println("==============" +f.getFields());
//
//        for (Field field : f.getFields()) {
//            System.out.println(field);
//        }

        return "capture";
    }

    @RequestMapping(value="/capture", method= RequestMethod.POST)
    public String capturePost(Model model, @RequestParam("formId") String formId, MultipartHttpServletRequest mrequest) {

        Form form = formRepository.findById(formId);
        //FormValidator fv = new FormValidator(form);
        model.addAttribute("form",  form);
        //model.addAttribute("fv",  fv);

        for (Field field : form.getFields()) {

            String fieldValue = mrequest.getParameter(field.getName());
            field.setValue(fieldValue);

            if(field.getReqired() != null && field.getReqired()){

                // if required
                if(fieldValue == null || fieldValue.trim().isEmpty() ){
                    field.setHasError(true);
                    field.setErrMsg("Invalid input");
                }

                // now check other validations
                for (Object validation : field.getValidation()) {
                    System.out.println(validation);
                }



            } else {
                //System.out.println("NULL" + field.getReqired());
            }


        }


       //



        //System.out.println(value1);
        return "capture";
    }

}
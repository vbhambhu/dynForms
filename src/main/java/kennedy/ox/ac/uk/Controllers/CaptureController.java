package kennedy.ox.ac.uk.Controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import kennedy.ox.ac.uk.Models.Field;
import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Models.Validation;
import kennedy.ox.ac.uk.Repositories.FormRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by vinod on 08/09/2016.
 */
@Controller
public class CaptureController {

    @Autowired
    private FormRepository formRepository;

    @Autowired
    MongoOperations mongoOperation;



    @RequestMapping(value="/capture/{id}", method= RequestMethod.GET)
    public String capture(Model model,
                          @PathVariable String id,
                          HttpServletResponse response,
                          @CookieValue(value="fidCookie", required=false) String fidCookie,
                          @CookieValue(value="feildIdsCookie", required=false) String feildIdsCookie) {

        Form form = formRepository.findById(id);
        model.addAttribute("form", form);


        System.out.println( this.getFieldIdsCookie(form));

        if(fidCookie != null){
            Cookie formIdcookie = new Cookie("fidCookie", form.getId());
            Cookie feildIdscookie = new Cookie("feildIdsCookie", this.getFieldIdsCookie(form) );
            formIdcookie.setMaxAge(360);
            feildIdscookie.setMaxAge(360);
            response.addCookie(formIdcookie);
            response.addCookie(feildIdscookie);
        }

        model.addAttribute("fieldLimit", 1);

        //Modify field list here


       // System.out.println(feildIdsCookie);


        //Settings settings = form.getSettings();
        //System.out.println(form.getSettings());
        return "form/capture";

    }

/*
    private List<Field> getDynamicFields(Form form) {

    }
*/
    private String getFieldIdsCookie(Form form){

        StringJoiner fieldSeu = new StringJoiner("|");

        for (Field field : form.getFields()) {
            fieldSeu.add(Integer.toString(field.getFieldId()) );
        }



        /*
        if adaptive - sequence

        else random - random sequence

        else per page - sequence with per page
         */
        return fieldSeu.toString();
    }


    @RequestMapping(value="/capture/{id}", method= RequestMethod.POST)
    public String capturePost(Model model, @RequestParam("formId") String formId, MultipartHttpServletRequest mrequest, @PathVariable String id) {

        Form form = formRepository.findById(formId);
        //FormValidator fv = new FormValidator(form);
        model.addAttribute("form",  form);
        //model.addAttribute("fv",  fv);

        Boolean formSuccess = true;

        //document object to keep data to store once successfull
        BasicDBObject document = new BasicDBObject();

        for (Field field : form.getFields()) {

            String fieldValue = mrequest.getParameter(field.getName());
            field.setValue(fieldValue);  // for redisplay on error

            document.put(field.getName(),fieldValue);

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

            for (Validation validation : field.getValidations()) {

                switch (validation.getKey()) {
                    case "minLength":
                        int minLengthValue = Integer.parseInt(validation.getValue());
                        if (fieldValue.length() < minLengthValue) {
                            formSuccess = false;
                            field.setHasError(true);
                            field.setErrMsg(String.format("The %s field must be at least %d characters in length.", field.getName(), minLengthValue));
                        }
                        break;
                    case "maxLength":
                        int maxLengthValue = Integer.parseInt(validation.getValue());
                        if (fieldValue.length() > maxLengthValue) {
                            formSuccess = false;
                            field.setHasError(true);
                            field.setErrMsg(String.format("The %s field cannot exceed %d characters in length", field.getName(), maxLengthValue));
                        }
                        break;
                }

//                if(field.getHasError() == null || !field.getHasError() ){
//
//                    System.out.println(fieldValue);
//
//                }

            }

        }

        if(formSuccess){

            //add data saving steps here!
            DBCollection collection = mongoOperation.getCollection(form.getId());
            collection.insert(document);
            return "form/thankyou";

        }


        return "form/capture";

    }
}

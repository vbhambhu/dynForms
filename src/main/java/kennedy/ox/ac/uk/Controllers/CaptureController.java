package kennedy.ox.ac.uk.Controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import kennedy.ox.ac.uk.Models.Field;
import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Models.Page;
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
import java.util.ArrayList;
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
                          HttpServletRequest request,
                          HttpServletResponse response,
                          @CookieValue(value="fidCookie", required=false) String fidCookie,
                          @CookieValue(value="fieldIdsCookie", required=false) String fieldIdsCookie) {
        return capture(model, id, 1, request, response, null, null);
    }
    @RequestMapping(value="/capture/{id}/{page}", method= RequestMethod.GET)
    public String capture(Model model,
                          @PathVariable String id,
                          @PathVariable int page,
                          HttpServletRequest request,
                          HttpServletResponse response,
                          @CookieValue(value="fidCookie", required=false) String fidCookie,
                          @CookieValue(value="fieldIdsCookie", required=false) String fieldIdsCookie) {

        Form form = formRepository.findById(id);
        model.addAttribute("form", form);

        final int fieldsPerPage = 2;

        if(page == 1) {
            if(fieldsPerPage  > 0) {
                // build page list at start.
                List<Page> pages = new ArrayList<>();
                Page _page = null;
                int number = 0;
                for (Field field : form.getFields()) {
                    if (_page == null || _page.getFields().size() >= fieldsPerPage) {
                        _page = new Page(++number);
                        pages.add(_page);
                    }
                    _page.add(field);
                }
                // store in session
                request.getSession().setAttribute("pages", pages);
            }
            else {
                request.getSession().setAttribute("pages", null);
            }
        }

        if(request.getSession().getAttribute("pages") != null)
            model.addAttribute("fields", ((List<Page>)request.getSession().getAttribute("pages")).get(page-1).getFields());
        else
            model.addAttribute("fields", form.getFields());

        System.out.println( this.getFieldIdsCookie(form));

        if(fidCookie != null){
            Cookie formIdcookie = new Cookie("fidCookie", form.getId());
            Cookie fieldIdscookie = new Cookie("fieldIdsCookie", this.getFieldIdsCookie(form) );
            formIdcookie.setMaxAge(360);
            fieldIdscookie.setMaxAge(360);
            response.addCookie(formIdcookie);
            response.addCookie(fieldIdscookie);
        }

        model.addAttribute("fieldLimit", 1);

        //Modify field list here


        // System.out.println(fieldIdsCookie);


        //Settings settings = form.getSettings();
        //System.out.println(form.getSettings());
        return "capture/index";

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


    @RequestMapping(value="/capture/{id}/{psge}", method= RequestMethod.POST)
    public String capturePost(Model model,
                              @RequestParam("formId") String formId,
                              MultipartHttpServletRequest mrequest,
                              @PathVariable String id,
                              @PathVariable int page) {

        Form form = formRepository.findById(formId);
        //FormValidator fv = new FormValidator(form);
        model.addAttribute("form",  form);
        //model.addAttribute("fv",  fv);

        Boolean formSuccess = true;

        //document object to keep data to store once successful
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
            if(mrequest.getSession().getAttribute("pages") != null || ((List<Page>)mrequest.getSession().getAttribute("pages")).size() > page)
                return "form/" + form.getId() + "/" + (page + 1);
            else
                return "form/thankyou";

        }


        return "capture/index";

    }
}
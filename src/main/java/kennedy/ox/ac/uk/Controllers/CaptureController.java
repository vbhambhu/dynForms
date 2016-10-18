package kennedy.ox.ac.uk.Controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import kennedy.ox.ac.uk.Models.Field;
import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Models.Page;
import kennedy.ox.ac.uk.Models.Validation;
import kennedy.ox.ac.uk.Repositories.FormRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.bson.types.ObjectId;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by vinod on 08/09/2016.
 */
@Controller
public class CaptureController {

    @Autowired
    MongoOperations mongoOperation;


    @RequestMapping(value="/capture/{id}", method = { RequestMethod.GET })
    public String capture(@PathVariable String id,
                          HttpServletRequest request,
                          HttpServletResponse response) {

        Form form = mongoOperation.findById(new ObjectId(id), Form.class);

        List<Page> pages = new ArrayList<>();
        int number = 0;

        if (form.getMode() == null) {
            form.setMode(Form.Modes.unpaged);
        }  else if (form.isPaged() && form.getQuestionsPerPage() > 0) {
            // build page list at start.
            Page _page = null;
            for (Field field : form.getFields()) {
                if (_page == null || _page.getFields().size() >= form.getQuestionsPerPage()) {
                    _page = new Page(++number);
                    pages.add(_page);
                }
                _page.add(field);
            }
            // store in session
        } else if (form.isRandom()) {
            // select questions
            form.setQuestionsPerPage(1);
            List<Integer> nums = new ArrayList<>();
            List<Integer> questions = new ArrayList<>();
            // Create random
            Random rand = new Random();
            //Create array of fields: 1,2,3,4,5,6,7,8,9,10
            for( int i = 0; i < form.getFields().size(); i++) {
                nums.add(i);
            }
            //use random to search in array (array size = 10 items)
            while(questions.size() < form.getRandomQuestions()) {
                // use random to search in array (array size = 8 items)
                int index = rand.nextInt(nums.size()); // to include 0
                questions.add(index);
                nums.remove(nums.indexOf(index));            // remove selected entry
            }
            // sort into ascending order
            Collections.sort(questions);

            Page _page = null;
            for (int  question : questions) {
                if (_page == null || _page.getFields().size() >= form.getQuestionsPerPage()) {
                    _page = new Page(++number);
                    pages.add(_page);
                }
                _page.add(form.getFields().get(question));
            }
        } else if (form.isAdaptive()) {
            // each one on a page and the page is stepped over
            for (Field field : form.getFields()) {
                pages.add(new Page(++number, field));
            }
        } else {    // all on one page
            pages.add(new Page(++number, form.getFields()));
        }

        request.getSession().setAttribute("pages", pages);

        return "redirect:/capture/" + id + "/1/";
    }

    @RequestMapping(value="/capture/{id}/{page}", method= RequestMethod.GET)
    public String capture(Model model,
                          @PathVariable String id,
                          @PathVariable int page,
                          HttpServletRequest request,
                          HttpServletResponse response) {

        Form form = mongoOperation.findById(new ObjectId(id), Form.class);

        //add * in front of label if required is in validation.
        for(Field field : form.getFields()){
            for (Validation validation : field.getValidations()) {
                if(validation.getKey().equals("required") && validation.getValue().equals("true")){
                    field.setLabel(field.getLabel() + "*");
                }
            }
            System.out.println(field.getLabel());
        }

        model.addAttribute("form", form);

        List<Page> pages = (List<Page>)request.getSession().getAttribute("pages");
        Page _page = pages.get(page - 1);    // start at 0
        model.addAttribute("page", _page);

        return "capture/index";

    }


    @RequestMapping(value="/capture/{id}/{page}", method= RequestMethod.POST)
    public String capturePost(Model model,
                              @RequestParam("formId") String formId,
                              MultipartHttpServletRequest mrequest,
                              @PathVariable String id,
                              @PathVariable int page) {

        Form form = mongoOperation.findById(new ObjectId(id), Form.class);
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

            //if(field.getRequired() != null && field.getRequired()){

                // if required
//                if(fieldValue == null || fieldValue.trim().isEmpty() ){
//                    field.setHasError(true);
//                    field.setErrMsg(String.format(" The %s field is required." , field.getName()));
//                }

           // } else {
                //System.out.println("NULL" + field.getReqired());
           // }


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

        if(formSuccess) {

            //add data saving steps here!
            DBCollection collection = mongoOperation.getCollection(form.getId());
            collection.insert(document);

            List<Page> pages = (List<Page>)mrequest.getSession().getAttribute("pages");

            int nextPage = page + 1;
            if(form.isAdaptive() && pages != null) {
                // disable a page based on answer of this
                // then see which is the next page
                while(!pages.get(page - 1).isEnabled()) {
                    if(page < pages.size())
                        nextPage++;
                    else
                        break;
                }
            }

            if(page < pages.size())
                return "redirect:/capture/" + form.getId() + "/" + nextPage;
            else
                return "redirect:/form/thankyou";

        }

        return "capture/index";

    }
}
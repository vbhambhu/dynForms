package kennedy.ox.ac.uk.API;

import groovy.time.BaseDuration;
import kennedy.ox.ac.uk.Models.Form;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by vinodkumar on 15/10/2016.
 */

@RestController
@RequestMapping("/api/forms")
public class FormRestController {

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Form> allForms() {
        return mongoOperation.findAll(Form.class);
    }

    @RequestMapping(value = "/{formId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Form showStory(@PathVariable("formId") String formId) {
        Form form = mongoOperation.findById(new ObjectId(formId), Form.class);
//        if (form == null) {
//            throw new StoryNotFoundException(storyId);
//        }
        return form;
    }

    @RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> submitForm(@RequestBody Form form) {

        mongoOperation.save(form);

        //mongoOperation.


        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        return responseEntity;
    }








}

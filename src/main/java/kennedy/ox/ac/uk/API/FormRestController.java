package kennedy.ox.ac.uk.API;

import kennedy.ox.ac.uk.Helpers.Validation;
import kennedy.ox.ac.uk.Models.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
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
    public DataTableOutput getForms(@RequestParam(value = "start", required = false) String start,
                                    @RequestParam(value = "length", required = false) String length,
                                    @RequestParam(value = "sortCol", required = false) String sortCol,
                                    @RequestParam(value = "orderDir", required = false) String orderDir,
                                    @RequestParam(value = "sCol", required = false) String sCol,
                                    @RequestParam(value = "sQry", required = false) String sQry) {

        DataTableOutput data = new DataTableOutput();

        HashMap cols = new HashMap();
        cols.put("1", "_id");
        cols.put("2", "title");
        cols.put("3", "description");
        cols.put("4", "createdAt");
        cols.put("5", "isPublished");
        cols.put("6", "isLocked");


        //Map<String, String[]> parameters = request.getParameterMap();

        if(start == null){ start = "1"; }
        if(length == null){ length = "10"; }
        if(sortCol == null){ sortCol = "name"; }
        if(orderDir == null){ orderDir = "asc"; }

        Validation validation = new Validation();

        if(!validation.isNumeric(start) || !validation.isNumeric(length)){
            data.setError("Invalid request.");
            return data;
        }

        //System.out.println(parameters);

        //total records
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        long totalRecords = mongoOperation.count(query, Form.class);
        data.setRecordsTotal(totalRecords);
        data.setRecordsFiltered(totalRecords);


        //records with query
        query = new Query();

        if(orderDir.equals("asc")){
            query.with(new Sort(Sort.Direction.ASC, sortCol));
        } else{
            query.with(new Sort(Sort.Direction.DESC, sortCol));
        }

        query.addCriteria(Criteria.where("isDeleted").is(false));
        query.skip(Integer.parseInt(start));
        query.limit(Integer.parseInt(length));
        List<Form> forms = mongoOperation.find(query, Form.class);
        data.setData(forms);

        return data;
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
    public RestResult createForm(@Valid @RequestBody Form form, Errors errors) {

        RestResult result =  new RestResult();

        if (errors.hasErrors()) {
            result.setHasError(true);

            for (FieldError fieldError : errors.getFieldErrors()) {
                result.addError(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return result;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        form.setCreatedAt(new Date());
        form.setOwner(username);

        if(!username.equals("anonymousUser")){
            mongoOperation.save(form);
        }

        return result;

    }








}

package kennedy.ox.ac.uk.API;

import kennedy.ox.ac.uk.Models.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.sym.error;

@RestController
@RequestMapping("/api/projects")
public class ProjectRestController {

    @Autowired
    MongoOperations mongoOperation;


    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DataTable allForms() {

        DataTable dt = new DataTable();


        Query query = new Query();
        //query.with(new Sort(Sort.Direction.DESC, "createdAt"));
        // query.fields().include("id");
        query.fields().include("username");
        //query.fields().include("firstName");

        long totalRecords = mongoOperation.count(query, User.class);
        long filteredRecords = mongoOperation.count(query, User.class);

        dt.setDraw(1);
        dt.setRecordsFiltered(filteredRecords);
        dt.setRecordsTotal(totalRecords);
        List<User> users = mongoOperation.findAll(User.class);

        dt.setData(users);
        return dt;

    }



    @RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestValidationResult createProject(@Valid @RequestBody Project project, Errors errors) {

        RestValidationResult result =  new RestValidationResult();

        if (errors.hasErrors()) {
            result.setHasError(true);

            for (FieldError fieldError : errors.getFieldErrors()) {
                result.addError(fieldError.getField(), fieldError.getDefaultMessage());
            }

            return result;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        project.setCreatedAt(new Date());
        project.setOwner(username);
        project.setUser(username);

        if(!username.equals("anonymousUser")){
            mongoOperation.save(project);
        }

        return result;

    }










}

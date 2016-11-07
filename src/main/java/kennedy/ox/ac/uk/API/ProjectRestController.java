package kennedy.ox.ac.uk.API;

import kennedy.ox.ac.uk.Models.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.sym.error;

@RestController
@RequestMapping("/api/projects")
public class ProjectRestController {

    @Autowired
    MongoOperations mongoOperation;


    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DataTable allForms(HttpServletRequest request, DataTableRequest dataTableRequest) {

        Map<String, String[]> parameters = request.getParameterMap();


       // List<HashMap<String, String>> order =  dataTableRequest.getOrder();

        System.out.println(parameters);
        System.out.println(dataTableRequest.getColumns());

        //System.out.println(dataTableRequest.getOrder().get(0).get("column"));




        //System.out.println(dataTableRequest.getColumns().get(orderColIndex).get("data"));
        //System.out.println(orderColDir);
        //System.out.println(length);


        DataTable dt = new DataTable();
        dt.setDraw(dataTableRequest.getDraw());

        //total records
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        long totalRecords = mongoOperation.count(query, Project.class);

        dt.setRecordsTotal(totalRecords);
        dt.setRecordsFiltered(totalRecords);

        //records with query
        int orderColIndex = Integer.parseInt(dataTableRequest.getOrder().get(0).get("column"));
        String orderColDir = dataTableRequest.getOrder().get(0).get("dir");
        String sortCol = dataTableRequest.getColumns().get(orderColIndex).get("data");
        query = new Query();
        if(orderColDir.equals("asc")){
            query.with(new Sort(Sort.Direction.ASC, sortCol));
        } else{
            query.with(new Sort(Sort.Direction.DESC, sortCol));
        }

        query.skip(dataTableRequest.getStart());
        query.limit(dataTableRequest.getLength());




        //long filteredRecords = mongoOperation.count(query, Project.class);
        //dt.setRecordsFiltered(filteredRecords);

        List<Project> projects = mongoOperation.find(query, Project.class);



        dt.setData(projects);
        return dt;

        //return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);

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

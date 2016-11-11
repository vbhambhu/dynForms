package kennedy.ox.ac.uk.API;


import kennedy.ox.ac.uk.Helpers.Validation;
import kennedy.ox.ac.uk.Models.DataTableInput;
import kennedy.ox.ac.uk.Models.DataTableOutput;
import kennedy.ox.ac.uk.Models.Project;
import kennedy.ox.ac.uk.Models.RestValidationResult;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectRestController {

    @Autowired
    MongoOperations mongoOperation;


    @RequestMapping(value = "/datatable", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DataTableOutput getProjectsDT(DataTableInput dataTableInput) {

        //total records
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        long totalRecords = mongoOperation.count(query, Project.class);

        //records with query
        int orderColIndex = Integer.parseInt(dataTableInput.getOrder().get(0).get("column"));
        String orderColDir = dataTableInput.getOrder().get(0).get("dir");
        String sortCol = dataTableInput.getColumns().get(orderColIndex).get("data");
        query = new Query();

        if(orderColDir.equals("asc")){
            query.with(new Sort(Sort.Direction.ASC, sortCol));
        } else{
            query.with(new Sort(Sort.Direction.DESC, sortCol));
        }

        query.skip(dataTableInput.getStart());
        query.limit(dataTableInput.getLength());
        List<Project> projects = mongoOperation.find(query, Project.class);

        return new DataTableOutput(dataTableInput.getDraw(),totalRecords, totalRecords, projects);

    }


    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DataTableOutput getProjects(HttpServletRequest request,
                                       @RequestParam(value = "start", required = false) String start,
                                       @RequestParam(value = "length", required = false) String length,
                                       @RequestParam(value = "sortCol", required = false) String sortCol,
                                       @RequestParam(value = "orderDir", required = false) String orderDir,
                                       @RequestParam(value = "scol", required = false) String sCol,
                                       @RequestParam(value = "sqry", required = false) String sQry) {

        DataTableOutput data = new DataTableOutput();
        HashMap cols = new HashMap();
        cols.put("1", "_id");
        cols.put("2", "name");
        cols.put("3", "description");
        cols.put("4", "createdAt");
        cols.put("5", "owner");
        cols.put("6", "isDeleted");
        cols.put("7", "formCount");
        cols.put("8", "isArchived");


        Map<String, String[]> parameters = request.getParameterMap();

        if(start == null){ start = "0"; }
        if(length == null){ length = "10"; }
        if(sortCol == null){ sortCol = "name"; }
        if(orderDir == null){ orderDir = "asc"; }

        Validation validation = new Validation();

        if(!validation.isNumeric(start) || !validation.isNumeric(length)){
            data.setError("Invalid request.");
            return data;
        }

        System.out.println(parameters);


        //total records
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        if(sCol != null && sQry != null){
            query.addCriteria(Criteria.where(cols.get(sCol).toString()).regex(sQry));
        }
        long totalRecords = mongoOperation.count(query, Project.class);
        data.setRecordsTotal(totalRecords);
        data.setRecordsFiltered(totalRecords);


        //records with query
        query = new Query();

        if(orderDir.equals("asc")){
            query.with(new Sort(Sort.Direction.ASC, sortCol));
        } else{
            query.with(new Sort(Sort.Direction.DESC, sortCol));
        }

        //if search is provided
        System.out.println(sCol);
        System.out.println(sQry);

        query.addCriteria(Criteria.where("isDeleted").is(false));

        if(sCol != null && sQry != null){
            query.addCriteria(Criteria.where(cols.get(sCol).toString()).regex(sQry));
        }


        query.skip(Integer.parseInt(start));
        query.limit(Integer.parseInt(length));
        List<Project> projects = mongoOperation.find(query, Project.class);
        data.setData(projects);

        return data;
    }






//    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public DataTableOutput allForms(HttpServletRequest request, DataTableInput dataTableRequest) {
//
//        Map<String, String[]> parameters = request.getParameterMap();
//
//        DataTableOutput dt = new DataTableOutput();
//        dt.setDraw(dataTableRequest.getDraw());
//
//        //total records
//        Query query = new Query();
//        query.addCriteria(Criteria.where("isDeleted").is(false));
//        long totalRecords = mongoOperation.count(query, Project.class);
//
//        dt.setRecordsTotal(totalRecords);
//        dt.setRecordsFiltered(totalRecords);
//
//        //records with query
//        int orderColIndex = Integer.parseInt(dataTableRequest.getOrder().get(0).get("column"));
//        String orderColDir = dataTableRequest.getOrder().get(0).get("dir");
//        String sortCol = dataTableRequest.getColumns().get(orderColIndex).get("data");
//        query = new Query();
//        if(orderColDir.equals("asc")){
//            query.with(new Sort(Sort.Direction.ASC, sortCol));
//        } else{
//            query.with(new Sort(Sort.Direction.DESC, sortCol));
//        }
//
//        query.skip(dataTableRequest.getStart());
//        query.limit(dataTableRequest.getLength());
//        List<Project> projects = mongoOperation.find(query, Project.class);
//        dt.setData(projects);
//        return dt;
//    }



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

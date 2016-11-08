package kennedy.ox.ac.uk.Controllers;

import com.mongodb.WriteResult;
import kennedy.ox.ac.uk.Models.Field;
import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Models.Project;
import kennedy.ox.ac.uk.Models.User;
import kennedy.ox.ac.uk.Repositories.FormRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vinodkumar on 10/09/2016.
 */
@Controller
public class FormController {

    @Autowired
    private FormRepository formRepository;

    @Autowired
    MongoOperations mongoOperation;


    @RequestMapping(value="/forms", method= RequestMethod.GET)
    public String showFroms(Model model) {

       // List<Form> forms = formRepository.findAll();
       // model.addAttribute("forms", forms);
        return "form/list";
    }


    @RequestMapping(value="/forms/new", method= RequestMethod.GET)
    public String createForm(Model model, Form form,
                             @RequestParam(value = "pid", required=false) String projectId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("owner").is("admin"));
        List<Project> projects = mongoOperation.findAll(Project.class);

        model.addAttribute("projectId", projectId);
        model.addAttribute("projects", projects);

        return "form/create";

    }


    @RequestMapping(value="/forms/new", method= RequestMethod.POST)
    public String insertForm(Model model, @Valid Form form, BindingResult bindingResult,
                             @RequestParam(value = "pid", required=false) String projectId,
                             @RequestParam("projectIdInput") String projectIdInput) {


        if(projectIdInput.equals("NONE")){
            model.addAttribute("projectIdErr", "Please select valid project.");
        }

        //Query query = new Query();
        //query.addCriteria(Criteria.where("owner").is("spadmin"));
        List<Project> projects = mongoOperation.findAll(Project.class);

        model.addAttribute("projectId", projectId);
        model.addAttribute("projects", projects);


        if (bindingResult.hasErrors()) {
            return "form/create";
        }

        form.setPublished(false);
        form.setMode("paged");
        form.setQuestionsPerPage(2);
        form.setRandomQuestions(0);
        form.setLocked(false);
        form.setCreatedAt(new Date());
        List<String> list = new ArrayList<>();
        list.add(projectIdInput);
        form.setProjectIds(list);
        mongoOperation.save(form);

        String newFormId = form.getId();

        //increment formCount
        Project project = mongoOperation.findById(new ObjectId(projectIdInput), Project.class);
        project.setFormCount(project.getFormCount() + 1);
        mongoOperation.save(project);

        return "redirect:/forms/design/"+newFormId;

    }

    @RequestMapping(value="/forms/design/{id}", method= RequestMethod.GET)
    public String designForm(Model model, @PathVariable String id) {

        //do validation here if that is exist, owned by user , not locked and enabled  - maybe permission too.
        Form form = mongoOperation.findById(new ObjectId(id), Form.class);

        model.addAttribute("form", form);

        return "form/design";
    }


    @RequestMapping(value="/form/insert", method= RequestMethod.GET)
    public String test() {

        Form form = new Form("form title", "this is form description.", new Date() );
        mongoOperation.save(form);

        System.out.print(form.getId());

        String fid = form.getId();

        Field field = new Field("text", "username","Username");
        System.out.print(field);
        System.out.print(field.getName());

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(fid)));
        Update update = new Update();
        update.push("fields", field);
        mongoOperation.updateFirst(query, update , Form.class);

        return "redirect:/forms";

    }
    @RequestMapping(value="/form/thankyou", method= RequestMethod.GET)
    public String thankyou() {
        return "form/thankyou";
    }
}
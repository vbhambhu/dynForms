package kennedy.ox.ac.uk.Controllers;

import com.mongodb.WriteResult;
import kennedy.ox.ac.uk.Models.Field;
import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Repositories.FormRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    public String showHome(Model model) {

        List<Form> forms = formRepository.findAll();
        model.addAttribute("forms", forms);
        return "form/list";

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

}
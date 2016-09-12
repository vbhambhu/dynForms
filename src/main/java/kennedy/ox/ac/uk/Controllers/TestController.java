package kennedy.ox.ac.uk.Controllers;

import groovy.time.BaseDuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Models.Field;

/**
 * Created by vinod on 12/09/2016.
 */
@Controller
public class TestController {


    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(value="/test", method= RequestMethod.GET)
    public String showHome() {


        //Form f = new Form("titltttfdfasdada", "dddddddd");

        //save employee object into "employee" collection
        //mongoOperation.save(f);

        Field f = new Field(2, "text","dddd","dddddddd");

        addTrack(f);


        return "home";
    }

    public void addTrack(Field field) {

        System.out.println("I sm in field" + field);
        Form form = mongoOperation.findOne
                (new Query(Criteria.where("_id").is("57d67f3b6862f511c5eda6e7")), Form.class);

        System.out.println("can find form" + form);

        if (form != null) {
            form.getFields().add(field);
            mongoOperation.save(form);
        }
    }


}

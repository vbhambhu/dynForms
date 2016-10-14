package kennedy.ox.ac.uk.Controllers;


import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by vinod on 08/09/2016.
 */
@Controller
public class ProjectController {

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(value="/projects", method= RequestMethod.GET)
    public String projectPage(Model model) {


//        Query query = new Query();
//        query.addCriteria(Criteria.where("_id").is(new ObjectId(fid)));
//        Update update = new Update();
//        update.push("fields", field);

        return "project/list";

    }

    @RequestMapping(value="/projects/new", method= RequestMethod.GET)
    public String createProjectPage(Model model) {

        return "project/create";

    }


}
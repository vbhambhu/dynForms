package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Helpers.storage.StorageService;
import kennedy.ox.ac.uk.Repositories.FormRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

/**
 * Created by vinodkumar on 11/09/2016.
 */
@Controller
public class ImportController {

    @Autowired
    MongoOperations mongoOperation;

    private final StorageService storageService;

    @Autowired
    public ImportController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(value = "/import/{id}", method = RequestMethod.GET)
    public String importForm(Model model, @PathVariable String id) {
        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        Form form = mongoOperation.findOne(q, Form.class);
        model.addAttribute("form", form);
        return "import/step-one";
    }

    @RequestMapping(value = "/import/{id}", method = RequestMethod.POST)
    public String importFormPost(Model model, @PathVariable String id, @RequestParam("file") MultipartFile file) {

        //List<Form> forms = formRepository.findAll();
        //model.addAttribute("forms", forms);

        storageService.store(file);

        return "import/step-one";

    }

    @RequestMapping(value = "/import/validation/{id}", method = RequestMethod.GET)
    public String imporstFormPost(Model model, @RequestParam("formId") String formId, MultipartHttpServletRequest mrequest, @PathVariable String id) {

        return "rrr";
    }
}
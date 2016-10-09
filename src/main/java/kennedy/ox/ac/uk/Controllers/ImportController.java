package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.FileImport;
import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Helpers.storage.StorageService;
import kennedy.ox.ac.uk.Repositories.FormRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.List;

/**
 * Created by vinodkumar on 11/09/2016.
 */
@Controller
public class ImportController {

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(value = "/import/{id}", method = RequestMethod.GET)
    public String importForm(Model model,
                             @PathVariable String id,
                             FileImport fileImport,
                             BindingResult bindResult) {
        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        Form form = mongoOperation.findOne(q, Form.class);
        model.addAttribute("form", form);
        return "import/step-one";
    }

    @RequestMapping(value = "/import/{id}", method = RequestMethod.POST)
    public String importFormPost(Model model,
                                 @PathVariable String id,
                                 FileImport fileImport,
                                 BindingResult bindingResult,
                                 HttpServletRequest request,
                                 @RequestParam("file") MultipartFile file) throws IOException {

        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        Form form = mongoOperation.findOne(q, Form.class);
        model.addAttribute("form", form);


        if (file.isEmpty()) {
            bindingResult.rejectValue("file","fileImport.file", "You did not select a file to upload.");
        } else if (!file.getContentType().equalsIgnoreCase("text/csv")){
            bindingResult.rejectValue("file","fileImport.file", "The filetype you are attempting to upload is not allowed.");
        } else if(file.getSize() > 5000000){ // 5mb
            bindingResult.rejectValue("file","fileImport.file", "The file you are attempting to upload is larger than the permitted size.");
        }


        if (bindingResult.hasErrors()) {
            return "import/step-one";
        }


        String name = file.getOriginalFilename();
        String ext = name.substring(name.lastIndexOf(".") + 1);
        String newFileName = new BigInteger(130, new SecureRandom()).toString(32);

        try {
           // File ds = new File("/Users/vinodkumar/IdeaProjects/dynForms/upload-dir/" + newFileName+"."+ext);
            File ds = new File("/Users/vinodkumar/IdeaProjects/dynForms/upload-dir/" + name);
            file.transferTo(ds);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return "redirect:/import/step-two/"+id;

    }


    @RequestMapping(value = "/import/step-two/{id}", method = RequestMethod.GET)
    public String stepTwo(Model model, @PathVariable String id) {

        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        Form form = mongoOperation.findOne(q, Form.class);
        model.addAttribute("form", form);

        return "import/step-two";
    }





}
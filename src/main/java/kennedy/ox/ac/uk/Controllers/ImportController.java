package kennedy.ox.ac.uk.Controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import kennedy.ox.ac.uk.Models.Field;
import kennedy.ox.ac.uk.Models.FileImport;
import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Helpers.storage.StorageService;
import kennedy.ox.ac.uk.Repositories.FormRepository;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.List;
import java.util.Scanner;

/**
 * Created by vinodkumar on 11/09/2016.
 */
@Controller
public class ImportController {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.size}")
    private int uploadSize;

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
        } else if(file.getSize() > uploadSize){ // 5mb
            bindingResult.rejectValue("file","fileImport.file", "The file you are attempting to upload is larger than the permitted size.");
        }

        //another validation here to check count of cols


        if (bindingResult.hasErrors()) {
            return "import/step-one";
        }


        String name = file.getOriginalFilename();
        String ext = name.substring(name.lastIndexOf(".") + 1);
        //random filename
        String newFileName = new BigInteger(130, new SecureRandom()).toString(32);

        try {
            File ds = new File(uploadPath + newFileName + "." + ext);
            file.transferTo(ds);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }




        try {

            File uploadedFile = new File(uploadPath + newFileName + "." + ext);
            Scanner inputStream = new Scanner(uploadedFile);

            String tempCollectionName = new BigInteger(130, new SecureRandom()).toString(20);

            BasicDBObject document;



            while (inputStream.hasNext()){

                String line = inputStream.nextLine();

                String[] lineData = line.split(",");

                document = new BasicDBObject();

                int fieldIndex = 0;

                for (Field field : form.getFields()){
                    document.put(field.getName(),lineData[fieldIndex]);
                    fieldIndex++;
                }

                //System.out.println("=====================");
                //System.out.println(document.toString());

                DBCollection collection = mongoOperation.getCollection("tmpdata-"+form.getId());
                collection.insert(document);

            }

            //Delete uploaded file as we have data in database
            uploadedFile.delete();

        }  catch (Exception e){
            e.printStackTrace();
        }

        //return "import/step-one";
        return "redirect:/import/step-two/"+id;

    }


    @RequestMapping(value = "/import/step-two/{id}", method = RequestMethod.GET)
    public String stepTwo(Model model, @PathVariable("id") String id) {

        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        Form form = mongoOperation.findOne(q, Form.class);
        model.addAttribute("form", form);

        DBCollection collection = mongoOperation.getCollection("tmpdata-"+id);
        DBCursor cursor = collection.find();

        model.addAttribute("data", cursor);

        while(cursor.hasNext()) {
            System.out.println(cursor.next());
        }


        return "import/step-two";
    }


    @RequestMapping(value = "/import/step-twdsaadadadadao/{id}/{name}", method = RequestMethod.GET)
    public String stepTwdfso(Model model, @PathVariable("id") String id) {

        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        Form form = mongoOperation.findOne(q, Form.class);
        model.addAttribute("form", form);


        //read CSV
        String fileName = ".csv";


        File file = new File(uploadPath+fileName);

        JSONObject rowObject = new JSONObject();
        JSONArray rowArray;

        JSONObject colObject;
        JSONArray colArray = new JSONArray();

        JSONObject outerObject  = new JSONObject();

        try{

            Scanner inputStream = new Scanner(file);

            int id_count = 1;

            while (inputStream.hasNext()){
                String line = inputStream.nextLine();

                String[] lineData = line.split(",");

                rowArray =  new JSONArray();

                for (int i = 0; i < lineData.length; i++) {

                    rowObject = new JSONObject();
                    rowObject.put("value", lineData[i]);
                    rowObject.put("colErr", false);
                    rowArray.add(rowObject);
                }

                //System.out.println(rowArray.toJSONString());

                colObject = new JSONObject();
                colObject.put("id", id_count);
                colObject.put("cols", rowArray);
                colObject.put("rowErr", false);



                colArray.add(colObject);
                id_count++;



               // System.out.println(line);
            }

            outerObject.put("rows", colArray);
            System.out.println("==================================");
            System.out.println(outerObject.toJSONString());

        } catch (FileNotFoundException e){
            e.printStackTrace();
        }


        model.addAttribute("data", outerObject);


        return "import/step-two";
    }



}
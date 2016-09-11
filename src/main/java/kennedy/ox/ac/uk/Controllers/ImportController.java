package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Models.Form;
import kennedy.ox.ac.uk.Helpers.storage.StorageService;
import kennedy.ox.ac.uk.Repositories.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private FormRepository formRepository;

    private final StorageService storageService;

    @Autowired
    public ImportController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(value = "/import/{id}", method = RequestMethod.GET)
    public String importForm(Model model, @PathVariable String id) {

        List<Form> forms = formRepository.findAll();
        model.addAttribute("forms", forms);
        return "import/index";

    }

    @RequestMapping(value = "/import/{id}", method = RequestMethod.POST)
    public String importFormPost(Model model, @PathVariable String id, @RequestParam("file") MultipartFile file) {

        List<Form> forms = formRepository.findAll();
        model.addAttribute("forms", forms);

        storageService.store(file);

        return "import/index";

    }

    @RequestMapping(value = "/import/validation/{id}", method = RequestMethod.GET)
    public String imporstFormPost(Model model, @RequestParam("formId") String formId, MultipartHttpServletRequest mrequest, @PathVariable String id) {

        return "rrr";
    }
}
package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Form;
import kennedy.ox.ac.uk.Helpers.storage.StorageService;
import kennedy.ox.ac.uk.Repositories.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

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

    @RequestMapping(value="/import/{id}", method= RequestMethod.GET)
    public String showHome(Model model, @PathVariable String id) {

        List<Form> forms = formRepository.findAll();
        model.addAttribute("forms", forms);

//        model.addAttribute("files", storageService
//                .loadAll()
//                .map(path ->
//                        MvcUriComponentsBuilder
//                                .fromMethodName(ImportController.class, "serveFile", path.getFileName().toString())
//                                .build().toString())
//                .collect(Collectors.toList()));


        return "import/index";

    }

}

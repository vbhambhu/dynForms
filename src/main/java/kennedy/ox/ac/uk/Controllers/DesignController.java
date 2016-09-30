package kennedy.ox.ac.uk.Controllers;

import kennedy.ox.ac.uk.Repositories.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by vinod on 08/09/2016.
 */
@Controller
public class DesignController {

    @Autowired
    private FormRepository formRepository;
    

    @RequestMapping(value="/form/design", method= RequestMethod.GET)
    public String designPage() {
        return "form/design";
    }

}
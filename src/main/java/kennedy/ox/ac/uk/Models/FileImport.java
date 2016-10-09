package kennedy.ox.ac.uk.Models;

/**
 * Created by vinodkumar on 08/10/2016.
 */
import org.springframework.web.multipart.MultipartFile ;

import static javax.swing.text.DefaultStyledDocument.ElementSpec.ContentType;


public class FileImport {


    MultipartFile file ;

    public MultipartFile getFile() {
        return file ;
    }

    public void setFile(MultipartFile file ) {
        this.file = file;
    }
}
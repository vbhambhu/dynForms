package kennedy.ox.ac.uk;

import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * Created by vinodkumar on 10/09/2016.
 */
public class TextValidation extends Validation  {
    int min_length = 0;
    int max_length = 0;
    int exact_length = 0;

    String matches = null;

    boolean valid_url  = false;
    boolean valid_email  = false;
    boolean valid_emails  = false;
    boolean valid_ip  = false;

}
package kennedy.ox.ac.uk;

/**
 * Created by vinodkumar on 10/09/2016.
 */
public class TextValidation extends TextAreaValidation {
    int min_length = 0;
    int max_length = 0;
    int exact_length = 0;

    String greater_than = null;
    String greater_than_equal_to = null;
    String less_than = null;
    String less_than_equal_to = null;

    String matches = null;


    String numeric  = null;
    String decimal  = null;
    String valid_url  = null;
    String valid_email  = null;
    String valid_emails  = null;
    String valid_ip  = null;

    String regex_match  = null;
    String in_list  = null;
    String alpha  = null;
    String alpha_numeric  = null;

}
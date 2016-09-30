package kennedy.ox.ac.uk.Models;

/**
 * Created by vinodkumar on 15/09/2016.
 */
public class FromValidation {

    String min_length = null;
    String max_length = null;
    String exact_length = null;

    String matches = null;

    String valid_url  = null;
    String valid_email  = null;
    String valid_emails  = null;
    String valid_ip  = null;

    String regex_match  = null;
    String in_list  = null;
    String alpha  = null;
    String alpha_numeric  = null;


    public String getMin_length() {
        return min_length;
    }

    public void setMin_length(String min_length) {
        this.min_length = min_length;
    }

    public String getMax_length() {
        return max_length;
    }

    public void setMax_length(String max_length) {
        this.max_length = max_length;
    }

    public String getExact_length() {
        return exact_length;
    }

    public void setExact_length(String exact_length) {
        this.exact_length = exact_length;
    }

    public String getMatches() {
        return matches;
    }

    public void setMatches(String matches) {
        this.matches = matches;
    }

    public String getValid_url() {
        return valid_url;
    }

    public void setValid_url(String valid_url) {
        this.valid_url = valid_url;
    }

    public String getValid_email() {
        return valid_email;
    }

    public void setValid_email(String valid_email) {
        this.valid_email = valid_email;
    }

    public String getValid_emails() {
        return valid_emails;
    }

    public void setValid_emails(String valid_emails) {
        this.valid_emails = valid_emails;
    }

    public String getValid_ip() {
        return valid_ip;
    }

    public void setValid_ip(String valid_ip) {
        this.valid_ip = valid_ip;
    }

    public String getRegex_match() {
        return regex_match;
    }

    public void setRegex_match(String regex_match) {
        this.regex_match = regex_match;
    }

    public String getIn_list() {
        return in_list;
    }

    public void setIn_list(String in_list) {
        this.in_list = in_list;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public String getAlpha_numeric() {
        return alpha_numeric;
    }

    public void setAlpha_numeric(String alpha_numeric) {
        this.alpha_numeric = alpha_numeric;
    }
}

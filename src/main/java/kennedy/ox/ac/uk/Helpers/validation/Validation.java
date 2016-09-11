package kennedy.ox.ac.uk.Helpers.validation;

import kennedy.ox.ac.uk.Models.Field;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vinodkumar on 10/09/2016.
 */
public class Validation {

    // TODO: further split when Spring Reflection used
    public class Keywords
    {
        public static final String minLength = "min_length";
        public static final String maxLength = "max_length";
        public static final String exactLength = "exact_length";
        public static final String greaterThan = "greater_than";
        public static final String lessThan = "less_than";
        public static final String greaterThanEqualTo = "greater_than_equal_to";
        public static final String lessThanEqualTo = "less_than_equal_to";
        public static final String matches = "matches";
        public static final String numeric = "numeric";
        public static final String decimal = "decimal";
        public static final String validUrl = "valid_url";
        public static final String validEmail = "valid_email";
        public static final String validIp = "valid_ip";

        public static final String regexMatch = "regex_match";
        public static final String inList = "in_list";
        public static final String alpha = "alpha";
        public static final String alphaNumeric = "alpha_numeric";
    }
    public void validate(Field field, String fieldValue, MultipartHttpServletRequest mrequest) {
        for (java.lang.reflect.Field reflectedField : this.getClass().getDeclaredFields()) {
            if (field.getHasError())
                continue;
            reflectedField.setAccessible(true); // You might want to set modifier to public first.
            try {
                Object value = reflectedField.get(this);
                if (value != null) {
                    System.out.println(reflectedField.getName() + "=" + value);
                }
                switch (reflectedField.getName()) {
                    case Keywords.minLength:
                        int minLength = (int) value;

                        if (minLength > 0 && fieldValue.length() < minLength) {
                            field.setHasErrorAndErrMsg("The %s field must be at least %d characters in length.", field.getName(), minLength);
                        }
                        break;
                    case Keywords.maxLength:
                        int maxLength = (int) value;
                        if (maxLength > 0 && fieldValue.length() > maxLength) {
                            field.setHasErrorAndErrMsg("The %s field cannot exceed %d characters in length.", field.getName(), maxLength);
                        }
                        break;
                    case Keywords.exactLength:
                        int exactLength = (int) value;
                        if (exactLength > 0 && fieldValue.length() != exactLength) {
                            field.setHasErrorAndErrMsg("The %s field must be exactly %d characters in length.", field.getName(), exactLength);
                        }
                        break;
                    case Keywords.greaterThan:
                        int greaterThan = (int) value;
                        if (greaterThan > 0 && (!isInt(fieldValue) || !(greaterThan < Integer.parseInt(fieldValue)))) {
                            field.setHasErrorAndErrMsg("The %s field must contain a number greater than %d.", field.getName(), greaterThan);
                        }
                        break;
                    case Keywords.lessThan:
                        int lessThan = (int) value;
                        if (lessThan > 0 && (!isInt(fieldValue) || !(lessThan > Integer.parseInt(fieldValue)))) {
                            field.setHasErrorAndErrMsg("The %s field must contain a number less than %d.", field.getName(), lessThan);
                        }
                        break;
                    case Keywords.greaterThanEqualTo:
                        int greaterThanEqualTo = (int) value;
                        if (greaterThanEqualTo > 0 && (!isInt(fieldValue) || !(greaterThanEqualTo <= Integer.parseInt(fieldValue)))) {
                            field.setHasErrorAndErrMsg("The %s field must contain a number greater than or equal to %d.", field.getName(), greaterThanEqualTo);
                        }
                        break;
                    case Keywords.lessThanEqualTo:
                        int lessThanEqualTo = (int) value;
                        if (lessThanEqualTo > 0 && (!isInt(fieldValue) || !(lessThanEqualTo >= Integer.parseInt(fieldValue)))) {
                            field.setHasErrorAndErrMsg("The %s field must contain a number less than or equal to %d.", field.getName(), lessThanEqualTo);
                        }
                        break;
                    case Keywords.matches:
                        String matchFieldValue = mrequest.getParameter(value.toString());
                        if (!fieldValue.trim().equals(matchFieldValue.trim())) {
                            field.setHasErrorAndErrMsg("The %s field does not match the %s field.", field.getName(), value.toString());
                        }
                        break;
                    case Keywords.numeric:
                        if ((Boolean)value && !isInt(fieldValue)) {
                            field.setHasErrorAndErrMsg("The %s field must contain an integer.", field.getName());
                        }
                        break;
                    case Keywords.decimal:
                        if ((Boolean)value && !isDecimal(fieldValue)) {
                            field.setHasErrorAndErrMsg("The %s field must contain a decimal number.", field.getName());
                        }
                        break;
                    case Keywords.validUrl:
                        if ((Boolean)value && !isValidUrl(fieldValue)) {
                            field.setHasErrorAndErrMsg("The %s field must contain a valid URL.", field.getName());
                        }
                        break;
                    case Keywords.validEmail:
                        if ((Boolean)value && !isValidEmail(fieldValue)) {
                            field.setHasErrorAndErrMsg("The %s field must contain a valid email address.", field.getName());
                        }
                        break;
                    case Keywords.validIp:
                        if ((Boolean)value && !isValidIp(fieldValue)) {
                            field.setHasErrorAndErrMsg("The %s field must contain a valid IP.", field.getName());
                        }
                        break;
                    case Keywords.regexMatch:
                        if(!isRegexMatch(value.toString(), fieldValue)){
                            field.setHasErrorAndErrMsg("The %s field is not in the correct format.", field.getName());
                        }
                        break;
                    case Keywords.inList:
                        if(!isInList(value.toString(), fieldValue)){
                            field.setHasErrorAndErrMsg("The %s field must be one of: %s.", field.getName(), value.toString());
                        }
                        break;
                    case Keywords.alpha:
                        if(((Boolean)value) && !fieldValue.matches("[a-zA-Z]+")){
                            field.setHasErrorAndErrMsg("The %s field may only contain alphabetical characters.", field.getName());
                        }
                        break;
                    case Keywords.alphaNumeric:
                        if(((Boolean)value) && !fieldValue.matches("[A-Za-z0-9]+")){
                            field.setHasErrorAndErrMsg("The %s field may only contain alpha-numeric characters.", field.getName());
                        }
                        break;
                    default :
                        break;
                }

            } catch (Exception ex) {
            }
        }
    }

    private Boolean isInList(String lists,String needle) {
        try {
            String[] haystack = lists.split(",");
            return Arrays.asList(haystack).contains(needle);
        } catch (RuntimeException e) {
            return false;
        }
    }


    private Boolean isRegexMatch(String regex,String text) {
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    private Boolean isValidUrl(String text) {
        try {
            String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    private Boolean isValidEmail(String text) {
        try {
            String regex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    private Boolean isValidIp(String text) {

        try {
            String regex = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }
    private Boolean isInt(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Boolean isDecimal(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
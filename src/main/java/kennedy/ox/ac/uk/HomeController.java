package kennedy.ox.ac.uk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kennedy.ox.ac.uk.FormRepository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vinod on 08/09/2016.
 */
@Controller
public class HomeController {

    @Autowired
    private FormRepository formRepository;
    

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String showHome() {

        return "home";

    }


    @RequestMapping(value="/capture/{id}", method= RequestMethod.GET)
    public String capture(Model model, @PathVariable String id) {

        Form form = formRepository.findById(id);
        model.addAttribute("form", form);
        return "capture";

    }

    public class ValidationKeywords
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
    }
    @RequestMapping(value="/capture/{id}", method= RequestMethod.POST)
    public String capturePost(Model model, @RequestParam("formId") String formId, MultipartHttpServletRequest mrequest, @PathVariable String id) {

        Form form = formRepository.findById(formId);
        //FormValidator fv = new FormValidator(form);
        model.addAttribute("form",  form);
        //model.addAttribute("fv",  fv);

        for (Field field : form.getFields()) {

            String fieldValue = mrequest.getParameter(field.getName());
            field.setValue(fieldValue);  // for redisplay on error

            if(field.getRequired() != null && field.getRequired()){

                // if required
                if(fieldValue == null || fieldValue.trim().isEmpty() ){
                    field.setHasError(true);
                    field.setErrMsg(String.format(" The %s field is required." , field.getName()));
                }

            } else {
                //System.out.println("NULL" + field.getReqired());
            }
            Validation validation = null;

            if(field.getType().equals("text")) {
               // System.out.println("getType(): " + field.getType());
                validation = field.<TextValidation>getValidations();
            }
            else if(field.getType().equals("textarea")) {
               // System.out.println("getType(): " + field.getType());
                //validation = field.<TextAreaValidation>getValidations();
            }
            else
                validation = field.getValidations();

            //System.out.println("field.getType(): " + field.getType());

            //System.out.println("validation" + validation);
            for (java.lang.reflect.Field reflectedField : validation.getClass().getDeclaredFields()) {
                if (field.getHasError())
                    continue;
                reflectedField.setAccessible(true); // You might want to set modifier to public first.
                try {
                    Object value = reflectedField.get(validation);
                    if (value != null) {
                        //System.out.println(field1.getName() + "=" + value);
                        //System.out.println(field1.getName() + "=" + Integer.parseInt(value.toString()));
                    }

                    switch(reflectedField.getName()) {
                        case ValidationKeywords.minLength:
                            int minLength = Integer.parseInt(value.toString());

                            if(minLength > 0 && fieldValue.length() < minLength) {
                                field.setHasErrorAndErrMsg("The %s field must be at least %d characters in length.", field.getName(), minLength);
                            }
                            break;
                        case ValidationKeywords.maxLength:
                            int maxLength = Integer.parseInt(value.toString());
                            if(maxLength > 0 && fieldValue.length() > maxLength) {
                                field.setHasErrorAndErrMsg("The %s field cannot exceed %d characters in length.", field.getName(), maxLength);
                            }
                            break;
                        case ValidationKeywords.exactLength:
                            int exactLength = Integer.parseInt(value.toString());
                            if(exactLength > 0 && fieldValue.length() != exactLength) {
                                field.setHasErrorAndErrMsg("The %s field must be exactly %d characters in length.", field.getName(), exactLength);
                            }
                            break;
                        case ValidationKeywords.greaterThan:
                            if(!isInt(fieldValue) || !(Integer.parseInt(value.toString()) < Integer.parseInt(fieldValue))  ){
                                field.setHasErrorAndErrMsg("The %s field must contain a number greater than %d.", field.getName(), Integer.parseInt(value.toString()));
                            }
                            break;
                        case ValidationKeywords.lessThan:
                            if(!isInt(fieldValue) || !(Integer.parseInt(value.toString()) > Integer.parseInt(fieldValue))  ){
                                field.setHasErrorAndErrMsg("The %s field must contain a number less than %d.", field.getName(), Integer.parseInt(value.toString()));
                            }
                            break;
                        case ValidationKeywords.greaterThanEqualTo:
                            if(!isInt(fieldValue) || !(Integer.parseInt(value.toString()) <= Integer.parseInt(fieldValue))  ){
                                field.setHasErrorAndErrMsg("The %s field must contain a number greater than or equal to %d.", field.getName(), Integer.parseInt(value.toString()));
                            }
                            break;
                        case ValidationKeywords.lessThanEqualTo:
                            if(!isInt(fieldValue) || !(Integer.parseInt(value.toString()) >= Integer.parseInt(fieldValue))  ){
                                field.setHasErrorAndErrMsg("The %s field must contain a number less than or equal to %d.", field.getName(), Integer.parseInt(value.toString()));
                            }
                            break;
                        case ValidationKeywords.matches:
                            String matchFieldValue = mrequest.getParameter(value.toString());
                            if(!fieldValue.trim().equals(matchFieldValue.trim())){
                                field.setHasErrorAndErrMsg("The %s field does not match the %s field.", field.getName(), value.toString());
                            }
                            break;
                        case ValidationKeywords.numeric:
                            if(Boolean.parseBoolean(value.toString()) && !isInt(fieldValue)){
                                field.setHasErrorAndErrMsg("The %s field must contain an integer.", field.getName());
                            }
                            break;
                        case ValidationKeywords.decimal:
                            if(Boolean.parseBoolean(value.toString()) && !isDecimal(fieldValue)){
                                field.setHasErrorAndErrMsg("The %s field must contain a decimal number.", field.getName());
                            }
                            break;
                        case ValidationKeywords.validUrl:
                            if(Boolean.parseBoolean(value.toString()) && !isValidUrl(fieldValue)){
                                field.setHasErrorAndErrMsg("The %s field must contain a valid URL.", field.getName());
                            }
                            break;
                        case ValidationKeywords.validEmail:
                            if(Boolean.parseBoolean(value.toString()) && !isValidEmail(fieldValue)){
                                field.setHasErrorAndErrMsg("The %s field must contain a valid email address.", field.getName());
                            }
                            break;
                        case ValidationKeywords.validIp:
                            System.out.println("reflectedField: " + reflectedField.getName());
                            if(Boolean.parseBoolean(value.toString()) && !isValidIp(fieldValue)){
                                field.setHasErrorAndErrMsg("The %s field must contain a valid IP.", field.getName());
                            }
                            break;
                        default :
                            break;

                    }




                } catch(Exception ex) {}
            }





            // now check other validations
//            for (Validation validation : field.getValidations()) {
//                if(validation == null)
//                    continue;
//                System.out.println(validation.getName());
//                switch(validation.getName()) {
//                    case "min_length":
//                    validation.getValue();
//                        break;
//                }
//            }


        }


       //



        //System.out.println(value1);
        return "capture";
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
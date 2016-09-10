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
            for (java.lang.reflect.Field field1 : validation.getClass().getDeclaredFields()) {
                //if (field.getHasError()) continue;
                field1.setAccessible(true); // You might want to set modifier to public first.
                try {
                    Object value = field1.get(validation);
                    if (value != null) {
                        //System.out.println(field1.getName() + "=" + value);
                        //System.out.println(field1.getName() + "=" + Integer.parseInt(value.toString()));
                    }

                    switch(field1.getName()) {
                        case "min_length":
                            int minLength = Integer.parseInt(value.toString());

                        if(minLength > 0 && fieldValue.length() < minLength) {
                            field.setHasError(true);
                            field.setErrMsg(String.format("The %s field must be at least %d characters in length.", field.getName(), minLength ));
                        }
                        break;
                        case "max_length":
                            int maxLength = Integer.parseInt(value.toString());
                            if(maxLength > 0 && fieldValue.length() > maxLength) {
                                field.setHasError(true);
                                field.setErrMsg(String.format("The %s field cannot exceed %d characters in length.", field.getName(), maxLength));
                            }
                            break;
                        case "exact_length":
                            int exactLength = Integer.parseInt(value.toString());
                            if(exactLength > 0 && fieldValue.length() != exactLength) {
                                field.setHasError(true);
                                field.setErrMsg(String.format("The %s field must be exactly %d characters in length.", field.getName(), exactLength));
                            }
                            break;
                        case "greater_than":
                            if(!isInt(fieldValue) || !(Integer.parseInt(value.toString()) < Integer.parseInt(fieldValue))  ){
                                field.setHasError(true);
                                field.setErrMsg(String.format("The %s field must contain a number greater than %d.", field.getName(), Integer.parseInt(value.toString()) ));
                            }
                            break;
                        case "less_than":
                            if(!isInt(fieldValue) || !(Integer.parseInt(value.toString()) > Integer.parseInt(fieldValue))  ){
                                field.setHasError(true);
                                field.setErrMsg(String.format("The %s field must contain a number less than %d.", field.getName(), Integer.parseInt(value.toString()) ));
                            }
                            break;
                        case "greater_than_equal_to":
                            if(!isInt(fieldValue) || !(Integer.parseInt(value.toString()) <= Integer.parseInt(fieldValue))  ){
                                field.setHasError(true);
                                field.setErrMsg(String.format("The %s field must contain a number greater than or equal to %d.", field.getName(), Integer.parseInt(value.toString()) ));
                            }
                            break;
                        case "less_than_equal_to":
                            if(!isInt(fieldValue) || !(Integer.parseInt(value.toString()) >= Integer.parseInt(fieldValue))  ){
                                field.setHasError(true);
                                field.setErrMsg(String.format("The %s field must contain a number less than or equal to %d.", field.getName(), Integer.parseInt(value.toString()) ));
                            }
                            break;
                        case "matches":
                            String matchFieldValue = mrequest.getParameter(value.toString());
                            if(!fieldValue.trim().equals(matchFieldValue.trim())){
                                field.setHasError(true);
                                field.setErrMsg(String.format("The %s field does not match the %s field.", field.getName(), value.toString() ));
                            }
                            break;
                        case "numeric":
                            if(!isInt(fieldValue)){
                                field.setHasError(true);
                                field.setErrMsg(String.format("The %s field must contain an integer.", field.getName() ));
                            }
                            break;
                        case "decimal":
                            if(!isDecimal(fieldValue)){
                                field.setHasError(true);
                                field.setErrMsg(String.format("The %s field must contain a decimal number.", field.getName() ));
                            }
                            break;
                        case "valid_url":
                            if(!isValidUrl(fieldValue)){
                                field.setHasError(true);
                                field.setErrMsg(String.format("The %s field must contain a valid URL.", field.getName() ));
                            }
                            break;
                        case "valid_email":
                            if(!isValidEmail(fieldValue)){
                                field.setHasError(true);
                                field.setErrMsg(String.format("The %s field must contain a valid email address.", field.getName() ));
                            }
                            break;
                        case "valid_ip":
                            if(!isValidIp(fieldValue)){
                                field.setHasError(true);
                                field.setErrMsg(String.format("The %s field must contain a valid IP.", field.getName() ));
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
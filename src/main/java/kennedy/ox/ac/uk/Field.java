package kennedy.ox.ac.uk;


import org.springframework.data.annotation.Id;

import java.util.*;

public class Field {


    private int fieldId;
    private String type;
    private String name;
    private String label;
    private Boolean isReqired;
    private String value;

    private Boolean hasError;
    private String errMsg;

    private List<Option> options = new ArrayList<>();

    private List<Object> validation = new ArrayList<>();



    public Field() {}


    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public Boolean getReqired() {
        return isReqired;
    }

    public void setReqired(Boolean reqired) {
        isReqired = reqired;
    }

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Object> getValidation() {
        return validation;
    }

    public void setValidation(List<Object> validation) {
        this.validation = validation;
    }
}

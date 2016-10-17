package kennedy.ox.ac.uk.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jim on 06/10/2016.
 */
public class Page {

    private  List<Field> fields = new ArrayList<>();
    private int number;
    private boolean enabled = true;

    public Page(int number) {
        this.number = number;
    }
    public Page(int number, Field field) {
        this.number = number;
        this.add(field);
    }
    public Page(int number, List<Field> fields) {
        this.number = number;
        this.addAll(fields);
    }
    public void add(Field field) {
        this.fields.add(field);
    }
    public void addAll(List<Field> fields) {
        fields.addAll(fields);
    }
    public List<Field> getFields() {
        return fields;
    }
    public int getNumber() {
        return number;
    }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

}

package kennedy.ox.ac.uk.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jim on 06/10/2016.
 */
public class Page {
    List<Field> fields = new ArrayList<>();
    int number;

    public Page(int number) {
        this.number = number;
    }
    public void add(Field field) {
        fields.add(field);
    }
    public List<Field> getFields() {
        return fields;
    }
    public int getNumber() {
        return number;
    }
}

package kennedy.ox.ac.uk.Models;

import org.springframework.data.annotation.Id;

/**
 * Created by vinod on 09/09/2016.
 */
public class Option {

    @Id
    private int id;

    private String label;
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

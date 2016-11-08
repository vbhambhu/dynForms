package kennedy.ox.ac.uk.Models;


import java.util.ArrayList;
import java.util.List;

public class DataTableOutput {

    private int draw;
    private long recordsTotal;
    private long recordsFiltered;
    private String error;
    private Boolean status = true;

    private List<?> data = new ArrayList<>();

    public DataTableOutput(){}


    public DataTableOutput(int draw, long recordsTotal, long recordsFiltered, List<?> data){
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.data = data;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
        this.status = false;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}

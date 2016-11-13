package kennedy.ox.ac.uk.Models;


public class RestInput {

    private int start;
    private int length;
    private int sortcol;
    private String dir;
    private String scol;

    public RestInput(){
        this.length = 10;
        this.dir = "asc";
    }


    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSortcol() {
        return sortcol;
    }

    public void setSortcol(int sortcol) {
        this.sortcol = sortcol;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getScol() {
        return scol;
    }

    public void setScol(String scol) {
        this.scol = scol;
    }


}

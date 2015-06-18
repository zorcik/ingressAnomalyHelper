package weblogic.pl.ingressanomalyhelper;

/**
 * Created by jacek on 2015-06-18.
 */
public class Portal {
    int code = 0;
    String name = null;
    boolean selected = false;

    public Portal(int code, String name, boolean selected) {
        super();
        this.code = code;
        this.name = name;
        this.selected = selected;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

package vernon.Data;

import java.io.Serializable;

public class Message implements Serializable {
    private Integer type;
    private Object object;

    public Message(Integer type, Object object){
        this.type = type;
        this.object = object;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}

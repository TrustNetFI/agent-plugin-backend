package fi.trustnet.browserextension;

import java.util.LinkedHashMap;


public class Command {
    private String operation;
    private Object params;

    public Command() {
        this.params = new LinkedHashMap<>();
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }
}

package fi.trustnet.browserextension;

import java.util.LinkedList;
import java.util.List;

public class DidList {
    private List<DidParams> dids;

    public DidList() {
        this.dids = new LinkedList<>();
    }

    public List<DidParams> getDids() {
        return dids;
    }

    public void setDids(List<DidParams> dids) {
        this.dids = dids;
    }
}

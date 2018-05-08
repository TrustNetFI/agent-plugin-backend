package fi.trustnet.browserextension;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DidParams {
    private String did;
    private String verkey;
    private String url;
    private Object didMetadata;

    public DidParams() {
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getVerkey() {
        return verkey;
    }

    public void setVerkey(String verkey) {
        this.verkey = verkey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getDidMetadata() {
        return didMetadata;
    }

    public void setDidMetadata(Object didMetadata) {
        this.didMetadata = didMetadata;
    }
}


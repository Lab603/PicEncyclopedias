package com.lab603.picencyclopedias.dao;

/**
 * Created by FutureApe on 2017/9/30.
 */

public class CollectionData {

    public String uri;
    public String result;
    public Boolean save;


    public String getUri() {
        return uri;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Boolean getSave() {
        return save;
    }

    public void setSave(Boolean save) {
        this.save = save;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
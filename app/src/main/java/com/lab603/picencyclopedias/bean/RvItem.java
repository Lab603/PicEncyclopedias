package com.lab603.picencyclopedias.bean;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by FutureApe on 2017/10/5.
 */

public class RvItem implements Serializable {
    private Bitmap bitmap;
    private String result_str;
    private Boolean save;
    private String uri;

    public RvItem(){}
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Boolean getSave() {
        return save;
    }

    public void setSave(Boolean save) {
        this.save = save;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getResult_str() {
        return result_str;
    }

    public void setResult_str(String result_str) {
        this.result_str = result_str;
    }
}

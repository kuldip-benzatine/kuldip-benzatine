package com.benzatine.lovestickerimages.Data;

import android.graphics.Bitmap;

/**
 * Created by ADMIN on 03-Apr-17.
 */

public class DataListView {

    private Bitmap image;

    public DataListView(Bitmap image) {
        super();
        this.image = image;
    }


    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


}

package com.regionaldeals.de.entities;

import java.io.Serializable;

/**
 * Created by Umi on 24.09.2017.
 */

public class CategoryObject implements Serializable {

    private String catName;
    private String catShortName;
    private String catLanguage;

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatShortName() {
        return catShortName;
    }

    public void setCatShortName(String catShortName) {
        this.catShortName = catShortName;
    }
}

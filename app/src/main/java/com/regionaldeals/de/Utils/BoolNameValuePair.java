package com.regionaldeals.de.Utils;

import org.apache.http.NameValuePair;

/**
 * Created by Umi on 11.10.2017.
 */

public class BoolNameValuePair implements NameValuePair {

    String name;

    Boolean value;

    public BoolNameValuePair(String name, Boolean value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return Boolean.toString(value);
    }

}
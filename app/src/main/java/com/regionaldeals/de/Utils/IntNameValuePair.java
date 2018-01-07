package com.regionaldeals.de.Utils;

import org.apache.http.NameValuePair;

/**
 * Created by Umi on 08.10.2017.
 */

public class IntNameValuePair implements NameValuePair {

    String name;

    int value;

    public IntNameValuePair(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return Integer.toString(value);
    }

}
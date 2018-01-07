package com.regionaldeals.de.Utils;

import org.apache.http.NameValuePair;

/**
 * Created by Umi on 08.10.2017.
 */

public class DoubleNameValuePair implements NameValuePair {

    String name;

    double value;

    public DoubleNameValuePair(String name, double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return Double.toString(value);
    }

}
package com.regionaldeals.de.entities;

import java.io.Serializable;

/**
 * Created by Umi on 23.01.2018.
 */

public class CitiesObject implements Serializable {
    private int id;
    private String cityName;
    private String countryCode;
    private Long cityLat;
    private Long cityLong;

    public CitiesObject(){}

    public CitiesObject(int id, String cityName, String countryCode, Long cityLat, Long cityLong) {
        this.id = id;
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.cityLat = cityLat;
        this.cityLong = cityLong;
    }

    public Long getCityLat() {
        return cityLat;
    }

    public void setCityLat(Long cityLat) {
        this.cityLat = cityLat;
    }

    public Long getCityLong() {
        return cityLong;
    }

    public void setCityLong(Long cityLong) {
        this.cityLong = cityLong;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}

package com.regionaldeals.de.location;

public class data {
    private String cityName;

    private String stateAbbrv;

    private String cityLat;

    private String stateName;

    private String cityLong;

    private String countryCode;

    private String regionName;

    private String active;

    private String postCode;

    private String id;

    public String getCityName ()
    {
        return cityName;
    }

    public void setCityName (String cityName)
    {
        this.cityName = cityName;
    }

    public String getStateAbbrv ()
    {
        return stateAbbrv;
    }

    public void setStateAbbrv (String stateAbbrv)
    {
        this.stateAbbrv = stateAbbrv;
    }

    public String getCityLat ()
    {
        return cityLat;
    }

    public void setCityLat (String cityLat)
    {
        this.cityLat = cityLat;
    }

    public String getStateName ()
    {
        return stateName;
    }

    public void setStateName (String stateName)
    {
        this.stateName = stateName;
    }

    public String getCityLong ()
    {
        return cityLong;
    }

    public void setCityLong (String cityLong)
    {
        this.cityLong = cityLong;
    }

    public String getCountryCode ()
    {
        return countryCode;
    }

    public void setCountryCode (String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getRegionName ()
    {
        return regionName;
    }

    public void setRegionName (String regionName)
    {
        this.regionName = regionName;
    }

    public String getActive ()
    {
        return active;
    }

    public void setActive (String active)
    {
        this.active = active;
    }

    public String getPostCode ()
    {
        return postCode;
    }

    public void setPostCode (String postCode)
    {
        this.postCode = postCode;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [cityName = "+cityName+", stateAbbrv = "+stateAbbrv+", cityLat = "+cityLat+", stateName = "+stateName+", cityLong = "+cityLong+", countryCode = "+countryCode+", regionName = "+regionName+", active = "+active+", postCode = "+postCode+", id = "+id+"]";
    }
}
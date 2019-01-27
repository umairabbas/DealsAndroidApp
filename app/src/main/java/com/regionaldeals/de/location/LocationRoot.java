package com.regionaldeals.de.location;

import java.util.ArrayList;

public class LocationRoot
{
    private ArrayList<data> data;

    private String message;

    private String status;

    public ArrayList<data> getData ()
    {
        return data;
    }

    public void setData (ArrayList<data> data)
    {
        this.data = data;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+", message = "+message+", status = "+status+"]";
    }
}
package com.dealspok.dealspok.entities;

import android.location.Location;
import android.net.Uri;
import java.util.Date;

public class DealsObject {

    private int catId;
    private int dealId;
    private Uri coverUrl;
    private String title;
    private String author;
    private String description;

    public DealsObject(int dealId, Uri coverUrl, String title, String description, String contact, Location location, Date createdDate, Date expireDate) {
        this.dealId = dealId;
        this.coverUrl = coverUrl;
        this.title = title;
        this.description = description;
        this.contact = contact;
        this.location = location;
        this.createdDate = createdDate;
        this.expireDate = expireDate;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public int getDealId() {
        return dealId;
    }

    public void setDealId(int dealId) {
        this.dealId = dealId;
    }

    public Uri getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(Uri coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    private String url;
    private String contact;
    private String address;
    private Location location;
    private Date createdDate;
    private Date expireDate;


}

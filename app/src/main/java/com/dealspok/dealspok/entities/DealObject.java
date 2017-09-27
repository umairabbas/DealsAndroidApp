package com.dealspok.dealspok.entities;

/**
 * Created by Umi on 25.09.2017.
 */

public class DealObject {

    private int dealId;
    private String dealTitle;
    private String dealImageUrl;
    private String dealDescription;
    private long dateCreated;

    public DealObject(int dealId, String dealTitle, String dealImageUrl, String dealDescription, long dateCreated, long datePublished, long dateExpire) {
        this.dealId = dealId;
        this.dealTitle = dealTitle;
        this.dealImageUrl = dealImageUrl;
        this.dealDescription = dealDescription;
        this.dateCreated = dateCreated;
        this.datePublished = datePublished;
        this.dateExpire = dateExpire;
    }

    private long datePublished;
    private long dateExpire;

    public int getDealId() {
        return dealId;
    }

    public void setDealId(int dealId) {
        this.dealId = dealId;
    }

    public String getDealTitle() {
        return dealTitle;
    }

    public void setDealTitle(String dealTitle) {
        this.dealTitle = dealTitle;
    }

    public String getDealImageUrl() {
        return dealImageUrl;
    }

    public void setDealImageUrl(String dealImageUrl) {
        this.dealImageUrl = dealImageUrl;
    }

    public String getDealDescription() {
        return dealDescription;
    }

    public void setDealDescription(String dealDescription) {
        this.dealDescription = dealDescription;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(long datePublished) {
        this.datePublished = datePublished;
    }

    public long getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(long dateExpire) {
        this.dateExpire = dateExpire;
    }
}

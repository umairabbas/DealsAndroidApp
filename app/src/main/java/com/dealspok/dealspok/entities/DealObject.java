package com.dealspok.dealspok.entities;

import android.content.Context;

import com.dealspok.dealspok.R;

/**
 * Created by Umi on 25.09.2017.
 */

public class DealObject {

    private int dealId;
    private String dealTitle;
    private String dealImageUrl;
    private String dealDescription;
    private long dateCreated;
    private long datePublished;
    private long dateExpire;
    private String timezone;
    private double originalPrice;
    private double dealPrice;
    private String currency;
    private Shop shop;
    private Boolean favourite;
    private String dealType;

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public DealObject(int dealId, String dealTitle, String dealImageUrl, String dealDescription, long dateCreated, long datePublished, long dateExpire, String timezone, long originalPrice, long dealPrice, String currency, Shop shop) {
        this.dealId = dealId;
        this.dealTitle = dealTitle;
        this.dealImageUrl = dealImageUrl;
        this.dealDescription = dealDescription;
        this.dateCreated = dateCreated;
        this.datePublished = datePublished;
        this.dateExpire = dateExpire;
        this.timezone = timezone;
        this.originalPrice = originalPrice;
        this.dealPrice = dealPrice;
        this.currency = currency;
        this.setShop(shop);
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(double dealPrice) {
        this.dealPrice = dealPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

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

    public String getDealImageUrl(Context c) {
        return c.getString(R.string.apiUrl) + dealImageUrl + dealId + "&" + "dealtype=" + getDealType();
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

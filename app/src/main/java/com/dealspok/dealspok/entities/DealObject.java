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
    private long originalPrice;
    private long dealPrice;
    private String currency;
    private Shop shop;

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
        this.setShopObj(shop);
    }

    public Shop getShopObj() {
        return shop;
    }

    public void setShopObj(Shop shopObj) {
        shop = shopObj;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public long getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(long originalPrice) {
        this.originalPrice = originalPrice;
    }

    public long getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(long dealPrice) {
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
        return c.getString(R.string.apiUrl) + dealImageUrl + dealId;
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

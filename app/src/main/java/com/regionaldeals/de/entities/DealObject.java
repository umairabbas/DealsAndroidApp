package com.regionaldeals.de.entities;

import android.content.Context;

import com.regionaldeals.de.R;

import java.io.Serializable;

/**
 * Created by Umi on 25.09.2017.
 */

public class DealObject implements Serializable {

    private int dealId;
    private String dealTitle;
    private String dealDescription;
    private long createDate;
    private long publishDate;
    private long expiryDate;
    private String timezone;
    private double originalPrice;
    private double dealPrice;
    private String currency;
    private Shop shop;
    private Boolean favourite;
    private String dealType;
    private int dealImageCount;
    private String dealUrl;

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDealUrl() {
        return dealUrl;
    }

    public void setDealUrl(String dealUrl) {
        this.dealUrl = dealUrl;
    }

    public int getDealImageCount() {
        return dealImageCount;
    }

    public void setDealImageCount(int dealImageCount) {
        this.dealImageCount = dealImageCount;
    }

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

    public DealObject(int dealId, String dealTitle, String dealDescription, long dateCreated, long datePublished, long dateExpire, String timezone, long originalPrice, long dealPrice, String currency, Shop shop) {
        this.dealId = dealId;
        this.dealTitle = dealTitle;
        this.dealDescription = dealDescription;
        this.createDate = dateCreated;
        this.publishDate = datePublished;
        this.expiryDate = dateExpire;
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
        return c.getString(R.string.apiUrl) + "/mobile/api/deals/dealimage?dealid=" + dealId + "&" + "dealtype=" + getDealType();
    }

    public String getDealDescription() {
        return dealDescription;
    }

    public void setDealDescription(String dealDescription) {
        this.dealDescription = dealDescription;
    }

    public long getDateCreated() {
        return createDate;
    }

    public void setDateCreated(long dateCreated) {
        this.createDate = dateCreated;
    }

    public long getDatePublished() {
        return publishDate;
    }

    public void setDatePublished(long datePublished) {
        this.publishDate = datePublished;
    }

    public long getDateExpire() {
        return expiryDate;
    }

    public void setDateExpire(long dateExpire) {
        this.expiryDate = dateExpire;
    }
}

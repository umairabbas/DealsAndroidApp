package com.dealspok.dealspok.entities;

/**
 * Created by Umi on 29.09.2017.
 */

public class Shop {
    private int shopId;
    private String shopName;
    private String shopAddress;
    private String shopCountry;
    private String shopDetails;
    private String taxNumber;
    private String shopLocationLat;
    private String shopLocationLong;
    private String shopContact;
    private Boolean isActive;


    public Shop(int shopId, String shopName, String shopAddress, String shopCountry, String shopDetails, String taxNumber, String shopLocationLat, String shopLocationLong, String shopContact, Boolean isActive) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopCountry = shopCountry;
        this.shopDetails = shopDetails;
        this.taxNumber = taxNumber;
        this.shopLocationLat = shopLocationLat;
        this.shopLocationLong = shopLocationLong;
        this.shopContact = shopContact;
        this.isActive = isActive;
    }

    public String getShopCountry() {
        return shopCountry;
    }

    public void setShopCountry(String shopCountry) {
        this.shopCountry = shopCountry;
    }

    public String getShopDetails() {
        return shopDetails;
    }

    public void setShopDetails(String shopDetails) {
        this.shopDetails = shopDetails;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getShopLocationLat() {
        return shopLocationLat;
    }

    public void setShopLocationLat(String shopLocationLat) {
        this.shopLocationLat = shopLocationLat;
    }

    public String getShopLocationLong() {
        return shopLocationLong;
    }

    public void setShopLocationLong(String shopLocationLong) {
        this.shopLocationLong = shopLocationLong;
    }

    public int getShopId() {

        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopContact() {
        return shopContact;
    }

    public void setShopContact(String shopContact) {
        this.shopContact = shopContact;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}

package com.dealspok.dealspok.entities;

/**
 * Created by Umi on 29.09.2017.
 */

public class Shop {
    private int shopId;
    private String shopName;
    private String shopAddress;
    private String shopLocation;
    private String shopContact;
    private Boolean isActive;

    public Shop(int shopId, String shopName, String shopAddress, String shopLocation, String shopContact, Boolean isActive) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopLocation = shopLocation;
        this.shopContact = shopContact;
        this.isActive = isActive;
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

    public String getShopLocation() {
        return shopLocation;
    }

    public void setShopLocation(String shopLocation) {
        this.shopLocation = shopLocation;
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

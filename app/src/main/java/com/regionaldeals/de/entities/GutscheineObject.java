package com.regionaldeals.de.entities;

import android.content.Context;

import com.regionaldeals.de.R;
import java.io.Serializable;

public class GutscheineObject implements Serializable {

    private int gutscheinId;
    private Shop shop;
    private long createDate;
    private long publishDate;
    private long expiryDate;
    private int timezone;
    private String gutscheinTitle;
    private String gutscheinImageUrl;
    private String gutscheinDescription;
    private long gutscheinPrice;
    private String currency;
    private boolean gutscheinAvailed;
    private boolean gutscheinWin;
    private CategoryObject category;
    private int gutscheinImageCount;

    public int getGutscheinImageCount() {
        return gutscheinImageCount;
    }

    public void setGutscheinImageCount(int gutscheinImageCount) {
        this.gutscheinImageCount = gutscheinImageCount;
    }

    public GutscheineObject(int gutscheinId, Shop shop, long createDate, long publishDate, long expiryDate, int timezone, String gutscheinTitle, String gutscheinImageUrl, String gutscheinDescription, long gutscheinPrice, String currency, boolean gutscheinAvailed, boolean gutscheinWin, CategoryObject category) {
        this.gutscheinId = gutscheinId;
        this.shop = shop;
        this.createDate = createDate;
        this.publishDate = publishDate;
        this.expiryDate = expiryDate;
        this.timezone = timezone;
        this.gutscheinTitle = gutscheinTitle;
        this.gutscheinImageUrl = gutscheinImageUrl;
        this.gutscheinDescription = gutscheinDescription;
        this.gutscheinPrice = gutscheinPrice;
        this.currency = currency;
        this.gutscheinAvailed = gutscheinAvailed;
        this.gutscheinWin = gutscheinWin;
        this.category = category;
    }

    public int getGutscheinId() {
        return gutscheinId;
    }

    public void setGutscheinId(int gutscheinId) {
        this.gutscheinId = gutscheinId;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

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

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public String getGutscheinTitle() {
        return gutscheinTitle;
    }

    public void setGutscheinTitle(String gutscheinTitle) {
        this.gutscheinTitle = gutscheinTitle;
    }

    public String getGutscheinImageUrl(Context c) {
        return c.getString(R.string.apiUrl) + "/mobile/api/gutschein/gutscheinimage?gutscheinid=" + gutscheinId;
    }

    public void setGutscheinImageUrl(String gutscheinImageUrl) {
        this.gutscheinImageUrl = gutscheinImageUrl;
    }

    public String getGutscheinDescription() {
        return gutscheinDescription;
    }

    public void setGutscheinDescription(String gutscheinDescription) {
        this.gutscheinDescription = gutscheinDescription;
    }

    public long getGutscheinPrice() {
        return gutscheinPrice;
    }

    public void setGutscheinPrice(long gutscheinPrice) {
        this.gutscheinPrice = gutscheinPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isGutscheinAvailed() {
        return gutscheinAvailed;
    }

    public void setGutscheinAvailed(boolean gutscheinAvailed) {
        this.gutscheinAvailed = gutscheinAvailed;
    }

    public boolean isGutscheinWin() {
        return gutscheinWin;
    }

    public void setGutscheinWin(boolean gutscheinWin) {
        this.gutscheinWin = gutscheinWin;
    }

    public CategoryObject getCategory() {
        return category;
    }

    public void setCategory(CategoryObject category) {
        this.category = category;
    }
}

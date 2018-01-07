package com.regionaldeals.de.entities;

import java.io.Serializable;

/**
 * Created by Umi on 03.01.2018.
 */

public class Plans implements Serializable {
    private Integer id;

    private String planName;

    private String planShortName;

    private String planDescription;

    private Double planPrice;

    private Integer billingCycle;

    private Integer numberBillingCycles;

    private String currency;

    private String planOffer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanShortName() {
        return planShortName;
    }

    public void setPlanShortName(String planShortName) {
        this.planShortName = planShortName;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public void setPlanDescription(String planDescription) {
        this.planDescription = planDescription;
    }

    public Double getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(Double planPrice) {
        this.planPrice = planPrice;
    }

    public Integer getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(Integer billingCycle) {
        this.billingCycle = billingCycle;
    }

    public Integer getNumberBillingCycles() {
        return numberBillingCycles;
    }

    public void setNumberBillingCycles(Integer numberBillingCycles) {
        this.numberBillingCycles = numberBillingCycles;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPlanOffer() {
        return planOffer;
    }

    public void setPlanOffer(String planOffer) {
        this.planOffer = planOffer;
    }
}

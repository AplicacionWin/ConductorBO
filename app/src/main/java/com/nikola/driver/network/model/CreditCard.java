package com.nikola.driver.network.model;

import androidx.annotation.Nullable;

public class CreditCard {
    private String id;
    private String cardLastFour;
    private String cardType;
    private boolean isDefault;

    public CreditCard(String id, String cardLastFour, String cardType, boolean isDefault) {
        this.id = id;
        this.cardLastFour = cardLastFour;
        this.cardType = cardType;
        this.isDefault = isDefault;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }

    public void setCardLastFour(String cardLastFour) {
        this.cardLastFour = cardLastFour;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        CreditCard temp = (CreditCard) obj;
        return temp.getId().equals(this.id);
    }
}

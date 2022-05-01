package com.nikola.driver.network.model;

/**
 * Created by ${Asher} on 11/14/2017.
 */

public class WalletPayments {
    private String uniqueId;
    private String description;
    private String wallet_amount_symbol;
    private String title;
    private String amount;
    private String wallet_image;
    private String redeemId;
    private boolean cancelButtonStatus;

    public boolean getCancelButtonStatus() {
        return cancelButtonStatus;
    }

    public void setCancelButtonStatus(boolean cancelButtonStatus) {
        this.cancelButtonStatus = cancelButtonStatus;
    }

    public String getRedeemId() {
        return redeemId;
    }

    public void setRedeemId(String redeemId) {
        this.redeemId = redeemId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWallet_amount_symbol() {
        return wallet_amount_symbol;
    }

    public void setWallet_amount_symbol(String wallet_amount_symbol) {
        this.wallet_amount_symbol = wallet_amount_symbol;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWallet_image() {
        return wallet_image;
    }

    public void setWallet_image(String wallet_image) {
        this.wallet_image = wallet_image;
    }
}

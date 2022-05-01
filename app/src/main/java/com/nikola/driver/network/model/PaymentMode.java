package com.nikola.driver.network.model;

public class PaymentMode {
    private long id;
    private String name;
    private String image;
    private boolean isDefault;

    @Override
    public String toString() {
        return "PaymentMode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }

    public PaymentMode(long id, String name, String image, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.isDefault = isDefault;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}

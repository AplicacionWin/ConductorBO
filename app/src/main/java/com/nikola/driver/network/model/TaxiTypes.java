package com.nikola.driver.network.model;

/**
 * Created by user on 1/9/2017.
 */

public class TaxiTypes {
    private String id;
    private String taxitype;
    private String taxiimage;
    private String taxi_cost;

    public String getTaxitype() {
        return taxitype;
    }

    public void setTaxitype(String taxitype) {
        this.taxitype = taxitype;
    }

    public String getTaxiimage() {
        return taxiimage;
    }

    public void setTaxiimage(String taxiimage) {
        this.taxiimage = taxiimage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaxi_cost() {
        return taxi_cost;
    }

    public void setTaxi_cost(String taxi_cost) {
        this.taxi_cost = taxi_cost;
    }
}

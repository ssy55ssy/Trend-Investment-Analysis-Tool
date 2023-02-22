package com.felix.trend.pojo;

import java.util.Date;

public class Trade {

    private float buyPoint;
    private float sellPoint;
    private String buyDate;
    private String sellDate;
    private float rate;

    public String getBuyDate() {
        return buyDate;
    }

    public String getSellDate() {
        return sellDate;
    }

    public float getBuyPoint() {
        return buyPoint;
    }

    public float getRate() {
        return rate;
    }

    public float getSellPoint() {
        return sellPoint;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public void setBuyPoint(float buyPoint) {
        this.buyPoint = buyPoint;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public void setSellDate(String sellDate) {
        this.sellDate = sellDate;
    }

    public void setSellPoint(float sellPoint) {
        this.sellPoint = sellPoint;
    }
}

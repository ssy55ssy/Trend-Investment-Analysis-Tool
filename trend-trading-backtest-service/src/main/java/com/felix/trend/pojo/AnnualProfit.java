package com.felix.trend.pojo;

public class AnnualProfit {

    private int year;
    private float indexProfit;
    private float trendProfit;

    public float getIndexProfit() {
        return indexProfit;
    }

    public float getTrendProfit() {
        return trendProfit;
    }

    public int getYear() {
        return year;
    }

    public void setIndexProfit(float indexProfit) {
        this.indexProfit = indexProfit;
    }

    public void setTrendProfit(float trendProfit) {
        this.trendProfit = trendProfit;
    }

    public void setYear(int year) {
        this.year = year;
    }
}

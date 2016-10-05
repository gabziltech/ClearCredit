package com.gabzil.retail_shop;

/**
 * Created by Yogesh on 06-Aug-16.
 */
public class OutStandingEntity {

    int ShopID;
    Double TotalOut,Extraout,DailyOut,WeekOut,DoubleWeekOut,MonthOut;

    // Empty constructor
    public OutStandingEntity(){

    }

    // constructor
    public OutStandingEntity(int ShopID, Double TotalOut, Double Extraout, Double DailyOut, Double WeekOut, Double DoubleWeekOut, Double MonthOut){
        this.ShopID = ShopID;
        this.TotalOut = TotalOut;
        this.Extraout = Extraout;
        this.DailyOut = DailyOut;
        this.WeekOut = WeekOut;
        this.DoubleWeekOut = DoubleWeekOut;
        this.MonthOut = MonthOut;
    }

    public int getShopID() {
        return ShopID;
    }

    public void setShopID(int shopID) {
        ShopID = shopID;
    }

    public Double getTotalOut() {
        return TotalOut;
    }

    public void setTotalOut(Double totalOut) {
        TotalOut = totalOut;
    }

    public Double getExtraout() {
        return Extraout;
    }

    public void setExtraout(Double extraout) {
        Extraout = extraout;
    }

    public Double getDailyOut() {
        return DailyOut;
    }

    public void setDailyOut(Double dailyOut) {
        DailyOut = dailyOut;
    }

    public Double getWeekOut() {
        return WeekOut;
    }

    public void setWeekOut(Double weekOut) {
        WeekOut = weekOut;
    }

    public Double getDoubleWeekOut() {
        return DoubleWeekOut;
    }

    public void setDoubleWeekOut(Double doubleWeekOut) {
        DoubleWeekOut = doubleWeekOut;
    }

    public Double getMonthOut() {
        return MonthOut;
    }

    public void setMonthOut(Double monthOut) {
        MonthOut = monthOut;
    }
}

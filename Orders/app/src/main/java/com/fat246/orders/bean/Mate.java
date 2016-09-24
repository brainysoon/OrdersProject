package com.fat246.orders.bean;

/**
 * Created by ken on 16-9-24.
 */
public class Mate {

    private String MateCode;
    private String MateName;
    private String ApplyNum;
    private String OrderNum;

    public Mate(String MateCode, String MateName, String ApplyNum,
                String OrderNum) {

        this.MateCode = MateCode;
        this.MateName = MateName;
        this.ApplyNum = ApplyNum;
        this.OrderNum = OrderNum;
    }

    //get
    public String getMateCode() {
        return this.MateCode;
    }

    public String getMateName() {
        return this.MateName;
    }

    public String getApplyNum() {
        return this.ApplyNum;
    }

    public String getOrderNum() {
        return this.OrderNum;
    }
}

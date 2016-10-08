package com.fat246.orders.bean;

/**
 * Created by ken on 16-10-8.
 */
public class OrderMate {

    private String MateCode;
    private String MateName;
    private String PrhsodAmnt;
    private String PrhsodAccein;
    private String PrhsodBillin;
    private String PrhsodAmntRtn;

    public OrderMate(String mateCode, String mateName, String prhsodAmnt, String prhsodAccein,
                     String prhsodBillin, String prhsodAmntRtn) {

        this.MateCode = mateCode;
        this.MateName = mateName;
        this.PrhsodAmnt = prhsodAmnt;
        this.PrhsodAccein = prhsodAccein;
        this.PrhsodBillin = prhsodBillin;
        this.PrhsodAmntRtn = prhsodAmntRtn;
    }

    //get
    public String getMateCode() {
        return this.MateCode;
    }

    public String getMateName() {
        return this.MateName;
    }

    public String getPrhsodAmnt() {
        return this.PrhsodAmnt;
    }

    public String getPrhsodAccein() {
        return this.PrhsodAccein;
    }

    public String getPrhsodBillin() {
        return this.PrhsodBillin;
    }

    public String getPrhsodAmntRtn() {
        return this.PrhsodAmntRtn;
    }
}

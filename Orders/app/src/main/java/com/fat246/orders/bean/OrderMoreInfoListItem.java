package com.fat246.orders.bean;

/**
 * Created by Administrator on 2016/3/28.
 */
public class OrderMoreInfoListItem {

    //详细信息
    private String MATE_Code;
    private String MATE_Name;
    private String MUNITU_NAME;
    private String MATE_Size;
    private String MATE_Model;
    private String PRHSOD_AMNT;
    private String PRHSOD_ACCEIN;
    private String PRHSOD_BILLIN;
    private String PRHSOD_AMNT_RTN;
    private String MATE_PRICEP;
    private String PSR_NAME;


    public OrderMoreInfoListItem(String MATE_Code, String MATE_Name, String MUNITU_NAME, String MATE_Size,
                                 String MATE_Model, String PRHSOD_AMNT, String PRHSOD_ACCEIN, String PRHSOD_BILLIN,
                                 String PRHSOD_AMNT_RTN, String MATE_PRICEP) {

        this.MATE_Code = MATE_Code;
        this.MATE_Name = MATE_Name;
        this.MUNITU_NAME = MUNITU_NAME;
        this.MATE_Size = MATE_Size;
        this.MATE_Model = MATE_Model;
        this.PRHSOD_AMNT = PRHSOD_AMNT;
        this.PRHSOD_ACCEIN = PRHSOD_ACCEIN;
        this.PRHSOD_BILLIN = PRHSOD_BILLIN;
        this.PRHSOD_AMNT_RTN = PRHSOD_AMNT_RTN;
        this.MATE_PRICEP = MATE_PRICEP;
    }

    public OrderMoreInfoListItem(String MATE_Code, String MATE_Name, String MATE_Model,
                                 String PSR_NAME, String MATE_Size, String PRHSOD_AMNT) {

        this.MATE_Code = MATE_Code;
        this.MATE_Name = MATE_Name;
        this.MATE_Model = MATE_Model;
        this.PSR_NAME = PSR_NAME;
        this.MATE_Size = MATE_Size;
        this.PRHSOD_AMNT = PRHSOD_AMNT;
    }

    public String getMATE_Code() {
        return this.MATE_Code;
    }

    public String getMATE_Name() {
        return this.MATE_Name;
    }

    public String getMUNITU_NAME() {
        return this.MUNITU_NAME;
    }

    public String getMATE_Size() {
        return this.MATE_Size;
    }

    public String getMATE_Model() {
        return this.MATE_Model;
    }

    public String getPRHSOD_AMNT() {
        return this.PRHSOD_AMNT;
    }

    public String getPRHSOD_ACCEIN() {
        return this.PRHSOD_ACCEIN;
    }

    public String getPRHSOD_BILLIN() {
        return this.PRHSOD_BILLIN;
    }

    public String getPRHSOD_AMNT_RTN() {
        return this.PRHSOD_AMNT_RTN;
    }

    public String getMATE_PRICEP() {
        return this.MATE_PRICEP;
    }

    public String getPSR_NAME() {
        return PSR_NAME;
    }
}

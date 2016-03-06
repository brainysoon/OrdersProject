package com.fat246.orders.bean;

/**
 * Created by Administrator on 2016/2/21.
 */
public class ApplyInfo {

    //订单号
    private String PRHS_ID;

    //部门
    private String DEP_NAME;

    //采购情况
    private String PSD_NAME;

    //用途
    private String PSR_NAME;


    public ApplyInfo(String PRHS_ID,String DEP_NAME,String PSD_NAME,String PSR_NAME){

        this.PRHS_ID=PRHS_ID;
        this.DEP_NAME=DEP_NAME;
        this.PSD_NAME=PSD_NAME;
        this.PSR_NAME=PSR_NAME;
    }

    //可获取
    public String getPRHS_ID(){return this.PRHS_ID;}
    public String getDEP_NAME(){return this.DEP_NAME;}
    public String getPSD_NAME(){return this.PSD_NAME;}
    public String getPSR_NAME(){return this.PSR_NAME;}
}

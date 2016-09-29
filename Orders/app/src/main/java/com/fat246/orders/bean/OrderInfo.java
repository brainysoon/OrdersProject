package com.fat246.orders.bean;

public class OrderInfo {

    public static final String prhsord_id = "prhsord_id";
    public static final String is_passed = "is_passed";

    //订单ID
    private String PRHSORD_ID;

    //NAMEE
    private String NAMEE;

    //状态
    private String PRAC_NAME;

    //未开放信息
    private String ID;
    private String MATE_CODE;
    private String PRHS_SOUR;
    private String DEP_CODE;
    private String SUM;
    private boolean IS_PASSED;

    public OrderInfo(String PRHSORD_ID, String NAMEE, String PRAC_NAME, boolean IS_PASSED) {

        this.IS_PASSED = IS_PASSED;
        this.PRHSORD_ID = PRHSORD_ID;
        this.NAMEE = NAMEE;
        this.PRAC_NAME = PRAC_NAME;
    }

    public OrderInfo(String PRHSORD_ID, String NAMEE, String PRAC_NAME,
                     String SUM, boolean IS_PASSED) {
        this(PRHSORD_ID, NAMEE, PRAC_NAME, IS_PASSED);

        this.SUM = SUM;
    }

    //只包含ID的OrderInfo
    public OrderInfo(String PRHSORD_ID) {

        this.PRHSORD_ID = PRHSORD_ID;
        this.MATE_CODE = "";
        this.PRHS_SOUR = "";
        this.DEP_CODE = "";
        this.IS_PASSED = false;
    }

    //只读
    public String getPRHSORD_ID() {
        return this.PRHSORD_ID;
    }

    public String getNAMEE() {
        return this.NAMEE;
    }

    public String getPRAC_NAME() {
        return this.PRAC_NAME;
    }

    public boolean getIS_PASSED() {

        return this.IS_PASSED;
    }

    public String getSUM() {
        return this.SUM;
    }
}

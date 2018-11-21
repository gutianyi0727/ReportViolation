package com.example.elvis.reportviolation.bean;

/**
 * Elvis Gu, May 2018
 */
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * set up a reporterVioaltion class which is pointed to appUser
 * */
public class ReporterViolationCase extends BmobObject {

    private static final long serialVersionUID = 1L;

    private String reportTitleandType;

    private String plateNumber;

    private BmobGeoPoint LLlocation;

    private String delatedLoc;

    private MyUser reporter;

    private Boolean isDealt;

    public MyUser getReporter() {
        return reporter;
    }

    public void setReporter(MyUser reporter) {
        this.reporter = reporter;
    }



    public BmobGeoPoint getLLlocation() {
        return LLlocation;
    }

    public void setLLlocation(BmobGeoPoint LLlocation) {
        this.LLlocation = LLlocation;
    }

    public String getReportTitleandType() {
        return reportTitleandType;
    }

    public void setReportTitleandType(String reportTitleandType) {
        this.reportTitleandType = reportTitleandType;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Boolean getDealt() {
        return isDealt;
    }

    public void setDealt(Boolean dealt) {
        isDealt = dealt;
    }

    public String getDelatedLoc() {
        return delatedLoc;
    }

    public void setDelatedLoc(String delatedLoc) {
        this.delatedLoc = delatedLoc;
    }
}



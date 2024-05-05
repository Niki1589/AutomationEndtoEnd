package com.rms.automation.objects;

import com.rms.automation.enums.UIEEnums;

public class TestExecutorConfig {

    String testNo;
    Boolean isRun;
    Boolean isRunUploadImportExpo;
    UIEEnums ifUploadImportExpo;

    public String getTestNo() {
        return testNo;
    }

    public void setTestNo(String testNo) {
        this.testNo = testNo;
    }

    public Boolean getRun() {
        return isRun;
    }

    public void setRun(Boolean run) {
        isRun = run;
    }

    public Boolean getRunUploadImportExpo() {
        return isRunUploadImportExpo;
    }

    public void setRunUploadImportExpo(Boolean runUploadImportExpo) {
        isRunUploadImportExpo = runUploadImportExpo;
    }

    public UIEEnums getIfUploadImportExpo() {
        return ifUploadImportExpo;
    }

    public void setIfUploadImportExpo(UIEEnums ifUploadImportExpo) {
        this.ifUploadImportExpo = ifUploadImportExpo;
    }
}

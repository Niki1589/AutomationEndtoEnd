package com.rms.automation.edm;

public class TestCase {

    private String filename;
    private String filepath;
    private String fileExt;
    private String dbType;

    public String getDbType(){
        return dbType;
    }

    public void setDbType(String dbType)
    {
        this.dbType=dbType;
    }

    public String getFilename(){
        return filename;
    }

    public void setFilename(String filename){
        this.filename=filename;
    }

    public String getFilepath(){
        return filepath;
    }

    public void setFilepath(String filepath){
        this.filepath=filepath;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }



}

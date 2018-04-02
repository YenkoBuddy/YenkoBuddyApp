package com.example.wendy.yenko;

import com.google.gson.annotations.SerializedName;

/**
 * Created by s215087038 on 2017/07/31.
 */

public class DataObject {
    @SerializedName("description")
    private String description;
    private String problemID;
    public DataObject(){}
    public DataObject(String description, String problemID) {
        this.description = description;
        this.problemID = problemID;
    }
    public String getName() {
        return description;

    }
    public String getProblemID(){
        return this.problemID;
    }

    public void setProblemID(String problemID){
        this.problemID = problemID;
    }
    public void setName(String description, String problemID) {
        this.description = description;
        this.problemID = problemID;

    }
}


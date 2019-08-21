package com.example.the_strox.studentportal.models;

/**
 * Created by THe_strOX on 7/4/2016.
 */
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class DownloadFiles {

    public String name;
    public String subjectcode;
    public String type;
    public Float size;
    public String subjectcode_type;
    public DownloadFiles() {
    }



    public DownloadFiles(String name, String subjectcode, String type, Float size) {
        this.name = name;
        this.subjectcode = subjectcode;
        this.type = type;
        this.size = size;
        this.subjectcode_type=subjectcode+"_"+type;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubjectcode() {
        return subjectcode;
    }

    public void setSubjectcode(String subjectcode) {
        this.subjectcode = subjectcode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public String getSubjectcode_type() {
        return subjectcode_type;
    }

    public void setSubjectcode_type(String subjectcode_type) {
        this.subjectcode_type = subjectcode_type;
    }
}

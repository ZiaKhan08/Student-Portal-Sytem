package com.example.the_strox.studentportal.models;

/**
 * Created by THe_strOX on 7/4/2016.
 */
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Club {

    public String name;
    public String dept;
    public String desc;
   // public Map<String, Boolean> users = new HashMap<>();
    public Club() {
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Club(String name, String dept, String desc) {
        this.name = name;
        this.dept = dept;
        this.desc = desc;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("dept", dept);
        result.put("desc", desc);

        return result;
    }

}

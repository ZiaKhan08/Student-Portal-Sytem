package com.example.the_strox.studentportal.models;

import java.util.ArrayList;
import java.util.List;


public class DownloadList {
     private List<String> subjectnameList = new ArrayList<String>();
        private List<String> categorynameList = new ArrayList<String>();

    public List<String> getSubjectnameList() {
        subjectnameList.add("Comp-101");
        subjectnameList.add("Comp-201");
        subjectnameList.add("Comp-301");
        subjectnameList.add("Comp-102");
        subjectnameList.add("Comp-202");
        subjectnameList.add("Comp-302");
        return subjectnameList;
    }

    public List<String> getCategorynameList() {
        categorynameList.add("Books");
        categorynameList.add("Notes");
        categorynameList.add("Labs");
        categorynameList.add("Miscellaneous");
        return categorynameList;
    }

}

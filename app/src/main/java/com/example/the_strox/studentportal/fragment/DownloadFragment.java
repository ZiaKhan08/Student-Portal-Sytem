package com.example.the_strox.studentportal.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.the_strox.studentportal.ClubPostActivity;
import com.example.the_strox.studentportal.DownloadActivity;
import com.example.the_strox.studentportal.R;
import com.example.the_strox.studentportal.models.Club;
import com.example.the_strox.studentportal.models.DownloadList;

import com.example.the_strox.studentportal.DownloadActivity;

import java.util.ArrayList;
import java.util.List;


public class DownloadFragment extends Fragment{

    private AutoCompleteTextView autoCompleteTextViewSubject;
    private AutoCompleteTextView autoCompleteTextViewCategory;
    private Button buttonFindFiles;
    List<String> subjectnameList = new ArrayList<String>();
    List<String> categorynameList = new ArrayList<String>();
    private DownloadList downloadList=new DownloadList();
    ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_download1, container, false);
        autoCompleteTextViewSubject = (AutoCompleteTextView) rootView.findViewById(R.id.autocompleteView_dsubject);
        autoCompleteTextViewCategory = (AutoCompleteTextView)rootView.findViewById(R.id.autocompleteView_dcategory);
        buttonFindFiles = (Button) rootView.findViewById(R.id.button_find_files) ;


        subjectnameList=  downloadList.getSubjectnameList();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, subjectnameList);
        autoCompleteTextViewSubject.setAdapter(adapter);
        autoCompleteTextViewSubject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    autoCompleteTextViewSubject.showDropDown();
            }
        });

        categorynameList = downloadList.getCategorynameList();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, categorynameList);
        autoCompleteTextViewCategory.setAdapter(adapter);
        autoCompleteTextViewCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    autoCompleteTextViewCategory.showDropDown();
            }
        });

        buttonFindFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), DownloadActivity.class);
                intent.putExtra(DownloadActivity.EXTRA_SUBJECT_CODE_CATEGORY, autoCompleteTextViewSubject.getText().toString()+"_"+autoCompleteTextViewCategory.getText().toString());
                startActivity(intent);
            }
        });

        return rootView;
    }
}

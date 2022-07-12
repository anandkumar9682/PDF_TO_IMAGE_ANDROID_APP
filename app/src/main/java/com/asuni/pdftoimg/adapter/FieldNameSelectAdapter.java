package com.asuni.pdftoimg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.asuni.pdftoimg.R;
import com.asuni.pdftoimg.views.MainAct;
import com.asuni.pdftoimg.viewsmodel.MainActVM;

import java.io.File;
import java.util.List;

public class FieldNameSelectAdapter extends BaseAdapter implements ListAdapter {

    List<String > list;


    MainAct activity;

    List<File> fileList;

    MainActVM loadPdf;

    View preView;

    public FieldNameSelectAdapter(MainActVM loadPdf, MainAct activity, List<String> nameList, List<File> arrayList) {
        this.activity=activity;
        this.list=nameList;
        this.loadPdf=loadPdf;
        this.fileList=arrayList;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.customlayout, null);
        }


        TextView tvContact = view.findViewById(R.id.tvContact);
        tvContact.setText(list.get(position));

        View finalView = view;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( preView!=null )
                    preView.setBackgroundColor( activity.getResources().getColor(R.color.white));

                finalView.setBackgroundColor( activity.getResources().getColor(R.color.gray));

                preView=finalView;

                loadPdf.showConfirmPopup( fileList.get(position) );


            }

        });

        return view;
    }
}
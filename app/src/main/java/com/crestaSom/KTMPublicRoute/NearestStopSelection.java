package com.crestaSom.KTMPublicRoute;

import android.content.Intent;
import android.os.Bundle;
import android.app.ListActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.crestaSom.model.Vertex;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NearestStopSelection extends ListActivity {

    ListView stopList;
    List<String> stopName;
    List<Integer> stopIds;
    List<Vertex> vertexList;
    Intent recv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_Dialog_NoActionBar);
        setContentView(R.layout.activity_nearest_stop_selection);
        stopIds=new ArrayList<Integer>();
        stopName=new ArrayList<String>();
        recv=getIntent();
        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("data");
        vertexList = dw.getvList();
        for(Vertex v:vertexList){
            stopIds.add(v.getId());
            stopName.add(v.getName()+" ("+ new DecimalFormat("#.##").format(v.getDistanceFromSource()) + " km"+")");
        }
        stopList=getListView();
        ArrayAdapter<String> ar=new ArrayAdapter<String>(this,
                R.layout.mytextview,stopName);
        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.mytextview,
                stopName));


        stopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                // source.postDelayed(new Runnable() {
                // @Override
                // public void run() {
                // source.showDropDown();
                // }
                // },100);
                // source.setText(source.getText().toString());
                // source.setSelection(source.getText().length());
                //source.dismissDropDown();

                String vName=vertexList.get(position).getName();
                recv.putExtra("vName",vName);
                setResult(RESULT_OK,recv);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED,recv);
        finish();
    }
}

package com.crestaSom.KTMPublicRoute;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.ListActivity;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crestaSom.KTMPublicRoute.data.DataWrapper;
import com.crestaSom.model.Vertex;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NearestStopSelection extends ListActivity {

    ListView stopList;
    List<String> stopName;
    List<Integer> stopIds;
    List<Vertex> vertexList;
    SharedPreferences prefs;
    TextView tv;
    Intent recv;
    int language;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_Dialog_NoActionBar);
        setContentView(R.layout.activity_nearest_stop_selection);
        tv = (TextView) findViewById(R.id.tv1);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        language = Integer.parseInt(prefs.getString("language", "1"));
        if (language == 2)
            tv.setText("नजिकको स्टपहरु");
        stopIds = new ArrayList<Integer>();
        stopName = new ArrayList<String>();
        recv = getIntent();

        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("data");
        vertexList = dw.getvList();
        stopList = getListView();
        if (vertexList.size() > 0) {
            for (Vertex v : vertexList) {
                stopIds.add(v.getId());
                if (language == 1)
                    stopName.add(v.getName());
                else if (language == 2)
                    stopName.add(v.getNameNepali());
            }

            ArrayAdapter<String> ar = new ArrayAdapter<String>(this,
                    R.layout.mytextview, stopName);
            setListAdapter(new ArrayAdapter<String>(this,
                    R.layout.mytextview,
                    stopName));

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_down);
            animation.setDuration(1000);
            animation.setStartOffset(0);
            stopList.setVisibility(View.VISIBLE);
            stopList.startAnimation(animation);


            stopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub

                    String vName = vertexList.get(position).getName();
                    recv.putExtra("vName", vName);
                    setResult(200, recv);
                    finish();
                }
            });

        } else {
            TextView msg = (TextView) findViewById(R.id.textView);
            final float scale = getResources().getDisplayMetrics().density;
            if(language==1)
            msg.setText("No nearby location found!!!");
            else if(language==2)
                msg.setText("कुनै नजिकको स्टप भेटिएन!!!");
            msg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED, recv);
        finish();
    }
}

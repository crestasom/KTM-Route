package com.crestaSom.KTMPublicRoute;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.crestaSom.KTMPublicRoute.data.DataWrapper;
import com.crestaSom.model.RouteDataWrapper;
import com.crestaSom.model.Vertex;

import java.util.List;

import com.crestaSom.viewPageAdapter.ViewPagerAdapter;

public class DetailActivity extends AppCompatActivity {
    RouteDataWrapper routeDataWrapper;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter mViewPagerAdapter;

    List<Vertex> path;
    Boolean flag,flagAlt=false;
    String rName="",vehicleType="";
    double []distanceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        setContentView(R.layout.activity_detail);


        flagAlt=getIntent().getBooleanExtra("flagAlt", false);
        flag=getIntent().getBooleanExtra("flag", false);
        if(flagAlt){
            routeDataWrapper=(RouteDataWrapper)getIntent().getSerializableExtra("data");
        }else {
            if (flag) {
                DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("data");
                path = dw.getvList();
                rName = getIntent().getStringExtra("routeName");
                vehicleType = getIntent().getStringExtra("vehicleType");
            } else {
                DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("data");
                path = dw.getvList();
                distanceList = new double[10];

                distanceList = getIntent().getDoubleArrayExtra("distanceList");
                for (int i = 0; i < 10; i++) {

//                //Log.d("d from detail",distanceList[i]+"");
                }
            }
        }
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" Route Detail");
        if(flagAlt){
            String source=routeDataWrapper.getRouteData1().get(0).getvList().get(0).toString();
            String dest=routeDataWrapper.getRouteData2().get(0).getvList().get((routeDataWrapper.getRouteData2().get(0).getvList().size()-1)).toString();
            getSupportActionBar().setSubtitle(source+" - "+dest);
        }else {
            if (flag) {
                getSupportActionBar().setSubtitle(rName);
            } else {
                getSupportActionBar().setSubtitle(path.get(0) + " - " + path.get((path.size() - 1)));
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tabLayout=(TabLayout)findViewById(R.id.tabLayoutDetail);
        viewPager=(ViewPager)findViewById(R.id.viewPagerDetail);
        mViewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragments(getFragmentData(new TransitFragment())," Transit Detail");
        mViewPagerAdapter.addFragments(getFragmentData(new MapFragment())," Map");
        viewPager.setAdapter(mViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        //setupTabIcons();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public Fragment getFragmentData(Fragment fragment){
        Bundle bundle=new Bundle();
        bundle.putString("Test","Test Value");

        //Log.d("Flag from Details",flag.toString());
        bundle.putBoolean("flag",flag);
        if(flagAlt){
         bundle.putSerializable("data",routeDataWrapper);
            bundle.putBoolean("flagAlt",flagAlt);
            bundle.putBoolean("flag",flag);
        }else {
            bundle.putSerializable("vList",new DataWrapper(path));
            if (flag) {

                bundle.putString("routeName", rName);
                bundle.putString("vehicleType", vehicleType);
            } else {
                bundle.putDoubleArray("distanceList", distanceList);
            }
        }
        //TransitFragment transitFragment=new TransitFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
    }

    @Override
    protected void onResume() {
        super.onResume();
       // overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.searchicon);
        tabLayout.getTabAt(1).setIcon(R.drawable.mapview);
    }
}

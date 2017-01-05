package com.crestaSom.KTMPublicRoute;

import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.crestaSom.model.Vertex;

import java.util.List;

import viewPageAdapter.ViewPagerAdapter;

public class DetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter mViewPagerAdapter;

    List<Vertex> path;
    Boolean flag;
    String rName="",vehicleType="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("data");
        path = dw.getvList();
        flag=getIntent().getBooleanExtra("flag", false);
        if(flag){
            rName=getIntent().getStringExtra("routeName");
            vehicleType=getIntent().getStringExtra("vehicleType");
        }
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        if(flag) {
            getSupportActionBar().setTitle("Route Detail: " + rName);
        }else{
            getSupportActionBar().setTitle("Route Detail: " + path.get(0)+" - "+path.get((path.size()-1)));
        }
        tabLayout=(TabLayout)findViewById(R.id.tabLayoutDetail);
        viewPager=(ViewPager)findViewById(R.id.viewPagerDetail);
        mViewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragments(getFragmentData(new TransitFragment()),"Transit Detail");
        mViewPagerAdapter.addFragments(getFragmentData(new MapFragment()),"Map");
        viewPager.setAdapter(mViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


    }


    public Fragment getFragmentData(Fragment fragment){
        Bundle bundle=new Bundle();
        bundle.putString("Test","Test Value");
        bundle.putSerializable("vList",new DataWrapper(path));
        Log.d("Flag from Details",flag.toString());
        bundle.putBoolean("flag",flag);
        if(flag){
            bundle.putString("routeName",rName);
            bundle.putString("vehicleType",vehicleType);
        }
        //TransitFragment transitFragment=new TransitFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.find);
        tabLayout.getTabAt(1).setIcon(R.drawable.view);
    }
}

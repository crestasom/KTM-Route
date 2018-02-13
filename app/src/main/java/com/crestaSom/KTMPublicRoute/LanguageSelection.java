package com.crestaSom.KTMPublicRoute;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class LanguageSelection extends AppCompatActivity implements View.OnClickListener {

    Button english,nepali;
    SharedPreferences sharedPref;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int startFlag;
    public static final String KEY = "flag";
    public static final String LANGUAGE_PREF = "language";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.iconktmlogo);
        getSupportActionBar().setTitle(" KTM Public Route");
        sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        english=(Button)findViewById(R.id.englishLan);
        nepali=(Button)findViewById(R.id.nepaliLan);
        english.setOnClickListener(this);
        nepali.setOnClickListener(this);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        animation.setDuration(1000);
        english.setVisibility(View.VISIBLE);
        english.startAnimation(animation);
        animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        animation.setDuration(1000);
        animation.setDuration(1000);
        animation.setStartOffset(1000);
        nepali.setVisibility(View.VISIBLE);
        nepali.startAnimation(animation);

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.englishLan){
            editor = prefs.edit();
            editor.putString(LANGUAGE_PREF, "1");
            editor.commit();
            editor = sharedPref.edit();
            editor.putInt(KEY, 2);
            editor.commit();
            finish();
        }else if(view.getId()==R.id.nepaliLan){
            editor = prefs.edit();
            editor.putString(LANGUAGE_PREF, "2");
            editor.commit();
            editor = sharedPref.edit();
            editor.putInt(KEY, 2);
            editor.commit();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

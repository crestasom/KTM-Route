package com.crestaSom.KTMPublicRoute;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.crestaSom.KTMPublicRoute.Custom.CustomAdapter;

import java.util.ArrayList;
import java.util.List;

public class FeedBackActivity extends ListActivity {

    ListView feedbackList;
    String[] feedbackName={"Email","Facebook"};
    int[] drawableIds = {R.drawable.email, R.drawable.facebook
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_Dialog_NoActionBar);
        setContentView(R.layout.activity_feed_back);
        feedbackList=getListView();
//        feedbackName=new ArrayList<>();
//        feedbackName.add("E-Mail");
//        feedbackName.add("Facebook");
        CustomAdapter adapter = new CustomAdapter(this,feedbackName, drawableIds);
        setListAdapter(adapter);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_down);
        animation.setDuration(1000);
        animation.setStartOffset(0);
        feedbackList.setVisibility(View.VISIBLE);
        feedbackList.startAnimation(animation);
        feedbackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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

              if(position==0){
                  Intent intent=new Intent(Intent.ACTION_SENDTO);
                  intent.setData(Uri.parse("mailto:" + "ktmpublicroute@gmail.com"));
                  //String[] recipients={"ktmpublicroute@gmail.com"};
                  //intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                  intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback Regarding Your App KTM Public Route");
                  //  intent.setType("text/html");
                  startActivity(Intent.createChooser(intent, "Choose an E-mail Client"));
                  finish();
              }else if(position==1){
                  launchFacebookMessenger();
                  finish();
              }
            }
        });

    }


    public final void launchFacebookMessenger() {
        final String urlFb = "fb://messaging/"+"1834218883515476";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlFb));

        // If a Facebook app is installed, use it. Otherwise, launch
        // a browser
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() == 0) {
            final String urlBrowser = "https://www.facebook.com/messages/thread/"+"1834218883515476";
            intent.setData(Uri.parse(urlBrowser));
        }

        startActivity(intent);
    }

}

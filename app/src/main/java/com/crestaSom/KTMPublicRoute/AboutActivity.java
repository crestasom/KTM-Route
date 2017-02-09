package com.crestaSom.KTMPublicRoute;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

public class AboutActivity extends AppCompatActivity {

	Toolbar toolbar;
	LinearLayout facebookMessage,facebook;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		//getSupportActionBar().setIcon(R.drawable.buszcpy);
		getSupportActionBar().setTitle(" KTM Public Route (Beta)");
		getSupportActionBar().setSubtitle("About");
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		facebook=(LinearLayout) findViewById(R.id.facebook);
		facebookMessage=(LinearLayout) findViewById(R.id.facebookMessage);
		facebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				launchFacebook();
			}
		});
		facebookMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				launchFacebookMessenger();
			}
		});
		//tabLayout=(TabLayout)findViewById(R.id.tabLayout);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		dismiss();
	}
	
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		dismiss();
		return super.onTouchEvent(event);
		
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
	
	  public void dismiss() {
	        finish();
	    }

//	public static Intent getOpenFacebookIntent(Context context) {
//
//		try {
//			context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
//			return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/KTMPublicRoute"));
//		} catch (Exception e) {
//			return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/KTMPublicRoute"));
//		}
//	}


	public final void launchFacebook() {
		final String urlFb = "fb://page/"+"1834218883515476";
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(urlFb));

		// If a Facebook app is installed, use it. Otherwise, launch
		// a browser
		final PackageManager packageManager = getPackageManager();
		List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		if (list.size() == 0) {
			final String urlBrowser = "https://www.facebook.com/"+"1834218883515476";
			intent.setData(Uri.parse(urlBrowser));
		}

		startActivity(intent);
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

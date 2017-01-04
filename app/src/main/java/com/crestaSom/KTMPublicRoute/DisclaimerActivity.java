package com.crestaSom.KTMPublicRoute;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;

public class DisclaimerActivity extends Activity {

	Button searchBtn = null;
	Intent locatorService = null;
	AlertDialog alertDialog = null;
	boolean flag=false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refractor);
		searchBtn = (Button) findViewById(R.id.button);

		searchBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(flag){
					flag=false;
					Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
					intent.putExtra("enabled", true);
					sendBroadcast(intent);
				}else{
					flag=true;
					Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
					intent.putExtra("enabled", false);
					sendBroadcast(intent);
				}

			}
		});

	}


}
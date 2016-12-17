package com.crestaSom.KTMPublicRoute;

import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.widget.Toast;

public class TestActivity extends Activity {

	Button searchBtn = null;
	Intent locatorService = null;
	AlertDialog alertDialog = null;
	boolean flag=false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
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
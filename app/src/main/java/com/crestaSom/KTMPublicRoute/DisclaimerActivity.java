package com.crestaSom.KTMPublicRoute;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.app.AlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DisclaimerActivity extends AppCompatActivity {

	Button searchBtn = null;
	Intent locatorService = null;
	AlertDialog alertDialog = null;
	boolean flag=false;
	ProgressDialog pDialog;
	Toolbar toolbar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disclaimer);
		toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setIcon(R.drawable.iconktmlogo);
		getSupportActionBar().setTitle(" KTM Public Route");
		searchBtn = (Button) findViewById(R.id.button);

		searchBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();

			}
		});
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
		animation.setDuration(1000);
		searchBtn.setVisibility(View.VISIBLE);
		searchBtn.startAnimation(animation);


	}


	class CopyMap extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(DisclaimerActivity.this);
			pDialog.setMessage("Initializing Map");
			pDialog.setIndeterminate(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			pDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Continue",new DialogInterface.OnClickListener(){
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//				}
//			});


			// pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			InputStream in = null;
			OutputStream out = null;
			String TAG = "error";
			try {

				in = getAssets().open("tiles.zip");

				Log.i(TAG, ": " + Environment.getExternalStorageDirectory());
				File dir = new File(Environment.getExternalStorageDirectory(), "osmdroid");
				Log.i(TAG, "isExist : " + dir.exists());
				if (!dir.exists())
					dir.mkdirs();
				File fileZip = new File(dir, "tiles.zip");

				Log.i(TAG, "isExist : " + fileZip.exists());

				out = new FileOutputStream(fileZip);
				byte[] buffer = new byte[1024];
				int read;
				int i = 0;
				int count = 12319;

//				for(i=0;i<100;i++){
//					publishProgress(i);
//				}
//				while ((in.read(buffer)) != -1) {
//					count++;
//				}
				////Log.d("count", ""+count);
				int prgss;
				while ((read = in.read(buffer)) != -1) {
					prgss = (100 * i) / count;
					publishProgress(prgss);
					out.write(buffer, 0, read);
					i++;
				}
				//Log.d("i:", "" + i);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				Log.e("tag", "Failed to copy asset file: " + e.getMessage());
			}

			// pDialog.setProgress(pDialog.getProgress());
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			//Log.d("Progress", progress[0] + "");
			pDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			//Toast.makeText(Welcome.this, "Map has been successfully initialized", Toast.LENGTH_SHORT).show();
		}

	}


}
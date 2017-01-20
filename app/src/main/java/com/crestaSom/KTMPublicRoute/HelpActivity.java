package com.crestaSom.KTMPublicRoute;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

public class HelpActivity extends AppCompatActivity {



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setIcon(R.drawable.buszcpy);
		getSupportActionBar().setTitle(" KTM Public Route (Beta)");
		getSupportActionBar().setSubtitle("User Guide");
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	  public void dismiss() {
	        finish();
	    }
}

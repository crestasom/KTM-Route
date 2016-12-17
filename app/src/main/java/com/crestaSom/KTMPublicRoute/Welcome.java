package com.crestaSom.KTMPublicRoute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.crestaSom.autocomplete.CustomAutoCompleteView;
import com.crestaSom.database.Database;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class Welcome extends Activity implements OnClickListener {

	CustomAutoCompleteView source, destination;
	TextView srcDest, routes;

	JSONParser jsonParser = new JSONParser();
	private static String urlCheck = "http://shresthasom.com.np/collegeProjectDatabase/admin.php?url=version/checkNew";
	private static String url = "http://shresthasom.com.np/collegeProjectDatabase/admin.php?url=route/findNewRecords";
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String KEY = "flag";
	public static final String DB_KEY = "dbFlag";
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	int startFlag;
	SharedPreferences prefs;
	private ProgressDialog pDialog;
	String dbCheckFlag = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isNetworkAvailable()) {
			new CheckNewRecord().execute();
			if (dbCheckFlag.equals("1")) {

			}
		}

		setContentView(R.layout.activity_welcome);
		srcDest = (TextView) findViewById(R.id.searchRoute);
		srcDest.setOnClickListener(this);
		routes = (TextView) findViewById(R.id.viewRoute);
		routes.setOnClickListener(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		startFlag = sharedPref.getInt(KEY, -1);
		if (startFlag == -1) {

			// to copy map tile for first use

			// moveFile(Environment.getRootDirectory()+"/osmdroid/","tiles.zip",Environment.getExternalStorageDirectory()+"/osmdroid/");
			// copyAssets();
			new CopyMap().execute();

			sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
			editor = sharedPref.edit();
			editor.putInt(KEY, 1);
			editor.putInt(DB_KEY, 1);
			editor.commit();
			// AlertDialog.Builder alertDialogBuilder = new
			// AlertDialog.Builder(this);
			// alertDialogBuilder.setMessage("We Recommend you to check for
			// route record updates at first use");
			// alertDialogBuilder.setTitle("Update Recommended");
			// // super.onBackPressed();
			// alertDialogBuilder.setNegativeButton("Check Update", new
			// DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface arg0, int arg1) {
			// if (isNetworkAvailable()) {
			// new CheckNewRecord().execute();
			// } else {
			// Toast.makeText(getApplicationContext(), "No Internet Access",
			// Toast.LENGTH_SHORT).show();
			// }
			// }
			// });
			//
			// alertDialogBuilder.setPositiveButton("Cancel", new
			// DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// // finish();
			//
			// }
			// });
			//
			// AlertDialog alertDialog = alertDialogBuilder.create();
			// alertDialog.show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.auto_complete, menu);
		 menu.add(0, 2, 0, "About");
		 menu.add(0, 3, 0, "Help");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == 3) {
			startActivity(new Intent(getApplicationContext(),
					HelpActivity.class));
		} else if (id == 2) {
			startActivity(new Intent(getApplicationContext(),
					AboutActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.searchRoute) {
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(i);
			// finish();
		}
		if (v.getId() == R.id.viewRoute) {
			Intent i = new Intent(getApplicationContext(), RouteList.class);
			startActivity(i);
			// finish();
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	static public boolean isURLReachable(Context context) {
		context.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			URL url = new URL(urlCheck); // Change to "http://google.com" for
											// www test.
			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
			urlc.setConnectTimeout(10 * 1000); // 10 s.
			urlc.connect();
			if (urlc.getResponseCode() == 200) { // 200 = "OK" code (http
													// connection is fine).
				Log.wtf("Connection", "Success !");
				return true;
			} else {
				return false;
			}
		} catch (MalformedURLException e1) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	// check whether there is any update in server
	class CheckNewRecord extends AsyncTask<String, String, String> {

		AlertDialog.Builder alertDialogBuilder;
		AlertDialog alertDialog1;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			alertDialogBuilder = new AlertDialog.Builder(Welcome.this);

			// pDialog = new ProgressDialog(Welcome.this);
			// pDialog.setMessage("Connecting to server");
			// pDialog.setIndeterminate(false);
			// pDialog.setCancelable(true);
			// pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (isURLReachable(getApplicationContext())) {
				try {
					// pDialog.setMessage("Getting New Records");
					JSONObject json = jsonParser.makeHttpRequest(urlCheck, "POST", params);
					Log.d("JSON Data", "" + json);
					int dbFlagServer = json.getInt("dbVersion");
					int dbFlagClient = sharedPref.getInt(DB_KEY, -1);
					Log.d("Flag Check", "server:" + dbFlagServer + "\tclient:" + dbFlagClient);
					if (dbFlagClient < dbFlagServer) {
						return "1";
					} else {
						return "0";
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// pDialog.dismiss();
			if (file_url.equals("1")) {

				alertDialogBuilder.setMessage("New Records are available.Do you like to update records?");
				alertDialogBuilder.setTitle("Record Update Available");
				// super.onBackPressed();
				alertDialogBuilder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (isNetworkAvailable()) {
							new GetNewRecord().execute();
						} else {
							Toast.makeText(getApplicationContext(), "No Internet Access", Toast.LENGTH_SHORT).show();
						}
					}
				});

				alertDialogBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// finish();

					}
				});

				alertDialog1 = alertDialogBuilder.create();
				alertDialog1.show();
			} else if (file_url.equals("0")) {
				// Toast.makeText(Welcome.this, "Server Not available",
				// Toast.LENGTH_LONG).show();
			} else {
				// Toast.makeText(Welcome.this, file_url,
				// Toast.LENGTH_LONG).show();
			}
		}

	}

	class GetNewRecord extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Welcome.this);
			pDialog.setMessage("Getting New Records");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			Database db = new Database(getApplicationContext());
			// String number = mobile_number.getText().toString();
			// String pwd = password.getText().toString();
			// params.add(new BasicNameValuePair("mobile_number", number));
			// params.add(new BasicNameValuePair("password", pwd));
			if (isURLReachable(getApplicationContext())) {
				try {
					// pDialog.setMessage("Getting New Records");
					JSONObject json = jsonParser.makeHttpRequest(urlCheck, "POST", params);
					Log.d("JSON Data", "" + json);
					int dbFlagServer = json.getInt("dbVersion");
					int dbFlagClient = sharedPref.getInt(DB_KEY, -1);
					Log.d("Flag Check", "server:" + dbFlagServer + "\tclient:" + dbFlagClient);
					if (dbFlagClient < dbFlagServer) {
						json = jsonParser.makeHttpRequest(url, "GET", params);
						JSONArray vertexNew = json.getJSONArray("Vertex");
						JSONArray edgeNew = json.getJSONArray("Edge");
						JSONArray routeNew = json.getJSONArray("Route");
						String message = json.getString("message");
						db.addNewRecords(vertexNew, edgeNew, routeNew);
						sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
						editor = sharedPref.edit();
						editor.putInt(DB_KEY, dbFlagServer);
						editor.commit();
						return message;
					} else {
						return "0";
						// Toast.makeText(getApplicationContext(),
						// "No new records found", Toast.LENGTH_SHORT).show();
					}

					// Log.d("Length", ""+size);

					// Log.d("Json Vertex", vertexNew.toString());
					// Log.d("Json Edge", edgeNew.toString());
					// Log.d("Json Route", routeNew.toString());
					// for (int i = 0; i < values.length(); i++) {
					// Vertex v = new Vertex();
					// if (names.getString(i).equals("id")) {
					// v.setId(values.getInt(i));
					// }else if (names.getString(i).equals("name")) {
					// v.setName(values.getString(i));
					// }
					// verts.add(v);
					// }

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				return "-1";
			}
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if (file_url.equals("0")) {
				Toast.makeText(Welcome.this, "No new Records Found", Toast.LENGTH_LONG).show();
			} else if (file_url.equals("-1")) {
				Toast.makeText(Welcome.this, "Server Not available", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(Welcome.this, file_url, Toast.LENGTH_LONG).show();
			}
		}

	}

	private void copyAssets() {
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {

	}

	class CopyMap extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Welcome.this);
			pDialog.setMessage("Initializing Map");
			pDialog.setIndeterminate(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

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
				int count =12319;
				
//				for(i=0;i<100;i++){
//					publishProgress(i);
//				}
//				while ((in.read(buffer)) != -1) {
//					count++;
//				}
				//Log.d("count", ""+count);
				int prgss;
				while ((read = in.read(buffer)) != -1) {
					prgss=(int)((100*i)/count);
					publishProgress(prgss);
					out.write(buffer, 0, read);
					i++;
				}
				Log.d("i:", ""+i);
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
			Log.d("Progress", progress[0] + "");
			pDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			Toast.makeText(Welcome.this, "Map has been successfully initialized", Toast.LENGTH_SHORT).show();
		}

	}
}

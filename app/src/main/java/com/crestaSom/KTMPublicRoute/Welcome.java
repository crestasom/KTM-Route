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

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import com.crestaSom.KTMPublicRoute.data.JSONParser;
import com.crestaSom.autocomplete.CustomAutoCompleteView;
import com.crestaSom.database.Database;
import com.crestaSom.model.Vertex;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;





import com.crestaSom.viewPageAdapter.ViewPagerAdapter;




@ReportsCrashes(
        mailTo = "ktmpublicroute@gmail.com",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
//        formUriBasicAuthLogin = "ormediffelikozelinsepout",
//        formUriBasicAuthPassword = "e2a3f4ebb4d4a81483885acfe93cf31ecbce285f",
        // formKey = "", // This is required for backward compatibility but not used
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.app_name
)
public class Welcome extends AppCompatActivity implements OnClickListener {

    CustomAutoCompleteView source, destination;
    TextView srcDest, routes;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter mViewPagerAdapter;

    JSONParser jsonParser = new JSONParser();
    private static String urlCheck = "http://shresthasom.com.np/collegeProjectDatabaseNew/admin.php?url=version/checkNew";
    private static String url = "http://shresthasom.com.np/collegeProjectDatabaseNew/admin.php?url=route/";
//    private static String urlCheck = "http://192.168.1.109/collegeDatabase/admin.php?url=version/checkNew";
//    private static String url = "http://192.168.1.109/collegeDatabase/admin.php?url=route/";
    //	private static String urlCheckTail = "/collegeDatabase/admin.php?url=version/checkNew";
//	private static String urlTail = "/collegeDatabase/admin.php?url=route/findNewRecords";
//	private static String urlCheck,url;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String KEY = "flag";
    public static final String DB_KEY = "dbFlag";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    int startFlag;
    SharedPreferences prefs;
    private ProgressDialog pDialog;
    String dbCheckFlag = "0";
    String urlHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ACRA.init(getApplication());
        setContentView(R.layout.activity_welcome);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.iconktmlogo);
        getSupportActionBar().setTitle(" KTM Public Route (Beta)");
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        mViewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragments(new SearchRouteFragment()," Search");
        mViewPagerAdapter.addFragments(new ViewRouteFragment()," View");
        viewPager.setAdapter(mViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setupTabIcons();
        sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        startFlag = sharedPref.getInt(KEY, -1);
        if (startFlag == -1) {

            // to copy map tile for first use
            new CopyMap().execute();
            sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.putInt(KEY, 1);
            editor.putInt(DB_KEY, 1);
            editor.commit();
            startActivityForResult(new Intent(this, DisclaimerActivity.class),100);

        }else if(startFlag==1){
            startActivity(new Intent(this, LanguageSelection.class));
        }
        if (isNetworkAvailable()) {
            new CheckNewRecord().execute();
            if (dbCheckFlag.equals("1")) {

            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        startFlag = sharedPref.getInt(KEY, -1);
        if(startFlag==1){
            startActivity(new Intent(this, LanguageSelection.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.auto_complete, menu);
        menu.add(0, 2, 0, "About");
        menu.add(0,4,0,"Disclaimer");
        menu.add(0,6,0,"Feedback");
        menu.add(0, 5, 0, "Setting");
        menu.add(0, 3, 0, "User Guide");
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
        } else if (id == 5) {
            startActivity(new Intent(getApplicationContext(),
                    SettingsActivity.class));
        } else if (id==4) {
            startActivity(new Intent(getApplicationContext(),
                    DisclaimerActivity.class));
        }
        else if (id==6) {
            startActivity(new Intent(getApplicationContext(),
                    FeedBackActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.direction);
        tabLayout.getTabAt(1).setIcon(R.drawable.view);
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

        }

        @Override
        protected String doInBackground(String... arg0) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            ////Log.d("urlcheck", isURLReachable(getApplicationContext()) + "");
            if (isURLReachable(getApplicationContext())) {
                try {
                    // pDialog.setMessage("Getting New Records");
                    JSONObject json = jsonParser.makeHttpRequest(urlCheck, "POST", params);
                    ////Log.d("JSON Data", "" + json);
                    int dbFlagServer = json.getInt("dbVersion");
                    int dbFlagClient = sharedPref.getInt(DB_KEY, -1);
                    ////Log.d("Flag Check", "server:" + dbFlagServer + "\tclient:" + dbFlagClient);
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

            return "0";
        }

        @Override
        protected void onPostExecute(String file_url) {
            // pDialog.dismiss();
            ////Log.d("returning", file_url);
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
            pDialog.setCancelable(false);
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
                    //Log.d("JSON Data", "" + json);
                    int dbFlagServer = json.getInt("dbVersion");
                    int dbFlagClient = sharedPref.getInt(DB_KEY, -1);
                    ////Log.d("Flag Check", "server:" + dbFlagServer + "\tclient:" + dbFlagClient);
                    if (dbFlagClient < dbFlagServer) {
                        json = jsonParser.makeHttpRequest(url+"findNewRecords", "GET", params);

                        Log.d("json data", json.toString());
                        JSONArray edgeNew = json.getJSONArray("Edge");
                        JSONArray fareNew=json.getJSONArray("Fare");

                        json = jsonParser.makeHttpRequest(url+"findNewRecords1", "GET", params);
                        JSONArray vertexNew = json.getJSONArray("Vertex");
                        JSONArray routeNew = json.getJSONArray("Route");
                        String message = json.getString("message");
                        db.addNewRecords(vertexNew, edgeNew, routeNew,fareNew);
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
                Toast.makeText(Welcome.this, file_url+"\nRestarting App", Toast.LENGTH_LONG).show();
            }

            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
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
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            pDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Continue",new DialogInterface.OnClickListener(){
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });


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
                //////Log.d("count", ""+count);
                int prgss;
                while ((read = in.read(buffer)) != -1) {
                    prgss = (100 * i) / count;
                    publishProgress(prgss);
                    out.write(buffer, 0, read);
                    i++;
                }
                ////Log.d("i:", "" + i);
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
            ////Log.d("Progress", progress[0] + "");
            pDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            //Toast.makeText(Welcome.this, "Map has been successfully initialized", Toast.LENGTH_SHORT).show();
        }

    }


    public List<Vertex> getItemsFromDb(String searchTerm) {

        // add items on the array dynamically
        ////Log.d("Application Context",getApplicationContext().toString());
        Database db = new Database(getBaseContext());
        List<Vertex> vertexes = new ArrayList<Vertex>();
        ////Log.d("Database",db.toString());
        vertexes = db.getVertexUsingQuery(searchTerm);
//        itemId.clear();
//        for (Vertex v : vertexes) {
//            itemId.add(v.getId());
//        }
        return vertexes;

    }



}

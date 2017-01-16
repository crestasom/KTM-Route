package com.crestaSom.KTMPublicRoute;


import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crestaSom.KTMPublicRoute.data.DataWrapper;
import com.crestaSom.autocomplete.CustomAutoCompleteView;
import com.crestaSom.database.Database;
import com.crestaSom.implementation.KtmPublicRoute;
import com.crestaSom.model.Edge;
import com.crestaSom.model.Route;
import com.crestaSom.model.RouteData;
import com.crestaSom.model.RouteDataWrapper;
import com.crestaSom.model.Vertex;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */

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
public class SearchRouteFragment extends Fragment implements View.OnClickListener {

    double[] distanceList;
    SharedPreferences prefs;
    ProgressDialog dialog;
    List<Vertex> path;
    Queue<RouteDataWrapper> altPathSingleTransit;
    List<RouteDataWrapper> transferRouteData;
    List<RouteData> tranPath;
    List<RouteData> singlePaths;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String KEY = "flag";
    public static final String DB_KEY = "dbFlag";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    TextView viewDetailTemplate;
    Button viewMap;
    List<Vertex> path1;
    Double distMin = 0.0;
    LocationManager locationmanager;
    Location mlocation;
    List<Vertex> singleRouteVertex;
    String provider;
    String gpsOrigin = "";
    Criteria cri;
    Double lat = 0.0, longi = 0.0;
    public CustomAutoCompleteView source, destination;
    int flag;
    View v;
    private ProgressDialog pDialog;
    CheckBox gps;
    int startFlag;
    int count = 0;
    private String display, displaySingle = "";
    // adapter for auto-complete
    public ArrayAdapter<String> myAdapter;

    InputMethodManager imm;

    ImageView gpsToggle, clearSource, clearDestination, gpsToggleDest;
    int gpsFlag = 0, gpsFlagDest = 0;
    // just to add some initial value
    public List<String> item = new ArrayList<String>();
    public List<Integer> itemId = new ArrayList<Integer>();
    TextView tv, singleRoute, ViewDetail, ViewDetailSingle, displayTextView;
    EditText source1, dest;
    Button submit;
    Button b, next;
    int srcId, destId;
    KtmPublicRoute imp;
    boolean checkNw = false;
    ScrollView sv;
    LayoutInflater inf;
    ViewGroup cont;
    LinearLayout shortestRouteLayout, singleRouteLayout, singleRouteLayoutMain, shortestRouteLayoutMain;


    public SearchRouteFragment() {
        // Required empty public constructor
    }


    public void setUIElements() {
        v = inf.inflate(R.layout.fragment_search_route, cont, false);
        source = (CustomAutoCompleteView) v.findViewById(R.id.editSource);
        destination = (CustomAutoCompleteView) v.findViewById(R.id.editDestination);
        myAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //ACRA.init();
        inf = inflater;
        cont = container;
        View v = inflater.inflate(R.layout.fragment_search_route, container, false);
        // Inflate the layout for this fragment
        path = new LinkedList<Vertex>();
        path1 = new ArrayList<Vertex>();
        sharedPref = getActivity().getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        startFlag = sharedPref.getInt(KEY, -1);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        sv = (ScrollView) v.findViewById(R.id.scrollView1);
        clearSource = (ImageView) v.findViewById(R.id.clearSource);
        clearSource.setOnClickListener(this);
        clearDestination = (ImageView) v.findViewById(R.id.clearDestination);
        clearDestination.setOnClickListener(this);
        singlePaths = new ArrayList<>();
        locationmanager=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        // viewMap = (Button) findViewById(R.id.submit);
        // viewMap.setOnClickListener(this);


        try {
            imp = new KtmPublicRoute(getActivity().getApplicationContext());
            imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            submit = (Button) v.findViewById(R.id.findRoute);
            submit.setOnClickListener(this);

            shortestRouteLayout = (LinearLayout) v.findViewById(R.id.shortestRoute);
            singleRouteLayout = (LinearLayout) v.findViewById(R.id.singleRoute);
            ViewDetail = (TextView) v.findViewById(R.id.viewDetailRoute);
            ViewDetail.setOnClickListener(this);
            ViewDetailSingle = (TextView) v.findViewById(R.id.viewSingleRoute);
            singleRouteLayoutMain = (LinearLayout) v.findViewById(R.id.singleRouteDisplay);
            shortestRouteLayoutMain = (LinearLayout) v.findViewById(R.id.shortestRouteLayout);
            ViewDetailSingle.setOnClickListener(this);

            // set our adapter
            myAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, item);
            // autocompletetextview is in activity_main.xml
            source = (CustomAutoCompleteView) v.findViewById(R.id.editSource);
            source.setDropDownBackgroundResource(R.color.dropDownBackground);
            //source.setTextColor(Color.BLUE);
            //source.setDropDownBackgroundResource(R.color.colorPrimary);

            // add the listener so it will tries to suggest while the user types
            source.addTextChangedListener(new CustomAutoCompleteTextChangedListener(getActivity().getApplicationContext(), source.getId()));
            source.setAdapter(myAdapter);
            source.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // TODO Auto-generated method stub
                    srcId = -1;
                    return false;
                }
            });
            // locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
            // 0, 0, this);
            source.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    destination.requestFocus();


                }
            });


            destination = (CustomAutoCompleteView) v.findViewById(R.id.editDestination);

            // add the listener so it will tries to suggest while the user types
            destination.addTextChangedListener(new CustomAutoCompleteTextChangedListener(getActivity().getApplicationContext(), destination.getId()));
            destination.setDropDownBackgroundResource(R.color.dropDownBackground);
            destination.setAdapter(myAdapter);
            destination.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // TODO Auto-generated method stub
                    destId = -1;
                    return false;
                }
            });
            destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    submit.requestFocus();
                    // TODO Auto-generated method stub
                }
            });

            gpsToggle = (ImageView) v.findViewById(R.id.gpslocation);
            gpsToggle.setOnClickListener(new View.OnClickListener() {

                @SuppressLint("InlinedApi")
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub



                    boolean enabled = locationmanager
                            .isProviderEnabled(LocationManager.GPS_PROVIDER);

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
                    if (!enabled) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Toast.makeText(getActivity().getApplicationContext(), "Please Enable Location", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }

                    /*String off = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                    if (off.isEmpty()) {
                        Toast.makeText(getActivity().getApplicationContext(), "Please Enable Location", Toast.LENGTH_SHORT).show();
                        Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(onGPS);
                    }*/ else if (gpsFlag == 0) {
                        gpsOrigin = "source";
                        gpsFlag = 1;
                        final SearchRouteFragment.FetchCordinates getCord = new SearchRouteFragment.FetchCordinates();
                        getCord.execute();
                        new CountDownTimer(10000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                // Do nothing
                                Log.d("Time left:", millisUntilFinished + "");
                            }

                            public void onFinish() {
                                Log.d("Status message", "Finish reached of Countdown");
                                Log.d("async task status", getCord.getStatus().toString());
                                if (getCord.getStatus() == AsyncTask.Status.RUNNING) {
                                    Log.d("Asnc Task canceal", "true");
                                    getCord.cancel(true);
                                }
                            }

                        }.start();
//
                    } else {
                        gpsFlag = 0;
                        source.setText("");
                        source.setEnabled(true);
                        source.requestFocus();
                        imm.showSoftInput(source, InputMethodManager.SHOW_IMPLICIT);
                        gpsToggle.setImageResource(R.drawable.gps);
                    }

                }
            });


            gpsToggleDest = (ImageView) v.findViewById(R.id.gpslocationDest);
            gpsToggleDest.setOnClickListener(new View.OnClickListener() {

                @SuppressLint("InlinedApi")
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub


                    String off = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                    if (off.isEmpty()) {
                        Toast.makeText(getActivity().getApplicationContext(), "Please Enable Location", Toast.LENGTH_SHORT).show();
                        Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(onGPS);
                    } else if (gpsFlagDest == 0) {
                        gpsFlagDest = 1;
                        gpsOrigin = "destination";
                        final SearchRouteFragment.FetchCordinates getCord = new SearchRouteFragment.FetchCordinates();
                        getCord.execute();
                        new CountDownTimer(20000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                // Do nothing
                                Log.d("Time left:", millisUntilFinished + "");
                            }

                            public void onFinish() {
                                Log.d("Status message", "Finish reached of Countdown");
                                Log.d("async task status", getCord.getStatus().toString());
                                if (getCord.getStatus() == AsyncTask.Status.RUNNING) {
                                    Log.d("Asnc Task canceal", "true");
                                    getCord.cancel(true);
                                }
                            }

                        }.start();
//
                    } else {
                        gpsFlagDest = 0;
                        destination.setText("");
                        destination.setEnabled(true);
                        destination.requestFocus();
                        imm.showSoftInput(destination, InputMethodManager.SHOW_IMPLICIT);
                        gpsToggleDest.setImageResource(R.drawable.gps);
                    }

                }
            });

        } catch (

                NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }


    public String findShortestPath(int srcId, int destId) {

        String display = "";
        Database db = new Database(getActivity().getApplicationContext());
        new ArrayList<Route>();

        new ArrayList<Edge>();
        new ArrayList<Vertex>();

        Vertex source = null, dest = null;
        source = db.getVertex(srcId);
        dest = db.getVertex(destId);
        path.clear();

        path = imp.findShortestPath(source, dest);
        path1.clear();
        path1.addAll(path);

        return display;


    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // String src = source.getText().toString();
        // String dst = dest.getText().toString();
        // int srcId = Integer.parseInt(src);
        // int destId = Integer.parseInt(dst);
        if (v.getId() == R.id.findRoute) {
            Database db = new Database(getActivity().getApplicationContext());
            String sourceString = source.getText().toString();
            String destString = destination.getText().toString();
            Vertex src = new Vertex();
            src = db.getVertexDetail(sourceString);
            Vertex dst = new Vertex();
            dst = db.getVertexDetail(destString);
            Log.d("Source", "" + src);
            Log.d("Dest", "" + dst);
            if (sourceString.equals("") || destString.equals("")) {
                if (sourceString.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Source not selected!", Toast.LENGTH_SHORT).show();
                } else if (destString.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Destination not selected!", Toast.LENGTH_SHORT).show();
                }
            } else if (src.getId() == -1 || dst.getId() == -1) {
                if (src.getId() == -1) {
                    Toast.makeText(getActivity().getApplicationContext(), "Not a valid source!Please choose from suggestion",
                            Toast.LENGTH_SHORT).show();
                } else if (dst.getId() == -1) {
                    Toast.makeText(getActivity().getApplicationContext(), "Not a valid destination!Please choose from suggestion",
                            Toast.LENGTH_SHORT).show();
                }
            } else if (src.equals(dst)) {
                Toast.makeText(getActivity().getApplicationContext(), "Source and destination cannot be same", Toast.LENGTH_SHORT)
                        .show();
            } else {
                distMin = Double.parseDouble(prefs.getString("walkingDist", "0.0"));
                sv.smoothScrollTo(0, 0);
                sv.setVisibility(View.GONE);
                ViewDetailSingle.setVisibility(View.GONE);
                singleRouteLayout.setVisibility(View.GONE);
                singleRouteLayoutMain.setVisibility(View.GONE);
                ViewDetail.setVisibility(View.INVISIBLE);
                InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                srcId = src.getId();
                destId = dst.getId();
                new SearchRouteFragment.CalculatePath().execute();

            }
        } else if (v.getId() == R.id.viewDetailRoute) {
            Intent i = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
            i.putExtra("data", new DataWrapper(path1));
            i.putExtra("flag", false);
            i.putExtra("distanceList", distanceList);
            //  i.putExtra("distanceList",distanceList);
            // i.putParcelableArrayListExtra("path", (ArrayList<Vertex>)
            // path);
            startActivity(i);

        } else if (v.getTag() == "View Detail") {
            Intent i = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
            for (int a = 0; a < count; a++) {
                if (v.getId() == 1000 * a) {
                    i.putExtra("data", new DataWrapper(tranPath.get(a).getvList()));
                }
            }

            i.putExtra("flag", false);
            i.putExtra("distanceList", distanceList);
            Log.d("data in search route", singleRouteVertex.toString());

            // i.putParcelableArrayListExtra("path", (ArrayList<Vertex>)
            // path);
            startActivity(i);
        } else if (v.getTag() == "View Detail Alternative") {
            Intent i = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
            for (int a = 0; a < count; a++) {
                if (v.getId() == 1000 * a) {
                    i.putExtra("data", transferRouteData.get(a));
                    Log.d("Data Transferred", transferRouteData.get(a).toString());
                }
            }

            i.putExtra("flag", false);
            i.putExtra("flagAlt", true);
            i.putExtra("distanceList", distanceList);
            Log.d("data in search route", singleRouteVertex.toString());

            // i.putParcelableArrayListExtra("path", (ArrayList<Vertex>)
            // path);
            startActivity(i);
        } else if (v.getId() == R.id.clearSource) {
            source.setText("");
            source.requestFocus();
            source.setError(null);
            imm.showSoftInput(source, InputMethodManager.SHOW_IMPLICIT);
        } else if (v.getId() == R.id.clearDestination) {
            destination.setText("");
            destination.requestFocus();
            destination.setError(null);
            imm.showSoftInput(destination, InputMethodManager.SHOW_IMPLICIT);
        }

    }

    public List<Vertex> getItemsFromDb(String searchTerm) {

        // add items on the array dynamically
        Database db = new Database(getActivity());
        List<Vertex> vertexes = new ArrayList<Vertex>();
        Log.d("Database", db.toString());
        vertexes = db.getVertexUsingQuery(searchTerm);
        itemId.clear();
        for (Vertex v : vertexes) {
            itemId.add(v.getId());
        }
        return vertexes;

    }

    class CalculatePath extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // new MyCustomProgressDialog(getApplicationContext());
            pDialog = new ProgressDialog(getActivity());
            pDialog.setIcon(R.drawable.busz);
            pDialog.setMessage("Detecting Path");
            //findShortestPath(srcId,destId);
            shortestRouteLayout.removeAllViews();
            singleRouteLayout.removeAllViews();
            pDialog.show();

            // dialog = MyCustomProgressDialog.ctor(getApplicationContext());
            // dialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            display = findShortestPath(srcId, destId);

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            // dialog.dismiss();
            Database db = new Database(getActivity());
            Vertex sourceP = db.getVertex(srcId);
            Vertex destP = db.getVertex(destId);
            altPathSingleTransit = imp.getAlternativeRouteOneTransit(sourceP, destP);
            pDialog.dismiss();
            List<Vertex> vertexList = new ArrayList<Vertex>();
            sv.setVisibility(View.VISIBLE);
            Route r;


            int i = 0;
            int totalCost = 0;
            double totalDist = 0;
            List<Integer> routeIds = new ArrayList<Integer>();
            int pixels;
            final float scale = getActivity().getResources().getDisplayMetrics().density;
            Map<List<Integer>, List<Vertex>> pathRoute;
            i = 1;
            int temp = 0;
            display += "Shortest Route:";
            singleRoute = new TextView(getActivity());
            singleRoute.setText(display);
            singleRoute.setPadding(2, 10, 2, 0);
            singleRoute.setGravity(Gravity.CENTER_HORIZONTAL);
            singleRoute.setTypeface(Typeface.DEFAULT_BOLD);
            pixels = (int) (15 * scale + 0.5f);
            singleRoute.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            singleRoute.setTextSize(pixels);
            shortestRouteLayout.addView(singleRoute);
//            addViewAnimation(singleRoute);
            distanceList = new double[10];
            while (!path.isEmpty()) {
                if (path.size() == 1) {
                    break;
                }
                pathRoute = imp.findRoutePath(path);
                Iterator<Map.Entry<List<Integer>, List<Vertex>>> it = pathRoute.entrySet().iterator();


                while (it.hasNext()) {
                    Map.Entry<List<Integer>, List<Vertex>> pair = it.next();

                    routeIds = pair.getKey();

                    vertexList = pair.getValue();
                    display = "";
                    display += "Travel " + i;
                    singleRoute = new TextView(getActivity());
                    singleRoute.setText(display);
                    pixels = (int) (12 * scale + 0.5f);
                    singleRoute.setTextSize(pixels);
                    singleRoute.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    singleRoute.setPadding(2, 0, 2, 0);
                    shortestRouteLayout.addView(singleRoute);
//                    addViewAnimation(singleRoute);
                    double d = imp.getRouteDistance(vertexList);
                    Log.d("temp", temp + "");
                    Log.d("d from search", d + "");
                    distanceList[temp++] = d;

                    if (distMin < d) {

                        display = "";
                        display += "Take a ride from " + vertexList.get(0) + " to " + vertexList.get(vertexList.size() - 1);
                        int fare = imp.getRouteCost(d);
                        totalCost += fare;
                        totalDist += d;
                        display += " with distance " + new DecimalFormat("#.##").format(d) + " km";
                        display += " and cost Rs." + fare;
                        i++;
                    } else {
                        display = "";
                        display += "Walk from " + vertexList.get(0) + " to " + vertexList.get(vertexList.size() - 1);
                        totalDist += d;

                        display += " with distance " + new DecimalFormat("#.##").format(d) + " km";
                        i++;
                    }
                    display += "\n";
                    singleRoute = new TextView(getActivity());
                    singleRoute.setText(display);
                    pixels = (int) (8 * scale + 0.5f);
                    singleRoute.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    singleRoute.setTextSize(pixels);
                    singleRoute.setPadding(2, 0, 2, 0);
                    shortestRouteLayout.addView(singleRoute);

//                    addViewAnimation(singleRoute);
                }
            }
            display = "";
            display += "Total Distance:" + new DecimalFormat("#.##").format(totalDist) + " km";
            display += "\nTotal Cost:Rs. " + totalCost;
            singleRoute = new TextView(getActivity());
            singleRoute.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            singleRoute.setText(display);
            singleRoute.setTypeface(Typeface.DEFAULT_BOLD);
            pixels = (int) (8 * scale + 0.5f);
            singleRoute.setTextSize(pixels);
            singleRoute.setPadding(2, 0, 2, 0);
            shortestRouteLayout.addView(singleRoute);

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_down);
            animation.setDuration(1000);
            //animation.setStartOffset(1000*(i-1));
            shortestRouteLayoutMain.setVisibility(View.VISIBLE);
            shortestRouteLayoutMain.startAnimation(animation);
            animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
            animation.setDuration(1500);
            animation.setStartOffset(1000);
            //singleRouteLayoutMain.setVisibility(View.VISIBLE);
            ViewDetail.setVisibility(View.VISIBLE);
            ViewDetail.startAnimation(animation);
//            addViewAnimation(singleRoute);

            singleRouteVertex = new ArrayList<Vertex>();
            displaySingle = "";
            //singleRouteVertex = imp.getSingleRoute(sourceP, destP);
            singlePaths = imp.getSingleRoutes(sourceP, destP);
            Log.d("Single Routes", singlePaths.toString());

            if (!singlePaths.isEmpty()) {
                // distanceList[0]=0;
                tranPath = new ArrayList<>();
                i = 1;
                displaySingle += "Direct Route:";
                singleRoute = new TextView(getActivity());
                singleRoute.setGravity(Gravity.CENTER_HORIZONTAL);
                singleRoute.setTypeface(Typeface.DEFAULT_BOLD);
                singleRoute.setText(displaySingle);
                pixels = (int) (15 * scale + 0.5f);
                singleRoute.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                singleRoute.setTextSize(pixels);
                singleRoute.setPadding(2, 2, 2, 0);
                singleRouteLayout.addView(singleRoute);
                for (RouteData dw : singlePaths) {
                    singleRouteVertex = dw.getvList();
                    if (!singleRouteVertex.equals(path1)) {
                        displaySingle = "";
                        displaySingle += "Route " + i;
                        singleRoute = new TextView(getActivity());
                        singleRoute.setText(displaySingle);
                        pixels = (int) (12 * scale + 0.5f);
                        singleRoute.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        singleRoute.setTextSize(pixels);
                        singleRoute.setPadding(2, 2, 2, 0);
                        singleRouteLayout.addView(singleRoute);

                        displaySingle = "";
                        singleRouteLayout.setVisibility(View.VISIBLE);
                        singleRouteLayoutMain.setVisibility(View.VISIBLE);
                        displaySingle = "";
                        displaySingle += "Take a ride from " + sourceP.toString() + " to " + destP.toString();
                        Route singleRoute = imp.getSingleRoute();
                        double d1 = imp.getRouteDistance(singleRouteVertex);
                        int fare1 = imp.getRouteCost(d1);
                        displaySingle += " with distance " + new DecimalFormat("#.##").format(d1) + " km";
                        displaySingle += " and cost Rs. " + fare1;
                        displayTextView = new TextView(getActivity());
                        displayTextView.setText(displaySingle);
                        displayTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        pixels = (int) (8 * scale + 0.5f);
                        displayTextView.setTextSize(pixels);
                        displayTextView.setPadding(2, 0, 2, 0);
                        singleRouteLayout.addView(displayTextView);

                        displaySingle = "Available Route:";
                        displayTextView = new TextView(getActivity());
                        displayTextView.setText(displaySingle);
                        displayTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        displayTextView.setTypeface(Typeface.DEFAULT_BOLD);
                        pixels = (int) (12 * scale + 0.5f);
                        displayTextView.setTextSize(pixels);
                        displayTextView.setPadding(2, 0, 2, 0);
                        singleRouteLayout.addView(displayTextView);

                        displaySingle = "";
                        displaySingle += dw.getrName();
                        displayTextView = new TextView(getActivity());
                        displayTextView.setText(displaySingle);
                        //displayTextView.setTypeface(Typeface.DEFAULT_BOLD);
                        displayTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        pixels = (int) (8 * scale + 0.5f);
                        displayTextView.setTextSize(pixels);
                        displayTextView.setPadding(2, 0, 2, 0);
                        singleRouteLayout.addView(displayTextView);

                        viewDetailTemplate = (TextView) View.inflate(getActivity(), R.layout.view_detail_textview, null);
                        viewDetailTemplate.setId(1000 * (i - 1));
                        viewDetailTemplate.setTag("View Detail");
                        viewDetailTemplate.setOnClickListener(SearchRouteFragment.this);
                        singleRouteLayout.addView(viewDetailTemplate);


                        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_down);
                        animation.setDuration(1000);
                        animation.setStartOffset(1000);
                        singleRouteLayoutMain.setVisibility(View.VISIBLE);
                        singleRouteLayoutMain.startAnimation(animation);
                        //ViewDetailSingle.setVisibility(View.VISIBLE);
                        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
                        animation.setDuration(1000);
                        animation.setStartOffset(1000);
                        //singleRouteLayoutMain.setVisibility(View.VISIBLE);


                        //viewDetailTemplate.setVisibility(View.VISIBLE);
                        ViewDetailSingle.startAnimation(animation);
                        tranPath.add(dw);

                        i++;
                    }

                }
                count = i;
            } else {

                if (!altPathSingleTransit.isEmpty()) {
                    int size=altPathSingleTransit.size();
                    Queue<RouteDataWrapper> tempRouteDataWrapper = new PriorityQueue<>();
                    tempRouteDataWrapper.addAll(altPathSingleTransit);
                    i = 1;
                    count = 1;
                    int loopVar=0;
                    displaySingle += "Alternative Route:";
                    singleRoute = new TextView(getActivity());
                    singleRoute.setGravity(Gravity.CENTER_HORIZONTAL);
                    singleRoute.setTypeface(Typeface.DEFAULT_BOLD);
                    singleRoute.setText(displaySingle);
                    pixels = (int) (15 * scale + 0.5f);
                    singleRoute.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    singleRoute.setTextSize(pixels);
                    singleRoute.setPadding(2, 2, 2, 0);
                    singleRouteLayout.addView(singleRoute);
                    transferRouteData = new ArrayList<>();
                    //while(!tempRouteDataWrapper.isEmpty()){
                    for (RouteDataWrapper routeData : altPathSingleTransit) {
                        //  RouteDataWrapper routeData=tempRouteDataWrapper.poll();
                        if ((routeData.getDistTotal() != totalDist) && (routeData.getDistTotal() < (totalDist + 6))) {

                            transferRouteData.add(routeData);
                            //transferRouteData.addAll(altPathSingleTransit);

                            List<Vertex> vertices1 = routeData.getRouteData1().get(0).getvList();
                            List<Vertex> vertices2 = routeData.getRouteData2().get(0).getvList();
                            Vertex transitStop = vertices2.get(0);

                            //display first transit
                            displaySingle = "";
                          //  displaySingle+="Total Distance:"+routeData.getDistTotal()+"\n"+"Total Distance:"+totalDist;
                            displaySingle += "Route " + i + ":";
                            singleRoute = new TextView(getActivity());
                            singleRoute.setText(displaySingle);
                            pixels = (int) (12 * scale + 0.5f);
                            singleRoute.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            singleRoute.setTextSize(pixels);
                            singleRoute.setPadding(2, 2, 2, 0);
                            singleRouteLayout.addView(singleRoute);


                            displaySingle = "";
                            displaySingle += "Travel 1";
                            singleRoute = new TextView(getActivity());
                            singleRoute.setText(displaySingle);
                            pixels = (int) (10 * scale + 0.5f);
                            singleRoute.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            singleRoute.setTextSize(pixels);
                            singleRoute.setPadding(2, 2, 2, 0);
                            singleRouteLayout.addView(singleRoute);

                            displaySingle = "";
                            singleRouteLayout.setVisibility(View.VISIBLE);
                            singleRouteLayoutMain.setVisibility(View.VISIBLE);
                            displaySingle = "";
                            displaySingle += "Take a ride from " + sourceP.toString() + " to " + transitStop.toString();

                            double d1 = imp.getRouteDistance(vertices1);
                            int fare1 = imp.getRouteCost(d1);
                            displaySingle += " with distance " + new DecimalFormat("#.##").format(d1) + " km";
                            displaySingle += " and cost Rs. " + fare1;
                            displayTextView = new TextView(getActivity());
                            displayTextView.setText(displaySingle);
                            displayTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            pixels = (int) (8 * scale + 0.5f);
                            displayTextView.setTextSize(pixels);
                            displayTextView.setPadding(2, 0, 2, 0);
                            singleRouteLayout.addView(displayTextView);

                            //display second transit
                            displaySingle = "";
                            displaySingle += "Travel 2 ";
                            singleRoute = new TextView(getActivity());
                            singleRoute.setText(displaySingle);
                            pixels = (int) (10 * scale + 0.5f);
                            singleRoute.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            singleRoute.setTextSize(pixels);
                            singleRoute.setPadding(2, 2, 2, 0);
                            singleRouteLayout.addView(singleRoute);

                            displaySingle = "";
                            singleRouteLayout.setVisibility(View.VISIBLE);
                            singleRouteLayoutMain.setVisibility(View.VISIBLE);
                            displaySingle = "";
                            displaySingle += "Take a ride from " + transitStop.toString() + " to " + destP.toString();

                            double d2 = imp.getRouteDistance(vertices2);
                            int fare2 = imp.getRouteCost(d2);
                            displaySingle += " with distance " + new DecimalFormat("#.##").format(d2) + " km";
                            displaySingle += " and cost Rs. " + fare2;
                            displayTextView = new TextView(getActivity());
                            displayTextView.setText(displaySingle);
                            displayTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            pixels = (int) (8 * scale + 0.5f);
                            displayTextView.setTextSize(pixels);
                            displayTextView.setPadding(2, 0, 2, 0);
                            singleRouteLayout.addView(displayTextView);
                            double totalDist1 = d1 + d2;
                            int totalCost1 = fare1 + fare2;

                            displaySingle = "";
                            displaySingle += "Total Distance:" + new DecimalFormat("#.##").format(totalDist1) + " km";
                            displaySingle += "\nTotal Cost:Rs. " + totalCost1;
                            displayTextView = new TextView(getActivity());
                            displayTextView.setText(displaySingle);
                            displayTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            displayTextView.setTypeface(Typeface.DEFAULT_BOLD);
                            pixels = (int) (8 * scale + 0.5f);
                            displayTextView.setTextSize(pixels);
                            displayTextView.setPadding(2, 0, 2, 0);
                            singleRouteLayout.addView(displayTextView);
//

                            viewDetailTemplate = (TextView) View.inflate(getActivity(), R.layout.view_detail_textview, null);
                            viewDetailTemplate.setId(1000 * (i - 1));
                            viewDetailTemplate.setTag("View Detail Alternative");
                            viewDetailTemplate.setOnClickListener(SearchRouteFragment.this);
                            singleRouteLayout.addView(viewDetailTemplate);
                            if(loopVar<size-1) {
                                displaySingle = "";
                                displayTextView = new TextView(getActivity());
                                displayTextView.setText(displaySingle);
                                displayTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                displayTextView.setBackgroundColor(getResources().getColor(R.color.detailBackground));
                                displayTextView.setTypeface(Typeface.DEFAULT_BOLD);
                                pixels = (int) (2 * scale + 0.5f);
                                displayTextView.setTextSize(pixels);
                                displayTextView.setPadding(2, 0, 2, 0);
                                singleRouteLayout.addView(displayTextView);
                            }
                            animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_down);
                            animation.setDuration(1000);
                            animation.setStartOffset(1000);
                            singleRouteLayoutMain.setVisibility(View.VISIBLE);
                            singleRouteLayoutMain.startAnimation(animation);
                            //ViewDetailSingle.setVisibility(View.VISIBLE);
                            animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
                            animation.setDuration(1000);
                            animation.setStartOffset(1000);
                            i++;
                        }
                        loopVar++;
                    }
                    count = i;
                }
            }

        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {
                // your code goes here
                source.requestFocus();

                //  imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                imm.showSoftInput(source, InputMethodManager.SHOW_IMPLICIT);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200 && requestCode == 100) {
            if (gpsOrigin.equals("source")) {
                String vName = data.getStringExtra("vName");
                gpsToggle.setImageResource(R.drawable.gpsselected);
                source.setText(vName);
                source.setEnabled(false);
                clearSource.setVisibility(View.INVISIBLE);
                destination.requestFocus();
//            InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(
//                    Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(destination, InputMethodManager.SHOW_IMPLICIT);
            } else if (gpsOrigin.equals("destination")) {
                String vName = data.getStringExtra("vName");
                gpsToggleDest.setImageResource(R.drawable.gpsselected);
                destination.setText(vName);
                destination.setEnabled(false);
                source.requestFocus();
//            InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(
//                    Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(source, InputMethodManager.SHOW_IMPLICIT);
            }

        } else if (resultCode == 300 && requestCode == 100) {

        }
    }

    public class FetchCordinates extends AsyncTask<String, Integer, String> {
        ProgressDialog progDailog = null;
        Boolean running = true;
        public double lati = 0.0;
        public double longi = 0.0;

        public LocationManager mLocationManager;
        public SearchRouteFragment.FetchCordinates.VeggsterLocationListener mVeggsterLocationListener;

        @Override
        protected void onPreExecute() {
            mVeggsterLocationListener = new SearchRouteFragment.FetchCordinates.VeggsterLocationListener();

            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            cri = new Criteria();
            provider = mLocationManager.getBestProvider(cri, false);
            //cri.setHorizontalAccuracy(1000);

            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                //	Toast.makeText(MainActivity.this,"Network Mode:"+LocationManager.NETWORK_PROVIDER.toString(),Toast.LENGTH_SHORT).show();


                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 0,
                            mVeggsterLocationListener);
               // mlocation = mLocationManager.getLastKnownLocation(provider);

//                    lat=mlocation.getLatitude();
//                    longi=mlocation.getLongitude();


            } else {
                //	Toast.makeText(MainActivity.this,"GPS Mode",Toast.LENGTH_SHORT).show();
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0,
                        mVeggsterLocationListener);
               // mlocation = mLocationManager.getLastKnownLocation(provider);

//                lat=mlocation.getLatitude();
//                longi=mlocation.getLongitude();

            }

            progDailog = new ProgressDialog(getActivity());
//            progDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setContentView(R.layout.dialog_login);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    SearchRouteFragment.FetchCordinates.this.cancel(true);
                }
            });
            progDailog.setMessage("Detecting your current location...");
            progDailog.setIndeterminate(false);
            progDailog.setCancelable(true);
            progDailog.show();

        }

        @Override
        protected void onCancelled() {
            Log.d("Cancel message", "Cancelled by user!");
            Toast.makeText(getActivity().getApplicationContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
            progDailog.dismiss();
            running = false;
            this.cancel(true);
            mLocationManager.removeUpdates(mVeggsterLocationListener);
        }

        @Override
        protected void onPostExecute(String result) {
            progDailog.dismiss();
            Log.d("coordinates", lati + longi + "");
//            lat=mlocation.getLatitude();
//            longi=mlocation.getLongitude();
            Queue<Vertex> sourceV = imp.getNearestStop(lati, longi);
            Vertex v;
            //source.setText(sourceV.getName());
            List<Vertex> vList = new ArrayList<Vertex>();
            int a=0;
            //for (int i = 0; i < 4; i++) {
            while(a<4){
                if(sourceV.isEmpty()){
                    break;
                }
                v = sourceV.poll();
                if (v.getDistanceFromSource() < 1.0) {
                    Log.d("Polled Vertex", v.getName());
                    vList.add(v);
                    a++;
                }
            }
            mLocationManager.removeUpdates(mVeggsterLocationListener);
            Log.d("vertex", vList.toString());
            Intent i = new Intent(getActivity().getApplicationContext(), NearestStopSelection.class);
            i.putExtra("data", new DataWrapper(vList));
            startActivityForResult(i, 100);
            //Toast.makeText(MainActivity.this,
//					"LATITUDE :" + lati + " LONGITUDE :" + longi,
//					Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            int x = 0;
//            while (mlocation.equals(null) && !this.isCancelled()) {
            while (this.lati == 0.0 && !this.isCancelled()) {
//				Log.d("x:",""+x++);
//				System.out.println("x:"+x++);
//                if(isCancelled()){
//                    break;
//                }
            }
            return null;
        }

        public class VeggsterLocationListener implements LocationListener {

            @Override
            public void onLocationChanged(Location location) {

               // mlocation = location;
//
//                String info = location.getProvider();
                try {

                    // LocatorService.myLatitude=location.getLatitude();

                    // LocatorService.myLongitude=location.getLongitude();

                    lati = location.getLatitude();
                    longi = location.getLongitude();

                } catch (Exception e) {
                    // progDailog.dismiss();
                    // Toast.makeText(getApplicationContext(),"Unable to get Location"
                    // , Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("OnProviderDisabled", "OnProviderDisabled");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("onProviderEnabled", "onProviderEnabled");
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.i("onStatusChanged", "onStatusChanged");

            }

        }

    }


    public class CustomAutoCompleteTextChangedListener implements TextWatcher {


        public static final String TAG = "CustomAutoCompleteTextChangedListener.java";
        Context context;
        int id;

        public CustomAutoCompleteTextChangedListener(Context context, int id) {
            this.context = context;
            this.id = id;
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence userInput, int start, int before,
                                  int count) {

            // if you want to see in the logcat what the user types
            //Log.e(TAG, "User input: " + userInput);

            List<Vertex> vertexes = getItemsFromDb(userInput
                    .toString());
            // query the database based on the user input

            // if(size==0){
            // item
            // }
            item.clear();
            itemId.clear();
            if (vertexes.size() == 0) {
                //mainActivity.item.add("No Suggestion found");
                //mainActivity.itemId.add(-1);
                //mainActivity.source.setCompletionHint("");
                if (id == source.getId()) {
                    source.setError("No suggestion found");
                } else {
                    destination.setError("No suggestion found");

                }
                //mainActivity.destination.setActivated(false);

            } else {
                for (Vertex v : vertexes) {
                    item.add(v.getName());
                    Log.d("vertexes", item.toString());
                    //		mainActivity.itemId.add(v.getId());

                }
            }

            // update the adapater

            // mainActivity.myAdapter.
            myAdapter.notifyDataSetChanged();
            myAdapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, item);
            if (id == source.getId()) {
                source.setAdapter(myAdapter);
                if (source.getText().toString().equals("")) {
                    clearSource.setVisibility(View.INVISIBLE);
                } else {
                    clearSource.setVisibility(View.VISIBLE);
                }
            } else {
                destination.setAdapter(myAdapter);
                if (destination.getText().toString().equals("")) {
                    clearDestination.setVisibility(View.INVISIBLE);
                } else {
                    clearDestination.setVisibility(View.VISIBLE);
                }
            }

        }


    }


    @Override
    public void onResume() {
//        destination.clearFocus();
//        source.requestFocus();
        super.onResume();
    }
}

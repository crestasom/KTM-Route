package com.crestaSom.KTMPublicRoute;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crestaSom.autocomplete.CustomAutoCompleteTextChangedListener;
import com.crestaSom.autocomplete.CustomAutoCompleteView;
import com.crestaSom.database.Database;
import com.crestaSom.implementation.KtmPublicRoute;
import com.crestaSom.model.Edge;
import com.crestaSom.model.Route;
import com.crestaSom.model.Vertex;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRouteFragment extends Fragment implements View.OnClickListener {


    SharedPreferences prefs;
    ProgressDialog dialog;
    List<Vertex> path;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String KEY = "flag";
    public static final String DB_KEY = "dbFlag";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Button viewMap;
    List<Vertex> path1;
    Double distMin = 0.0;
    LocationManager locationmanager;
    List<Vertex> singleRouteVertex;
    String provider;
    Criteria cri;
    Double lat = 0.0, longi = 0.0;
    public CustomAutoCompleteView source, destination;
    int flag;
    View v;
    private ProgressDialog pDialog;
    CheckBox gps;
    int startFlag;
    private String display, displaySingle = "";
    // adapter for auto-complete
    public ArrayAdapter<String> myAdapter;

    ImageView gpsToggle;
    int gpsFlag = 0;
    // just to add some initial value
    public List<String> item = new ArrayList<String>();
    public List<Integer> itemId = new ArrayList<Integer>();
    TextView tv, singleRoute, ViewDetail, ViewDetailSingle;
    EditText source1, dest;
    Button submit;
    Button b, next;
    int srcId, destId;
    KtmPublicRoute imp;
    boolean checkNw = false;
    ScrollView sv;
    LayoutInflater inf;
    ViewGroup cont;


    public SearchRouteFragment() {
        // Required empty public constructor
    }


    public void setUIElements() {
        v = inf.inflate(R.layout.fragment_home, cont, false);
        source = (CustomAutoCompleteView) v.findViewById(R.id.editSource);
        destination = (CustomAutoCompleteView) v.findViewById(R.id.editDestination);
        myAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inf = inflater;
        cont = container;
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        path = new LinkedList<Vertex>();
        path1 = new ArrayList<Vertex>();
        sharedPref = getActivity().getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        startFlag = sharedPref.getInt(KEY, -1);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        sv = (ScrollView) v.findViewById(R.id.scrollView1);
        // viewMap = (Button) findViewById(R.id.submit);
        // viewMap.setOnClickListener(this);


        try {
            imp = new KtmPublicRoute(getActivity().getApplicationContext());

            submit = (Button) v.findViewById(R.id.findRoute);
            submit.setOnClickListener(this);
            tv = (TextView) v.findViewById(R.id.shortestRoute);
            singleRoute = (TextView) v.findViewById(R.id.singleRoute);
            ViewDetail = (TextView) v.findViewById(R.id.viewDetailRoute);
            ViewDetail.setOnClickListener(this);
            ViewDetailSingle = (TextView) v.findViewById(R.id.viewSingleRoute);
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
                    // TODO Auto-generated method stub
                    // source.postDelayed(new Runnable() {
                    // @Override
                    // public void run() {
                    // source.showDropDown();
                    // }
                    // },100);
                    // source.setText(source.getText().toString());
                    // source.setSelection(source.getText().length());
                    // source.dismissDropDown();
                    // Toast.makeText(MainActivity.this,
                    // parent.getItemAtPosition(position).toString(),
                    // Toast.LENGTH_SHORT)
                    // .show();
                    // srcId = itemId.get(position);

                }
            });
            // source.post(new Runnable() {
            // public void run() {
            // source.dismissDropDown();
            // }
            // });
            // //source.setId(1);
            // source.setTag("source");

            destination = (CustomAutoCompleteView) v.findViewById(R.id.editDestination);

            // add the listener so it will tries to suggest while the user types
            destination.addTextChangedListener(new CustomAutoCompleteTextChangedListener(getActivity().getApplicationContext(), destination.getId()));
            destination.setDropDownBackgroundResource(R.color.dropDownBackground);
            //destination.setDropDownBackgroundResource(R.color.colorPrimary);
            //destination.setTextColor(Color.BLUE);
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
                    // destination.dismissDropDown();
                    // Toast.makeText(MainActivity.this,
                    // parent.getItemAtPosition(position).toString(),
                    // Toast.LENGTH_SHORT)
                    // .show();
                    // destId = itemId.get(position);

                }
            });

            gpsToggle = (ImageView) v.findViewById(R.id.gpslocation);
            gpsToggle.setOnClickListener(new View.OnClickListener() {

                @SuppressLint("InlinedApi")
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub


                    String off = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                    if (off.isEmpty()) {
                        Toast.makeText(getActivity().getApplicationContext(), "Please Enable Location", Toast.LENGTH_SHORT).show();
                        Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(onGPS);
                    } else if (gpsFlag == 0) {
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
//						getNearestLocation();
                        // // Log.d("Lat1,Long1", "" + lat + "\t" + longi);
//						if (lat != 0.0 || longi != 0.0) {
//                            Queue<Vertex>  sourceV = imp.getNearestStop(lat, longi);
//							//source.setText(sourceV.getName());
//                            List<Vertex> vList=new ArrayList<Vertex>();
//                            vList.add(sourceV.poll());
//                            vList.add(sourceV.poll());
//                            vList.add(sourceV.poll());
//							vList.add(sourceV.poll());
//							Log.d("vertex", vList.toString());
//                            Intent i=new Intent(MainActivity.this,NearestStopSelection.class);
//                            i.putExtra("data", new DataWrapper(vList));
//                            startActivityForResult(i,100);
//
//						} else {
//							// gps.setChecked(false);
//							flag = 0;
//							Toast.makeText(getApplicationContext(), "Error Detecting Current Location",
//									Toast.LENGTH_LONG).show();
//							source.requestFocus();
//							InputMethodManager imm = (InputMethodManager) getSystemService(
//									Context.INPUT_METHOD_SERVICE);
//							imm.showSoftInput(destination, 0);
//						}
                    } else {
                        gpsFlag = 0;
                        source.setText("");
                        source.setEnabled(true);
                        source.requestFocus();
                        gpsToggle.setImageResource(R.drawable.gps);
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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getActivity().getApplicationContext().getMenuInflater().inflate(R.menu.auto_complete, menu);
//        menu.add(0, 2, 0, "About");
//        menu.add(0, 3, 0, "Help");
//        menu.add(0,4,0,"Setting");
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == 3) {
            startActivity(new Intent(getActivity().getApplicationContext(),
                    HelpActivity.class));
        } else if (id == 2) {
            startActivity(new Intent(getActivity().getApplicationContext(),
                    AboutActivity.class));
        } else if (id == 4) {
            startActivity(new Intent(getActivity().getApplicationContext(),
                    SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public String findShortestPath(int srcId, int destId) {
        String display = "";
        Database db = new Database(getActivity().getApplicationContext());
        new ArrayList<Route>();

        new ArrayList<Edge>();
        new ArrayList<Vertex>();

        //
        //
        //
        Vertex source = null, dest = null;
        source = db.getVertex(srcId);
        dest = db.getVertex(destId);
        path.clear();

        path = imp.findShortestPath(source, dest);
        path1.clear();
        path1.addAll(path);

        List<Vertex> vertexList = new ArrayList<Vertex>();
        Route r;
        int i = 0;
        int totalCost = 0;
        double totalDist = 0;
        List<Integer> routeIds = new ArrayList<Integer>();
        // Map<Integer,List<Vertex>> pathRoute;
        Map<List<Integer>, List<Vertex>> pathRoute;
        i = 1;
        display += "Shortest Route:\n";
        while (!path.isEmpty()) {
            if (path.size() == 1) {
                break;
            }
            pathRoute = imp.findRoutePath(path);
            // Log.d("path", "" + pathRoute);

            // Iterator<Map.Entry<Integer, List<Vertex>>> it =
            // pathRoute.entrySet()
            // .iterator();

            Iterator<Map.Entry<List<Integer>, List<Vertex>>> it = pathRoute.entrySet().iterator();
            while (it.hasNext()) {
                // Map.Entry<Integer, List<Vertex>> pair=(Map.Entry<Integer,
                // List<Vertex>>) it
                // .next();
                Map.Entry<List<Integer>, List<Vertex>> pair = it.next();

                routeIds = pair.getKey();
                // Log.d("Mapped Routes", "Mapped Routes" + routeIds);
                // System.out.println("Mapped Routes" + routeIds);


                vertexList = pair.getValue();
                // display += "\nRoute" + i + ":" + r.getName();
                // System.out.println("Vehicle Type:"+r.getVehicleType());
                // display += "\nVehicle Type:" + r.getVehicleType();
                // for (Vertex v : vertexList) {
                // System.out.println(v);
                // display += "\n" + v.toString();
                // }
                double d = imp.getRouteDistance(vertexList);
                if (distMin < d) {
                    display += "Travel " + i;
                    display += "\nTake a ride from " + vertexList.get(0) + " to " + vertexList.get(vertexList.size() - 1);

                    int fare = imp.getRouteCost(d);
                    totalCost += fare;
                    totalDist += d;
                    // System.out.println("\nRoute Distance:" + d);

                    display += " with distance " + new DecimalFormat("#.##").format(d) + " km";
                    display += " and cost Rs." + fare;
//                    display += "Available Routes:";
//                    for (int z : routeIds) {
//
//                        r = imp.getRoute(z);
//                        display += "\n" + r.getName();
//                        // System.out.println("Vehicle Type:"+r.getVehicleType());
//                        display += "\n(" + r.getVehicleType() + ")";
//                    }

                    i++;
                } else {
                    display += "Travel " + i;
                    display += "\nWalk from " + vertexList.get(0) + " to " + vertexList.get(vertexList.size() - 1);

                    //int fare = imp.getRouteCost(d);
                    //totalCost += fare;
                    totalDist += d;
                    // System.out.println("\nRoute Distance:" + d);

                    display += " with distance " + new DecimalFormat("#.##").format(d) + " km";
                    //display += " and cost Rs." + fare + "\n";
//					display += "Available Routes:";
//					for (int z : routeIds) {
//
//						r = imp.getRoute(z);
//						display += "\n" + r.getName();
//						// System.out.println("Vehicle Type:"+r.getVehicleType());
//						display += "\n(" + r.getVehicleType() + ")";
//					}

                    i++;
                }
                // i++;
                display += "\n\n";

            }
        }
        display += "\nTotal Distance:" + new DecimalFormat("#.##").format(totalDist) + " km";
        display += "\nTotal Cost:Rs. " + totalCost;
        singleRouteVertex = new ArrayList<Vertex>();
        displaySingle = "";
        singleRouteVertex = imp.getSingleRoute(source, dest);
        if (!singleRouteVertex.isEmpty() && !singleRouteVertex.equals(path1)) {

            displaySingle += "\n\n Single Route Detected.";
            displaySingle += "\nTake a ride from " + source.toString() + " to " + dest.toString();

//			displaySingle+="Available Routes:";
            Route singleRoute = imp.getSingleRoute();
//			displaySingle += "\n Route:" + singleRoute.getName();
//			displaySingle += "\n Vehicle Type:" + singleRoute.getVehicleType();
            for (Vertex v : singleRouteVertex) {
                // display+="\n"+v;
            }
            double d1 = imp.getRouteDistance(singleRouteVertex);

            int fare1 = imp.getRouteCost(d1);
            displaySingle += " with distance " + new DecimalFormat("#.##").format(d1) + " km";
            displaySingle += " and cost Rs. " + fare1 + "\n";
//			displaySingle += "\nRoute Distance:" + new DecimalFormat("#.##").format(d1) + "\n";
//			displaySingle += "Route Cost:" + fare1 + "\n";
            displaySingle += "Available Route:";
            displaySingle += "\n" + singleRoute.getName();
            displaySingle += "\n (" + singleRoute.getVehicleType() + ")";
        }

        // System.out.println("Path1:"+path1);
        // System.out.println("Shortest Path detected:");
        // for(int i=0;i<size;i++){
        //
        // }
        //
        // if (path != null) {
        // for (Vertex nextVertex : path) {
        // System.out.println(nextVertex.toString() + "\n");
        // display += nextVertex.toString() + "\n";
        // }
        // System.out
        // .println("\nTotal Path Cost is " + imp.getTotalDistance());
        // display += "\nTotal Path Cost is " + imp.getTotalDistance();
        // // // System.out.println("Total iteration no is " + imp.getI());
        // } else {
        // display += "No Path exists";
        // System.out.println("No Path exists");
        // }
        return display;
        // tv.setText(display);

        // }

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
                tv.setVisibility(View.INVISIBLE);
                singleRoute.setVisibility(View.INVISIBLE);
                ViewDetail.setVisibility(View.INVISIBLE);
                ViewDetailSingle.setVisibility(View.INVISIBLE);
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
            // i.putParcelableArrayListExtra("path", (ArrayList<Vertex>)
            // path);
            startActivity(i);

        } else if (v.getId() == R.id.viewSingleRoute) {
            Intent i = new Intent(getActivity().getApplicationContext(), MainActivity2.class);
            i.putExtra("data", new DataWrapper(singleRouteVertex));
            i.putExtra("flag", false);
            Log.d("data in search route", singleRouteVertex.toString());

            // i.putParcelableArrayListExtra("path", (ArrayList<Vertex>)
            // path);
            startActivity(i);
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
            pDialog.dismiss();
            tv.setVisibility(View.VISIBLE);
            ViewDetail.setVisibility(View.VISIBLE);
            tv.setText(display);
            if (!displaySingle.equals("")) {
                singleRoute.setVisibility(View.VISIBLE);
                ViewDetailSingle.setVisibility(View.VISIBLE);
                singleRoute.setText(displaySingle);
            } else {
                singleRoute.setVisibility(View.INVISIBLE);
                ViewDetailSingle.setVisibility(View.INVISIBLE);
                displaySingle = "";
            }
            // if (flag == 1)
            // Toast.makeText(MainActivity.this,
            // "Please Enter Correct informations", Toast.LENGTH_LONG)
            // .show();

        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {
                // your code goes here
                source.requestFocus();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200 && requestCode == 100) {
            String vName = data.getStringExtra("vName");
            gpsToggle.setImageResource(R.drawable.gpsselected);
            source.setText(vName);
            source.setEnabled(false);
            destination.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(destination, 0);

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

            mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            cri = new Criteria();
            provider = mLocationManager.getBestProvider(cri, false);
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                //	Toast.makeText(MainActivity.this,"Network Mode:"+LocationManager.NETWORK_PROVIDER.toString(),Toast.LENGTH_SHORT).show();
                mLocationManager.requestLocationUpdates(provider, 30000, 0,
                        mVeggsterLocationListener);

            } else {
                //	Toast.makeText(MainActivity.this,"GPS Mode",Toast.LENGTH_SHORT).show();
                mLocationManager.requestLocationUpdates(provider, 0, 0,
                        mVeggsterLocationListener);

            }


            progDailog = new ProgressDialog(getActivity());
            progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    SearchRouteFragment.FetchCordinates.this.cancel(true);
                }
            });
            progDailog.setMessage("Detecting your current location....");
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
            Queue<Vertex> sourceV = imp.getNearestStop(lati, longi);
            Vertex v;
            //source.setText(sourceV.getName());
            List<Vertex> vList = new ArrayList<Vertex>();
            for (int i = 0; i < 4; i++) {
                v = sourceV.poll();
                if (v.getDistanceFromSource() < 1.0)
                    Log.d("Polled Vertex", v.getName());
                vList.add(v);
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

                int lat = (int) location.getLatitude(); // * 1E6);
                int log = (int) location.getLongitude(); // * 1E6);
                int acc = (int) (location.getAccuracy());

                String info = location.getProvider();
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
            } else {
                destination.setAdapter(myAdapter);
            }

        }


    }


    @Override
    public void onResume() {
        destination.clearFocus();
        source.requestFocus();
        super.onResume();
    }
}

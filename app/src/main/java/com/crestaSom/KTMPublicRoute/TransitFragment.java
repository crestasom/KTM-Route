package com.crestaSom.KTMPublicRoute;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crestaSom.KTMPublicRoute.data.DataWrapper;
import com.crestaSom.database.Database;
import com.crestaSom.implementation.KtmPublicRoute;
import com.crestaSom.model.Edge;
import com.crestaSom.model.Route;
import com.crestaSom.model.RouteData;
import com.crestaSom.model.RouteDataWrapper;
import com.crestaSom.model.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransitFragment extends Fragment {
    RouteDataWrapper routeDataWrapper;
    List<Vertex> path, pathTemp;
    double[] distanceList;
    Boolean flag,flagAlt;
    TextView tv;
    TextView disp;
    LinearLayout displayTransit, dyLayout;
    String vehicleType = "";
    SharedPreferences prefs;

    public TransitFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transit, container, false);
        Bundle bundle = getArguments();

        KtmPublicRoute imp = new KtmPublicRoute(getActivity());

        displayTransit = (LinearLayout) view.findViewById(R.id.transitDetail);


        path = new ArrayList<>();


        flagAlt=bundle.getBoolean("flagAlt",false);
        String display = "";
        List<Vertex> vertexList = new ArrayList<Vertex>();
        Route r;
        int i = 0;
        int totalCost = 0;
        double totalDist = 0;
        List<Integer> routeIds = new ArrayList<Integer>();
        Map<List<Integer>, List<Vertex>> pathRoute;
        i = 1;
        int pixels;
        final float scale = this.getResources().getDisplayMetrics().density;
    if(flagAlt){
            routeDataWrapper=(RouteDataWrapper)bundle.getSerializable("data");
            RouteDataWrapper tempWrapper=new RouteDataWrapper();
        tempWrapper=routeDataWrapper;

            //display first transit

        RouteData routeData1=routeDataWrapper.getRouteData1().get(0);
        dyLayout = new LinearLayout(getActivity());
        dyLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        dyLayout.setBackgroundResource(R.drawable.rounded_layout);
        dyLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        params.setMargins(0, 0, 0, 20);
        dyLayout.setLayoutParams(params);
        display = "";

        display += "Travel 1:";
        display += routeData1.getvList().get(0)+" - "+routeData1.getvList().get((routeData1.getvList().size()-1));

        disp = new TextView(getActivity());
        disp.setText(display);
        pixels = (int) (12 * scale + 0.5f);
        disp.setTextSize(pixels);
        disp.setPadding(2, 0, 2, 0);
        //disp.setBackgroundColor(Color.WHITE);
        disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //disp.setTextAppearance(R.style.displayTextStyleBold);
        disp.setTypeface(Typeface.DEFAULT_BOLD);
        dyLayout.addView(disp);
        //displayTransit.addView(disp);
        display = "";
        disp = new TextView(getActivity());
        disp.setText("Transit Stops:");
        pixels = (int) (10 * scale + 0.5f);
        disp.setTextSize(pixels);
        disp.setTypeface(Typeface.DEFAULT_BOLD);
        //disp.setBackgroundColor(Color.WHITE);
        disp.setPadding(2, 0, 2, 0);
        disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        dyLayout.addView(disp);
        display = "";
        int cnt = 0;
        for (Vertex v : routeData1.getvList()) {
            if (!v.isTransit()) {
                display += "-> " + v.getName();
                cnt++;
                if (cnt < routeData1.getvList().size()) {
                    display += "\n";
                }
            }

        }

        disp = new TextView(getActivity());
        disp.setText(display);
        pixels = (int) (8 * scale + 0.5f);
        disp.setTextSize(pixels);
        disp.setPadding(2, 0, 2, 0);
        //disp.setBackgroundColor(Color.WHITE);
        disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        dyLayout.addView(disp);
        Log.d("temp", (i - 1) + "");
//                    Log.d("d from transit",distanceList[i - 1]+"");
        //Log.d("distance",""+distanceList[i - 1]);
            display = "";
            display += "Available Routes:";
            disp = new TextView(getActivity());
            disp.setText(display);
            disp.setPadding(2, 0, 2, 0);
            disp.setTypeface(Typeface.DEFAULT_BOLD);
            pixels = (int) (12 * scale + 0.5f);
            disp.setTextSize(pixels);
            //disp.setBackgroundColor(Color.WHITE);
            disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            dyLayout.addView(disp);

            display = "";
            display+=routeData1.getrName();
            //display += "\n";
            disp = new TextView(getActivity());
            disp.setText(display);
            pixels = (int) (8 * scale + 0.5f);
            disp.setPadding(2, 0, 2, 0);
            disp.setTextSize(pixels);
            //disp.setBackgroundColor(Color.WHITE);
            disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            //params.setMargins(0,0,0,20);
            //disp.setLayoutParams(params);
            dyLayout.addView(disp);

        displayTransit.addView(dyLayout);
//
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_down);
        animation.setDuration(1000);
        animation.setStartOffset(1000 * (i - 1));
        dyLayout.startAnimation(animation);

        //second transit

        dyLayout = new LinearLayout(getActivity());
        dyLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        dyLayout.setBackgroundResource(R.drawable.rounded_layout);
        dyLayout.setOrientation(LinearLayout.VERTICAL);

        params = new LinearLayout.LayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        params.setMargins(0, 0, 0, 20);
        dyLayout.setLayoutParams(params);

        display = "";
        routeData1=routeDataWrapper.getRouteData2().get(0);
        display += "\nTravel 2:";
        display += routeData1.getvList().get(0)+" - "+routeData1.getvList().get((routeData1.getvList().size()-1));

        disp = new TextView(getActivity());
        disp.setText(display);
        pixels = (int) (12 * scale + 0.5f);
        disp.setTextSize(pixels);
        disp.setPadding(2, 0, 2, 0);
        //disp.setBackgroundColor(Color.WHITE);
        disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //disp.setTextAppearance(R.style.displayTextStyleBold);
        disp.setTypeface(Typeface.DEFAULT_BOLD);
        dyLayout.addView(disp);
        //displayTransit.addView(disp);
        display = "";
        disp = new TextView(getActivity());
        disp.setText("Transit Stops:");
        pixels = (int) (10 * scale + 0.5f);
        disp.setTextSize(pixels);
        disp.setTypeface(Typeface.DEFAULT_BOLD);
        //disp.setBackgroundColor(Color.WHITE);
        disp.setPadding(2, 0, 2, 0);
        disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        dyLayout.addView(disp);
        display = "";
        cnt = 0;
        for (Vertex v : routeData1.getvList()) {
            if (!v.isTransit()) {
                display += "-> " + v.getName();
                cnt++;
                if (cnt < routeData1.getvList().size()) {
                    display += "\n";
                }
            }

        }

        disp = new TextView(getActivity());
        disp.setText(display);
        pixels = (int) (8 * scale + 0.5f);
        disp.setTextSize(pixels);
        disp.setPadding(2, 0, 2, 0);
        //disp.setBackgroundColor(Color.WHITE);
        disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        dyLayout.addView(disp);
        Log.d("temp", (i - 1) + "");
//                    Log.d("d from transit",distanceList[i - 1]+"");
        //Log.d("distance",""+distanceList[i - 1]);
        display = "";
        display += "Available Routes:";
        disp = new TextView(getActivity());
        disp.setText(display);
        disp.setPadding(2, 0, 2, 0);
        disp.setTypeface(Typeface.DEFAULT_BOLD);
        pixels = (int) (12 * scale + 0.5f);
        disp.setTextSize(pixels);
        //disp.setBackgroundColor(Color.WHITE);
        disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        dyLayout.addView(disp);

        display = "";
        display+=routeData1.getrName();
        //display += "\n";
        disp = new TextView(getActivity());
        disp.setText(display);
        pixels = (int) (8 * scale + 0.5f);
        disp.setPadding(2, 0, 2, 0);
        disp.setTextSize(pixels);
        //disp.setBackgroundColor(Color.WHITE);
        disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //params.setMargins(0,0,0,20);
        //disp.setLayoutParams(params);
        dyLayout.addView(disp);


        //dyLayout.startAnimation(AnimationUtils.makeInAnimation(getActivity(),false));

        displayTransit.addView(dyLayout);
//
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_down);
        animation.setDuration(1000);
        animation.setStartOffset(2000 );
        dyLayout.startAnimation(animation);



    }else {
        DataWrapper dw = (DataWrapper) bundle.getSerializable("vList");
        pathTemp = dw.getvList();
        path.addAll(pathTemp);
        flag = bundle.getBoolean("flag");
        if (!flag) {
            distanceList = new double[10];
            distanceList = bundle.getDoubleArray("distanceList");
            //Log.d("distanceList",distanceList.toString());
            while (!path.isEmpty()) {
                if (path.size() == 1) {
                    break;
                }

                pathRoute = imp.findRoutePath(path);
                Iterator<Map.Entry<List<Integer>, List<Vertex>>> it = pathRoute.entrySet().iterator();
                while (it.hasNext()) {

                    dyLayout = new LinearLayout(getActivity());
                    dyLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    dyLayout.setBackgroundResource(R.drawable.rounded_layout);
                    dyLayout.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    params.setMargins(0, 0, 0, 20);
                    dyLayout.setLayoutParams(params);
                    display = "";
                    Map.Entry<List<Integer>, List<Vertex>> pair = it.next();
                    routeIds = pair.getKey();
                    vertexList = pair.getValue();
                    // Log.d("path", vertexList.toString());
                    //double d = imp.getRouteDistance(vertexList);
                    prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    double distMin = Double.parseDouble(prefs.getString("walkingDist", "0.0"));

                    display += "Travel " + i + ": ";
                    display += vertexList.get(0) + " to " + vertexList.get(vertexList.size() - 1);

                    disp = new TextView(getActivity());
                    disp.setText(display);
                    pixels = (int) (12 * scale + 0.5f);
                    disp.setTextSize(pixels);
                    disp.setPadding(2, 0, 2, 0);
                    //disp.setBackgroundColor(Color.WHITE);
                    disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    //disp.setTextAppearance(R.style.displayTextStyleBold);
                    disp.setTypeface(Typeface.DEFAULT_BOLD);
                    dyLayout.addView(disp);
                    //displayTransit.addView(disp);
                    display = "";
                    disp = new TextView(getActivity());
                    disp.setText("Transit Stops:");
                    pixels = (int) (10 * scale + 0.5f);
                    disp.setTextSize(pixels);
                    disp.setTypeface(Typeface.DEFAULT_BOLD);
                    //disp.setBackgroundColor(Color.WHITE);
                    disp.setPadding(2, 0, 2, 0);
                    disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    dyLayout.addView(disp);
                    display = "";
                    int cnt = 0;
                    for (Vertex v : vertexList) {
                        if (!v.isTransit()) {
                            display += "-> " + v.getName();
                            cnt++;
                            if (cnt < vertexList.size()) {
                                display += "\n";
                            }
                        }

                    }

                    disp = new TextView(getActivity());
                    disp.setText(display);
                    pixels = (int) (8 * scale + 0.5f);
                    disp.setTextSize(pixels);
                    disp.setPadding(2, 0, 2, 0);
                    //disp.setBackgroundColor(Color.WHITE);
                    disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    dyLayout.addView(disp);
                    Log.d("temp", (i - 1) + "");
//                    Log.d("d from transit",distanceList[i - 1]+"");
                    //Log.d("distance",""+distanceList[i - 1]);
                    if (distMin < distanceList[i - 1]) {
                        display = "";
                        display += "Available Routes:";
                        disp = new TextView(getActivity());
                        disp.setText(display);
                        disp.setPadding(2, 0, 2, 0);
                        disp.setTypeface(Typeface.DEFAULT_BOLD);
                        pixels = (int) (12 * scale + 0.5f);
                        disp.setTextSize(pixels);
                        //disp.setBackgroundColor(Color.WHITE);
                        disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        dyLayout.addView(disp);

                        display = "";
                        cnt = 0;
                        for (int z : routeIds) {

                            r = imp.getRoute(z);
                            display += "-> " + r.getName();
                            display += " (" + r.getVehicleType() + ")";
                            cnt++;
                            if (cnt < routeIds.size()) {
                                display += "\n";
                            }
                        }
                        //display += "\n";
                        disp = new TextView(getActivity());
                        disp.setText(display);
                        pixels = (int) (8 * scale + 0.5f);
                        disp.setPadding(2, 0, 2, 0);
                        disp.setTextSize(pixels);
                        //disp.setBackgroundColor(Color.WHITE);
                        disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        //params.setMargins(0,0,0,20);
                        //disp.setLayoutParams(params);
                        dyLayout.addView(disp);

                    }
                    i++;
                    //dyLayout.startAnimation(AnimationUtils.makeInAnimation(getActivity(),false));

                    displayTransit.addView(dyLayout);
//
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_down);
                    animation.setDuration(1000);
                    animation.setStartOffset(1000 * (i - 1));
                    dyLayout.startAnimation(animation);


                }
            }
//        display += "\nTotal Distance:" + new DecimalFormat("#.##").format(totalDist) + " km";
//        display += "\nTotal Cost:Rs. " + totalCost;
        } else {
            dyLayout = new LinearLayout(getActivity());
            dyLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            dyLayout.setBackgroundResource(R.drawable.rounded_layout);
            dyLayout.setOrientation(LinearLayout.VERTICAL);
            vehicleType = bundle.getString("vehicleType");
            display += "Vehicle Type: " + vehicleType;
            disp = new TextView(getActivity());
            disp.setText(display);
            pixels = (int) (12 * scale + 0.5f);
            disp.setTextSize(pixels);
            disp.setPadding(2, 0, 2, 0);
            // disp.setBackgroundColor(Color.WHITE);
            disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            //disp.setTextAppearance(R.style.displayTextStyleBold);
            disp.setTypeface(Typeface.DEFAULT_BOLD);
            dyLayout.addView(disp);
            display = "";


            display += "Transit Stops:";
            disp = new TextView(getActivity());
            disp.setText(display);
            pixels = (int) (12 * scale + 0.5f);
            disp.setTextSize(pixels);
            //disp.setBackgroundColor(Color.WHITE);
            disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            //disp.setTextAppearance(R.style.displayTextStyleBold);
            disp.setTypeface(Typeface.DEFAULT_BOLD);
            dyLayout.addView(disp);
            display = "";
            int cnt = 0;
            for (Vertex v : path) {
                if (!v.isTransit()) {
                    display += "-> " + v.getName();
                    cnt++;
                    if (cnt < path.size()) {
                        display += "\n";
                    }
                }
            }
            disp = new TextView(getActivity());
            disp.setText(display);
            pixels = (int) (8 * scale + 0.5f);
            disp.setTextSize(pixels);
            //disp.setBackgroundColor(Color.WHITE);
            disp.setPadding(2, 0, 2, 0);
            disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            dyLayout.addView(disp);
            display = "";
            displayTransit.addView(dyLayout);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
            animation.setDuration(1000);
            // animation.setStartOffset(1000*(i-1));
            dyLayout.startAnimation(animation);
        }
    }

//        tv.setText(display);

        return view;
    }

}

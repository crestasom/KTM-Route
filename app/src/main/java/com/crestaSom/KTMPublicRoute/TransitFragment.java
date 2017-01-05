package com.crestaSom.KTMPublicRoute;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crestaSom.implementation.KtmPublicRoute;
import com.crestaSom.model.Route;
import com.crestaSom.model.Vertex;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransitFragment extends Fragment {
    List<Vertex> path,pathTemp;
    Boolean flag;
    TextView tv;
    TextView disp;
    LinearLayout displayTransit;
    String vehicleType="";

    public TransitFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transit, container, false);
        Bundle bundle = getArguments();
        //tv = (TextView) view.findViewById(R.id.displayFragmentText);
        KtmPublicRoute imp = new KtmPublicRoute(getActivity());
        displayTransit=(LinearLayout)view.findViewById(R.id.transitDetail);
        DataWrapper dw = (DataWrapper) bundle.getSerializable("vList");
        pathTemp = dw.getvList();
        path=new ArrayList<>();
        path.addAll(pathTemp);
        flag = bundle.getBoolean("flag");
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
        if (!flag) {
            while (!path.isEmpty()) {
                if (path.size() == 1) {
                    break;
                }
                pathRoute = imp.findRoutePath(path);
                Iterator<Map.Entry<List<Integer>, List<Vertex>>> it = pathRoute.entrySet().iterator();
                while (it.hasNext()) {
                    display="";
                    Map.Entry<List<Integer>, List<Vertex>> pair = it.next();
                    routeIds = pair.getKey();
                    vertexList = pair.getValue();
                    double d = imp.getRouteDistance(vertexList);
                    display += "Travel " + i+": ";
                    display += vertexList.get(0) + " to " + vertexList.get(vertexList.size() - 1);

                    disp=new TextView(getActivity());
                    disp.setText(display);
                    pixels = (int) (12 * scale + 0.5f);
                    disp.setTextSize(pixels);
                    disp.setBackgroundColor(Color.WHITE);
                    disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    //disp.setTextAppearance(R.style.displayTextStyleBold);
                    disp.setTypeface(Typeface.DEFAULT_BOLD);
                    displayTransit.addView(disp);
                    display="";

                    disp=new TextView(getActivity());
                    disp.setText("Transit Stops:");
                    pixels = (int) (10 * scale + 0.5f);
                    disp.setTextSize(pixels);
                    disp.setTypeface(Typeface.DEFAULT_BOLD);
                    disp.setBackgroundColor(Color.WHITE);
                    disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    displayTransit.addView(disp);
                    display="";
                    int cnt=0;
                    for (Vertex v : vertexList) {
                        display += "-> "+v.getName() ;
                        cnt++;
                        if(cnt<vertexList.size()){
                            display+="\n";
                        }

                    }
                    disp=new TextView(getActivity());
                    disp.setText(display);
                    pixels = (int) (8 * scale + 0.5f);
                    disp.setTextSize(pixels);
                    disp.setBackgroundColor(Color.WHITE);
                    disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    displayTransit.addView(disp);

                    int fare = imp.getRouteCost(d);
                    totalCost += fare;
                    totalDist += d;
                    display="";
                    display += "Available Routes:";
                    disp=new TextView(getActivity());
                    disp.setText(display);
                    disp.setTypeface(Typeface.DEFAULT_BOLD);
                    pixels = (int) (12 * scale + 0.5f);
                    disp.setTextSize(pixels);
                    disp.setBackgroundColor(Color.WHITE);
                    disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    displayTransit.addView(disp);

                    display="";
                    cnt=0;
                    for (int z : routeIds) {

                        r = imp.getRoute(z);
                        display += "-> "+r.getName();
                        display += " (" + r.getVehicleType() + ")";
                        cnt++;
                        if(cnt<routeIds.size()){
                            display+="\n";
                        }
                    }
                    //display += "\n";
                    disp=new TextView(getActivity());
                    disp.setText(display);
                    pixels = (int) (8 * scale + 0.5f);
                    disp.setTextSize(pixels);
                    disp.setBackgroundColor(Color.WHITE);
                    disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    params.setMargins(0,0,0,20);
                    disp.setLayoutParams(params);
                    displayTransit.addView(disp);

                    i++;



                }
            }
//        display += "\nTotal Distance:" + new DecimalFormat("#.##").format(totalDist) + " km";
//        display += "\nTotal Cost:Rs. " + totalCost;
        } else {
            System.out.println(path);
            vehicleType = bundle.getString("vehicleType");
            display += "Vehicle Type: "+vehicleType;
            disp=new TextView(getActivity());
            disp.setText(display);
            pixels = (int) (12 * scale + 0.5f);
            disp.setTextSize(pixels);
            disp.setBackgroundColor(Color.WHITE);
            disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            //disp.setTextAppearance(R.style.displayTextStyleBold);
            disp.setTypeface(Typeface.DEFAULT_BOLD);
            displayTransit.addView(disp);
            display="";


            display += "Transit Stops:";
            disp=new TextView(getActivity());
            disp.setText(display);
            pixels = (int) (12 * scale + 0.5f);
            disp.setTextSize(pixels);
            disp.setBackgroundColor(Color.WHITE);
            disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            //disp.setTextAppearance(R.style.displayTextStyleBold);
            disp.setTypeface(Typeface.DEFAULT_BOLD);
            displayTransit.addView(disp);
            display="";
            int cnt=0;
            for (Vertex v : path) {
                display += "-> "+v.getName();
                cnt++;
                if(cnt<path.size()){
                    display+="\n";
                }
            }
            disp=new TextView(getActivity());
            disp.setText(display);
            pixels = (int) (8 * scale + 0.5f);
            disp.setTextSize(pixels);
            disp.setBackgroundColor(Color.WHITE);
            disp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            displayTransit.addView(disp);
            display="";

        }

//        tv.setText(display);

        return view;
    }

}

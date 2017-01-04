package com.crestaSom.KTMPublicRoute;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    List<Vertex> path;
    Boolean flag;
    TextView tv;

    public TransitFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transit, container, false);
        Bundle bundle = getArguments();
        tv = (TextView) view.findViewById(R.id.displayFragmentText);
        KtmPublicRoute imp = new KtmPublicRoute(getActivity());
        DataWrapper dw = (DataWrapper) bundle.getSerializable("vList");
        path = dw.getvList();
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


        if (!flag) {
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
                    double d = imp.getRouteDistance(vertexList);
                    display += "Travel " + i+"\n";
                    display += vertexList.get(0) + " to " + vertexList.get(vertexList.size() - 1) + "\n";
                    for (Vertex v : vertexList) {
                        display += v.getName() + "\n";
                    }
                    int fare = imp.getRouteCost(d);
                    totalCost += fare;
                    totalDist += d;
                    display += "Available Routes:";
                    for (int z : routeIds) {

                        r = imp.getRoute(z);
                        display += "\n" + r.getName();
                        display += "\n(" + r.getVehicleType() + ")";
                    }

                    i++;

                    display += "\n\n";

                }
            }
//        display += "\nTotal Distance:" + new DecimalFormat("#.##").format(totalDist) + " km";
//        display += "\nTotal Cost:Rs. " + totalCost;
        } else {
            System.out.println(path);
            String rName = bundle.getString("routeName");
            display += "Transit Stops:"+ "\n";
            for (Vertex v : path) {
                display += v.getName() + "\n";
            }

        }

        tv.setText(display);

        return view;
    }

}

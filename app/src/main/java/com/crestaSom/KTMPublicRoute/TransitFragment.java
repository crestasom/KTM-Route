package com.crestaSom.KTMPublicRoute;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crestaSom.KTMPublicRoute.data.DataWrapper;
import com.crestaSom.database.Database;
import com.crestaSom.implementation.KtmPublicRoute;
import com.crestaSom.model.Edge;
import com.crestaSom.model.Route;
import com.crestaSom.model.RouteData;
import com.crestaSom.model.RouteDataWrapper;
import com.crestaSom.model.Vertex;

import java.text.DecimalFormat;
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
    int language;

    final static String[] nepaliNum = {"०", "१", "२", "३", "४", "५", "६", "७", "८", "९"};
    RouteDataWrapper routeDataWrapper;
    List<Vertex> path, pathTemp;
    double[] distanceList;
    Boolean flag, flagAlt;
    int mark1,mark2,mark3,mark4;
    TextView tv;
    TextView disp;
    LinearLayout displayTransit, dyLayout;
    String vehicleType = "";
    SharedPreferences prefs;
    int textColor;
    public TransitFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transit, container, false);
        Bundle bundle = getArguments();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        language = Integer.parseInt(prefs.getString("language", "1"));
        KtmPublicRoute imp = new KtmPublicRoute(getActivity());
        //Toast.makeText(getActivity(),language+"",Toast.LENGTH_LONG).show();
        displayTransit = (LinearLayout) view.findViewById(R.id.transitDetail);


        path = new ArrayList<>();


        flagAlt = bundle.getBoolean("flagAlt", false);
        String display = "";
        List<Vertex> vertexList = new ArrayList<Vertex>();
        textColor = getResources().getColor(R.color.colorPrimaryDark);
        Route r;
        int i = 0;
        int totalCost = 0;
        double totalDist = 0;
        List<Integer> routeIds = new ArrayList<Integer>();
        Map<List<Integer>, List<Vertex>> pathRoute;
        i = 1;
        int pixels;
        final float scale = this.getResources().getDisplayMetrics().density;
        if (flagAlt) {
            routeDataWrapper = (RouteDataWrapper) bundle.getSerializable("data");
            RouteDataWrapper tempWrapper = new RouteDataWrapper();
            tempWrapper = routeDataWrapper;

            //display first transit

            RouteData routeData1 = routeDataWrapper.getRouteData1().get(0);
            dyLayout = new LinearLayout(getActivity());
            dyLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            dyLayout.setBackgroundResource(R.drawable.rounded_layout);
            dyLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            params.setMargins(0, 0, 0, 20);
            dyLayout.setLayoutParams(params);
            display = "";

            if (language == 1) {
                display += "Travel " + 1 + ": ";
                display += routeData1.getvList().get(0) + " to " + routeData1.getvList().get((routeData1.getvList().size() - 1));
            }else {
                display += "यात्रा " + convertNepali(1) + ": ";
                display += routeData1.getvList().get(0).getNameNepali() + " देखी " + routeData1.getvList().get((routeData1.getvList().size() - 1)).getNameNepali();
            }

            addTextView(new SpannableString(display), dyLayout, 24, true, textColor);
            display="";
            if(language==1)
            display = "Transit Stops:";
            else
                display = "बिचमा आउने स्टपहरु:";
            addTextView(new SpannableString(display), dyLayout, 20, true, textColor);

            display = getVertexList(routeData1.getvList(),language);
            addTextView(new SpannableString(display), dyLayout, 16, false, textColor);
            if (language == 1) {
                display = "Available Route:";
            } else {
                display = "उपलब्ध रुटहरु:";
            }
            addTextView(new SpannableString(display), dyLayout, 24, true, textColor);
            display = "";
            if(language==1)
            display += routeData1.getrName();
            else
                display += routeData1.getrNameNepali();
            addTextView(new SpannableString(display), dyLayout, 16, false, textColor);
            displayTransit.addView(dyLayout);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_down);
            animation.setDuration(1000);
            dyLayout.startAnimation(animation);

            //second transit

            routeData1 = routeDataWrapper.getRouteData2().get(0);

            dyLayout = new LinearLayout(getActivity());
            dyLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            dyLayout.setBackgroundResource(R.drawable.rounded_layout);
            dyLayout.setOrientation(LinearLayout.VERTICAL);
            params = new LinearLayout.LayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            params.setMargins(0, 0, 0, 20);
            dyLayout.setLayoutParams(params);
            display = "";

            if (language == 1)
                display += "Travel " + 2;
            else
                display += "यात्रा " + convertNepali(2);
            display += routeData1.getvList().get(0) + " - " + routeData1.getvList().get((routeData1.getvList().size() - 1));
            addTextView(new SpannableString(display), dyLayout, 24, true, textColor);
            display="";
            if(language==1)
                display = "Transit Stops:";
            else
                display = "बिचमा आउने स्टपहरु:";
            addTextView(new SpannableString(display), dyLayout, 20, true, textColor);
            display = getVertexList(routeData1.getvList(),language);
            addTextView(new SpannableString(display), dyLayout, 16, false, textColor);
            if (language == 1) {
                display = "Available Route:";
            } else {
                display = "उपलब्ध रुटहरु:";
            }
            addTextView(new SpannableString(display), dyLayout, 24, true, textColor);
            display = "";
            if(language==1)
                display += routeData1.getrName();
            else
                display += routeData1.getrNameNepali();
            addTextView(new SpannableString(display), dyLayout, 16, false, textColor);
            displayTransit.addView(dyLayout);
//
            animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_down);
            animation.setDuration(1000);
            animation.setStartOffset(1000);
            dyLayout.startAnimation(animation);


        } else {
            DataWrapper dw = (DataWrapper) bundle.getSerializable("vList");
            pathTemp = dw.getvList();
            path.addAll(pathTemp);
            flag = bundle.getBoolean("flag");
            if (!flag) {
                distanceList = new double[10];
                distanceList = bundle.getDoubleArray("distanceList");
                ////Log.d("distanceList",distanceList.toString());
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

                        double distMin = Double.parseDouble(prefs.getString("walkingDist", "0.0"));
                        if (language == 1) {
                            display += "Travel " + i + ": ";
                            display += vertexList.get(0) + " to " + vertexList.get(vertexList.size() - 1);
                        }else {
                            display += "यात्रा " + convertNepali(i) + ": ";
                            display += vertexList.get(0).getNameNepali() + " देखी " + vertexList.get(vertexList.size() - 1).getNameNepali();
                        }
                        addTextView(new SpannableString(display), dyLayout, 24, true, textColor);

                        display = "";
                        if(language==1)
                            display = "Transit Stops:";
                        else
                            display = "बिचमा आउने स्टपहरु:";
                        addTextView(new SpannableString(display), dyLayout, 20, true, textColor);
                        display = "";
                        int cnt = 0;
                        display = getVertexList(vertexList,language);
                        addTextView(new SpannableString(display), dyLayout, 16, false, textColor);
                        if (distMin < distanceList[i - 1]) {
                            display = "";
                            if (language == 1) {
                                display = "Available Route:";
                            } else {
                                display = "उपलब्ध रुटहरु:";
                            }
                            addTextView(new SpannableString(display), dyLayout, 24, true, textColor);
                            display = "";
                            cnt = 0;
                            for (int z : routeIds) {
                                r = imp.getRoute(z);
                                if(language==1) {
                                    display += "-> " + r.getName();
                                    display += " (" + r.getVehicleType() + ")";
                                }else{
                                    display += "-> " + r.getNameNepali();
                                    display += " (" + r.getVehicleTypeNepali() + ")";
                                }
                                cnt++;
                                if (cnt < routeIds.size()) {
                                    display += "\n";
                                }
                            }

                            addTextView(new SpannableString(display), dyLayout, 20, false, textColor);

                        }
                        i++;

                        displayTransit.addView(dyLayout);
                        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_down);
                        animation.setDuration(1000);
                        animation.setStartOffset(1000 * (i - 1));
                        dyLayout.startAnimation(animation);


                    }
                }
            } else {
                dyLayout = new LinearLayout(getActivity());
                dyLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                dyLayout.setBackgroundResource(R.drawable.rounded_layout);
                dyLayout.setOrientation(LinearLayout.VERTICAL);
                vehicleType = bundle.getString("vehicleType");
                if(language==1)
                display += "Vehicle Type: " + vehicleType;
                else
                    display += "सवारीसाधनको प्रकार: " + vehicleType;
                addTextView(new SpannableString(display), dyLayout, 20, true, textColor);
                display = "";

                if(language==1)
                    display = "Transit Stops:";
                else
                    display = "बिचमा आउने स्टपहरु:";
                addTextView(new SpannableString(display), dyLayout, 20, true, textColor);
                display = "";
                int cnt = 0;
                display=getVertexList(path,language);
                addTextView(new SpannableString(display), dyLayout, 16, false, textColor);
                display = "";
                displayTransit.addView(dyLayout);
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
                animation.setDuration(1000);
                // animation.setStartOffset(1000*(i-1));
                dyLayout.startAnimation(animation);
            }
        }
        return view;
    }

    private String getVertexList(List<Vertex> path, int language) {
        String display="";
        int cnt=0;
        if(language==1) {
            for (Vertex v : path) {
                cnt++;
                if (!v.isTransit()) {
                    display += "-> " + v.getName();

                    if (cnt < path.size()) {
                        display += "\n";
                    }
                }


            }
        }else{
            for (Vertex v : path) {
                cnt++;
                if (!v.isTransit()) {
                    display += "-> " + v.getNameNepali();

                    if (cnt < path.size()) {
                        display += "\n";
                    }
                }


            }
        }
        return display;
    }


    /***
     * To add a text view to given linearlayout
     *
     * @param SpannableString displayText
     * @param LinearLayout    parentLayout
     * @param int             textSize
     * @param Boolean         isBold
     * @param int             textColor
     * @return
     */
    public TextView addTextView(SpannableString displayText, LinearLayout parentLayout, int textSize, Boolean isBold, int textColor) {
        TextView displayView = new TextView(getActivity());
        displayView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        if (isBold)
            displayView.setTypeface(Typeface.DEFAULT_BOLD);
        displayView.setText(displayText);
        displayView.setTextColor(textColor);
        displayView.setPadding(0, 2, 0, 2);
        parentLayout.addView(displayView);

        return displayView;
    }

    public static String convertNumberToNepali(double num) {
        String numInNep = "";
        String temp = String.valueOf(num);
        String temp1 = "";
        System.out.println(temp);
        for (int i = 0; i < temp.length(); i++) {
            temp1 = String.valueOf(temp.charAt(i));
            if (temp1.equals(".")) {
                numInNep += temp1;
            } else {
                numInNep += convertNepali(Integer.parseInt(temp1));
            }

        }
        return numInNep;

    }

    public static String convertNumberToNepali(String num) {
        String numInNep = "";
        //String temp=String.valueOf(num);
        String temp1 = "";
        for (int i = 0; i < num.length(); i++) {
            temp1 = String.valueOf(num.charAt(i));
            if (temp1.equals(".")) {
                numInNep += temp1;
            } else {
                numInNep += convertNepali(Integer.parseInt(temp1));
            }

        }
        return numInNep;

    }

    public static String convertNumberToNepali(int num) {
        String numInNep = "";
        String temp = String.valueOf(num);
        String temp1 = "";
        for (int i = 0; i < temp.length(); i++) {
            temp1 = String.valueOf(temp.charAt(i));

            numInNep += convertNepali(Integer.parseInt(temp1));

        }
        return numInNep;
    }

    public static String convertNepali(int n) {
        String s = "";
        //System.out.println("sdfa:"+n);
        s = nepaliNum[n];
        return s;
    }


    public SpannableString displayTravelText(List<Vertex> vertexList, double dst, int fareL, boolean isWalk, int lang) {
        String dsply = "";
        SpannableString displayTravelL = null;
        if (!isWalk) {
            dsply = "";
            if (lang == 1) {
                dsply += "Take a ride from ";
                mark1 = dsply.length();
                dsply += vertexList.get(0);
                mark2 = dsply.length();
                dsply += " to ";
                mark3 = dsply.length();
                dsply += vertexList.get(vertexList.size() - 1);
                mark4 = dsply.length();
                dsply += " with distance " + new DecimalFormat("#.##").format(dst) + " km";
                dsply += " and cost Rs." + fareL + ".";
                dsply += "\n";
            } else if (lang == 2) {
                mark1 = dsply.length();
                dsply += vertexList.get(0);
                mark2 = dsply.length();
                dsply += " देखी ";
                mark3 = dsply.length();
                dsply += vertexList.get(vertexList.size() - 1);
                mark4 = dsply.length();
                dsply += " सम्म यात्रा गर्नुहोस।";
                dsply += "\nदुरी: " + convertNumberToNepali(new DecimalFormat("#.##").format(dst)) + " कि.मी.";
                dsply += "\nभाडा रु. " + convertNumberToNepali(fareL);
            }

        } else {
            if (lang == 1) {
                dsply += "Walk from ";
                mark1 = dsply.length();
                dsply += vertexList.get(0);
                mark2 = dsply.length();
                dsply += " to ";
                mark3 = dsply.length();
                dsply += vertexList.get(vertexList.size() - 1);
                mark4 = dsply.length();
                dsply += " with distance " + new DecimalFormat("#.##").format(dst) + " km";
            } else if (lang == 2) {
                mark1 = dsply.length();
                dsply += vertexList.get(0);
                mark2 = dsply.length();
                dsply += " देखी ";
                mark3 = dsply.length();
                dsply += vertexList.get(vertexList.size() - 1);
                mark4 = dsply.length();
                dsply += " सम्म हिंड्नुस।";
                dsply += "\nदुरी: " + convertNumberToNepali(new DecimalFormat("#.##").format(dst)) + " कि.मी.";
            }
        }
        displayTravelL = new SpannableString(dsply);
        displayTravelL.setSpan(new StyleSpan(Typeface.BOLD), mark1, mark2, 0);
        displayTravelL.setSpan(new StyleSpan(Typeface.BOLD), mark3, mark4, 0);
        return displayTravelL;
    }


}

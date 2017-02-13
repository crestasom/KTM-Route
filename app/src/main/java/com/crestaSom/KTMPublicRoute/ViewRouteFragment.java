package com.crestaSom.KTMPublicRoute;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.crestaSom.KTMPublicRoute.data.DataWrapper;
import com.crestaSom.autocomplete.CustomAutoCompleteView;
import com.crestaSom.database.Database;
import com.crestaSom.implementation.KtmPublicRoute;
import com.crestaSom.model.Route;
import com.crestaSom.model.Vertex;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewRouteFragment extends Fragment {
    ListView routeList;
    List<String> routeName;
    List<Integer> routeId;
    List<Route> routes,routesTemp;
    Database db;
    KtmPublicRoute imp;
    ImageView clearSearchPlace;
    ArrayAdapter<String> ar;
    TextView dp;
    public CustomAutoCompleteView searchPlace;
    public ArrayAdapter<String> myAdapter;
    public List<String> item = new ArrayList<String>();
    public List<Integer> itemId = new ArrayList<>();
    SharedPreferences prefs;
    int language,intLang=-1;


    public ViewRouteFragment() {
        // Required empty public constructor
    }




    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            if (getView() != null) {
                // your code goes here

                InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                  imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                language=Integer.parseInt(prefs.getString("language", "1"));
                Log.d("lang and intlan",language+""+intLang);
                if(intLang==-1)
                    intLang=language;
                else if(intLang!=language){
                    ar.notifyDataSetChanged();
                    routeName.clear();
                    routeId.clear();
                    for(Route r:routes){
                        if(language==1) {
                            routeName.add(r.getName());

                        }else {
                            routeName.add(r.getNameNepali());

                        }
                        routeId.add(r.getId());

                    }
                    intLang=language;
                    setDisplayTextViewText();
                    ar = new ArrayAdapter<String>(getActivity(),
                            R.layout.mytextview, routeName);
                    routeList.setAdapter(ar);
                }

                //imm.showSoftInput(source,0);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        language=Integer.parseInt(prefs.getString("language", "1"));
        Log.d("lang and intlan",language+""+intLang);
        if(intLang==-1)
            intLang=language;
        else if(intLang!=language){
            ar.notifyDataSetChanged();
            routeName.clear();
            routeId.clear();
            for(Route r:routes){
                if(language==1) {
                    routeName.add(r.getName());

                }else {
                    routeName.add(r.getNameNepali());

                }
                routeId.add(r.getId());

            }
            intLang = language;
            setDisplayTextViewText();
            ar = new ArrayAdapter<String>(getActivity(),
                    R.layout.mytextview, routeName);
            routeList.setAdapter(ar);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_view_route, container, false);
        routeList=(ListView)view.findViewById(R.id.list);
        searchPlace=(CustomAutoCompleteView)view.findViewById(R.id.searchPlace);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        dp=(TextView)view.findViewById(R.id.textView1);
        intLang=language = Integer.parseInt(prefs.getString("language", "1"));
        clearSearchPlace=(ImageView)view.findViewById(R.id.clearSearchPlace);
        clearSearchPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPlace.setText("");
                routeName.clear();
                routeId.clear();
                for(Route r:routes){
                    if(language==1)
                    routeName.add(r.getName());
                    else
                        routeName.add(r.getNameNepali());
                    routeId.add(r.getId());
                }
                routesTemp.clear();
                routesTemp.addAll(routes);
                ar.notifyDataSetChanged();
                ar = new ArrayAdapter<String>(getActivity(),
                        R.layout.mytextview, routeName);
                routeList.setAdapter(ar);
                clearSearchPlace.setVisibility(View.INVISIBLE);
                searchPlace.setError(null);

            }
        });
        myAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, item);
        // autocompletetextview is in activity_main.xml

        searchPlace.setDropDownBackgroundResource(R.color.dropDownBackground);
        //source.setTextColor(Color.BLUE);
        //source.setDropDownBackgroundResource(R.color.colorPrimary);

        // add the listener so it will tries to suggest while the user types
        searchPlace.addTextChangedListener(new CustomAutoCompleteTextChangedListener(getActivity().getApplicationContext(), searchPlace.getId()));
        searchPlace.setAdapter(myAdapter);
        searchPlace.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                //srcId = -1;
                return false;
            }
        });
        // locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
        // 0, 0, this);
        searchPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateRouteList(searchPlace.getText().toString());
                //destination.requestFocus();


            }
        });


        db=new Database(getActivity());
        routes=db.getAllRoute();

        imp=new KtmPublicRoute(getActivity());
        routeId=new ArrayList<Integer>();
        routeName=new ArrayList<String>();

        for(Route r:routes){
            if(language==1)
            routeName.add(r.getName());
            else
                routeName.add(r.getNameNepali());
            routeId.add(r.getId());
        }
        routesTemp=new ArrayList<>();
        routesTemp.addAll(routes);
        ar=new ArrayAdapter<String>(getActivity(),
                R.layout.mytextview,routeName);
        routeList.setAdapter(ar);

        routeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                List<Integer> vList=new ArrayList<Integer>();
                List<Vertex> vLists=new ArrayList<Vertex>();
                vList=routesTemp.get(position).getAllVertexes();
                Vertex v=null;
                for(int idr:vList){
                    v=db.getVertex(idr);
                    vLists.add(v);
                }
                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra("data", new DataWrapper(vLists));
                i.putExtra("flag", true);
                i.putExtra("routeName", routes.get(position).getName());
                if(language==1)
                i.putExtra("vehicleType", routes.get(position).getVehicleType());
                else
                    i.putExtra("vehicleType", routes.get(position).getVehicleTypeNepali());
                startActivity(i);

            }
        });
        setDisplayTextViewText();
        return view;
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
            if (searchPlace.getText().toString().equals("")) {
            clearSearchPlace.setVisibility(View.INVISIBLE);
            }else{
                clearSearchPlace.setVisibility(View.VISIBLE);
            }
            item.clear();
            itemId.clear();
            if (vertexes.size() == 0) {
            searchPlace.setError("No suggestion found");

            } else {
                for (Vertex v : vertexes) {
                    if(language==1)
                        item.add(v.getName());
                    else{
                        item.add(v.getNameNepali());
                    }

                }
            }

            // update the adapater

            // mainActivity.myAdapter.
            myAdapter.notifyDataSetChanged();
            myAdapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, item);
            searchPlace.setAdapter(myAdapter);
           /* if (id == source.getId()) {
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
            }*/

        }


    }


    public List<Vertex> getItemsFromDb(String searchTerm) {

        // add items on the array dynamically
        Database db = new Database(getActivity());
        List<Vertex> vertexes = new ArrayList<Vertex>();
        ////Log.d("Database", db.toString());
        vertexes = db.getVertexUsingQuery(searchTerm);
        itemId.clear();
        for (Vertex v : vertexes) {
            itemId.add(v.getId());
        }
        return vertexes;

    }

    public void updateRouteList(String searchPlace){
            List<Route> routeListPlace=imp.findRoutePlace(searchPlace);
        routeName.clear();
        routeId.clear();
        for(Route r:routeListPlace){
            if(language==1)
            routeName.add(r.getName());
            else
                routeName.add(r.getNameNepali());
            routeId.add(r.getId());
        }
        routesTemp.clear();
        routesTemp.addAll(routeListPlace);
        ar.notifyDataSetChanged();
        ar = new ArrayAdapter<String>(getActivity(),
                R.layout.mytextview, routeName);
        routeList.setAdapter(ar);

    }


    public void setDisplayTextViewText(){
        if(language==1){
            searchPlace.setHint("Search for a place here");
            dp.setText("List of Routes Available");

        }else{
            searchPlace.setHint("ठाउँ खोज्नुहोस्");
            dp.setText("उपलब्ध रुटहरु");
        }
    }

}

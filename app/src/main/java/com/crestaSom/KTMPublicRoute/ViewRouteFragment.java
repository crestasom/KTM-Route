package com.crestaSom.KTMPublicRoute;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.crestaSom.database.Database;
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
    List<Route> routes;
    Database db;

    public ViewRouteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_new, container, false);
        routeList=(ListView)view.findViewById(R.id.list);
        db=new Database(getActivity());
        routes=db.getAllRoute();
        routeId=new ArrayList<Integer>();
        routeName=new ArrayList<String>();
        for(Route r:routes){
            routeName.add(r.getName());
            routeId.add(r.getId());
        }
        ArrayAdapter<String> ar=new ArrayAdapter<String>(getActivity(),
                R.layout.mytextview,routeName);
        routeList.setAdapter(ar);

        routeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                // source.postDelayed(new Runnable() {
                // @Override
                // public void run() {
                // source.showDropDown();
                // }
                // },100);
                // source.setText(source.getText().toString());
                // source.setSelection(source.getText().length());
                //source.dismissDropDown();
                List<Integer> vList=new ArrayList<Integer>();
                List<Vertex> vLists=new ArrayList<Vertex>();
                vList=routes.get(position).getAllVertexes();
                Vertex v=null;
                for(int idr:vList){
                    v=db.getVertex(idr);
                    vLists.add(v);
                }
                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra("data", new DataWrapper(vLists));
                i.putExtra("flag", true);
                i.putExtra("routeName", routes.get(position).getName());
                // i.putParcelableArrayListExtra("path", (ArrayList<Vertex>) path);
                startActivity(i);
//							int rid = routeId.get(position);
//							Toast.makeText(getApplicationContext(),
//									""+rid,
//									Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

}

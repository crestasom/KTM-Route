package com.crestaSom.KTMPublicRoute;

import java.util.ArrayList;
import java.util.List;


import com.crestaSom.database.Database;
import com.crestaSom.model.Route;
import com.crestaSom.model.Vertex;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RouteList extends ListActivity implements OnClickListener {

	ListView routeList;
	List<String> routeName;
	List<Integer> routeId;
	List<Route> routes;
	Database db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_complete1);
		db=new Database(this);
		routes=db.getAllRoute();
		routeId=new ArrayList<Integer>();
		routeName=new ArrayList<String>();
		for(Route r:routes){
			routeName.add(r.getName());
			routeId.add(r.getId());
		}
		routeList=getListView();
		//routeList=(ListView)findViewById(R.id.li);
		ArrayAdapter<String> ar=new ArrayAdapter<String>(this,
				R.layout.mytextview,routeName);
				 setListAdapter(new ArrayAdapter<String>(this,
						 R.layout.mytextview,
				 routeName));
				

				 routeList.setAdapter(ar);
		//registerForContextMenu(getListView());
				 routeList.setOnItemClickListener(new OnItemClickListener() {

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
							Intent i = new Intent(getApplicationContext(), MainActivity2.class);
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
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
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
		
		
	}
	
	
	

}
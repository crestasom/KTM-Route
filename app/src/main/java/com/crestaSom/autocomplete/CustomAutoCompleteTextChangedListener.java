package com.crestaSom.autocomplete;

import java.util.ArrayList;
import java.util.List;

import com.crestaSom.KTMPublicRoute.HomeFragment;
import com.crestaSom.KTMPublicRoute.MainActivity;
import com.crestaSom.KTMPublicRoute.Welcome;
import com.crestaSom.database.Database;
import com.crestaSom.model.Vertex;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;

public class CustomAutoCompleteTextChangedListener implements TextWatcher {
	
	
	

	public static final String TAG = "CustomAutoCompleteTextChangedListener.java";
	Context context;
	int id;

	public CustomAutoCompleteTextChangedListener(Context context, int id) {
		this.context = context;
		this.id=id;
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
		HomeFragment mainActivity = new HomeFragment();
		List<Vertex> vertexes =getItemsFromDb(userInput
				.toString());
		// query the database based on the user input

		// if(size==0){
		// item
		// }
		mainActivity.item.clear();
		mainActivity.itemId.clear();
		mainActivity.setUIElements();
		if (vertexes.size() == 0) {
			//mainActivity.item.add("No Suggestion found");
			//mainActivity.itemId.add(-1);
			//mainActivity.source.setCompletionHint("");
			if(id==mainActivity.source.getId()){
			mainActivity.source.setError("No suggestion found");
			}else{
			mainActivity.destination.setError("No suggestion found");
			}
			//mainActivity.destination.setActivated(false);
			
		} else {
			for (Vertex v : vertexes) {
				mainActivity.item.add(v.getName());
				Log.d("vertexes",mainActivity.item.toString());
		//		mainActivity.itemId.add(v.getId());

			}
		}

		// update the adapater

		// mainActivity.myAdapter.
		//mainActivity.myAdapter.notifyDataSetChanged();
		mainActivity.myAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_dropdown_item_1line, mainActivity.item);
//		if(id==mainActivity.source.getId()){
//		mainActivity.source.setAdapter(mainActivity.myAdapter);
//		}else{
		mainActivity.destination.setAdapter(mainActivity.myAdapter);
//		}

	}


	public List<Vertex> getItemsFromDb(String searchTerm) {

		// add items on the array dynamically
		Database db = new Database(context);
		List<Vertex> vertexes = new ArrayList<Vertex>();
		Log.d("Database",db.toString());
		vertexes = db.getVertexUsingQuery(searchTerm);
//		itemId.clear();
//		for (Vertex v : vertexes) {
//			itemId.add(v.getId());
//		}
		return vertexes;

	}
	
	
	
	
}
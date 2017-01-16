package com.crestaSom.KTMPublicRoute.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.crestaSom.model.Vertex;

public class DataWrapper implements Serializable {

	   private List<Vertex> vList;

	public List<Vertex> getvList() {
		return vList;
	}

	public void setvList(ArrayList<Vertex> vList) {
		this.vList = vList;
	}

	public DataWrapper(List<Vertex> data) {
	      this.vList = data;
	   }

	@Override
	public String toString() {
		return vList.toString();
	}
}
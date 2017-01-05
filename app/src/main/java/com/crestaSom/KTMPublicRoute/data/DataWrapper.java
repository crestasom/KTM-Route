package com.crestaSom.KTMPublicRoute.data;

import com.crestaSom.model.Vertex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	}
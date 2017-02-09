package com.crestaSom.model;

import java.sql.Date;
import java.util.List;

public class Route {
	private int id;
	private String name;
	private String nameNepali;
	private List<Integer> allVertexes;
	private boolean doubleSided;
	private String vehicleType;
	private String vehicleTypeNepali;

	public String getNameNepali() {
		return nameNepali;
	}

	public void setNameNepali(String nameNepali) {
		this.nameNepali = nameNepali;
	}

	public String getVehicleTypeNepali() {
		return vehicleTypeNepali;
	}

	public void setVehicleTypeNepali(String vehicleTypeNepali) {
		this.vehicleTypeNepali = vehicleTypeNepali;
	}

	private Date postDate;
	
	
	public List<Integer> getAllVertexes() {
		return allVertexes;
	}
	public void setAllVertexes(List<Integer> allEdges) {
		this.allVertexes = allEdges;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Route [name=" + name + ", allVertex=" + allVertexes + "]";
	}
	public boolean isDoubleSided() {
		return doubleSided;
	}
	public void setDoubleSided(boolean doubleSided) {
		this.doubleSided = doubleSided;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	
	
}

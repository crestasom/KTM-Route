package com.crestaSom.model;

import java.io.Serializable;



public class Vertex implements Comparable<Vertex>,Serializable{

	 private int id=0;
	 private String name;

	 private double latCode;

	 private double longCode;
	 private int referenceStop;
	 private int referenceStop1;
	private boolean isTransit;
	private String nameNepali;

	public String getNameNepali() {
		return nameNepali;
	}

	public void setNameNepali(String nameNepali) {
		this.nameNepali = nameNepali;
	}



	public boolean isTransit() {
		return isTransit;
	}

	public void setTransit(boolean transit) {
		isTransit = transit;
	}

	public int getReferenceStop1() {
		return referenceStop1;
	}




	public void setReferenceStop1(int referenceStop1) {
		this.referenceStop1 = referenceStop1;
	}




	 private Double distanceFromSource=999.99;
	 
	 
	 public int getReferenceStop() {
		return referenceStop;
	}




	public void setReferenceStop(int referenceStop) {
		this.referenceStop = referenceStop;
	}


//	public Vertex(String name, double latCode, double longCode) {
//		super();
//		this.name = name;
//		this.latCode = latCode;
//		this.longCode = longCode;
//	}
	
	
	

	public Double getDistanceFromSource() {
		return distanceFromSource;
	}




	public void setDistanceFromSource(Double distanceFromSource) {
		this.distanceFromSource = distanceFromSource;
	}




	public void setId(int id) {
		this.id = id;
	}




	public void setName(String name) {
		this.name = name;
	}




	public void setLatCode(double latCode) {
		this.latCode = latCode;
	}




	public void setLongCode(double longCode) {
		this.longCode = longCode;
	}




	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getLatCode() {
		return latCode;
	}


	public double getLongCode() {
		return longCode;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
	//	System.out.println(this+"\t"+obj);
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
//		return "id:" + id + " \nName:" + name;
		//return name+"  "+referenceStop+"  "+referenceStop1+"\n";
		//return name+"\t"+latCode+","+longCode;
		return name;
	}




	@Override
	public int compareTo(Vertex o) {
		// TODO Auto-generated method stub
		if(this.equals(o)){
			return 0;
		}else if(this.distanceFromSource>o.distanceFromSource){
			return 1;
			
		}else{
			return -1;
		}
		
	}

}

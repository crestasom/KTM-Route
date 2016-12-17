package com.crestaSom.model;

public class Edge {

	
	private int id;
	
	private Vertex source;
	
	private Vertex destination;
	private String name;
	private double weight;
	private boolean oneway;
	private int referenceStop;
	private int referenceStop1;
	public int getReferenceStop1() {
		return referenceStop1;
	}
	public void setReferenceStop1(int referenceStop1) {
		this.referenceStop1 = referenceStop1;
	}

	private boolean flag=false;

	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public int getReferenceStop() {
		return referenceStop;
	}
	public void setReferenceStop(int referenceStop) {
		this.referenceStop = referenceStop;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSource(Vertex source) {
		this.source = source;
	}

	public void setDestination(Vertex destination) {
		this.destination = destination;
	}

	public void setOneway(boolean oneway) {
		this.oneway = oneway;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public Vertex getDestination() {
		return destination;
	}

	public Vertex getSource() {
		return source;
	}

	public double getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		if(flag){
		return source.toString() + " - " + destination.toString()+"\t"+weight+"\t"+oneway;
		}else{
			return destination.toString() + " - " + source.toString()+"\t"+weight+"\t"+oneway;
		}
			
	}

	public boolean isOneway() {
		return oneway;
	}
	

}

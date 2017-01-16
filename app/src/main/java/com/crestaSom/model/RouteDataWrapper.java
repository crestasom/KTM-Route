package com.crestaSom.model;

import java.io.Serializable;
import java.util.List;

public class RouteDataWrapper implements Comparable<RouteDataWrapper>, Serializable {
	private List<RouteData> routeData1,routeData2;


	public double getDist1() {
		return dist1;
	}

	public void setDist1(double dist1) {
		this.dist1 = dist1;
	}

	public double getDist2() {
		return dist2;
	}

	public void setDist2(double dist2) {
		this.dist2 = dist2;
	}

	public double getDistTotal() {
		return distTotal;
	}

	public void setDistTotal(double distTotal) {
		this.distTotal = distTotal;
	}

	private double dist1,dist2,distTotal;

	public List<RouteData> getRouteData1() {
		return routeData1;
	}

	public void setRouteData1(List<RouteData> routeData1) {
		this.routeData1 = routeData1;
	}

	public List<RouteData> getRouteData2() {
		return routeData2;
	}

	public void setRouteData2(List<RouteData> routeData2) {
		this.routeData2 = routeData2;
	}

	@Override
	public String toString() {
		return "\nRouteDataWrapper [routeData1=" + routeData1 + ", routeData2=" + routeData2 + "]";
	}


	@Override
	public int compareTo(RouteDataWrapper o) {
		if(this.equals(o)){
			return 0;
		}else if(this.distTotal>o.distTotal){
			return 1;

		}else{
			return -1;
		}
	}
}

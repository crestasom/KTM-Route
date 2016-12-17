package com.crestaSom.implementation;

public class Functions {

	
	public double calculateAerialDistance(double lat1,double long1,double lat2,double long2){
		double distance=0.0;
//		System.out.println("Lat long code");
//		System.out.println(lat1);
//		System.out.println(long1);
//		System.out.println(lat2);
//		System.out.println(long2);
		double dlat,dlong,r=6372.797; // mean radius of Earth in km
		double a,c;
		double pi80 = Math.PI / 180;
        lat1 *= pi80;
        long1 *= pi80;
        lat2 *= pi80;
        long2 *= pi80;

        r = 6372.797; // mean radius of Earth in km
        dlat = lat2 - lat1;
        dlong = long2 - long1;
        a = Math.sin(dlat / 2) * Math.sin(dlat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlong / 2) * Math.sin(dlong / 2);
        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distance = r * c;
		
		return distance;
	}
}

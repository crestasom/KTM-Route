package com.crestaSom.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 1/8/2017.
 */

public class RouteData implements Serializable {

    private List<Vertex> vList;
    private String rName;

    public String getrName() {
        return rName;
    }

    public void setrName(String rName) {
        this.rName = rName;
    }

    public List<Vertex> getvList() {
        return vList;
    }

    public void setvList(ArrayList<Vertex> vList) {
        this.vList = vList;
    }





    public RouteData(List<Vertex> data, String name) {
        this.vList = data;
        this.rName=name;

    }

    @Override
    public String toString() {
        return vList.toString();
    }
}

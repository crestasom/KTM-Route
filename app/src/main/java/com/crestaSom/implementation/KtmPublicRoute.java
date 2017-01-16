package com.crestaSom.implementation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.PriorityQueue;
import java.util.Queue;

import android.content.Context;
import android.util.Log;

import com.crestaSom.KTMPublicRoute.data.DataWrapper;
import com.crestaSom.database.Database;
import com.crestaSom.model.Edge;
import com.crestaSom.model.Fare;
import com.crestaSom.model.NearestStop;
import com.crestaSom.model.Route;
import com.crestaSom.model.RouteData;
import com.crestaSom.model.RouteDataWrapper;
import com.crestaSom.model.Vertex;
import com.crestaSom.implementation.Functions;

public class KtmPublicRoute {
    private int refSource, refSource1, refDest, refDest1;
    private Database db;
    private List<Vertex> nodes;
    private List<Edge> edges;
    private Set<Edge> allEdges;
    private Route singleRoute;

    public Route getSingleRoute() {
        return singleRoute;
    }

    public void setSingleRoute(Route singleRoute) {
        this.singleRoute = singleRoute;
    }

    public void setAllEdges(Set<Edge> allEdges) {
        this.allEdges = allEdges;
    }

    private List<Route> routes;
    private List<Route> allRoutes;
    private DijkstrasAlgorithm algo;
    private double totalDistance;
    private String routeName = "";

    public double getExecutionTime() {
        return algo.totalTime;
    }

    public KtmPublicRoute(Context context) {
        super();
        db = new Database(context);
        algo = new DijkstrasAlgorithm(context);
        routes = db.getAllRoute();
        allEdges = new HashSet<Edge>();

    }

    /**
     * Main algorithm is implemented here
     *
     * @throws SQLException
     */

    public List<Vertex> findShortestPath(Vertex source, Vertex dest) {
        List<Vertex> path = new LinkedList<Vertex>();
        refSource = source.getReferenceStop();
        refSource1 = source.getReferenceStop1();
        refDest = dest.getReferenceStop();
        refDest1 = dest.getReferenceStop1();
        // //System.out.println(refSource1);
        // //System.out.println(refDest1);

        // //System.out.println("Reference Stops 1:" + refSource1 + "\t" +
        // refDest1);

        // System.out.println("Double Ref");
        try {
            if (refSource1 != -1 || refDest1 != -1) {
                path = calculateShortestPathDoubleRef(source, dest, refSource,
                        refDest, refSource1, refDest1);
            } else {
                // System.out.println("Single Ref");
                path = calculateShortestPathSingleRef(source, dest, refSource,
                        refDest);

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // db.close();
        return path;
    }

    private List<Vertex> calculateShortestPathSingleRef(Vertex source,
                                                        Vertex dest, int refSource, int refDest) throws SQLException {
        double dist1 = 0, dist2 = 0, dist3 = 0;
        // TODO Auto-generated method stub
        Vertex refSrc = null, refDst = null;
        Vertex temp = null;
        List<Vertex> path = new LinkedList<Vertex>();
        List<Vertex> pathMain = new LinkedList<Vertex>();
        List<Vertex> pathSrc = new LinkedList<Vertex>();
        List<Vertex> pathDst = new LinkedList<Vertex>();
        if (refSource == 0 && refDest == 0) {
            //	nodes = db.getVertexes("0");
            edges = db.getEdges("0");
            allEdges.addAll(edges);
            algo.execute(nodes, edges, source, dest, temp, temp, true);
            path = algo.getPath(dest, true);
            setTotalDistance(algo.getTotalDistance());

        } else if (refSource == refDest || refDest == source.getId()
                || refSource == dest.getId()) {
            if (refSource == dest.getId()) {
                //	nodes = db.getVertexes(String.valueOf(refSource));
                edges = db.getEdges(String.valueOf(refSource));
                allEdges.addAll(edges);
            } else {
                //nodes = db.getVertexes(String.valueOf(refDest));
                edges = db.getEdges(String.valueOf(refDest));
                allEdges.addAll(edges);
            }
            algo.execute(nodes, edges, source, dest, temp, temp, true);
            path = algo.getPath(dest, true);
            setTotalDistance(algo.getTotalDistance());
            // System.out.println(path);

        } else if (refSource != 0 && refDest != 0) {
            //nodes = db.getVertexes("0");
            edges = db.getEdges("0");
            refSrc = db.getVertex(refSource);
            refDst = db.getVertex(refDest);
            allEdges.addAll(edges);

            algo.execute(nodes, edges, refSrc, refDst, temp, temp, true);
            pathMain = algo.getPath(refDst, true);
            dist2 = algo.getTotalDistance();

            //nodes = db.getVertexes(String.valueOf(refSource));
            edges = db.getEdges(String.valueOf(refSource));
            allEdges.addAll(edges);
            algo.execute(nodes, edges, source, refSrc, temp, temp, true);
            pathSrc = algo.getPath(refSrc, true);
            dist1 = algo.getTotalDistance();
            //nodes = db.getVertexes(String.valueOf(refDest));
            edges = db.getEdges(String.valueOf(refDest));
            allEdges.addAll(edges);
            algo.execute(nodes, edges, refDst, dest, temp, temp, true);
            pathDst = algo.getPath(dest, true);
            dist3 = algo.getTotalDistance();

            path = concatPathTest(pathSrc, pathMain, pathDst, dist1, dist2,
                    dist3);

        } else if (refSource == 0 && refDest != 0) {
            refDst = db.getVertex(refDest);
            //nodes = db.getVertexes("0");
            edges = db.getEdges("0");
            allEdges.addAll(edges);
            // //System.out.println(source+"\t"+refDst);
            algo.execute(nodes, edges, source, refDst, temp, temp, true);
            pathMain = algo.getPath(refDst, true);
            dist2 = algo.getTotalDistance();
            // //System.out.println(pathMain);

            //nodes.clear();
            edges.clear();
            //nodes = db.getVertexes(String.valueOf(refDest));
            edges = db.getEdges(String.valueOf(refDest));
            allEdges.addAll(edges);
            // System.out.println(refDst + "\t" + dest);
            algo.execute(nodes, edges, refDst, dest, temp, temp, true);
            // //System.out.println(refDest+"\t"+dest);
            pathDst = algo.getPath(dest, true);
            dist3 = algo.getTotalDistance();
            // System.out.println(pathDst.toString());
            path = concatPathTest(pathMain, pathDst, pathSrc, dist2, dist3,
                    dist1);

        } else if (refSource != 0 && refDest == 0) {
            refSrc = db.getVertex(refSource);

            //nodes = db.getVertexes(String.valueOf(refSource));
            // //System.out.println(nodes);
            edges = db.getEdges(String.valueOf(refSource));
            allEdges.addAll(edges);
            // System.out.println(refSource);
            // System.out.println(edges);
            algo.execute(nodes, edges, source, refSrc, temp, temp, true);
            // System.out.println("Reference:" + refSrc);
            pathSrc = algo.getPath(refSrc, true);
            dist1 = algo.getTotalDistance();
            // System.out.println(pathSrc);

            //nodes = db.getVertexes("0");
            edges = db.getEdges("0");
            allEdges.addAll(edges);
            algo.execute(nodes, edges, refSrc, dest, temp, temp, true);
            pathMain = algo.getPath(dest, true);
            dist2 = algo.getTotalDistance();
            // //System.out.println(nodes.size() + "\t" + edges.size());
            //nodes.clear();
            edges.clear();
            // System.out.println(pathMain.toString());

            // //System.out.println(pathSrc.toString() + "\n" +
            // pathMain.toString());
            path = concatPathTest(pathSrc, pathMain, pathDst, dist1, dist2,
                    dist3);
            // System.out.println(nodes.size() + "\t" + edges.size());
        }

        return path;

    }

    private List<Vertex> calculateShortestPathDoubleRef(Vertex source,
                                                        Vertex dest, int refSource, int refDest, int refSource1,
                                                        int refDest1) throws SQLException {
        // TODO Auto-generated method stub
        System.out.println("Double Reference Source or destination");
        double dist1 = 0, dist2 = 0, dist3 = 0, dist4 = 0, dist5 = 0;
        Functions fn = new Functions();
        Vertex temp = null;
        Vertex refSrc = db.getVertex(refSource);
        Vertex refDst = db.getVertex(refDest);
        Vertex refSrc1 = null;
        Vertex refDst1 = null;
        List<Vertex> path = new LinkedList<Vertex>();
        List<Vertex> path1 = new LinkedList<Vertex>();
        List<Vertex> path2 = new LinkedList<Vertex>();
        List<Vertex> path3 = new LinkedList<Vertex>();
        List<Vertex> path4 = new LinkedList<Vertex>();
        List<Vertex> path5 = new LinkedList<Vertex>();
        List<Vertex> pathA = new LinkedList<Vertex>();
        List<Vertex> pathB = new LinkedList<Vertex>();
        double d1 = 0.0, d2 = 0.0, d1temp = 0.0, d2temp = 0.0;
        if (refSource1 != -1 && refDest1 != -1) {
            System.out.println("Double Reference source and destination");
            refSrc1 = db.getVertex(refSource1);
            refDst1 = db.getVertex(refDest1);
            if (refSource == refDest && refSource1 == refDest1) {
                System.out.println("Same Double Reference");
                //nodes = db.getVertexes(String.valueOf(refSource + ","
                //	+ refSource1));
                edges = db.getEdges(String
                        .valueOf(refSource + "," + refSource1));
                allEdges.addAll(edges);
                // for(Edge e:edges){
                // ////System.out.println(e.toString());
                // }

                algo.execute(nodes, edges, source, dest, refSrc, refSrc1, true);
                pathA = algo.getPath(dest, true);
                d1 = algo.getTotalDistance();
                System.out.println("D1:" + d1);
                // return pathA;
                System.out.println("\nPathA:" + pathA);
                path1 = algo.getPath(refSrc, true);
                d1temp = algo.getTotalDistance();
                path2 = algo.getPath(refSrc1, true);
                d2temp = algo.getTotalDistance();
                System.out.println("path1:" + path1);
                System.out.println("\npath2:" + path2);
                System.out.println("D1temp:" + d1temp + "\tD2temp:" + d2temp);

                //nodes = db.getVertexes("0");
                edges = db.getEdges("0");
                allEdges.addAll(edges);

                if (d1temp < d2temp) {
                    // System.out.println("Check point 1");
                    // System.out.println("path1:" + path1);
                    algo.execute(nodes, edges, refSrc, refSrc1, temp, temp,
                            true);
                    path3 = algo.getPath(refSrc1, true);
                    dist3 = algo.getTotalDistance();
                    //nodes = db.getVertexes(refSource + "," + refSource1);
                    edges = db.getEdges(refSource + "," + refSource1);
                    // allEdges.addAll(edges);
                    algo.execute(nodes, edges, refSrc1, dest, null, null, true);
                    path4 = algo.getPath(dest, true);
                    dist4 = algo.getTotalDistance();
                    pathB = concatPathTest(path1, path3, path4, d1temp, dist3,
                            dist4);
                    d2 = getTotalDistance();
                } else {
                    // System.out.println("Check point 2");
                    algo.execute(nodes, edges, refSrc1, refSrc, temp, temp,
                            true);
                    path3 = algo.getPath(refSrc, true);
                    dist3 = algo.getTotalDistance();
                    System.out.println("Path3:" + path3);
                    nodes = db.getVertexes(refSource + "," + refSource1);
                    edges = db.getEdges(refSource + "," + refSource1);
                    algo.execute(nodes, edges, refSrc, dest, temp, temp, true);
                    path4 = algo.getPath(dest, true);
                    dist4 = algo.getTotalDistance();
                    pathB = concatPathTest(path2, path3, path4, d2temp, dist3,
                            dist4);
                    d2 = getTotalDistance();
                    System.out.println("PathB:" + pathB);
                    System.out.println("D1:" + d1 + "D2:" + d2);
                }

                // //System.out.println("PathB:"+pathB);
//				if (d1 < d2) {
//					algo.setTotalDistance(d1);
//				}
                // //System.out.println(path.toString());
            } else {

            }
        } else if (refSource1 != -1) {

            System.out.println("Double Reference Source");
            refSrc1 = db.getVertex(refSource1);
            // lat1 = source.getLatCode();
            // long1 = source.getLongCode();
            // lat2 = refSrc.getLatCode();
            // long2 = refSrc.getLongCode();
            // aerialSrc = fn.calculateAerialDistance(lat1, long1, lat2, long2);
            // //
            // System.out.println(source.getName()+","+refSrc.getName()+":"+aerialSrc);
            // lat2 = refSrc1.getLatCode();
            // long2 = refSrc1.getLongCode();
            // aerialSrc1 = fn.calculateAerialDistance(lat1, long1, lat2,
            // long2);
            // //
            // System.out.println(source.getName()+","+refSrc1.getName()+":"+aerialSrc1);
            //
            // lat1 = dest.getLatCode();
            // long1 = dest.getLongCode();
            // lat2 = refSrc.getLatCode();
            // long2 = refSrc.getLongCode();
            // aerialDest = fn.calculateAerialDistance(lat1, long1, lat2,
            // long2);
            // //
            // System.out.println(dest.getName()+","+refSrc.getName()+":"+aerialDest);
            // lat2 = refSrc1.getLatCode();
            // long2 = refSrc1.getLongCode();
            // aerialDest1 = fn.calculateAerialDistance(lat1, long1, lat2,
            // long2);
            // System.out.println(dest.getName()+","+refSrc1.getName()+":"+aerialDest1);
            // //System.out.println("Reference Source:"+refSrc+" Reference Source1:"+refSrc1);

            //nodes = db.getVertexes(refSource + "," + refSource1);
            edges = db.getEdges(refSource + "," + refSource1);
            allEdges.addAll(edges);
            // System.out.println(source + " " + refSrc);
            algo.execute(nodes, edges, source, refSrc, refSrc1, temp, true);
            path1 = algo.getPath(refSrc, true);
            dist1 = algo.getTotalDistance();
            // //System.out.println("Longitude:"+refSrc.getName()+" "+refSrc.getLongCode());
            System.out.println("Path1" + path1);
            path3 = algo.getPath(refSrc1, true);
            dist3 = algo.getTotalDistance();
            System.out.println("Path3" + path3);
            // aerial1=fn.calculateAerialDistance(source.getLatCode(),
            // source.getLongCode(), refSrc1.getLatCode(),
            // refSrc1.getLongCode());
            // //System.out.println("Dist1:"+dist1+"\tDist2:"+dist2);
            // aerial = aerialSrc + aerialDest;
            // aerial1 = aerialSrc1 + aerialDest1;
            // //System.out.println("AerialSource:"+aerialSrc+"\tAerialSource1:"+aerialSrc1);
            // System.out.println("AerialSrc:"+aerial+"\tAerialSrc1:"+aerial1);
            if (dest.equals(refSrc) || dest.equals(refSrc1)) {
                // System.out.println("Reference Point matched");
                if (dest.equals(refSrc)) {
                    if (dist1 < dist3) {
                        setTotalDistance(dist1);
                        return path1;

                    } else {
                        //		nodes = db.getVertexes("0");
                        edges = db.getEdges("0");
                        allEdges.addAll(edges);
                        // System.out.println(source + " " + refSrc);
                        algo.execute(nodes, edges, refSrc1, refSrc, temp, temp,
                                true);
                        path2 = algo.getPath(refSrc, true);
                        dist2 = algo.getTotalDistance();
                        System.out.println("Path2:" + path2);
                        pathB = concatPathTest(path3, path2, path5, dist3,
                                dist2, dist5);
                        dist2 = algo.getTotalDistance();
                        dist3 = dist3 + dist2;
                        //	System.out.println("Path1:" + path1 + "\t" + dist1);
                        //System.out.println("PathB:" + pathB + "\t" + dist3);
                        if (dist1 > dist3) {
                            path = pathB;
                            setTotalDistance(dist3);
                            return path;
                        } else
                            path = path1;
                        setTotalDistance(dist1);
                        return path;
                    }
                } else if (dest.equals(refSrc1)) {
                    if (dist1 > dist3) {
                        setTotalDistance(dist3);
                        return path3;
                    } else {
                        //nodes = db.getVertexes("0");
                        edges = db.getEdges("0");
                        allEdges.addAll(edges);
                        // System.out.println(source + " " + refSrc);
                        algo.execute(nodes, edges, refSrc, refSrc1, temp, temp,
                                true);
                        path2 = algo.getPath(refSrc1, true);
                        dist2 = algo.getTotalDistance();
                        System.out.println("Path2:" + path2);
                        pathB = concatPathTest(path1, path2, path5, dist1,
                                dist2, dist5);
                        dist2 = algo.getTotalDistance();
                        dist1 = dist1 + dist2;
                        //System.out.println("Path3:" + path3 + "\t" + dist3);
                        //System.out.println("PathB:" + pathB + "\t" + dist3);
                        if (dist3 > dist1) {
                            path = pathB;
                            setTotalDistance(dist1);
                            return path;
                        } else
                            path = path3;
                        setTotalDistance(dist3);
                        return path;
                    }
                }

            } else if (refDest == 0) {
                System.out.println("No Reference Destination");

                //nodes.clear();
                edges.clear();
                //nodes = db.getVertexes("0");
                edges = db.getEdges("0");
                allEdges.addAll(edges);
                algo.execute(nodes, edges, dest, refSrc, refSrc1, temp, false);
                path2 = algo.getPath(refSrc, false);
                //System.out.println("path2" + path2);
                dist2 = algo.getTotalDistance();
                //nodes = db.getVertexes("0");
                edges = db.getEdges("0");
                //algo.execute(nodes, edges, refSrc1, dest, temp, temp, true);
                path4 = algo.getPath(refSrc1, false);
                System.out.println("path4" + path4);
                dist4 = algo.getTotalDistance();
                // System.out.println("\nPath2:" + path2);
                // System.out.println("Path4:" + path4 + "\n");

            } else {
                // System.out.println("Single Refenrence Destination");
                //nodes.clear();
                edges.clear();
                // nodes = db.getVertexes("0");
                edges = db.getEdges("0");
                allEdges.addAll(edges);
                algo.execute(nodes, edges, refDst, refSrc, refSrc1, temp, false);
                path2 = algo.getPath(refSrc, false);
                dist2 = algo.getTotalDistance();
                // //System.out.println("Path2:" + path2);
//				edges = db.getEdges("0");
//				algo.execute(nodes, edges, refSrc1, refDst, temp, temp, true);
                path4 = algo.getPath(refSrc1, false);
                dist4 = algo.getTotalDistance();
                // //System.out.println("Path4:" + path4);

                //nodes.clear();
                edges.clear();
                //nodes = db.getVertexes(String.valueOf(refDest));
                edges = db.getEdges(String.valueOf(refDest));
                allEdges.addAll(edges);
                algo.execute(nodes, edges, refDst, dest, temp, temp, true);
                path5 = algo.getPath(dest, true);
                dist5 = algo.getTotalDistance();

            }
            pathA = concatPathTest(path1, path2, path5, dist1, dist2, dist5);
            // System.out.println("PathA:" + pathA);
            d1 = getTotalDistance();
            // System.out.println("D1:" + d1);
            pathB = concatPathTest(path3, path4, path5, dist3, dist4, dist5);
            // System.out.println("PathB:" + pathB);
            d2 = getTotalDistance();
            // System.out.println("D2:" + d2);

        } else if (refDest1 != -1) {
            System.out.println("Double Reference Destination");
            refDst1 = db.getVertex(refDest1);

            //nodes = db.getVertexes(refDest + "," + refDest1);
            edges = db.getEdges(refDest + "," + refDest1);
            allEdges.addAll(edges);
            algo.execute(nodes, edges, dest, refDst, refDst1, temp, false);
            path3 = algo.getPath(refDst, false);
            dist3 = algo.getTotalDistance();
            //System.out.println("\npath3:" + path3.toString());
            // ////System.out.println(path3);
//			nodes.clear();
//			edges.clear();
//			nodes = db.getVertexes(refDest + "," + refDest1);
//			edges = db.getEdges(refDest + "," + refDest1);
            //algo.execute(nodes, edges, refdDst1, dest, temp, temp, true);
            path4 = algo.getPath(refDst1, false);
            System.out.println("Path3: " + path3);
            System.out.println("Path4: " + path4);
            dist4 = algo.getTotalDistance();
            //System.out.println("\npath4:" + path4.toString());
            // //System.out.println(path4);
            if (source.equals(refDst) || source.equals(refDst1)) {
                System.out.println("Source is reference stop for destination");
                if (source.equals(refDst)) {
                    if (dist3 < dist4) {
                        setTotalDistance(dist3);
                        return path3;
                    } else {
                        //		nodes = db.getVertexes("0");
                        edges = db.getEdges("0");
                        allEdges.addAll(edges);
                        // System.out.println(source + " " + refSrc);
                        algo.execute(nodes, edges, refDst1, refDst, temp, temp,
                                true);
                        path2 = algo.getPath(refDst, true);
                        dist2 = algo.getTotalDistance();
                        System.out.println("Path2:" + path2);
                        pathB = concatPathTest(path2, path4, path5, dist2,
                                dist4, dist5);
                        dist2 = algo.getTotalDistance();
                        dist4 = dist4 + dist2;
                        System.out.println("Path1:" + path1 + "\t" + dist1);
                        System.out.println("PathB:" + pathB + "\t" + dist3);
                        if (dist3 > dist4) {
                            path = pathB;
                            setTotalDistance(dist4);
                            return path;
                        } else
                            path = path3;
                        setTotalDistance(dist3);
                        return path;
                    }
                } else if (dest.equals(refDst1)) {
                    if (dist3 > dist4) {
                        setTotalDistance(dist4);
                        return path4;

                    } else {
                        //	nodes = db.getVertexes("0");
                        edges = db.getEdges("0");
                        allEdges.addAll(edges);
                        // System.out.println(source + " " + refSrc);
                        algo.execute(nodes, edges, refDst, refDst1, temp, temp,
                                true);
                        path2 = algo.getPath(refDst1, true);
                        dist2 = algo.getTotalDistance();
                        System.out.println("Path2:" + path2);
                        pathB = concatPathTest(path2, path3, path5, dist2,
                                dist3, dist5);
                        dist2 = algo.getTotalDistance();
                        dist3 = dist3 + dist2;
                        //System.out.println("Path3:" + path3 + "\t" + dist3);
                        //System.out.println("PathB:" + pathB + "\t" + dist3);
                        if (dist4 > dist3) {
                            path = pathB;
                            setTotalDistance(dist3);
                            return path;
                        } else
                            path = path4;
                        setTotalDistance(dist4);
                        return path;
                    }
                }
            } else if (refSource == 0) {
                // System.out.println("No Referenced point for source");
                //nodes.clear();
                edges.clear();
                //nodes = db.getVertexes("0");
                edges = db.getEdges("0");
                allEdges.addAll(edges);
                algo.execute(nodes, edges, source, refDst, refDst1, temp, true);
                path1 = algo.getPath(refDst, true);
                dist1 = algo.getTotalDistance();
                // nodes.clear();
                // edges.clear();
                // nodes = db.getVertexes("0");
                // edges = db.getEdges("0");
                // algo.execute(nodes, edges, source, refDst1);
                path2 = algo.getPath(refDst1, true);
                dist2 = algo.getTotalDistance();
                // //System.out.println("Path1:"+path1.toString());
                // //System.out.println("Path3:"+path3.toString());
                pathA = concatPathTest(path1, path3, path5, dist1, dist3, dist5);
                d1 = getTotalDistance();
                // //System.out.println("Path2:"+path2.toString());
                // //System.out.println("Path4:"+path4.toString());
                pathB = concatPathTest(path2, path4, path5, dist2, dist4, dist5);
                d2 = getTotalDistance();
            } else {
                // System.out.println("Single Referenced point for source");
                //nodes.clear();
                edges.clear();
                //nodes = db.getVertexes("0");
                edges = db.getEdges("0");
                allEdges.addAll(edges);

                algo.execute(nodes, edges, refSrc, refDst, refDst1, temp, true);
                path1 = algo.getPath(refDst, true);
                dist1 = algo.getTotalDistance();
                // nodes.clear();
                // edges.clear();
                // nodes = db.getVertexes("0");
                // edges = db.getEdges("0");
                // algo.execute(nodes, edges, refSrc, refDst1);
                path2 = algo.getPath(refDst1, true);
                dist2 = algo.getTotalDistance();
                // //System.out.println("\npath2:"+path2.toString());

                //nodes.clear();
                edges.clear();
                //nodes = db.getVertexes(String.valueOf(refSource));
                edges = db.getEdges(String.valueOf(refSource));
                allEdges.addAll(edges);
                algo.execute(nodes, edges, source, refSrc, temp, temp, true);
                path5 = algo.getPath(refSrc, true);
                dist5 = algo.getTotalDistance();
                // //System.out.println("\npath5:"+path5.toString());

                pathA = concatPathTest(path5, path1, path3, dist5, dist1, dist3);
                d1 = getTotalDistance();
                pathB = concatPathTest(path5, path2, path4, dist5, dist2, dist4);
                d2 = getTotalDistance();
                // System.out.println("\npathA:" + pathA.toString());
                // System.out.println("\npathB:" + pathB.toString());
            }

        }
        // System.out.println("d1=" + d1 + "\td2=" + d2);
        if (d1 < d2) {
            path = pathA;
            setTotalDistance(d1);
        } else {
            path = pathB;
            setTotalDistance(d2);
        }
        return path;
    }

    private List<Vertex> concatPath(List<Vertex> pathSrc,
                                    List<Vertex> pathMain, List<Vertex> pathDst) {
        // TODO Auto-generated method stub

        List<Vertex> temp = new ArrayList<Vertex>();
        algo.setTotalDistance(0.0);
        for (Vertex e : pathSrc) {
            // algo.setTotalDistance(algo.getTotalDistance() + e.getWeight());
            temp.add(e);
        }

        if (!pathMain.isEmpty()) {
            // //System.out.println("path main is not empty");
            for (Vertex e : pathMain) {
                temp.add(e);
                // algo.setTotalDistance(algo.getTotalDistance() +
                // e.getWeight());
            }
        }
        // //System.out.println("After joining path main:"+pathSrc);
        if (!pathDst.isEmpty()) {
            // //System.out.println("path dest is not empty");
            for (Vertex e : pathDst) {
                temp.add(e);
                // algo.setTotalDistance(algo.getTotalDistance() +
                // e.getWeight());
            }
        }
        // //System.out.println("After joining path dest:"+pathSrc);
        return temp;
    }

    private List<Vertex> concatPathTest(List<Vertex> pathSrc,
                                        List<Vertex> pathMain, List<Vertex> pathDst, double src,
                                        double main, double dist) {
        // TODO Auto-generated method stub
        // System.out.println("Dist1:" + src);
        // System.out.println("Dist2:" + main);
        // System.out.println("Dist3:" + dist);
        int i = 0;

        List<Vertex> temp = new ArrayList<Vertex>();
        // algo.setTotalDistance(0.0);
        double distance = src + main + dist;
        setTotalDistance(distance);
        // System.out.println("Distance Concat:" + distance);
        for (Vertex e : pathSrc) {
            // algo.setTotalDistance(algo.getTotalDistance() + e.getWeight());
            temp.add(e);
        }

        if (!pathMain.isEmpty()) {
            // //System.out.println("path main is not empty");
            for (Vertex e : pathMain) {
                if (i == 0) {
                    i++;
                } else {
                    temp.add(e);
                }
                // algo.setTotalDistance(algo.getTotalDistance() +
                // e.getWeight());
            }
        }
        i = 0;
        // //System.out.println("After joining path main:"+pathSrc);
        if (!pathDst.isEmpty()) {
            // //System.out.println("path dest is not empty");
            for (Vertex e : pathDst) {
                if (i == 0) {
                    i++;
                } else {
                    temp.add(e);
                }
                // algo.setTotalDistance(algo.getTotalDistance() +
                // e.getWeight());
            }
        }
        // //System.out.println("After joining path dest:"+pathSrc);
        return temp;
    }

    /***
     * To find the route direction
     *
     * @param ArrayList <Vertex> vertexPath
     * @return list of route that match vertexPath with minimum number of
     * transit points
     */

    public Map<List<Integer>, List<Vertex>> findRoutePath(List<Vertex> vertexPath) {
        // System.out.println("vertexPath before remove:" + vertexPath);
        Map<List<Integer>, List<Vertex>> routeLists = new HashMap<List<Integer>, List<Vertex>>();
        Map<Integer, List<Vertex>> routeList = new HashMap<Integer, List<Vertex>>();
        List<Integer> routeIds = new ArrayList<Integer>();
        List<Integer> itemPos = new ArrayList<Integer>();
        Map<Integer, Integer> stopCount = new HashMap<Integer, Integer>();
        List<Vertex> tempVertexList;
        int count = 0, max, routeId = 0, j, pos, i, size, k, var = 0, sizeOfPath;
        int countTemp = 0;
        List<Integer> stops = null;
        int countFlag = 0;
        List<Route> tempList;
        // while (!vertexPath.isEmpty()) {

        sizeOfPath = vertexPath.size();
        // System.out.println(sizeOfPath);
        tempVertexList = new ArrayList<Vertex>();
        // routeLists.clear();
        max = 0;
        tempList = getVertexRoutes(vertexPath.get(0)); // returns all routes
        // that contains
        // current vertex
        // System.out.println(vertexPath.get(0));
//		 System.out.println(tempList);
        // System.out.println("Route Count"+tempList.size());
        for (Route r : tempList) {
            countFlag = 0;
//			System.out.println("\nMatched Route:"+r.getName());
            stops = r.getAllVertexes();
            itemPos.clear();
//			pos = stops.indexOf(vertexPath.get(0).getId());
//			System.out.println("Matched Position:"+pos);
            // get all position of occurence of current stop in route
            for (int s = 0; s < stops.size(); s++) {
                if (stops.get(s) == vertexPath.get(0).getId()) {
                    itemPos.add(s);
                }
            }
//			System.out.println("Matched Vertex"+vertexPath.get(0));
//			System.out.println("Matched Positions:"+itemPos);

            size = stops.size();
            // System.out.println("POS:"+pos);
            for (int pos1 : itemPos) {

                count = 0;
                k = 0;
                for (i = pos1; i < size; i++) {

                    if (k == sizeOfPath) {
                        break;
                    }
                    // System.out.println("i="+i+"\tk="+k);
                    if (stops.get(i) != vertexPath.get(k).getId()) {
                        break;
                    }
                    count++;
                    k++;
                }
                if (countFlag == 0) {
                    countFlag = 1;
                    countTemp = count;
                }


                // System.out.println("Route id:"+r.getId());
                // System.out.println("max:"+max);

                // System.out.println("max:"+max);
            }
            if (count < countTemp) {
                count = countTemp;
            }


            if (count > max) {
                max = count;
            }
            stopCount.put(r.getId(), count);

        }

        for (Entry<Integer, Integer> entry : stopCount.entrySet()) {
            if (max == entry.getValue()) {
                //	routeId = entry.getKey();
//				System.out.println("Route With maxm match"+entry.getKey());
                routeIds.add(entry.getKey());
            }
        }
//		 System.out.println("Route id:"+routeIds);
        for (j = 0; j < max; j++) {
            // System.out.println(tempVertexList);
            tempVertexList.add(vertexPath.get(0));
            // System.out.println(tempVertexList);

            if (!(j == max - 1)) {
                // System.out.println("checkpoint");
                vertexPath.remove(0);
                // break;
            }
            // System.out.println("vertexPath after remove:"+vertexPath);
        }
        routeList.put(routeId, tempVertexList);
        routeLists.put(routeIds, tempVertexList);
        // System.out.println("Mapped Route Detail:"+getRoute(routeId));
//		System.out.println("Mapped Route:" + tempVertexList);
        // }
        // routeLists.
        return routeLists;
    }

    private Route findMatchingRoute(List<Route> tempList, Edge flagEdge2) {
        // TODO Auto-generated method stub
        int count, temp, pos;
        List<Integer> edgeList;
        for (Route r : tempList) {
            edgeList = r.getAllVertexes();
            pos = getEdgePosition(edgeList, flagEdge2);

        }

        return null;
    }

    private int getEdgePosition(List<Integer> edgeList, Edge flagEdge2) {
        // TODO Auto-generated method stub
        int pos = -1;
        for (Integer e : edgeList) {
            if (e == flagEdge2.getId()) {
                pos = e;
                break;
            }
        }
        return pos;
    }

    /**
     * to find a single route path if exists
     *
     * @param source
     * @param destination
     * @param route
     * @return
     */
    public List<RouteData> getSingleRoutes(Vertex source, Vertex destination) {
        int pos, size, flag = 0, i, j;
        List<RouteData> singlePaths = new ArrayList<>();
        RouteData path;
        setSingleRoute(new Route());
        Vertex v;
        List<Vertex> vertexList;
        List<Integer> tempVertexList;
        List<Route> vertexRoute = new ArrayList<Route>();
        vertexRoute = getVertexRoutes(source);
        for (Route r : vertexRoute) {
            flag = 0;
            vertexList = new ArrayList<>();
            tempVertexList = new ArrayList();
            tempVertexList = r.getAllVertexes();
            pos = tempVertexList.indexOf(source.getId());
            size = tempVertexList.size();
            for (i = pos; i < size; i++) {
                if (tempVertexList.get(i) == destination.getId()) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 1) {
                setRouteName(r.getName());
                setSingleRoute(r);

                for (j = pos; j <= i; j++) {
                    v = db.getVertex(tempVertexList.get(j));
                    System.out.println("VertexList: " + vertexList);
                    if (vertexList.contains(v) && v.equals(source)) {
                        vertexList = new ArrayList<>();
                    }
                    vertexList.add(v);

                }
                boolean tempFlag = false;
                for (RouteData rd : singlePaths) {
                    tempFlag = false;
                    if (rd.getvList().equals(vertexList)) {
                        String tempName = rd.getrName();
                        tempName += "\n" + r.getName() + " (" + r.getVehicleType() + ")";
                        rd.setrName(tempName);
                        tempFlag = true;
                        break;
                    }

                }
                if (tempFlag == false) {
                    path = new RouteData(vertexList, r.getName() + " (" + r.getVehicleType() + ")");
                    singlePaths.add(path);
                }

            }
        }

        return singlePaths;
    }


    /**
     * to find all direct routes from source to destination
     *
     * @param source
     * @param destination
     * @return
     */
    public List<Vertex> getSingleRoute(Vertex source, Vertex destination) {
        int pos, size, flag = 0, i, j;
        Vertex v;
        List<Vertex> vertexList = new ArrayList<Vertex>();
        List<Integer> tempVertexList;
        List<Route> vertexRoute = new ArrayList<Route>();
        vertexRoute = getVertexRoutes(source);
        for (Route r : vertexRoute) {
            tempVertexList = r.getAllVertexes();
            pos = tempVertexList.indexOf(source.getId());
            size = tempVertexList.size();
            for (i = pos; i < size; i++) {
                if (tempVertexList.get(i) == destination.getId()) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 1) {
                setRouteName(r.getName());
                setSingleRoute(r);
                for (j = pos; j <= i; j++) {
                    v = db.getVertex(tempVertexList.get(j));
                    vertexList.add(v);
                }
                break;
            }
        }

        return vertexList;
    }

    /**
     * returns all routes that contains given vertex
     *
     * @param List   <Route> routeList
     * @param Vertex source
     * @return List<Route> vertexRoute
     */
    private List<Route> getVertexRoutes(Vertex source) {
        // TODO Auto-generated method stub
        List<Route> vertexRouteList = new ArrayList<Route>();
        List<Integer> vertexList;
        Route r1;
        int id = 200, flag = 1;
        List<Integer> temp = new ArrayList<Integer>();
        for (Route r : routes) {
            vertexList = r.getAllVertexes();
            if (vertexList.contains(source.getId())) {
                vertexRouteList.add(r);
                // if (r.isDoubleSided()) {
                // r1 = new Route();
                // temp.addAll(vertexList);
                // Collections.reverse(temp);
                // r1.setAllVertexes(temp);
                // r1.setName(r.getName() + " Return Route");
                // while (flag == 1) {
                // id = ((int) (Math.random()+100) * ((int)
                // Math.random()+100));

                // for (Route r2 : routes) {
                // if (r2.getId() == id) {
                // flag = 0;
                // break;
                // }
                //
                // }
                // }
                // r1.setId(id++);
                // r1.setVehicleType(r.getVehicleType());
                // vertexRouteList.add(r1);
                // }
            }
        }
        // System.out.println(vertexRouteList);
        return vertexRouteList;
    }

    /***
     * To find all routes that contains Edge e
     *
     * @param Edge e
     * @return List<Route>
     */
    public List<Route> findRoutes(Edge e) {
        // //System.out.println(e);
        // //System.out.println(routes.toString());
        List<Route> routeEdge = new ArrayList<Route>();
        for (Route r : routes) {
            // //System.out.println(checkEdge(r, e));
            if (checkEdge(r, e)) {
                routeEdge.add(r);
            }
        }

        return routeEdge;
    }

    private boolean checkEdge(Route r, Edge edge) {
        // TODO Auto-generated method stub
        List<Integer> checkEdge = r.getAllVertexes();
        // //System.out.println(checkEdge.toString());
        for (int e : checkEdge) {
            if (e < 0) {
                e = -e;
            }
            if (e == edge.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * To calculate distance of a specified path
     *
     * @param List <Vertex> stops
     * @return distance
     */
    public double getRouteDistance(List<Vertex> stops) {
        double routeDistance = 0.0;
        List<Vertex> temp = new ArrayList<Vertex>();
        temp.addAll(stops);
        while (temp.size() != 1) {
            routeDistance += getEdgeDistance(temp.get(0), temp.get(1));
            //	Log.d("route distance",routeDistance+"");
            temp.remove(0);
        }
        return routeDistance;
    }

    public Double getEdgeDistance(Vertex source, Vertex dest) {
        double d = 0.0;
        //Log.d("check point"," In getEdgeDistance");
        //Log.d("edges",allEdges.toString());
        List<Edge> allEdgeTemp = new ArrayList<>();

        for (Edge e1 : allEdges) {
            if ((e1.getSource().equals(source) && e1.getDestination().equals(
                    dest))) {
                // System.out.println(e1);
                d = e1.getWeight();
                //	Log.d("Distance from getEdge:",e1.getName()+d+"");
                break;

            } else if ((e1.getSource().equals(dest) && e1.getDestination()
                    .equals(source))) {
                d = e1.getWeight();
                //	Log.d("Distance from getEdge:",e1.getName()+d+"");
                break;
            }

        }
        // System.out.println("printed from getEdge:"+e);
        // System.out.println("D:" + d);
        return d;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Route getRoute(int id) {
        for (Route r : routes) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    public Queue<Vertex> getNearestStop(Double lat, Double longi) {
        Vertex v1, v2, v3;
        Queue<Vertex> stopN = new PriorityQueue<Vertex>();
        int flag = -1, i = 0;
        v1 = new Vertex();
        v2 = new Vertex();
        v3 = new Vertex();
        double distance1 = 999.99, distance2 = 999.99, distance3 = 999.99;
        List<Edge> path = new LinkedList<Edge>();
        Functions fn = new Functions();
        List<Vertex> vertexes = new ArrayList<Vertex>();

        vertexes = db.getAllVertex();
        //System.out.println(vertexes.toString());
        for (Vertex v : vertexes) {

            double temp;
            temp = fn.calculateAerialDistance(lat, longi, v.getLatCode(), v.getLongCode());
            //	System.out.println("\n"+v1+"\t"+"\t"+temp);
            v1 = new Vertex();
            v1 = v;
            v1.setDistanceFromSource(temp);
            stopN.add(v1);
        }
        //double distSrc = distance1;
        //System.out.println(flag);
        //System.out.println("\n"+vertexes.get(flag));

//		Vertex source=vertexes.get(flag);
//		v=source;


        return stopN;
    }


    public int getRouteCost(double routeDistance) {
        List<Fare> fareList = db.getFareList();
        for (Fare f : fareList) {
            if (routeDistance < f.getDistance())
                return f.getFare();
        }
        return 24;
//		int cost=0;
//		if(routeDistance<=4.0){
//			return 13;
//		}else if(routeDistance<=5.0){
//			return 15;
//		}else if(routeDistance<=6.0){
//			return 16;
//		}else if(routeDistance<=8.0){
//			return 17;
//		}else if(routeDistance<=10.0){
//			return 19;
//		}else if(routeDistance<=13.0){
//			return 21;
//		}else if(routeDistance<=16.0){
//			return 23;
//		}else if(routeDistance<=19.0){
//			return 24;
//		}else if(routeDistance>19.0){
//			return 25;
//		}
//		return 0;


    }


    public Queue<RouteDataWrapper> getAlternativeRouteOneTransit(Vertex source, Vertex destination) {
        Queue<RouteDataWrapper> routeDatas = new PriorityQueue<>();
        RouteDataWrapper routeDataTemp = null;
        List<RouteData> tempList1, tempList2;
        List<Route> sourceRoutes = getVertexRoutes(source);
        List<Route> destinationRoutes = getVertexRoutes(destination);
        List<Integer> tempVertexIds, tempDestIds;
        int flag = 0, vertexSize;
        int idPosS1, idPosS2, idPosD1, idPosD2;
        double d1, d2, d = 0;
        Vertex matchedVertex = null;

        boolean localFlag = false;
        String tempRname1, tempRname2, tempRname3, tempRname4;

        Set<Vertex> matchVertexList = new HashSet<>();
        Route destR = null;
        System.out.println("Source Containing Routes: " + sourceRoutes);
        System.out.println("Destination Containing Routes: " + destinationRoutes);
        for (Route sourceR : sourceRoutes) {
            idPosS1 = sourceR.getAllVertexes().indexOf(source.getId());
            tempVertexIds = sourceR.getAllVertexes();
            vertexSize = sourceR.getAllVertexes().size();
            for (Route destinationR : destinationRoutes) {
                tempDestIds = destinationR.getAllVertexes();
                for (int i = idPosS1; i < vertexSize; i++) {
                    flag = 0;
                    if (destinationR.getAllVertexes().contains(tempVertexIds.get(i))) {
                        matchedVertex = db.getVertex(tempVertexIds.get(i));
                        //System.out.println("Matched vertex: " + matchedVertex);
                        //idPosS1 = tempVertexIds.indexOf(source.getId());
//						idPosS2 = i;
//						idPosD2 = destinationR.getAllVertexes().indexOf(destination.getId());
//						idPosD1 = destinationR.getAllVertexes().indexOf(matchedVertex);
                        flag = 1;
                        break;

                    }
                    destR = destinationR;
                }
                if (flag == 1) {

                    matchVertexList.add(matchedVertex);

                }
            }


        }
        System.out.println("Match Vertex: " + matchVertexList);

        for (Vertex v : matchVertexList) {
            tempList1 = getSingleRoutes(source, v);
            tempList2 = getSingleRoutes(v, destination);
            if (!tempList1.isEmpty() && !tempList2.isEmpty()) {
                routeDataTemp = new RouteDataWrapper();
                routeDataTemp.setRouteData1(tempList1);
                d1 = getRouteDistance(tempList1.get(0).getvList());
                routeDataTemp.setDist1(d1);
                routeDataTemp.setRouteData2(tempList2);
                d2 = getRouteDistance(tempList2.get(0).getvList());
                routeDataTemp.setDist2(d2);

                d = d1 + d2;
                routeDataTemp.setDistTotal(d);
               // if(!routeDatas.isEmpty())
                for (RouteDataWrapper routeDataWrapper : routeDatas) {
                    localFlag = false;
                    tempRname1 = tempList1.get(0).getrName();
                    tempRname2 = tempList2.get(0).getrName();
                    tempRname3 = routeDataWrapper.getRouteData1().get(0).getrName();
                    tempRname4 = routeDataWrapper.getRouteData2().get(0).getrName();
                    if (((tempRname1.equals(tempRname3)) && (tempRname2.equals(tempRname4)) || d==routeDataWrapper.getDistTotal())) {

                        localFlag = true;
                        break;
                    }
                }
                if (!localFlag) {

                    routeDatas.add(routeDataTemp);
//					}
//
                }

            }
//					if(!(routeDataWrapper.getDistTotal()==d ) && !(routeDataWrapper.getRouteData1().get(0).getrName().equals(routeDataTemp.getRouteData1().get(0).getrName())) && !(routeDataWrapper.getRouteData2().get(0).getrName().equals(routeDataTemp.getRouteData2().get(0).getrName()))){

        }
        System.out.println("" + routeDatas);
        return routeDatas;
    }
}

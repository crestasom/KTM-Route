package com.crestaSom.implementation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import android.content.Context;

import com.crestaSom.database.Database;
import com.crestaSom.model.Edge;
import com.crestaSom.model.Route;
import com.crestaSom.model.Vertex;

public class DijkstrasAlgorithm {

	private List<Vertex> nodes;
	private List<Edge> edges;
	private List<Route> routes;
	private Set<Vertex> settledNodes;
	// private Set<Vertex> unSettledNodes;
	private Queue<Vertex> unSettledNodes;
	private Map<Vertex, Vertex> predecessors, predecessors1;
	private Map<Vertex, Double> distance;
	private double totalDistance;
	private List<Route> allRoutes;
	private Edge flagEdge;
	Database db;
	int refSource, refSource1, refDest, refDest1;
	private boolean reverseFlag = false;
	boolean flag,flag1,flag2;
	public long totalTime;

	int i = 0;

	public DijkstrasAlgorithm(Context context) {
		totalTime = 0;
		db = new Database(context);

	}

	public Edge getFlagEdge() {
		return flagEdge;
	}

	public void setFlagEdge(Edge flagEdge) {
		this.flagEdge = flagEdge;
	}

	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}

	public int getI() {
		return i;
	}

	public double getTotalDistance() {
		return totalDistance;
	}

	public void execute(List<Vertex> nodes, List<Edge> edges, Vertex source,
			Vertex destination,Vertex destination1,Vertex destination2, boolean flagrev) throws SQLException {
		flag=true;
		flag1=true;
		flag2=true;
		if(destination1==null){
			flag1=false;
		}
		if(destination2==null){
			flag2=false;
		}
			
		this.nodes = nodes;
		this.edges = edges;
		settledNodes = new HashSet<Vertex>();
		// unSettledNodes = new HashSet<Vertex>();
		unSettledNodes = new PriorityQueue<Vertex>();
		//distance = new HashMap<Vertex, Double>();
		predecessors = new HashMap<Vertex, Vertex>();
		predecessors1 = new HashMap<Vertex, Vertex>();
		// distance.put(source, 0.00);
		// unsetteledNodes.add(source);
		source.setDistanceFromSource(0.00);
		unSettledNodes.add(source);
		System.out.println(unSettledNodes);
		// unSettledNodes.
		// while (!unSettledNodes.isEmpty() && !isSettled(destination) ) {
		while (!unSettledNodes.isEmpty()&&(flag||flag1||flag2)) {
		//	System.out.println("UnsettledNodes:"+unSettledNodes);
			Vertex node = unSettledNodes.poll();
			if(node.equals(destination)){
				System.out.println(destination+" Completed");
				flag=false;
			}else if(node.equals(destination1)){
				System.out.println(destination1+" Completed");
				flag1=false;
			}else if(node.equals(destination2)){
				System.out.println(destination2+" Completed");
				flag2=false;
			}
			
			// node.setDistanceFromSource(0.00);
			// System.out.println("Node added:"+node);
			settledNodes.add(node);
			// unSettledNodes.remove(node);
			findMinimalDistances(node, flagrev);
			// System.out.println(node+"\n");
			// System.out.println(unSettledNodes);
		}
		System.out.println(unSettledNodes+"\n");
//		stopTime = System.currentTimeMillis();
//		elapsedTime = stopTime - startTime;
//		totalTime += elapsedTime;
//		db.close();
		// System.out.println("Total Time:"+totalTime);
		// System.out.println("Predecessors:"+predecessors);
		// System.out.println("Predecessors1:"+predecessors1);
		// System.out.println("1" + distance);
		// System.out.println("Completed");
	}

	private void findMinimalDistances(Vertex node, boolean flag) {
		List<Vertex> adjacentNodes = getNeighbors(node, flag);
		Vertex temp = new Vertex();
		// System.out.println(adjacentNodes);
		for (Vertex target : adjacentNodes) {
			// System.out.println(getShortestDistance(target));
			if (getShortestDistance(target) > getShortestDistance(node)
					+ getDistance(node, target)) {
				// System.out.println("Flag set");
				// distance.put(target,
				// getShortestDistance(node) + getDistance(node, target));
				// if (!unSettledNodes.contains(target)) {
				target.setDistanceFromSource(getShortestDistance(node)
						+ getDistance(node, target));
				temp = predecessors.get(target);
				// System.out.println("Printed frm MinimalDistance:"+temp+"\t"+target);
				predecessors.put(target, node);
				if (temp == null) {
					predecessors1.put(target, node);
				} else {
					predecessors1.put(target, temp);
				}

				unSettledNodes.add(target);
				// }else{

				// }
				// unSettledNodes.
			}
		}
		// for(Vertex v:adjacentNodes){
		// System.out.println(distance.get(v));
		// }
		// System.exit(0);

	}

	private double getDistance(Vertex node, Vertex target) {
		for (Edge edge : edges) {
			if (edge.getSource().equals(node)
					&& edge.getDestination().equals(target)) {
				return edge.getWeight();
			} else if (edge.getSource().equals(target)
					&& edge.getDestination().equals(node)) {
				return edge.getWeight();
			}
		}
		throw new RuntimeException("Should not happen");
	}

	private List<Vertex> getNeighbors(Vertex node, boolean flag) {
		List<Vertex> neighbors = new ArrayList<Vertex>();
		Iterator<Edge> edgeIterator = edges.iterator();
		while (edgeIterator.hasNext()) {
			Edge edge = edgeIterator.next();
			if (flag) {
				if (edge.getSource().equals(node)
						&& !isSettled(edge.getDestination())) {
					neighbors.add(edge.getDestination());
				} else if (edge.getDestination().equals(node)
						&& !isSettled(edge.getSource()) && !edge.isOneway()) {
					neighbors.add(edge.getSource());
				}
			} else {
				if (edge.getSource().equals(node)
						&& !isSettled(edge.getDestination())
						&& !edge.isOneway()) {
					neighbors.add(edge.getDestination());
				} else if (edge.getDestination().equals(node)
						&& !isSettled(edge.getSource())) {
					neighbors.add(edge.getSource());
				}
			}
		}
		// for (Edge edge : edges) {
		// if (edge.getSource().equals(node)
		// && !isSettled(edge.getDestination())) {
		// neighbors.add(edge.getDestination());
		// }else if(edge.getDestination().equals(node) &&
		// !isSettled(edge.getSource()) && !edge.isOneway()){
		// neighbors.add(edge.getSource());
		// }
		// }
		// for(Vertex v:neighbors){
		// System.out.println(v);
		// }
		// System.exit(0);
		return neighbors;

	}

	private Vertex getMinimum(Queue<Vertex> vertexes) {
		Vertex minimum = null;
		minimum = vertexes.poll();

		// for (Vertex vertex : vertexes) {
		// if (minimum == null) {
		// minimum = vertex;
		// } else {
		// if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
		// minimum = vertex;
		// }
		// }
		// }
		// System.out.println(String.valueOf(minimum));

		return minimum;
	}

	private boolean isSettled(Vertex vertex) {
		return settledNodes.contains(vertex);
	}

	private double getShortestDistance(Vertex destination) {
		Double d = destination.getDistanceFromSource();
		for (Vertex v : unSettledNodes) {
			if (v.equals(destination))
				d = v.getDistanceFromSource();
		}
		// if (d == null) {
		// return Double.MAX_VALUE;
		// } else {
		// return d;
		// }
		return d;
	}

	public double getTotalDistance(Vertex source, Vertex dest) {
		return totalDistance;

	}

	/*
	 * This method returns the path from the source to the selected target and
	 * NULL if no path exists
	 */
	public LinkedList<Vertex> getPath(Vertex target, boolean flag) {
		double distance;
		setTotalDistance(0);
		LinkedList<Vertex> path = new LinkedList<Vertex>();
		Vertex step = target, temp;
		Edge e = new Edge();
		// check if a path exists
		if (predecessors.get(step) == null) {
			return null;
		}
		path.add(step);
		while (predecessors.get(step) != null) {
			temp = step;
			step = predecessors.get(step);
			distance = getDistance(temp, step);
			setTotalDistance(getTotalDistance() + distance);
			path.add(step);
		}
		// Put it into the correct order
		if (flag == true)
			Collections.reverse(path);
		return path;
	}

	public LinkedList<Edge> getPathEdge(Vertex target, boolean revFlag) {
		reverseFlag = revFlag;
		setTotalDistance(0.0);
		double distance = 0.0;
		LinkedList<Edge> path = new LinkedList<Edge>();
		Vertex step = target, temp;
		Edge e = new Edge();
		// check if a path exists
		// System.out.println(predecessors.get(step));
		// System.out.println(predecessors);
		if (predecessors.get(step) == null) {
			return null;
		}
		// System.out.println(predecessors.toString());
		// path.add(step.getName());
		while (predecessors.get(step) != null) {
			temp = step;
			step = predecessors.get(step);
			distance = getDistance(temp, step);
			e = getEdge(temp, step);
			// System.out.println(e.toString());
			path.add(e);
			setTotalDistance(getTotalDistance() + distance);

			// path.add(step.toString());
			// System.out.println("i++"+step);
		}
		// System.out.println(path);
		// Put it into the correct order
		// if(!reverseFlag){
		Collections.reverse(path);
		// }
		return path;
	}

	public Edge getEdge(Vertex source, Vertex dest) {
		Edge e = null;
		for (Edge e1 : edges) {
			if ((e1.getSource().equals(source) && e1.getDestination().equals(
					dest))) {
				e = e1;
				e.setFlag(false);

				break;

			} else if ((e1.getSource().equals(dest) && e1.getDestination()
					.equals(source))) {
				e = e1;
				e.setFlag(true);
				// if(!reverseFlag){
				// e.setFlag(!e.isFlag());
				// }
				break;
			}

		}
		// System.out.println("printed from getEdge:"+e);

		return e;
	}

	public Route getRoute(int id) {
		Route r = null;
		return r;
	}

	public List<Route> getRealRoute() {
		List<Route> routes = null;
		return routes;
	}

}

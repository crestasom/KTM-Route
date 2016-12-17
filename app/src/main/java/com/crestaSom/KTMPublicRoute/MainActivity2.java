package com.crestaSom.KTMPublicRoute;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import com.crestaSom.implementation.KtmPublicRoute;
import com.crestaSom.model.Vertex;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity2 extends Activity {
	private MapView mMapView;
	private MapController mMapController;
	List<Integer> cList;
	TextView routeInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		cList = new ArrayList<Integer>();
		cList.add(Color.BLUE);
		cList.add(Color.GREEN);
		cList.add(Color.MAGENTA);
		cList.add(Color.CYAN);
		cList.add(Color.DKGRAY);
		cList.add(Color.YELLOW);
		routeInfo=(TextView)findViewById(R.id.routeInfo);
		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setTileSource(TileSourceFactory.MAPNIK);
		mMapView.setBuiltInZoomControls(true);
		mMapController = (MapController) mMapView.getController();
		mMapController.setZoom(14);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setMultiTouchControls(true);
		mMapView.setMaxZoomLevel(15);
		mMapView.setMinZoomLevel(13);
		BoundingBoxE6 boundingBox=new BoundingBoxE6(27.79778, 85.583496, 27.589241, 85.216141);
		//new BoundingBoxE6(north, east, south, west)
		mMapView.setScrollableAreaLimit(boundingBox);
		// double latCenter,LongCenter;

		// GeoPoint gPt = new GeoPoint(27.7, 85.3);
		// mMapController.setCenter(gPt);

		// Intent i=getIntent();
		DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("data");
		List<Vertex> path = dw.getvList();
		boolean flag=getIntent().getBooleanExtra("flag", false);
		Log.d("Flag",flag+"");
		List<Vertex> vertexList = null;
		int i = 0;
		Log.d("i", "i:"+i);
		System.out.println(flag+" is value of flag");
		double latCode = 0, longCode = 0;
		for (Vertex v : path) {
			latCode += v.getLatCode();
			longCode += v.getLongCode();
		}
		
		String display="";
		if(!flag){
		display+="Route View:"+path.get(0)+" - "+path.get(path.size()-1);
		}else{
			String rName=getIntent().getStringExtra("routeName");
			display+="Route View:"+rName;
		}
		routeInfo.setText(display);
		latCode /= path.size();
		longCode /= path.size();
//		GeoPoint p1=new GeoPoint(latCode,longCode);
		mMapController.setCenter(new GeoPoint(path.get(0).getLatCode(),path.get(0).getLongCode()));
//		mMapController.setCenter(p1);
		new ArrayList<Integer>();
		// Map<Integer,List<Vertex>> pathRoute;
		Map<List<Integer>, List<Vertex>> pathRoute;
		KtmPublicRoute imp = new KtmPublicRoute(this);
		while (!path.isEmpty()) {
			vertexList = new ArrayList<Vertex>();
			if (path.size() == 1) {
				break;
			}
			pathRoute = imp.findRoutePath(path);
			Log.d("path", "" + pathRoute);
		
			Iterator<Map.Entry<List<Integer>, List<Vertex>>> it = pathRoute.entrySet().iterator();
			while (it.hasNext()) {
			
				Map.Entry<List<Integer>, List<Vertex>> pair = it.next();
			
				vertexList = pair.getValue();
				createMarker(vertexList, i);
				Log.d("Vertex", "" + vertexList);

			
			}
			i++;
		}
		

		

	}

	void createMarker(List<Vertex> path, int color) {

		Log.d("", "Vertex Path in Map:" + path.toString());
		Log.d("Line Color", "pos:" + color);
		
		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		DefaultResourceProxyImpl resourceProxy1 = new CustomResourceProxy(getApplicationContext());
		PathOverlay myPath = new PathOverlay(cList.get(color), 15, resourceProxy1);
		// Double latCode=0.0,longCode=0.0;
	//	items.add(new OverlayItem("", "",pa));
		
		for (Vertex v : path) {
			// items.clear();
			// latCode+=v.getLatCode();
			// longCode+=v.getLongCode();
			GeoPoint p1 = new GeoPoint(v.getLatCode(), v.getLongCode());
			items.add(new OverlayItem(v.getName(), "", p1));

			myPath.addPoint(p1);
		}

		mMapView.getOverlays().add(myPath);
		ItemizedIconOverlay<OverlayItem> currentLocationOverlay;
		DefaultResourceProxyImpl resourceProxy = new CustomResourceProxy(getApplicationContext());
		// new DefaultResourceProxyImpl(getApplicationContext());

		currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
					@Override
					public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
						Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
						return true;

					}

					@Override
					public boolean onItemLongPress(final int index, final OverlayItem item) {
						return true;
					}
				}, resourceProxy);

		mMapView.getOverlays().add(currentLocationOverlay);
		mMapView.invalidate();
//		
//		ArrayList<OverlayItem> items1 = new ArrayList<OverlayItem>();
//		DefaultResourceProxyImpl resourceProxy11 = new CustomResourceProxy1(getApplicationContext());
//		PathOverlay myPath1 = new PathOverlay(cList.get(color), 15, resourceProxy11);
//		
//		// Double latCode=0.0,longCode=0.0;
//		GeoPoint p1 = new GeoPoint(path.get(path.size()-1).getLatCode(), path.get(path.size()-1).getLongCode());
//		items1.add(new OverlayItem("", "",p1));
////		//myPath.clearPath();
//		myPath1.addPoint(p1);
//		mMapView.getOverlays().add(myPath1);
//		mMapView.getOverlays().add(myPath);
		// Overlay touchOverlay = new Overlay(this){
		// ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
		// @Override
		// protected void draw(Canvas arg0, MapView arg1, boolean arg2) {
		//
		// }
		// @Override
		// public boolean onSingleTapConfirmed(final MotionEvent e, final
		// MapView mapView) {
		//
		// final Drawable marker =
		// getApplicationContext().getResources().getDrawable(R.drawable.icons);
		// Projection proj = mapView.getProjection();
		// GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(),
		// (int)e.getY());
		// String longitude =
		// Double.toString(((double)loc.getLongitudeE6())/1000000);
		// String latitude =
		// Double.toString(((double)loc.getLatitudeE6())/1000000);
		// Toast.makeText(getApplicationContext(), "- Latitude = " + latitude +
		// ", Longitude = " + longitude,Toast.LENGTH_SHORT ).show();
		//
		// ArrayList<OverlayItem> overlayArray = new ArrayList<OverlayItem>();
		// OverlayItem mapItem = new OverlayItem("", "", new
		// GeoPoint((((double)loc.getLatitudeE6())/1000000),
		// (((double)loc.getLongitudeE6())/1000000)));
		// mapItem.setMarker(marker);
		// overlayArray.add(mapItem);
		// DefaultResourceProxyImpl resourceProxy = new
		// CustomResourceProxy(getApplicationContext());
		//
		// if(anotherItemizedIconOverlay==null){
		// //anotherItemizedIconOverlay = new
		// ItemizedIconOverlay<OverlayItem>(getApplicationContext(),
		// overlayArray,null);
		// anotherItemizedIconOverlay=new
		// ItemizedIconOverlay<OverlayItem>(overlayArray,
		// new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
		// @Override
		// public boolean onItemSingleTapUp(final int index, final OverlayItem
		// item) {
		// return true;
		// }
		// @Override
		// public boolean onItemLongPress(final int index, final OverlayItem
		// item) {
		// return true;
		// }
		// }, resourceProxy);
		// mapView.getOverlays().add(anotherItemizedIconOverlay);
		// mapView.invalidate();
		// }else{
		// mapView.getOverlays().remove(anotherItemizedIconOverlay);
		// mapView.invalidate();
		// //anotherItemizedIconOverlay = new
		// ItemizedIconOverlay<OverlayItem>(getApplicationContext(),
		// overlayArray,null);
		// anotherItemizedIconOverlay=new
		// ItemizedIconOverlay<OverlayItem>(overlayArray,
		// new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
		// @Override
		// public boolean onItemSingleTapUp(final int index, final OverlayItem
		// item) {
		// return true;
		// }
		// @Override
		// public boolean onItemLongPress(final int index, final OverlayItem
		// item) {
		// return true;
		// }
		// }, resourceProxy);
		// mapView.getOverlays().add(anotherItemizedIconOverlay);
		// }
		// return true;
		// }
		// };
		// mMapView.getOverlays().add(touchOverlay);

	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

}

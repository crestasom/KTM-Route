package com.crestaSom.KTMPublicRoute;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Queue;

public class MainActivity2 extends Activity implements View.OnClickListener {
	private MapView mMapView;
	private MapController mMapController;
	List<Integer> cList;
	TextView routeInfo;
	Button tracker;
	ImageView currentPosition,zoomIn,zoomOut;
	String provider;
	Criteria cri;
	ItemizedIconOverlay<OverlayItem> currentLocationOverlay;
	ArrayList<OverlayItem> items;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		currentPosition=(ImageView)findViewById(R.id.current_location);
		currentPosition=(ImageView)findViewById(R.id.current_location);
		zoomIn=(ImageView)findViewById(R.id.zoomin);
		zoomOut=(ImageView)findViewById(R.id.zoomout);
//		zoomIn.setVisibility(View.INVISIBLE);
//		zoomOut.setVisibility(View.INVISIBLE);
		zoomIn.setOnClickListener(this);
		zoomOut.setOnClickListener(this);
		cList = new ArrayList<Integer>();
		currentPosition.setOnClickListener(this);
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
		mMapView.setBuiltInZoomControls(false);
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
			currentPosition.setVisibility(View.INVISIBLE);
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

//		Log.d("", "Vertex Path in Map:" + path.toString());
//		Log.d("Line Color", "pos:" + color);
		
		items = new ArrayList<OverlayItem>();
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

	@Override
	public void onClick(View view) {
		if(view.getId()==R.id.current_location) {
			FetchCordinates gpstracker = new FetchCordinates();
			gpstracker.execute();
		}else if(view.getId()==R.id.zoomin){
			mMapController.zoomIn();
		}else if(view.getId()==R.id.zoomout){
			mMapController.zoomOut();
		}
	}


	public class FetchCordinates extends AsyncTask<String, Integer, String> {
		ProgressDialog progDailog = null;

		public double lati = 0.0;
		public double longi = 0.0;

		public LocationManager mLocationManager;
		public VeggsterLocationListener mVeggsterLocationListener;

		@Override
		protected void onPreExecute() {
//			ConnectivityManager connectivityMgr = (ConnectivityManager)
//					getSystemService(Context.CONNECTIVITY_SERVICE);
//			NetworkInfo[] nwInfos = connectivityMgr.getAllNetworkInfo();
//			for (NetworkInfo nwInfo : nwInfos) {
//				Log.d(TAG, "Network Type Name: " + nwInfo.getTypeName());
//				Log.d(TAG, "Network available: " + nwInfo.isAvailable());
//				Log.d(TAG, "Network c_or-c: " + nwInfo.isConnectedOrConnecting());
//				Log.d(TAG, "Network connected: " + nwInfo.isConnected());
//			}
			mVeggsterLocationListener = new VeggsterLocationListener();
			mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			cri = new Criteria();
			provider = mLocationManager.getBestProvider(cri, false);
			if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
				//	Toast.makeText(MainActivity.this,"Network Mode:"+LocationManager.NETWORK_PROVIDER.toString(),Toast.LENGTH_SHORT).show();
				mLocationManager.requestLocationUpdates(provider, 0, 0,
						mVeggsterLocationListener);
//				Location currentL=mLocationManager.getLastKnownLocation(provider);
//				lati=currentL.getLatitude();
//				longi=currentL.getLongitude();

			}else{
				//	Toast.makeText(MainActivity.this,"GPS Mode",Toast.LENGTH_SHORT).show();
				mLocationManager.requestLocationUpdates(provider, 0, 0,
						mVeggsterLocationListener);
			}


			progDailog = new ProgressDialog(MainActivity2.this);
			progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					FetchCordinates.this.cancel(true);
				}
			});
			progDailog.setMessage("Detecting your current location....");
			progDailog.setIndeterminate(false);
			progDailog.setCancelable(true);
			progDailog.show();

		}

		@Override
		protected void onCancelled(){
			System.out.println("Cancelled by user!");
			Toast.makeText(MainActivity2.this, "Location Detection Cancel", Toast.LENGTH_SHORT).show();
			progDailog.dismiss();
			mLocationManager.removeUpdates(mVeggsterLocationListener);
		}

		@Override
		protected void onPostExecute(String result) {
			progDailog.dismiss();
			//mMapView.getOverlays().remove(currentLocationOverlay);
			ArrayList<OverlayItem> itemst=new ArrayList<OverlayItem>();
			GeoPoint p1 = new GeoPoint(lati, longi);
			mMapController.setCenter(p1);
			itemst.add(new OverlayItem("Current Position", "", p1));
			DefaultResourceProxyImpl resourceProxy = new CustomResourceProxy1(getApplicationContext());
			currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(itemst,
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


		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			int x=0;
			while (this.lati == 0.0) {
//				Log.d("x:",""+x++);
//				System.out.println("x:"+x++);
			}
			return null;
		}

		public class VeggsterLocationListener implements LocationListener {

			@Override
			public void onLocationChanged(Location location) {

				int lat = (int) location.getLatitude(); // * 1E6);
				int log = (int) location.getLongitude(); // * 1E6);
				int acc = (int) (location.getAccuracy());

				String info = location.getProvider();
				try {

					// LocatorService.myLatitude=location.getLatitude();

					// LocatorService.myLongitude=location.getLongitude();

					lati = location.getLatitude();
					longi = location.getLongitude();

				} catch (Exception e) {
					// progDailog.dismiss();
					// Toast.makeText(getApplicationContext(),"Unable to get Location"
					// , Toast.LENGTH_LONG).show();
				}

			}

			@Override
			public void onProviderDisabled(String provider) {
				Log.i("OnProviderDisabled", "OnProviderDisabled");
			}

			@Override
			public void onProviderEnabled(String provider) {
				Log.i("onProviderEnabled", "onProviderEnabled");
			}

			@Override
			public void onStatusChanged(String provider, int status,
										Bundle extras) {
				Log.i("onStatusChanged", "onStatusChanged");

			}

		}

	}

}

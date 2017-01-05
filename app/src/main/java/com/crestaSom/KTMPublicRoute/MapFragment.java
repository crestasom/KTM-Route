package com.crestaSom.KTMPublicRoute;


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
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crestaSom.KTMPublicRoute.customMapProxy.CustomResourceProxy;
import com.crestaSom.KTMPublicRoute.customMapProxy.CustomResourceProxy1;
import com.crestaSom.implementation.KtmPublicRoute;
import com.crestaSom.model.Vertex;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements View.OnClickListener {
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
    Boolean flag;
    List<Vertex> path,localPath;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_map, container, false);
                currentPosition=(ImageView)view.findViewById(R.id.current_location);
        currentPosition=(ImageView)view.findViewById(R.id.current_location);
        zoomIn=(ImageView)view.findViewById(R.id.zoomin);
        zoomOut=(ImageView)view.findViewById(R.id.zoomout);
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
        routeInfo=(TextView)view.findViewById(R.id.routeInfo);
        mMapView = (MapView) view.findViewById(R.id.mapview);
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


        //receiving data from activity
        Bundle bundle=getArguments();
        DataWrapper dw =(DataWrapper) bundle.getSerializable("vList");
        path=dw.getvList();
        flag=bundle.getBoolean("flag");
        localPath=new ArrayList<>();
        localPath.addAll(path);
        Log.d("Vertex List",path.toString());
        List<Vertex> vertexList = null;
        int i = 0;
        Log.d("i", "i:"+i);
        System.out.println(flag+" is value of flag");
        double latCode = 0, longCode = 0;
        for (Vertex v : path) {
            latCode += v.getLatCode();
            longCode += v.getLongCode();
        }
        if(flag){
            currentPosition.setVisibility(View.INVISIBLE);
        }

        String display="";
        routeInfo.setText(display);
        mMapController.setCenter(new GeoPoint(path.get(0).getLatCode(),path.get(0).getLongCode()));
        new ArrayList<Integer>();
        Map<List<Integer>, List<Vertex>> pathRoute;
        KtmPublicRoute imp = new KtmPublicRoute(getActivity());
        while (!localPath.isEmpty()) {
            vertexList = new ArrayList<Vertex>();
            if (localPath.size() == 1) {
                break;
            }
            pathRoute = imp.findRoutePath(localPath);
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


        return view;
    }


    void createMarker(List<Vertex> path, int color) {

//		Log.d("", "Vertex Path in Map:" + path.toString());
//		Log.d("Line Color", "pos:" + color);

        items = new ArrayList<OverlayItem>();
        DefaultResourceProxyImpl resourceProxy1 = new CustomResourceProxy(getActivity());
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

        DefaultResourceProxyImpl resourceProxy = new CustomResourceProxy(getActivity());
        // new DefaultResourceProxyImpl(getApplicationContext());

        currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Toast.makeText(getActivity(), item.getTitle(), Toast.LENGTH_SHORT).show();
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
    public void onClick(View view) {
        if(view.getId()==R.id.current_location) {
            String off = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (off.isEmpty()) {
                Toast.makeText(getActivity().getApplicationContext(), "Please Enable Location", Toast.LENGTH_SHORT).show();
                Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(onGPS);
            }
            final FetchCordinates gpstracker = new FetchCordinates();
            gpstracker.execute();

            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    // Do nothing
                    Log.d("Time left:", millisUntilFinished + "");
                }

                public void onFinish() {
                    Log.d("Status message", "Finish reached of Countdown");
                    Log.d("async task status", gpstracker.getStatus().toString());
                    if (gpstracker.getStatus() == AsyncTask.Status.RUNNING) {
                        Log.d("Asnc Task canceal", "true");
                        gpstracker.cancel(true);
                    }
                }

            }.start();


        }else if(view.getId()==R.id.zoomin){
            mMapController.zoomIn();
        }else if(view.getId()==R.id.zoomout){
            mMapController.zoomOut();
        }
    }

    public class FetchCordinates extends AsyncTask<String, Integer, String> {
        ProgressDialog progDailog = null;
        Boolean running = true;
        public double lati = 0.0;
        public double longi = 0.0;

        public LocationManager mLocationManager;
        public FetchCordinates.VeggsterLocationListener mVeggsterLocationListener;

        @Override
        protected void onPreExecute() {
            mVeggsterLocationListener = new FetchCordinates.VeggsterLocationListener();

            mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            cri = new Criteria();
            provider = mLocationManager.getBestProvider(cri, false);
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                //	Toast.makeText(MainActivity.this,"Network Mode:"+LocationManager.NETWORK_PROVIDER.toString(),Toast.LENGTH_SHORT).show();
                mLocationManager.requestLocationUpdates(provider, 30000, 0,
                        mVeggsterLocationListener);

            } else {
                //	Toast.makeText(MainActivity.this,"GPS Mode",Toast.LENGTH_SHORT).show();
                mLocationManager.requestLocationUpdates(provider, 0, 0,
                        mVeggsterLocationListener);

            }


            progDailog = new ProgressDialog(getActivity());
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
        protected void onCancelled() {
            Log.d("Cancel message", "Cancelled by user!");
            Toast.makeText(getActivity().getApplicationContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
            progDailog.dismiss();
            running = false;
            this.cancel(true);
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
            DefaultResourceProxyImpl resourceProxy = new CustomResourceProxy1(getActivity());
            currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(itemst,
                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                        @Override
                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                            Toast.makeText(getActivity(), item.getTitle(), Toast.LENGTH_SHORT).show();
                            return true;

                        }

                        @Override
                        public boolean onItemLongPress(final int index, final OverlayItem item) {
                            return true;
                        }
                    }, resourceProxy);

            mMapView.getOverlays().add(currentLocationOverlay);
            mMapView.invalidate();
//            progDailog.dismiss();
//            Log.d("coordinates", lati + longi + "");
//            Queue<Vertex> sourceV = imp.getNearestStop(lati, longi);
//            Vertex v;
//            //source.setText(sourceV.getName());
//            List<Vertex> vList = new ArrayList<Vertex>();
//            for (int i = 0; i < 4; i++) {
//                v = sourceV.poll();
//                if (v.getDistanceFromSource() < 1.0)
//                    Log.d("Polled Vertex", v.getName());
//                vList.add(v);
//            }
//            mLocationManager.removeUpdates(mVeggsterLocationListener);
//            Log.d("vertex", vList.toString());
//            Intent i = new Intent(getActivity().getApplicationContext(), NearestStopSelection.class);
//            i.putExtra("data", new DataWrapper(vList));
//            startActivityForResult(i, 100);
            //Toast.makeText(MainActivity.this,
//					"LATITUDE :" + lati + " LONGITUDE :" + longi,
//					Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            int x = 0;
            while (this.lati == 0.0 && !this.isCancelled()) {
//				Log.d("x:",""+x++);
//				System.out.println("x:"+x++);
//                if(isCancelled()){
//                    break;
//                }
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

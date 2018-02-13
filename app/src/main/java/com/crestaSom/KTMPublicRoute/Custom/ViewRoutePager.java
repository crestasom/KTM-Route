package com.crestaSom.KTMPublicRoute.Custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crestaSom.KTMPublicRoute.R;
import com.crestaSom.model.Route;
import com.crestaSom.viewPageAdapter.ViewPagerAdapter;

import java.util.List;

/**
 * Created by crestaSom on 10/6/2017.
 */

public class ViewRoutePager extends RecyclerView.Adapter<ViewRoutePager.RouteView> {

    Context mContext;
    List<Route> routeList;
    int language;

    public ViewRoutePager(Context context,List<Route> routes,int lang){
        language=lang;
        mContext=context;
        routeList=routes;
    }

    @Override
    public RouteView onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View view=inflater.inflate(R.layout.route_list_row,parent,false);
        return new RouteView(view);
    }

    @Override
    public void onBindViewHolder(RouteView holder, int position) {
        holder.view.setTag(routeList.get(position).getId());
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(mContext);
        language=Integer.parseInt(prefs.getString("language", "1"));
        if(language==1) {
            holder.routeName.setText(routeList.get(position).getName());
        }else {
            holder.routeName.setText(routeList.get(position).getNameNepali());
        }
        String vehicleType=routeList.get(position).getVehicleType();
        switch (vehicleType){
            case "Bus":
                holder.routeType.setImageResource(R.drawable.bus);
                break;
            case "Micro Bus":
                holder.routeType.setImageResource(R.drawable.micro);
                break;
            case "Tempo":
                holder.routeType.setImageResource(R.drawable.tempo);
                break;
            default:
                holder.routeType.setImageResource(R.drawable.bus);
        }
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public class RouteView extends RecyclerView.ViewHolder{
        TextView routeName;
        ImageView routeType;
        View view;

        public RouteView(View itemView) {
            super(itemView);
            view=itemView;
            routeName=(TextView)itemView.findViewById(R.id.textView6);
            routeType=(ImageView)itemView.findViewById(R.id.imageView);
        }
    }

    public void swapData(List<Route> routes){
        if(routeList!=null){
            routeList.clear();
        }
        routeList.addAll(routes);
        notifyDataSetChanged();
    }
}

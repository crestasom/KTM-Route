package com.crestaSom.KTMPublicRoute.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crestaSom.KTMPublicRoute.R;

/**
 * Created by user on 1/20/2017.
 */

public class CustomAdapter extends BaseAdapter {

    private Context mContext;
    private String[]  Title;
    LayoutInflater inflater;
    private int[] imge;

    public CustomAdapter(Context context, String[] text1,int[] imageIds) {
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        Title = text1;
        imge = imageIds;

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return Title.length;
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        //LayoutInflater inflater = getLayoutInflater();
        View row;
        row = inflater.inflate(R.layout.row, parent, false);
        TextView title;
        ImageView i1;
        i1 = (ImageView) row.findViewById(R.id.imgIcon);
        title = (TextView) row.findViewById(R.id.txtTitle);
        title.setText(Title[position]);
        i1.setImageResource(imge[position]);

        return (row);
    }
}

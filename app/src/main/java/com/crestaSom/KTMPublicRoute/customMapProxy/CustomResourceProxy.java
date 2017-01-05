package com.crestaSom.KTMPublicRoute.customMapProxy;

import org.osmdroid.DefaultResourceProxyImpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.crestaSom.KTMPublicRoute.R;

public class CustomResourceProxy extends DefaultResourceProxyImpl {

    private final Context mContext;
    public CustomResourceProxy(Context pContext) {
         super(pContext);
      mContext = pContext;
    }

    @Override
  public Bitmap getBitmap(final bitmap pResId) {
      switch (pResId){
              case person:
                   //your image goes here!!!
                   return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icons);

         }
      return BitmapFactory.decodeResource(mContext.getResources(),R.drawable.icons);
         //return super.getBitmap(pResId);
  }

  @Override
  public Drawable getDrawable(final bitmap pResId) {
      switch (pResId){
              case person:
                   //your image goes here!!!
                   return mContext.getResources().getDrawable(R.drawable.icons);
         }
         return super.getDrawable(pResId);
  }

}
package com.crestaSom.KTMPublicRoute;

import org.osmdroid.DefaultResourceProxyImpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class CustomResourceProxy1 extends DefaultResourceProxyImpl {

    private final Context mContext;
    public CustomResourceProxy1(Context pContext) {
         super(pContext);
      mContext = pContext;
    }

    @Override
  public Bitmap getBitmap(final bitmap pResId) {
      switch (pResId){
              case person:
                   //your image goes here!!!
                   return BitmapFactory.decodeResource(mContext.getResources(),R.drawable.person);

         }
      return BitmapFactory.decodeResource(mContext.getResources(),R.drawable.person);
         //return super.getBitmap(pResId);
  }

  @Override
  public Drawable getDrawable(final bitmap pResId) {
      switch (pResId){
              case person:
                   //your image goes here!!!
                   return mContext.getResources().getDrawable(R.drawable.icons_cpy);
         }
         return super.getDrawable(pResId);
  }

}
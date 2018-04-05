package org.kwansystems.sensorlog;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.graphics.*;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Stack;


public class BubbleCompassFragment extends Fragment {
    private SensorManager mSensorManager;
    public GraphView mGraphView;

    public BubbleCompassFragment() {
        saveStack = new Stack<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSensorManager = (SensorManager) (getActivity()).getSystemService(Context.SENSOR_SERVICE);
        mGraphView = new GraphView(getActivity());
        return mGraphView;
    }

    Stack<String> saveStack;
    void save(Canvas canvas, String purpose) {
        Log.d("save",purpose);
        saveStack.push(purpose);
        canvas.save();
    }

    void restore(Canvas canvas, String purpose) {
        String popped=saveStack.pop();
        Log.d("restore",popped);
        Log.d("should have restored",purpose);
        if(!purpose.equals(popped)) {
            while(!saveStack.isEmpty()) {
                Log.d("Remaining stack",saveStack.pop());
            }
            throw new RuntimeException("Unbalanced save/restore");
        } else {
            Log.d("restore","Balanced save/restore");
        }
        canvas.restore();
    }

    private class GraphView extends View implements SensorEventListener {
        private Bitmap  mBitmap;
        private Paint   mPaint = new Paint();
        private Canvas  mCanvas = new Canvas();
        private Path    mPath = new Path();
        private RectF   mRect = new RectF();
        private float   mMag[] = new float[3];
        private float   mAcc[] = new float[3];
        private int     mColors[] = new int[3];
        private float   mLastX;
        private float   mScale[] = new float[2];
        private float   mYOffset;
        private float   mMaxX;
        private float   mSpeed = 1.0f;
        private float   mWidth;
        private float   mHeight;

        public GraphView(Context context) {
            super(context);
            mColors[0] = Color.argb(192, 255, 64, 64);
            mColors[1] = Color.argb(192, 64, 128, 64);
            mColors[2] = Color.argb(192, 64, 64, 255);

            // mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mRect.set(-0.5f, -0.5f, 0.5f, 0.5f);
            mPath.arcTo(mRect, 0, 180);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            mCanvas.setBitmap(mBitmap);
            mCanvas.drawColor(0xFFFFFFFF);
            mYOffset = h * 0.5f;
            mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
            mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
            mWidth = w;
            mHeight = h;
            if (mWidth < mHeight) {
                mMaxX = w;
            } else {
                mMaxX = w-50;
            }
            mLastX = mMaxX;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            synchronized (this) {
                Log.d("BubbleCompassFragment","onDraw");
                if (mBitmap != null) {
                    final Paint paint = mPaint;
                    final Path path = mPath;

                    canvas.drawBitmap(mBitmap, 0, 0, null);

                    for (int i=0 ; i<1 ; i++) {
                        save(canvas,"MagVector");
                        canvas.translate(mWidth/2, mHeight/4);
                        drawVector(canvas,paint,path,mMag,100,0xFF404040,0xFFFF0000);
                        restore(canvas,"MagVector");
                        save(canvas,"AccVector");
                        canvas.translate(mWidth/2, mHeight*3/4);
                        drawVector(canvas,paint,path,mAcc,20,0xFF804000,0xFF8080FF);
                        restore(canvas,"AccVector");
                    }
                }
            }
        }

        private void drawVector(Canvas canvas, Paint paint, Path path, float[] mVec, float scl, final int outer, final int inner) {
            float w0 = Math.min(mWidth,mHeight);
            float w  = w0 - 32;
            paint.setColor(outer);
            int size=25;
            int linespace=size+5;
            paint.setTextSize(size);
            canvas.drawText(String.format("v[0]: %f", mVec[0]), -mWidth/2+20, -mHeight/4+linespace*0, paint);
            canvas.drawText(String.format("v[1]: %f", mVec[1]), -mWidth/2+20, -mHeight/4+linespace*1, paint);
            canvas.drawText(String.format("v[2]: %f", mVec[2]), -mWidth/2+20, -mHeight/4+linespace*2, paint);
            float lon=(float)Math.toDegrees(Math.atan2(mVec[0], mVec[1]))+180;
            if(mWidth>mHeight)lon-=90;
            float lat=(float)Math.toDegrees(Math.atan2(mVec[2],Math.hypot(mVec[1], mVec[0])));
            float slat=(float)Math.sin(Math.toRadians(lat));
            float clat=(float)Math.cos(Math.toRadians(lat));
            float mag=(float)Math.hypot(Math.hypot(mVec[0],mVec[1]), mVec[2]);
            canvas.drawText(String.format("lon:  %f", lon),  -mWidth/2+20,-mHeight/4+linespace*3, paint);
            canvas.drawText(String.format("lat:  %f", lat),  -mWidth/2+20,-mHeight/4+linespace*4, paint);
            canvas.drawText(String.format("clat: %f", clat), -mWidth/2+20,-mHeight/4+linespace*5, paint);
            canvas.drawText(String.format("mag:  %f", mag),  -mWidth/2+20,-mHeight/4+linespace*6, paint);
            canvas.drawLine(0,-mHeight/4,0,mHeight/4,paint);
            canvas.drawLine(-mWidth/2,0,mWidth/2,0,paint);
            canvas.scale(w*mag/scl, w*mag/scl);
            save(canvas,"Main rotation"); //1 main rotation
            canvas.rotate(lon);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawOval(mRect, paint);
            paint.setColor(inner);
            canvas.drawPath(path, paint);
            save(canvas, "cos scaling"); //2. cos scaling
            canvas.scale(1, slat);
            if(lat<0) paint.setColor(outer);
            canvas.drawOval(mRect, paint);
            restore(canvas,"cos scaling");                     //2.
            if(lat>0) {
                paint.setColor(0xFFFFFFFF);
                canvas.drawLine(-0.05f, clat*0.5f-0.05f,  0.05f, clat*0.5f+0.05f, paint);
                canvas.drawLine( 0.05f, clat*0.5f-0.05f, -0.05f, clat*0.5f+0.05f, paint);
            } else {
                paint.setColor(0xFFFFFFFF);
                canvas.drawLine(-0.05f, -clat*0.5f-0.05f,  0.05f, -clat*0.5f+0.05f, paint);
                canvas.drawLine( 0.05f, -clat*0.5f-0.05f, -0.05f, -clat*0.5f+0.05f, paint);
            }
            save(canvas,"compass rose rotation"); //2. Compass rose rotation
            paint.setColor(outer);
            for(int ii=0;ii<72;ii++) {
                canvas.drawLine(0, 0.5f, 0, 0.55f+((ii%3==0)?0.1f:0.0f)+(ii==0?0.15f:0.0f), paint);
                canvas.rotate(5);
            }
            paint.setColor(0xFFFFFFFF);
            paint.setStyle(Paint.Style.STROKE);
            RectF R=new RectF();
            restore(canvas,"compass rose rotation");
            for(int ii=0;ii<90;ii+=15) {
                float s=(float)Math.sin(Math.toRadians(ii));
                R.set(0.5f*s,0.5f*s,-0.5f*s,-0.5f*s);
                canvas.drawOval(R,paint);
            }
            for(int ii=0;ii<180;ii+=30) {
                canvas.drawLine(0f,-0.5f,0f,0.5f,paint);
                canvas.rotate(30);
            }
            restore(canvas,"Main rotation");                    //1.
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values=event.values;
            synchronized (this) {
                if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
                    for (int i=0 ; i<3 ; i++) {
                        mAcc[i] = values[i];
                    }
                } else {
                    for (int i=0 ; i<3 ; i++) {
                        mMag[i] = values[i];
                    }
                }
                invalidate();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

}
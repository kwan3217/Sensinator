package org.kwansystems.sensorlog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.SensorManager;
import android.location.GpsStatus.NmeaListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GPSPlotView extends View implements NmeaListener {
    private Paint paint=new Paint();
    private int mWidth;
    private int mHeight;
    private int[] az=new int[199];
    private int[] el=new int[az.length];
    private int[] str=new int[el.length];
    public GPSPlotView(Context context) {
        super(context);
        for(int i=0;i<az.length;i++) {
            az[i]=-1;
            el[i]=-1;
            str[i]=-1;
        }
    }
    public GPSPlotView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (this) {
            paint.setARGB(255, 0, 0, 0);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            canvas.drawCircle(mWidth/2, mWidth/2, mWidth*0.4f, paint);
            paint.setTextSize(20);
            for(int prn=0;prn<az.length;prn++) if(az[prn]>0) {
                if(prn<100) paint.setARGB(255,0,str[prn]*255/40,255); else paint.setARGB(255,255,str[prn]*255/40,0);
                double s=Math.sin(az[prn]*Math.PI/180.0);
                double c=Math.cos(az[prn]*Math.PI/180.0);
                double r=(90-el[prn])*mWidth*0.4f/90;
                float x=mWidth/2+((float)(r*s));
                float y=mWidth/2-((float)(r*c));
                paint.setStrokeWidth(4);
                canvas.drawLine(x-5, y-5, x+5,y+5, paint);
                canvas.drawLine(x-5, y+5, x+5,y-5, paint);
                paint.setStrokeWidth(1);
                canvas.drawText(Integer.toString(prn),x+5, y-20,paint);
                canvas.drawText(Integer.toString(str[prn]),x+5, y  ,paint);
            }
        }
    }
    @Override
    public void onNmeaReceived(long timestamp, String nmea) {
        String[] p=nmea.split(",");
        if(p[0].substring(3).equals("GSV")) {
            int prnOffset=p[0].equals("$GPGSV")?0:100;
//            Log.d("GPSPlotView",nmea);
//            Log.d("GPSPlotView","N parts: "+Integer.toString(p.length));
//            Log.d("GPSPlotView","p[0]: "+p[0]);
            int n_sent=Integer.parseInt(p[1]);
//            Log.d("GPSPlotView","n_sent "+p[1]);
            int i_sent=Integer.parseInt(p[2])-1;
//            Log.d("GPSPlotView","i_sent "+Integer.toString(i_sent));
            int n_sats_sent=(p.length-4)/4;
//            Log.d("GPSPlotView","n_sats_sent "+Integer.toString(n_sats_sent));
            for(int i=0;i<n_sats_sent;i++) {
                int i_sat=i_sent*4+i;
//                Log.d("GPSPlotView","i_sat "+Integer.toString(i_sat));
                if(p[4+4*i+0].equals("")) {
                } else {
                    int prn=-1;
                    try {
                        prn=Integer.parseInt(p[4+4*i+0])+prnOffset;
                        if(p[4+4*i+1].equals("")) el [prn]=-1; else el [prn]=Integer.parseInt(p[4+4*i+1]);
                        if(p[4+4*i+2].equals("")) az [prn]=-1; else az [prn]=Integer.parseInt(p[4+4*i+2]);
                        if(p[4+4*i+3].equals("")) str[prn]=0; else str[prn]=Integer.parseInt(p[4+4*i+3]);
                    } catch (NumberFormatException E) {
                        if(prn>0) {
                            el[prn]=-1;
                            az[prn]=-1;
                            str[prn]=0;
                        }
                    }
//                    if(prn>0) Log.d("GPSPlotView","prn: "+Integer.toString(prn)+" el: "+Integer.toString(el[prn])+" az: "+Integer.toString(az[prn])+" str: "+Integer.toString(str[prn]));
                }
            }
        }
        invalidate();
    }
}

package org.kwansystems.sensinator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.*;
import android.content.*;
import android.os.IBinder;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.*;
import android.location.GpsStatus.NmeaListener;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.widget.*;

public class SensinatorService extends Service implements NmeaListener, LocationListener, SensorEventListener {
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
    public static SimpleDateFormat sdfIso = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private NotificationManager nm;
    private File folder;
    private LocationManager locationManager=null;
    private PrintWriter oufNMEA, oufSensor;
    private String oldContentText=null;
    private SensorManager mSensorManager;
    private Sensor acc, bfld, gyro, pres, ornt;
    private String prefix=null;
    @Override
    public void onCreate() {
      folder=new File(new File(Environment.getExternalStorageDirectory().getPath()), "SensorLogs");
      if(!folder.isDirectory()) {
          folder.mkdirs();
      }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(locationManager==null) {
            locationManager = (LocationManager) this
                    .getSystemService(Context.LOCATION_SERVICE);
            locationManager.addNmeaListener(this);
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            bfld = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            ornt = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            pres = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);
            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                CharSequence name = "SensorLog channel";
                String description = "SensorLog description";
                NotificationChannel channel = new NotificationChannel("SensorLog Channel ID", name, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(description);
                // Register the channel with the system
                nm.createNotificationChannel(channel);
            }
            Toast.makeText(this, "Sensor service starting", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sensor service reconnected", Toast.LENGTH_SHORT).show();
        }
        setForegroundState(true);
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private Notification buildNotification(String ContentTitle, String ContentText) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "SensorLog Channel ID")
                .setSmallIcon(android.R.drawable.star_on)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if(ContentTitle!=null)mBuilder.setContentTitle(ContentTitle);
        if(ContentText !=null)mBuilder.setContentText(ContentText);

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(0);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        return mBuilder.build();
    }

    private void setForegroundState(boolean isForeground) {
        if(isForeground) {
            startForeground(1, buildNotification("SensorLog Title","SensorLog Text"));
        } else {
            stopForeground( true );
        }
    }

    private String timeSince(Date then) {
        Date currentTime=new Date();
        long timeInterval=currentTime.getTime()-then.getTime();
        timeInterval/=1000;
        long hours=timeInterval/3600;
        timeInterval-=hours*3600;
        long minutes=timeInterval/60;
        timeInterval-=minutes*60;
        long seconds=timeInterval;
        String displayInterval=String.format("%02d:%02d:%02d",hours,minutes,seconds);
        return displayInterval;
    }

    private void showNotification() {
        String contentText="";
        if(nmeaStartTime!=null) {
            if (!contentText.equals("")) {
                contentText += "\r\n";
            }
            contentText += "NMEA recording for " + timeSince(nmeaStartTime);
        }
        if(sensorStartTime!=null) {
            if (!contentText.equals("")) {
                contentText += "\r\n";
            }
            contentText += "Sensor recording for " + timeSince(sensorStartTime);
        }
        if(oldContentText==null || !contentText.equals(oldContentText)) {
            oldContentText=contentText;
            nm.notify(1,buildNotification("SensorLog Title",contentText));
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Sensor service done", Toast.LENGTH_SHORT).show();
        locationManager=null;
    }

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder. Because we know this service always runs
     * in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        SensinatorService getService() {
            // Return this instance of LocalService so clients can call public
            // methods
            return SensinatorService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    Date nmeaStartTime=null,sensorStartTime=null;

    /** method for clients */
    public void mark(String s) {
        String ts = sdf.format(new Date());
        if(oufNMEA!=null) oufNMEA.println(ts + "$PKWNE,"+s+"*");
        if(oufSensor!=null) oufSensor.println(","+ts+",\""+s+"\"");
    }

    public void openNMEA() {
        if(oufNMEA==null) try {
            Toast.makeText(this, "openNMEA", Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            String oufn="NMEA"+ sdfIso.format(new Date()) + ".txt";
            if(prefix!=null)oufn=prefix+oufn;
            oufNMEA = new PrintWriter(new FileWriter(new File(folder,oufn)));
            nmeaStartTime = new Date();
            setForegroundState(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeNMEA() {
        Toast.makeText(this, "closeNMEA", Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(this);
        oufNMEA.close();
        oufNMEA=null;
        nmeaStartTime=null;
        setForegroundState(oufSensor!=null);
    }

    public boolean isNMEAOpen() {
        return oufNMEA!=null;
    }

    public void openSensor() {
        if(oufSensor==null) try {
            Toast.makeText(this, "openSensor", Toast.LENGTH_SHORT).show();
            mSensorManager.registerListener(this, acc,  SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, bfld, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, pres, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, ornt, SensorManager.SENSOR_DELAY_FASTEST);
            String oufn="Sensor"+ sdfIso.format(new Date()) + ".csv";
            if(prefix!=null)oufn=prefix+oufn;
            oufSensor = new PrintWriter(new FileWriter(new File(folder,oufn)));
            sensorStartTime=new Date();
            setForegroundState(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeSensor() {
        if(oufSensor!=null) {
            mSensorManager.unregisterListener(this);
            Toast.makeText(this, "closeSensor", Toast.LENGTH_SHORT).show();
            oufSensor.close();
            oufSensor = null;
            sensorStartTime = null;
            setForegroundState(oufNMEA!=null);
        }
    }

    public boolean isSensorOpen() {
        return oufSensor!=null;
    }

    public void setPrefix(String Lprefix) {
        prefix=Lprefix;
    }

    /* Listeners */
    public void onNmeaReceived(long timestamp, String Data) {
        showNotification();
        if(oufNMEA==null) return;
        String ts = sdf.format(new Date(timestamp));
        String[] NMEAs=Data.split("\\r?\\n");
        for(String NMEA:NMEAs) {
            oufNMEA.println(ts + NMEA);
        }
    }

    @Override public void onLocationChanged(Location arg0) {}
    @Override public void onProviderDisabled(String arg0) {}
    @Override public void onProviderEnabled(String arg0) {}
    @Override public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
    @Override public void onAccuracyChanged(Sensor arg0, int arg1) {}

    @Override
    public void onSensorChanged(SensorEvent arg0) {
        if(oufSensor!=null) {
            oufSensor.printf("%d,%s,%d",arg0.timestamp, sdf.format(new Date()), arg0.sensor.getType());
            for(double v:arg0.values) oufSensor.printf(",%f", v);
            oufSensor.println();
            showNotification();
        }
    }
}

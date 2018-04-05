package org.kwansystems.sensorlog;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.GpsStatus.NmeaListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GPSPlotFragment extends Fragment implements NmeaListener {
    private GPSPlotView gpsPlotView;
    private TextView txtLatitude,txtLongitude,txtAltitude;
    public GPSPlotFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gps_plot, container, false);
        gpsPlotView=(GPSPlotView)rootView.findViewById(R.id.gpsPlotView);
        txtLatitude =(TextView)rootView.findViewById(R.id.txtLatitude );
        if(txtLatitude==null) {
            Log.d("GPSPlotFragment","txtLatitude: null");
        } else {
            Log.d("GPSPlotFragment","txtLatitude: "+txtLatitude.toString());
        }
        txtLongitude=(TextView)rootView.findViewById(R.id.txtLongitude);
        txtAltitude=(TextView)rootView.findViewById(R.id.txtAltitude);
        if(gpsPlotView==null) {
            Log.d("GPSPlotFragment","gpsPlotView: null");
        } else {
            Log.d("GPSPlotFragment","gpsPlotView: "+gpsPlotView.toString());
        }
        return rootView;
    }
    @Override
    public void onNmeaReceived(long timestamp, String nmea) {
        // TODO Auto-generated method stub
        if(gpsPlotView!=null) gpsPlotView.onNmeaReceived(timestamp, nmea);
        String[] p=nmea.split(",");
        if(p[0].equals("$GPRMC")) {
            if(txtLatitude !=null)txtLatitude .setText(p[3]+","+p[4]);
            if(txtLongitude!=null)txtLongitude.setText(p[5]+","+p[6]);
        } else if(p[0].equals("$GPGGA")) {
            if(txtAltitude!=null)txtAltitude.setText(p[9]);
        }
    }

}
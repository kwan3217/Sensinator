package org.kwansystems.sensorlog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus.NmeaListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class GPSLogFragment extends Fragment implements NmeaListener,LocationListener,OnCheckedChangeListener {
    TextView[] txtSystemClock,txtNMEA;
    SimpleDateFormat sdf=new SimpleDateFormat("hh:mm:ss.SSS");
    ArrayList<String> l=new ArrayList<String>();
    LocationManager locationManager;
    GPSPlotFragment sibling;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    public GPSLogFragment() {this(null);}
    public GPSLogFragment(GPSPlotFragment Lsibling) {
        sibling=Lsibling;
    }

    @Override
    public void onLocationChanged(Location loc) {}
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status,Bundle extras) {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_gps_log, container, false);
        txtSystemClock=new TextView[] {
                (TextView)rootView.findViewById(R.id.txtSystemClock00),
                (TextView)rootView.findViewById(R.id.txtSystemClock01),
                (TextView)rootView.findViewById(R.id.txtSystemClock02),
                (TextView)rootView.findViewById(R.id.txtSystemClock03),
                (TextView)rootView.findViewById(R.id.txtSystemClock04),
                (TextView)rootView.findViewById(R.id.txtSystemClock05),
                (TextView)rootView.findViewById(R.id.txtSystemClock06),
                (TextView)rootView.findViewById(R.id.txtSystemClock07),
                (TextView)rootView.findViewById(R.id.txtSystemClock08),
                (TextView)rootView.findViewById(R.id.txtSystemClock09),
                (TextView)rootView.findViewById(R.id.txtSystemClock10),
                (TextView)rootView.findViewById(R.id.txtSystemClock11),
                (TextView)rootView.findViewById(R.id.txtSystemClock12),
                (TextView)rootView.findViewById(R.id.txtSystemClock13),
                (TextView)rootView.findViewById(R.id.txtSystemClock14),
                (TextView)rootView.findViewById(R.id.txtSystemClock15),
                (TextView)rootView.findViewById(R.id.txtSystemClock16),
                (TextView)rootView.findViewById(R.id.txtSystemClock17),
                (TextView)rootView.findViewById(R.id.txtSystemClock18),
                (TextView)rootView.findViewById(R.id.txtSystemClock19)
        };
        txtNMEA=new TextView[] {
                (TextView)rootView.findViewById(R.id.txtNMEA00),
                (TextView)rootView.findViewById(R.id.txtNMEA01),
                (TextView)rootView.findViewById(R.id.txtNMEA02),
                (TextView)rootView.findViewById(R.id.txtNMEA03),
                (TextView)rootView.findViewById(R.id.txtNMEA04),
                (TextView)rootView.findViewById(R.id.txtNMEA05),
                (TextView)rootView.findViewById(R.id.txtNMEA06),
                (TextView)rootView.findViewById(R.id.txtNMEA07),
                (TextView)rootView.findViewById(R.id.txtNMEA08),
                (TextView)rootView.findViewById(R.id.txtNMEA09),
                (TextView)rootView.findViewById(R.id.txtNMEA10),
                (TextView)rootView.findViewById(R.id.txtNMEA11),
                (TextView)rootView.findViewById(R.id.txtNMEA12),
                (TextView)rootView.findViewById(R.id.txtNMEA13),
                (TextView)rootView.findViewById(R.id.txtNMEA14),
                (TextView)rootView.findViewById(R.id.txtNMEA15),
                (TextView)rootView.findViewById(R.id.txtNMEA16),
                (TextView)rootView.findViewById(R.id.txtNMEA17),
                (TextView)rootView.findViewById(R.id.txtNMEA18),
                (TextView)rootView.findViewById(R.id.txtNMEA19),
        };
        ToggleButton btnGPS=(ToggleButton)rootView.findViewById(R.id.btnGPS);
        btnGPS.setOnCheckedChangeListener(this);
        // Register the listener with the Location Manager to receive location updates
        // Acquire a reference to the system Location Manager

        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.addNmeaListener(this);

        return rootView;
    }

    @Override
    public void onNmeaReceived(long timestamp, String NMEA) {
        String type=NMEA.substring(1,6);
        if(type.equals("GPGSV")||type.equals("GLGSV"))  type=NMEA.substring(1,10);
        int i=l.indexOf(type);
        if(i<0) {
            l.add(type);
            i=l.indexOf(type);
        }
        if(i<txtNMEA.length) {
            txtNMEA[i].setText(NMEA);
            String ts=sdf.format(new Date(timestamp));
            txtSystemClock[i].setText(ts);
        }
        sibling.onNmeaReceived(timestamp, NMEA);
    }

    @Override
    public void onCheckedChanged(CompoundButton btnGPS, boolean isChecked) {
        if(isChecked) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
            MainActivity.mService.openNMEA();
        } else {
            locationManager.removeUpdates(this);
            MainActivity.mService.closeNMEA();
        }
    }
}
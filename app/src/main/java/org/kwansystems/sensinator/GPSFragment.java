package org.kwansystems.sensinator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

@RequiresApi(api = Build.VERSION_CODES.N)
public class GPSFragment extends Fragment implements OnNmeaMessageListener,LocationListener {
    TextView[] txtSystemClock,txtNMEA;
    ToggleButton btnGPS;
    SimpleDateFormat sdf=new SimpleDateFormat("hh:mm:ss.SSS");
    ArrayList<String> l=new ArrayList<String>();
    LocationManager locationManager;
    private GPSPlotView gpsPlotView;
    private TextView txtLatitude,txtLongitude,txtAltitude;
        /**
    * The fragment argument representing the section number for this
    * fragment.
    */
    public static final String ARG_SECTION_NUMBER = "section_number";
    public GPSFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        if(MainActivity.mService!=null) btnGPS.setChecked(MainActivity.mService.isNMEAOpen());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_gps, container, false);
        gpsPlotView=rootView.findViewById(R.id.gpsPlotView);
        txtLatitude =rootView.findViewById(R.id.txtLatitude );
        txtLongitude=rootView.findViewById(R.id.txtLongitude);
        txtAltitude=rootView.findViewById(R.id.txtAltitude);
        txtSystemClock=new TextView[] {
                rootView.findViewById(R.id.txtSystemClock00),
                rootView.findViewById(R.id.txtSystemClock01),
                rootView.findViewById(R.id.txtSystemClock02),
                rootView.findViewById(R.id.txtSystemClock03),
                rootView.findViewById(R.id.txtSystemClock04),
                rootView.findViewById(R.id.txtSystemClock05),
                rootView.findViewById(R.id.txtSystemClock06),
                rootView.findViewById(R.id.txtSystemClock07),
                rootView.findViewById(R.id.txtSystemClock08),
                rootView.findViewById(R.id.txtSystemClock09),
                rootView.findViewById(R.id.txtSystemClock10),
                rootView.findViewById(R.id.txtSystemClock11),
                rootView.findViewById(R.id.txtSystemClock12),
                rootView.findViewById(R.id.txtSystemClock13),
                rootView.findViewById(R.id.txtSystemClock14),
                rootView.findViewById(R.id.txtSystemClock15),
                rootView.findViewById(R.id.txtSystemClock16),
                rootView.findViewById(R.id.txtSystemClock17),
                rootView.findViewById(R.id.txtSystemClock18),
                rootView.findViewById(R.id.txtSystemClock19)
        };
        txtNMEA=new TextView[] {
                rootView.findViewById(R.id.txtNMEA00),
                rootView.findViewById(R.id.txtNMEA01),
                rootView.findViewById(R.id.txtNMEA02),
                rootView.findViewById(R.id.txtNMEA03),
                rootView.findViewById(R.id.txtNMEA04),
                rootView.findViewById(R.id.txtNMEA05),
                rootView.findViewById(R.id.txtNMEA06),
                rootView.findViewById(R.id.txtNMEA07),
                rootView.findViewById(R.id.txtNMEA08),
                rootView.findViewById(R.id.txtNMEA09),
                rootView.findViewById(R.id.txtNMEA10),
                rootView.findViewById(R.id.txtNMEA11),
                rootView.findViewById(R.id.txtNMEA12),
                rootView.findViewById(R.id.txtNMEA13),
                rootView.findViewById(R.id.txtNMEA14),
                rootView.findViewById(R.id.txtNMEA15),
                rootView.findViewById(R.id.txtNMEA16),
                rootView.findViewById(R.id.txtNMEA17),
                rootView.findViewById(R.id.txtNMEA18),
                rootView.findViewById(R.id.txtNMEA19),
        };
        btnGPS=rootView.findViewById(R.id.btnGPS);
        btnGPS.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btnGPS, boolean isChecked) {
                if(isChecked) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,GPSFragment.this);
                } else {
                    locationManager.removeUpdates(GPSFragment.this);
                }
            }
        });
        // Register the listener with the Location Manager to receive location updates
        // Acquire a reference to the system Location Manager

        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.addNmeaListener(this);

        return rootView;
    }

    @Override public void onLocationChanged(Location loc) {}
    @Override public void onProviderDisabled(String provider) {}
    @Override public void onProviderEnabled(String provider) {}
    @Override public void onStatusChanged(String provider, int status,Bundle extras) {}

    @Override
    public void onNmeaMessage(String Data, long timestamp) {
        String[] NMEAs=Data.split("\\r?\\n");
        for(String NMEA:NMEAs) {
            if(gpsPlotView!=null) gpsPlotView.onNmeaReceived(timestamp, NMEA);
            String[] p=NMEA.split(",");
            if(p[0].substring(3).equals("RMC")) {
                if(txtLatitude !=null)txtLatitude .setText(p[3]+","+p[4]);
                if(txtLongitude!=null)txtLongitude.setText(p[5]+","+p[6]);
            } else if(p[0].substring(3).equals("GGA")) {
                if(txtAltitude!=null)txtAltitude.setText(p[9]);
            }
            String type = NMEA.substring(1, 6);
            if (type.substring(2).equals("GSV")) type = NMEA.substring(1, 10);
            int i = l.indexOf(type);
            if (i < 0) {
                l.add(type);
                i = l.indexOf(type);
            }
            if (i < txtNMEA.length) {
                txtNMEA[i].setText(NMEA);
                String ts = sdf.format(new Date(timestamp));
                txtSystemClock[i].setText(ts);
            }
        }
    }

}
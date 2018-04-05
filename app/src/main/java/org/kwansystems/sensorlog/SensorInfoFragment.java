package org.kwansystems.sensorlog;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SensorInfoFragment extends Fragment {
    private SensorManager mSensorManager;
    private TextView txtSensorInfo;

    public SensorInfoFragment() {
    }
    final String[] typeName=new String[] {
            /* 0*/"",
            /* 1*/"Accelerometer",
            /* 2*/"Magnetic Field",
            /* 3*/"Orientation*",
            /* 4*/"Gyroscope",
            /* 5*/"Light",
            /* 6*/"Pressure",
            /* 7*/"Temperature*",
            /* 8*/"Proximity",
            /* 9*/"Gravity",
            /*10*/"Linear Acceleration",
            /*11*/"Rotation Vector",
            /*12*/"Relative Humidity",
            /*13*/"Ambient Temperature",
            /*14*/"Magnetic Field Uncalibrated",
            /*15*/"Game Rotation Vector",
            /*16*/"Gyroscope Uncalibrated",
            /*17*/"Significant Motion"
    };
    final String[] unitName=new String[] {
            /* 0*/"",
            /* 1*/"m/s^2",
            /* 2*/"\u03bcT",
            /* 3*/"\u00b0",
            /* 4*/"rad/s",
            /* 5*/"lux",
            /* 6*/"hPa",
            /* 7*/"\u00b0C",
            /* 8*/"cm",
            /* 9*/"m/s^2",
            /*10*/"m/s^2",
            /*11*/"",
            /*12*/"%",
            /*13*/"\u00b0C",
            /*14*/"\u03bcT",
            /*15*/"",
            /*16*/"rad/s",
            /*17*/"" //significant motion
    };
    //    String sensorToString(Sensor I) {
//      String s="";
//
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensor_info, container, false);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        txtSensorInfo=(TextView)rootView.findViewById(R.id.txtSensorInfo);
        List<Sensor> sensors=mSensorManager.getSensorList(Sensor.TYPE_ALL);
        String s="";
        int i=0;
        for(Sensor I:sensors) {
            s=s+String.format("%d %s\n",i,I.toString());
            i++;
        }
        txtSensorInfo.setText(s);
        return rootView;
    }

}
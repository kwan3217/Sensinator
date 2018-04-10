package org.kwansystems.sensinator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.List;

public class SensorFragment extends Fragment implements SensorEventListener {
    private SensorManager mSensorManager;
    private TextView txtAcc[]=new TextView[3];
    private TextView txtBfld[]=new TextView[3];
    private TextView txtGyro[]=new TextView[3];
    private TextView txtQuat[]=new TextView[3];
    private TextView txtPres[]=new TextView[1];
    private ToggleButton btnShow;
    private TextView txtSensorInfo;
    Sensor acc,bfld,gyro,quat,pres;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensor, container, false);
        btnShow=rootView.findViewById(R.id.btnShow);
        txtAcc[0]=rootView.findViewById(R.id.txtAccX);
        txtAcc[1]=rootView.findViewById(R.id.txtAccY);
        txtAcc[2]=rootView.findViewById(R.id.txtAccZ);
        txtBfld[0]=rootView.findViewById(R.id.txtBfldX);
        txtBfld[1]=rootView.findViewById(R.id.txtBfldY);
        txtBfld[2]=rootView.findViewById(R.id.txtBfldZ);
        txtGyro[0]=rootView.findViewById(R.id.txtGyroX);
        txtGyro[1]=rootView.findViewById(R.id.txtGyroY);
        txtGyro[2]=rootView.findViewById(R.id.txtGyroZ);
        txtQuat[0]=rootView.findViewById(R.id.txtQuatX);
        txtQuat[1]=rootView.findViewById(R.id.txtQuatY);
        txtQuat[2]=rootView.findViewById(R.id.txtQuatZ);
        txtPres[0]=rootView.findViewById(R.id.txtPresX);
        txtSensorInfo=rootView.findViewById(R.id.txtSensorInfo);
        btnShow.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton btnGPS, boolean isChecked) {
                if (isChecked) {
                    mSensorManager.registerListener(SensorFragment.this, acc, SensorManager.SENSOR_DELAY_NORMAL);
                    mSensorManager.registerListener(SensorFragment.this, bfld, SensorManager.SENSOR_DELAY_NORMAL);
                    mSensorManager.registerListener(SensorFragment.this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
                    mSensorManager.registerListener(SensorFragment.this, quat, SensorManager.SENSOR_DELAY_NORMAL);
                    mSensorManager.registerListener(SensorFragment.this, pres, SensorManager.SENSOR_DELAY_NORMAL);
                } else {
                    mSensorManager.unregisterListener(SensorFragment.this);
                }
            }
        });
        mSensorManager = (SensorManager) (getActivity()).getSystemService(Context.SENSOR_SERVICE);
        acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        bfld = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        quat = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        pres = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
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

    @Override public void onAccuracyChanged(Sensor arg0, int arg1) { }
    @Override
    public void onSensorChanged(SensorEvent arg0) {
        TextView[] txtSens=null;
        int l;
        if(arg0.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            txtSens=txtAcc;
        } else if(arg0.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD) {
            txtSens=txtBfld;
        } else if(arg0.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            txtSens=txtGyro;
        } else if(arg0.sensor.getType()==Sensor.TYPE_ORIENTATION) {
            txtSens=txtQuat;
        } else if(arg0.sensor.getType()==Sensor.TYPE_PRESSURE) {
            txtSens=txtPres;
        }
        for(int i=0;i<arg0.values.length && i<txtSens.length;i++) txtSens[i].setText(String.format("%f",arg0.values[i]));
    }
}
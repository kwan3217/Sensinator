package org.kwansystems.sensorlog;

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

public class SensorLogFragment extends Fragment implements OnCheckedChangeListener,SensorEventListener {
    private SensorManager mSensorManager;
    private TextView txtAcc[]=new TextView[3];
    private TextView txtBfld[]=new TextView[3];
    private TextView txtGyro[]=new TextView[3];
    private TextView txtPres[]=new TextView[1];
    Sensor acc,bfld,gyro,pres;
    BubbleCompassFragment sibling;
    public SensorLogFragment() {this(null);}
    public SensorLogFragment(BubbleCompassFragment Lsibling) {
        sibling=Lsibling;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensor_log, container, false);
        ToggleButton btnSensors=(ToggleButton)rootView.findViewById(R.id.btnSensors);
        txtAcc[0]=(TextView)rootView.findViewById(R.id.txtAccX);
        txtAcc[1]=(TextView)rootView.findViewById(R.id.txtAccY);
        txtAcc[2]=(TextView)rootView.findViewById(R.id.txtAccZ);
        txtBfld[0]=(TextView)rootView.findViewById(R.id.txtBfldX);
        txtBfld[1]=(TextView)rootView.findViewById(R.id.txtBfldY);
        txtBfld[2]=(TextView)rootView.findViewById(R.id.txtBfldZ);
        txtGyro[0]=(TextView)rootView.findViewById(R.id.txtGyroX);
        txtGyro[1]=(TextView)rootView.findViewById(R.id.txtGyroY);
        txtGyro[2]=(TextView)rootView.findViewById(R.id.txtGyroZ);
        txtPres[0]=(TextView)rootView.findViewById(R.id.txtPresX);
        btnSensors.setOnCheckedChangeListener(this);
        mSensorManager = (SensorManager) (getActivity()).getSystemService(Context.SENSOR_SERVICE);
        acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        bfld = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        pres = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        return rootView;
    }
    @Override
    public void onCheckedChanged(CompoundButton btnGPS, boolean isChecked) {
        if(isChecked) {
            mSensorManager.registerListener(this,acc, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this,bfld,SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this,gyro,SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this,pres,SensorManager.SENSOR_DELAY_NORMAL);
            if(sibling!=null)mSensorManager.registerListener(sibling.mGraphView,acc, SensorManager.SENSOR_DELAY_NORMAL);
            if(sibling!=null)mSensorManager.registerListener(sibling.mGraphView,bfld,SensorManager.SENSOR_DELAY_NORMAL);
            MainActivity.mService.openSensor();
        } else {
            mSensorManager.unregisterListener(this);
            if(sibling!=null)mSensorManager.unregisterListener(sibling.mGraphView);
            MainActivity.mService.closeSensor();
        }
    }
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }
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
        } else if(arg0.sensor.getType()==Sensor.TYPE_PRESSURE) {
            txtSens=txtPres;
        }
        for(int i=0;i<txtSens.length;i++) txtSens[i].setText(String.format("%d",((int)(arg0.values[i]/arg0.sensor.getResolution()+0.5))));
    }
}
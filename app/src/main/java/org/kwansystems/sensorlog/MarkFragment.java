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
import android.widget.Button;
import android.widget.EditText;

public class MarkFragment extends Fragment {
    private EditText txtEvent;
    private Button btnMark,btnStart,btnFinish;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mark, container, false);
        btnMark=(Button)rootView.findViewById(R.id.btnMark);
        btnStart=(Button)rootView.findViewById(R.id.btnStart);
        btnFinish=(Button)rootView.findViewById(R.id.btnFinish);
        txtEvent=(EditText)rootView.findViewById(R.id.txtEvent);
        btnMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mService.mark(txtEvent.getText().toString());
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mService.mark("Start "+txtEvent.getText().toString());
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mService.mark("Finish "+txtEvent.getText().toString());
            }
        });
        return rootView;
    }
}
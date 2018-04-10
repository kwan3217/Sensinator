package org.kwansystems.sensinator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

public class ControlFragment extends Fragment {
    EditText txtEvent, txtPrefix;
    Button btnMark,btnStart,btnFinish,btnBoth;
    ToggleButton btnGPS,btnSensor;
    @Override
    public void onResume() {
        super.onResume();
        if(MainActivity.mService!=null) btnSensor.setChecked(MainActivity.mService.isSensorOpen());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_control, container, false);
        btnMark=rootView.findViewById(R.id.btnMark);
        btnStart=rootView.findViewById(R.id.btnStart);
        btnFinish=rootView.findViewById(R.id.btnFinish);
        btnBoth=rootView.findViewById(R.id.btnBoth);

        btnGPS=rootView.findViewById(R.id.btnGPS);
        btnSensor=rootView.findViewById(R.id.btnSens);

        txtEvent=rootView.findViewById(R.id.txtEvent);
        txtPrefix=rootView.findViewById(R.id.txtPrefix);
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
        btnGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btnGPS, boolean isChecked) {
                if(isChecked) {
                    MainActivity.mService.openNMEA();
                } else {
                    MainActivity.mService.closeNMEA();
                }
            }
        });
        btnSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btnSensor, boolean isChecked) {
                if(isChecked) {
                    MainActivity.mService.openSensor();
                } else {
                    MainActivity.mService.closeSensor();
                }
            }
        });
        btnBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnGPS.setChecked(!btnGPS.isChecked());
                btnSensor.setChecked(!btnSensor.isChecked());
            }
        });
        txtPrefix.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { if(MainActivity.mService!=null)MainActivity.mService.setPrefix(s.toString()); }

        });
        return rootView;
    }
}
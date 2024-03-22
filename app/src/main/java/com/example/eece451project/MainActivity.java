package com.example.eece451project;

import android.widget.TextView;
import android.util.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPermission()) {
            queryCellInfo();
        } else {
            requestPermission();
        }

    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) ==
                        PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                queryCellInfo();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void queryCellInfo() {
        // Check if permissions are granted
        if (checkPermission()) {
            // Permissions are granted, proceed with querying cell info
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            TextView telephonyManagerTextView = findViewById(R.id.telephonyManagerTextView);
            telephonyManagerTextView.setText("Telephony Manager: " + telephonyManager);
            Executor executor = new Executor() {
                @Override
                public void execute(Runnable command) {
                }
            };
            if (telephonyManager != null) {
                List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
                TextView signalStrengthTextView = findViewById(R.id.signalStrengthTextView);
                if (cellInfoList != null) {
                    for (CellInfo cellInfo : cellInfoList) {
                        if (cellInfo instanceof CellInfoGsm) {
                            signalStrengthTextView.setText("Signal Strength: 2 dBm");
                            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                            int signalStrength = cellSignalStrengthGsm.getDbm();
                            Log.d("SignalStrength", "Signal Strength: " + signalStrength + " dBm");

                            // Get other information like operator, cell ID, etc.
                            // Send this information to the server along with a timestamp
                            // Inside the loop where you obtain the signal strength
                            // Update the TextView with the obtained signal strength

                            //signalStrengthTextView.setText("Signal Strength: " + signalStrength + " dBm");
                            if (signalStrengthTextView != null) {
                                signalStrengthTextView.setText("Signal Strength: " + signalStrength + " dBm");
                            } else {
                                Log.e("SignalStrength", "TextView is null");
                            }
                        }
                        else {
                            //TextView signalStrengthTextView = findViewById(R.id.signalStrengthTextView);
                            signalStrengthTextView.setText("Signal Strength: 0 dBm");
                        }
                    }
                }
            }
        } else {
            // Permissions are not granted, request them
            requestPermission();
        }
    }
}

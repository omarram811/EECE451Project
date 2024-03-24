package com.example.eece451project;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoWcdma;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellIdentityLte;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextView signalStrengthText;
    TextView snrText;
    TextView frequencyText;
    TextView timeText;
    TextView cellIdText;
    TextView networkTypeText;
    TextView networkOperatortext;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signalStrengthText = findViewById(R.id.signalStrengthText);
        snrText = findViewById(R.id.snrText);
        frequencyText = findViewById(R.id.frequencyText);
        timeText = findViewById(R.id.timeText);
        cellIdText = findViewById(R.id.cellIdText);
        networkTypeText = findViewById(R.id.networkTypeText);
        networkOperatortext = findViewById(R.id.networkOperatorText);
        networkOperatortext.setText(getOperator());
        queryCellInfo();
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
    public boolean checkMobileNet(Context context) {//Checks if device is connected to a mobile network
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        }
        return false;
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
    public static boolean checkWifi(Context context) {//Checks if the device is connected to a WiFi network
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
                Network network = connectivityManager.getActiveNetwork();
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        }
        return false;
    }
    public String getOperator() {
        TelephonyManager manager = (TelephonyManager) getSystemService(TelephonyManager.class);
        return Objects.requireNonNull(manager.getNetworkOperatorName());
    }
    public String getNetworkType(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GSM:
                return "GSM";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            default:
                return "Outside Scope";
        }
    }
    private void queryCellInfo() {
        // Check if permissions are granted
        if (checkPermission()) {
            // Permissions are granted, proceed with querying cell info
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            int networkType = telephonyManager.getNetworkType();
            networkTypeText.setText(getNetworkType(networkType));
            Executor executor = new Executor() {
                @Override
                public void execute(Runnable command) {
                }
            };
            TelephonyManager.CellInfoCallback callback = null;
            callback = new TelephonyManager.CellInfoCallback() {
                @Override
                public void onCellInfo(@NonNull List<CellInfo> cellInfo) {
                }
            };
            telephonyManager.requestCellInfoUpdate(executor, callback);
            List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
            //Log.d("List of Values", String.valueOf(cellInfoList));
            if (cellInfoList != null) {
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                timeText.setText(date);
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoGsm) {
                        CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                        int signalStrength = cellSignalStrengthGsm.getDbm();
                        signalStrengthText.setText(signalStrength + "dBm");

                        CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
                        int CellId = cellIdentityGsm.getCid();
                        cellIdText.setText(String.valueOf(CellId));

                        //not applicable for GSM
                        snrText.setText(String.valueOf("NONE"));
                        frequencyText.setText(String.valueOf("NONE"));

                    } else if (cellInfo instanceof CellInfoWcdma) { //WCDMA = UMTS
                        CellInfoWcdma cellInfoUmts = (CellInfoWcdma) cellInfo;
                        CellSignalStrengthWcdma cellSignalStrengthUmts = cellInfoUmts.getCellSignalStrength();
                        int signalStrength = cellSignalStrengthUmts.getDbm();
                        signalStrengthText.setText(signalStrength + "dBm");

                        CellIdentityWcdma cellIdentityUmts = cellInfoUmts.getCellIdentity();
                        int CellId = cellIdentityUmts.getCid();
                        cellIdText.setText(String.valueOf(CellId));

                        //UARFCN stands for UTRA Absolute Radio Frequency Channel Number
                        //Frequency Band = UARFCNx0.2
                        int UARFCN = cellIdentityUmts.getUarfcn();
                        float frequencyBand = (float) (UARFCN * 0.2);
                        frequencyText.setText(String.valueOf(frequencyBand));

                        //not available
                        snrText.setText(String.valueOf("None"));

                    } else if(cellInfo instanceof CellInfoLte){
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;

                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        int signalStrength = cellSignalStrengthLte.getDbm();
                        signalStrengthText.setText(signalStrength + "dBm");

                        CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                        int CellId = cellIdentityLte.getCi();
                        cellIdText.setText(String.valueOf(CellId));

                        int SNR = cellSignalStrengthLte.getRssnr();
                        snrText.setText(String.valueOf(SNR));

                        int frequencyBand =  cellIdentityLte.getBandwidth();
                        frequencyText.setText(String.valueOf(frequencyBand));
                    }
                }
            }
        } else {
            // Permissions are not granted, request them
            requestPermission();
        }
    }
}

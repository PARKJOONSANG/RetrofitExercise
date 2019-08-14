package com.joonsang.retrofit;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.joonsang.retrofit.gpsService.MyGpsService;
import com.joonsang.retrofit.network.APIinterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {


    private final String BROADCSST_MESSAGE = "com.com.com.com";
    private BroadcastReceiver mReceiver = null;
    public MyGpsService mGpsService;
    public View mLayout;
    String[] permList;
    public final int PERMISSIONREQUESTCODE = 123141;
    public boolean permissionState = true;
    private String LOGTAG = this.getClass().getSimpleName();
    protected TextView button;
    protected TextView infoTextView;
    Handler handler;


    APIinterface api;

    protected ServiceConnection gpsConnecter = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyGpsService.GpsServiceBinder myBinder = (MyGpsService.GpsServiceBinder) service;
            mGpsService = myBinder.getService();
            Log.e(LOGTAG, "onServiceConnected");


            Toast.makeText(MainActivity.this,
                    String.valueOf(mGpsService.PERMISSIONREQUESTCODE), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(LOGTAG, "onServiceDisconnected");
        }
    };


    private void doBindGps() {
        Intent intent = null;
        intent = new Intent(this, MyGpsService.class);
        bindService(intent, gpsConnecter, Context.BIND_AUTO_CREATE);
    }

    private boolean hasAllGranted(int grantResult[]) {
        for (int grant : grantResult) {
            if (grant == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(LOGTAG, "REQCODE -->" + requestCode);
        switch (requestCode) {
            case PERMISSIONREQUESTCODE:
                if (hasAllGranted(grantResults)) {
                    Log.e(LOGTAG, "PERMISSIONS GRANTED ALL");
                    permissionState = true;
                } else {

                    permissionState = false;
                    if (mGpsService != null) {
                        unbindService(gpsConnecter);
                        mGpsService = null;
                        Log.e(LOGTAG, "do UnBinding");
                    }
                    unregisterReceiver();


                    final Snackbar snackbar = Snackbar.make(mLayout,
                            "위치 권한 요청이 거절되었습니다.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("설정으로 이동", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts
                                    ("package", getPackageName(), null));
                            //intent.setData(Uri.parse("package:com.joonsang.retrofit"));
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            Log.e(LOGTAG, String.valueOf(Uri.fromParts
                                    ("package", getPackageName(), null)));
                            startActivity(intent);
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                    //finish();
                }
                break;

        }

    }


    private void registerReceiver() {

        //todo 1. intent filter를 만든다.
        //todo 2. intent filter에 action을 추가한다.
        //todo 3. BroadCastReceiver 를 익명클래소 구현한다.
        //todo 4. intent filter와 BroadCastReceiver를 등록한다.
        if (mReceiver != null) {
            return;
        }

        final IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCSST_MESSAGE);
        this.mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                permList = intent.getStringArrayExtra("list");

                if (intent.getAction().equals(BROADCSST_MESSAGE)) {

                    Toast.makeText(context, "receiveData\n" + permList[0] + " \n" + permList[1]
                            , Toast.LENGTH_LONG).show();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        requestPermissions(permList, PERMISSIONREQUESTCODE);
                    }
                }
            }
        };
        this.registerReceiver(this.mReceiver, filter);

    }

    private void unregisterReceiver() {
        if (mReceiver != null) {
            this.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.main);
        button = findViewById(R.id.getGPSButton);
        infoTextView = findViewById(R.id.textview_info);
        button.setOnClickListener(this :: onClick );
        button.setOnLongClickListener(this::onLongClick);

//        api  = ApiClient.getClient().create(APIinterface.class);
//        final Call<UserInfo> requestUserInfo = api.userInfo("df","df");
//
//        requestUserInfo.enqueue(new Callback<UserInfo>() {
//            @Override
//            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<UserInfo> call, Throwable t) {
//
//            }
//
//
//        });

    }


    @Override
    protected void onResume() {
        //if(hasAllGranted())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

            } else {
                permissionState = true;
            }
            if (permissionState) {
                Log.e(LOGTAG, "do Binding");

                if (mGpsService == null) {
                    doBindGps();
                }
                registerReceiver();
            }

            super.onResume();
        }
    }

        @Override
        protected void onPause () {

            if (mGpsService != null) {
                unbindService(gpsConnecter);
                mGpsService = null;
                Log.e(LOGTAG, "do UnBinding");
            }
            unregisterReceiver();

            super.onPause();
        }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
        case R.id.getGPSButton :
            handler = new Handler();
            Location currentLocation = mGpsService.requestLocation();
                if(mGpsService != null){

                   Thread gpsUpdateThread = new Thread(() -> {



                       handler.post(() -> infoTextView.setText(
                               "Latitude : "+currentLocation.getLatitude()
                               +"\nLongitude : "+currentLocation.getLongitude()
                               +"\nAltitude :"+currentLocation.getAltitude()
                               +"\nAccuracy : "+currentLocation.getAccuracy()
                       ));
                       infoTextView.setBackgroundColor(getResources().
                               getColor(R.color.design_default_color_primary));
                   });
                    gpsUpdateThread.start();


                }
            break;

        }
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()){
            case R.id.getGPSButton :

                infoTextView
                        .setText("clickAgain");
                infoTextView
                        .setBackgroundColor(getResources()
                        .getColor(R.color.design_default_color_surface));

                mGpsService.removeLocation();
                return true;

        }
        return  false;
    }
}

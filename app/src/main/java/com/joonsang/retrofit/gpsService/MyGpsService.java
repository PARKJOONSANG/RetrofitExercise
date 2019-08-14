package com.joonsang.retrofit.gpsService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MyGpsService extends Service {


//D=================================================================================================[Start] Class Attribute

    private static final String TAG = "MyGpsService";
    private LocationManager mLocationManager = null;

    //Constant
    private static final int LOCATION_INTERVAL = 500;
    private static final float LOCATION_DISTANCE = 10f;
    public final int PERMISSIONREQUESTCODE = 123141;
    private static final int GPSLISTENER = 0;
    private static final int NETWORKLISTENER = 1;
    IBinder mBinder = new GpsServiceBinder();


    //D========================================================[Start] Inner Class(LocationListener)
    private class LocationListener implements android.location.LocationListener {

        Location mLastLocation;

        //JSPREMARK : Constructer

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener" + provider);
            mLastLocation = new Location(provider);
        }

        //JSPREMARK : Attribute


        //JSPREMARK : OverrideMethod
        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged : \n(latit,longit) : (" +location.getLatitude()+","+
                    location.getLongitude()+")-,"+mLocationManager.getAllProviders() );
            mLastLocation.set(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle bundle) {
            Log.e(TAG, "onStatusChanged" + provider);

        }

        @Override
        public void onProviderEnabled(String provider) {

            Log.e(TAG, "onProficerEnabled" + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {

            Log.e(TAG, "onProviderDisabled :" + provider);

        }
    }
    //D _______________________________________________________________________________________[End]



    //D =========================================================[Start]InnerClass(GpsServiceBinder)

    public class GpsServiceBinder extends Binder {

        public MyGpsService getService() {
            return MyGpsService.this;
        }
    }
    //D _______________________________________________________________________________________[End]


    public LocationListener[] mLocationListenrs = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
//D ________________________________________________________________________________________________[End]




//D ================================================================================================[Start] overriden Methods about Binding at Service
    @Override
    public IBinder onBind(Intent intent) {

        Log.e(TAG, "ONBIND");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.e(TAG, "ONREBIND");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "ONUNBIND");

        return super.onUnbind(intent);
    }



//D ________________________________________________________________________________________________[End]





//D ================================================================================================[Start] OverridenMethodsAtService
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        initializeLocationManager();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    !=  PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    !=  PackageManager.PERMISSION_GRANTED)
            {
                            // TODO: Consider calling
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                final String[] permList =
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        };

                Intent intent = new Intent("com.com.com.com");
                intent.putExtra("list", permList);
                sendBroadcast(intent);

                return;
            }
            else
            {
                Log.e(TAG, "All permissions are granted");

                //PJS : 정상적인 권한 허가 시점
                //TODO 1.

                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                criteria.setAltitudeRequired(true);
                criteria.setBearingRequired(false);
                criteria.setSpeedRequired(true);
                mLocationManager.getBestProvider(criteria,true);
                try {

                    mLocationManager.requestLocationUpdates(

                                    LocationManager.NETWORK_PROVIDER,
                                    LOCATION_INTERVAL,
                                    LOCATION_DISTANCE,
                                    mLocationListenrs[NETWORKLISTENER]
                            );

                } catch(java.lang.SecurityException ex) {
                    Log.i(TAG,"Fail to request location update, ignore",ex);
                } catch (IllegalArgumentException ex) {
                    Log.d(TAG,"Net work provider does not exist," + ex.getMessage());
                }


                try {
                    mLocationManager.requestLocationUpdates (
                            LocationManager.GPS_PROVIDER,
                            LOCATION_INTERVAL,
                            LOCATION_DISTANCE,
                            mLocationListenrs[GPSLISTENER]
                    );
                }  catch (java.lang.SecurityException ex) {
                    Log.i(TAG,"Fail to request location update, ignore", ex);
                } catch (IllegalArgumentException ex) {
                    Log.d(TAG, " gps provider does not exist" + ex.getMessage());
                }
            }


        }




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        if(mLocationManager != null) {
            for (LocationListener loc : mLocationListenrs){

                try {
                    mLocationManager.removeUpdates(loc);
                } catch (Exception ex) {
                    Log.i(TAG, "Fail to remove location listeners, ignore",ex);
                }
                //mLocationManager.removeUpdates(loc);

            }
        }

    }

//D ________________________________________________________________________________________________ [End]





//D ================================================================================================[Start] CustomMethods

    private void initializeLocationManager() {
        Log.e(TAG,"InitializeLocationManager");
        if(mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext()
                    .getSystemService(Context.LOCATION_SERVICE);
        }
    }
//D ________________________________________________________________________________________________[End]


    @SuppressLint("MissingPermission")
    public Location requestLocation(){
        Location currentLocation = null;
        mLocationManager.removeUpdates(mLocationListenrs[NETWORKLISTENER]);
        mLocationManager.removeUpdates(mLocationListenrs[GPSLISTENER]);
        try {

            mLocationManager.requestLocationUpdates(

                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListenrs[NETWORKLISTENER]
            );

        } catch(java.lang.SecurityException ex) {
            Log.i(TAG,"Fail to request location update, ignore",ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG,"Net work provider does not exist," + ex.getMessage());
        }


        try {
            mLocationManager.requestLocationUpdates (
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListenrs[GPSLISTENER]
            );
        }  catch (java.lang.SecurityException ex) {
            Log.i(TAG,"Fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, " gps provider does not exist" + ex.getMessage());
        }
        if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
           currentLocation= mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
             currentLocation =  mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }

        return  currentLocation;



    }
    public void removeLocation(){
        mLocationManager.removeUpdates(mLocationListenrs[NETWORKLISTENER]);
        mLocationManager.removeUpdates(mLocationListenrs[GPSLISTENER]);
    }
}


/// 나의 주석
/**
 * 안드로이드에는 크게 4개의 컴포넌트로 나눕니다. 그 중 하나가 서비스 입니다.
 * 서비스는 안드로이드 OS에서 백그라운드로 돌아가는 객체 입니다.
 * 실행과 종료는 Activity에서 하겠죠
 * 주로 주기 쩍인 관찰과 피드백이 필요한 기능에 사용합니다.
 * 이런 Service에는 두 종류가 있습니다. 연격을 유지해서 데이터를 주고받을 수 있는 BoundService와 시작과 종료에만 관여하는
 * UnBoundService입니다.
 * Service 컴퍼넌트와 데이터를 주고 받으며 관리하고 싶다면 Bound Service로 만들어야겠죠
 * Bound/ UNBound는 근본이 다릅니다. 연결을 유지하고 데이터를 전송받기 위한 ServiceConnection 객체와 IBinder 인터페이스
 * 객체가 필요합니다.
 */
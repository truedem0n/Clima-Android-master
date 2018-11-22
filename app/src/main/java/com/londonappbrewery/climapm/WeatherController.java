package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.net.URL;
import java.security.spec.ECField;
import java.util.Calendar;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final int ResponseCode=123;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    final String TIME_URL="http://api.timezonedb.com/v2.1/get-time-zone";
    String lng,lat,latForGeoLocation,lonForGeoLocation;
    Intent myIntent;
    static boolean done=false;
    // App ID to use OpenWeather data
    final String TIME_APP_ID="IFEFSQG263TH";
    final String APP_ID = "13ede99df506ae362bfdc92589e818b0";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // TODO: Set LOCATION_PROVIDER here:
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;


    // Member Variables:
    TextView mCityLabel,middleText1,middleText2,middleText3,middleText4,tempText1,tempText2,tempText3,tempText4;
    ImageView mWeatherImage,weatherImage1,weatherImage2,weatherImage3,weatherImage4;
    TextView mTemperatureLabel;
    String CurrentLocation="";
    String RealTimeLocation;
    String time,tempUpdater,time1,time2,time3,time4,tt1="",tt2="",tt3="",tt4="";
    private Handler mhandler=new Handler();
    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager mlocationMangaer;
    LocationListener mlocationListener;
    public void startRepeating(){
        mTimeRunnable.run();
    }
    public void stopRepeating(){
        mhandler.removeCallbacks(mTimeRunnable);
    }
    private Runnable mTimeRunnable=new Runnable() {
        @Override
        public void run() {
            getTimeByCityName();
            getTimeByCityNameForSimulatedCities();
            mCityLabel.setText(tempUpdater+String.format(String.format("\nLocal Time: %s",time)));
            try{
                if(!tt1.equals(time1)||!tt2.equals(time2)||!tt3.equals(time3)||!tt4.equals(time4)){
                    middleText1.setText(String.format("    Karnal    \nLocal Time: %s",time1));
                    middleText2.setText(String.format("    London    \nLocal Time: %s",time2));
                    middleText3.setText(String.format("    California    \nLocal Time: %s",time3));
                    middleText4.setText(String.format("    Ottawa    \nLocal Time: %s",time4));
                    middleText1.setText(String.format("    Karnal    \nLocal Time: %s",time1));
                    middleText2.setText(String.format("    London    \nLocal Time: %s",time2));
                    middleText3.setText(String.format("    California    \nLocal Time: %s",time3));
                    middleText4.setText(String.format("    Ottawa    \nLocal Time: %s",time4));
                    if(time1!=null)tt1=time1;
                    if(time2!=null)tt2=time2;
                    if(time3!=null)tt3=time3;
                    if(time4!=null)tt4=time4;
                }
            }
            catch(Exception e){

            }

            mhandler.postDelayed(mTimeRunnable,1000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);
        Log.d("clima", "onCreate() ");
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        weatherImage1=(ImageView)findViewById(R.id.weatherLocation1);
        weatherImage2=(ImageView)findViewById(R.id.weatherLocation2);
        weatherImage3=(ImageView)findViewById(R.id.weatherLocation3);
        weatherImage4=(ImageView)findViewById(R.id.weatherLocation4);
        middleText1=(TextView)findViewById(R.id.weatherLocationText1);
        middleText2=(TextView)findViewById(R.id.weatherLocationText2);
        middleText3=(TextView)findViewById(R.id.weatherLocationText3);
        middleText4=(TextView)findViewById(R.id.weatherLocationText4);
        tempText1=(TextView)findViewById(R.id.temp1);
        tempText2=(TextView)findViewById(R.id.temp2);
        tempText3=(TextView)findViewById(R.id.temp3);
        tempText4=(TextView)findViewById(R.id.temp4);
        final ImageButton changeCityButton = (ImageButton) findViewById(R.id.menu);
        final ImageButton reloadButton = (ImageButton) findViewById(R.id.reload);
        final ImageButton currentLocationReloadbutton = (ImageButton) findViewById(R.id.reload2);



        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent=new Intent(WeatherController.this,changeCityController.class);
                startActivity(myIntent);
            }
        });
        currentLocationReloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CurrentLocation!=""){
                    getWeatherForNewCity(CurrentLocation);
                    Log.d("clima", "currentLocationReload()  and getWeatherForNewCityCalled()");
                }else{
                    getWeatherForCurrentLocation();
                    Log.d("clima", "currentLocationReload()  and getWFCL()");
                }
            }
        });
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("clima", "reloadButtonCalled() ");
                getWeatherForCurrentLocation();


            }
        });

        getWeatherForSimulatedCities("Karnal","London","california","Ottawa");
        if(!done){
            getWeatherForCurrentLocation();
            getWeatherForCurrentLocation();
            Log.d("clima", "done1() ");
            done=true;
        }
    }




    // TODO: Add onResume() here:

    @Override
    protected void onResume() {
        super.onResume();
        myIntent=getIntent();
        String value=myIntent.getStringExtra("value");
        boolean defaultMethod=myIntent.getBooleanExtra("defaultMethod",false);
        tt1=" ";tt2=" ";tt3=" ";tt4=" ";
        if(defaultMethod){
            getWeatherForNewCity(value);
            CurrentLocation=value;
        }else if(!defaultMethod){
            try{
                String[] temp=value.split(",");
                String lat=temp[0];
                String lon=temp[1];
                RequestParams params=new RequestParams();
                Log.d("clima", "lat: "+lat+" lon:"+lon);
                params.put("lat",lat);
                params.put("lon",lon);
                params.put("appId",APP_ID);
                AsyncHttpClient client=new AsyncHttpClient();
                client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                        try {
                            CurrentLocation=response.getString("name");
                            getWeatherForNewCity(CurrentLocation);
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                        Log.e("clima", "Fail: "+e.toString() );
                        Log.d("Cligma", "Status Code: "+statusCode);
                        Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        if(CurrentLocation!=""){
            getWeatherForNewCity(CurrentLocation);
        }
        startRepeating();
    }


    private void getWeatherForSimulatedCities(String city1,String city2,String city3,String city4){
        RequestParams parms1=new RequestParams();
        parms1.put("q",city1);
        parms1.put("appid",APP_ID);
        RequestParams parms2=new RequestParams();
        parms2.put("q",city2);
        parms2.put("appid",APP_ID);
        RequestParams parms3=new RequestParams();
        parms3.put("q",city3);
        parms3.put("appid",APP_ID);
        RequestParams parms4=new RequestParams();
        parms4.put("q",city4);
        parms4.put("appid",APP_ID);
        letsDoSomeNetworkingForSimulatedCities(parms1,parms2,parms3,parms4);
    }
    private void letsDoSomeNetworkingForSimulatedCities(RequestParams params1,RequestParams params2,RequestParams params3,RequestParams params4){
        AsyncHttpClient client1=new AsyncHttpClient();
        AsyncHttpClient client2=new AsyncHttpClient();
        AsyncHttpClient client3=new AsyncHttpClient();
        AsyncHttpClient client4=new AsyncHttpClient();
        client1.get(WEATHER_URL,params1,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                WeatherDataModel weatherData=WeatherDataModel.fromJson(response);
                updateUIForSimulatedCities1(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
        client2.get(WEATHER_URL,params2,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                WeatherDataModel weatherData=WeatherDataModel.fromJson(response);
                updateUIForSimulatedCities2(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
        client3.get(WEATHER_URL,params3,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                WeatherDataModel weatherData=WeatherDataModel.fromJson(response);
                updateUIForSimulatedCities3(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
        client4.get(WEATHER_URL,params4,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                WeatherDataModel weatherData=WeatherDataModel.fromJson(response);
                updateUIForSimulatedCities4(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: Add getWeatherForNewCity(String city) here:
//    private void getWeatherForNewCity(String lat,String lng){
//
//        RequestParams parms=new RequestParams();
//
//        parms.put("appid",APP_ID);
//        letsDoSomeNetworking(parms);
//
//    }
    private void getWeatherForNewCity(String city){
        if(city==""){
            getWeatherForCurrentLocation();
        }else{
            RequestParams parms=new RequestParams();
            parms.put("q",city);
            parms.put("appid",APP_ID);
            letsDoSomeNetworking(parms);
        }
    }
    private void getTimeByCityName(){
        RequestParams params=new RequestParams();
        params.put("key",TIME_APP_ID);
        params.put("format","json");
        params.put("by","position");
        params.put("lng",lng);
        params.put("lat",lat);
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(TIME_URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                try {
                    time=response.getString("formatted");
                    if(time!=null){
                        String[] temp=time.split(" ");
                        time=temp[1];
                        String[] temp1=time.split(":");
                        time=temp1[0]+":"+temp1[1];
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTimeByCityNameForSimulatedCities(){
        RequestParams params1=new RequestParams();
        params1.put("key",TIME_APP_ID);
        params1.put("format","json");
        params1.put("by","position");
        params1.put("lng","76.990547");
        params1.put("lat","29.685629");
        AsyncHttpClient client1=new AsyncHttpClient();
        client1.get(TIME_URL,params1,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                try {
                    time1=response.getString("formatted");
                    if(time1!=null){
                        String[] temp=time1.split(" ");
                        time1=temp[1];
                        String[] temp1=time1.split(":");
                        time1=temp1[0]+":"+temp1[1];
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
        RequestParams params2=new RequestParams();
        params2.put("key",TIME_APP_ID);
        params2.put("format","json");
        params2.put("by","position");
        params2.put("lng","-0.118092");
        params2.put("lat","51.509865");
        AsyncHttpClient client2=new AsyncHttpClient();
        client2.get(TIME_URL,params2,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                try {
                    time2=response.getString("formatted");
                    String[] temp=time2.split(" ");
                    time2=temp[1];
                    String[] temp1=time2.split(":");
                    time2=temp1[0]+":"+temp1[1];
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
        RequestParams params3=new RequestParams();
        params3.put("key",TIME_APP_ID);
        params3.put("format","json");
        params3.put("by","position");
        params3.put("lng","-119.417931");
        params3.put("lat","36.778259");
        AsyncHttpClient client3=new AsyncHttpClient();
        client3.get(TIME_URL,params3,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                try {
                    time3=response.getString("formatted");
                    String[] temp=time3.split(" ");
                    time3=temp[1];
                    String[] temp1=time3.split(":");
                    time3=temp1[0]+":"+temp1[1];
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
        RequestParams params4=new RequestParams();
        params4.put("key",TIME_APP_ID);
        params4.put("format","json");
        params4.put("by","position");
        params4.put("lng","-75.695419");
        params4.put("lat","45.420315");
        AsyncHttpClient client4=new AsyncHttpClient();
        client4.get(TIME_URL,params4,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                try {
                    time4=response.getString("formatted");
                    String[] temp=time4.split(" ");
                    time4=temp[1];
                    String[] temp1=time4.split(":");
                    time4=temp1[0]+":"+temp1[1];

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLocation() {
        Log.d("clima", "getWeatherForCLocCalled() ");
        mlocationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String longitude=String.valueOf(location.getLongitude());
                String latitude=String.valueOf(location.getLatitude());
                RequestParams params =new RequestParams();
                params.put("lat",latitude);
                params.put("lon",longitude);
                params.put("appId",APP_ID);
                Log.d("clima", "onLocationChangedCalled() "+latitude+longitude);
                letsDoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},ResponseCode );
            return;
        }
        mlocationMangaer.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mlocationListener);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==ResponseCode){
            if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d("Clima", "onRequestPermissionsResult: Permission Granted");
            }else {
                Log.d("Cligma", "onRequestPermissionsResult: Permission denied");
            }
        }
    }
    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void letsDoSomeNetworking(RequestParams params){
        AsyncHttpClient client=new AsyncHttpClient();
        Log.d("clima", "LetsDoSomeNetworkingCalled() ");
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("weather", " "+response.toString());
                WeatherDataModel weatherData=WeatherDataModel.fromJson(response);
                Log.d("clima", "OnSuccess() "+response.toString());

                try {
                    RealTimeLocation=response.getString("name");
                    double latTemp=response.getJSONObject("coord").getDouble("lat");
                    lat=String.valueOf(latTemp);
                    double lngTemp=response.getJSONObject("coord").getDouble("lon");
                    lng=String.valueOf(lngTemp);
                }catch (Exception e){
                    e.printStackTrace();
                }
                updateUI(weatherData);
            }
            
            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Log.e("clima", "Fail: "+e.toString() );
                Log.d("Cligma", "Status Code: "+statusCode);
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }


    // TODO: Add updateUI() here:
    private void updateUI(WeatherDataModel weather){
        mTemperatureLabel.setText(weather.getmTemperature());

        mCityLabel.setText(weather.getmCity());
        tempUpdater=mCityLabel.getText().toString();
        int resourceID=getResources().getIdentifier(weather.getmIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourceID);
        Log.e("clima", "UpdateUI Called()"+mCityLabel);
    }

    private void updateUIForSimulatedCities1(WeatherDataModel weather){
        tempText1.setText(weather.getmTemperature());
        middleText1.setText(String.format("    Karnal    \nLocal Time: ",weather.getmCity()));
        int resourceID=getResources().getIdentifier(weather.getmIconName(),"drawable",getPackageName());
        weatherImage1.setImageResource(resourceID);
    }
    private void updateUIForSimulatedCities2(WeatherDataModel weather){
        tempText2.setText(weather.getmTemperature());
        middleText2.setText(String.format("    %s    \nLocal Time: ",weather.getmCity()));
        int resourceID=getResources().getIdentifier(weather.getmIconName(),"drawable",getPackageName());
        weatherImage2.setImageResource(resourceID);
    }
    private void updateUIForSimulatedCities3(WeatherDataModel weather){
        tempText3.setText(weather.getmTemperature());
        middleText3.setText(String.format("    California    \nLocal Time: "));
        int resourceID=getResources().getIdentifier(weather.getmIconName(),"drawable",getPackageName());
        weatherImage3.setImageResource(resourceID);
    }
    private void updateUIForSimulatedCities4(WeatherDataModel weather){
        tempText4.setText(weather.getmTemperature());
        middleText4.setText(String.format("    %s    \nLocal Time: ",weather.getmCity()));
        int resourceID=getResources().getIdentifier(weather.getmIconName(),"drawable",getPackageName());
        weatherImage4.setImageResource(resourceID);
    }

    // TODO: Add onPause() here:
    protected void onPause(){
        super.onPause();
        if(mlocationMangaer!=null) mlocationMangaer.removeUpdates(mlocationListener);
        stopRepeating();
    }


}

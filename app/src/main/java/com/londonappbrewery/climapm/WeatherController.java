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
import android.widget.RelativeLayout;
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
    static String simCity1="Karnal",simCity2="London",simCity3="Paris",simCity4="Goa";
    static String nd,nd1,nd2,nd3,nd4,simCityName1,simCityName2,sinTemp1,sinTemp2,sinTemp3,sinTemp4,simCityName3,simCityName4,simLon1,simLat1,simLon2,simLat2,simLon3,simLat3,simLon4,simLat4;
    static boolean done=false;
    static int simResourceId1,simResourceId2,simResourceId3,simResourceId4;
    // App ID to use OpenWeather data
    final String APP_ID = "13ede99df506ae362bfdc92589e818b0";
    final String TIME_APP_ID="IFEFSQG263TH";
    RelativeLayout layout;

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
    static String CurrentLocation="",checkerForLoading;
    String RealTimeLocation;
    static boolean loading=false;
    String time,tempUpdater,time1,time2,time3,time4,tt1="",tt2="",tt3="",tt4="";
    private Handler mhandler=new Handler();

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager mlocationMangaer;
    LocationListener mlocationListener;


    //
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
            mCityLabel.setText(tempUpdater+String.format(String.format("  \n%s  (%s)",time,nd)));
            //try this line of code and keep updating these variables
            try{
                    middleText1.setText(String.format("%s  (%s)\n%s",simCityName1,nd1,time1));
                    middleText2.setText(String.format("%s  (%s)\n%s",simCityName2,nd2,time2));
                    middleText3.setText(String.format("%s  (%s)\n%s",simCityName3,nd3,time3));
                    middleText4.setText(String.format("%s  (%s)\n%s",simCityName4,nd4,time4));
                    if(time1!=null)tt1=time1;
                    if(time2!=null)tt2=time2;
                    if(time3!=null)tt3=time3;
                    if(time4!=null)tt4=time4;
            }
            catch(Exception e){

            }
            // repeat the process by calling mhandler again
            mhandler.postDelayed(mTimeRunnable,1000);
        }
    };

    // on create execute this code
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialization needed for these objects
        layout= findViewById(R.id.backG);
        setContentView(R.layout.weather_controller_layout);
        mCityLabel = findViewById(R.id.locationTV);
        mWeatherImage = findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = findViewById(R.id.tempTV);
        weatherImage1= findViewById(R.id.weatherLocation1);
        weatherImage2= findViewById(R.id.weatherLocation2);
        weatherImage3= findViewById(R.id.weatherLocation3);
        weatherImage4= findViewById(R.id.weatherLocation4);
        middleText1= findViewById(R.id.weatherLocationText1);
        middleText2= findViewById(R.id.weatherLocationText2);
        middleText3= findViewById(R.id.weatherLocationText3);
        middleText4= findViewById(R.id.weatherLocationText4);
        tempText1= findViewById(R.id.temp1);
        tempText2= findViewById(R.id.temp2);
        tempText3= findViewById(R.id.temp3);
        tempText4= findViewById(R.id.temp4);
        final ImageButton changeCityButton = findViewById(R.id.menu);
        final ImageButton reloadButton = findViewById(R.id.reload);
        final ImageButton currentLocationReloadbutton = findViewById(R.id.reload2);



        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent=new Intent(WeatherController.this,changeCityController.class);
                startActivity(myIntent);
            }
        });

        // refresh button listener
        currentLocationReloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CurrentLocation!=""){
                    getWeatherForNewCity(CurrentLocation);
                }else{
                    getWeatherForCurrentLocation();
                }
            }
        });

        // gps button listener
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCityLabel.setText("Loading");
                loading=true;
                checkerForLoading=tempUpdater;
                getWeatherForCurrentLocation();


            }
        });

        // Making sure this code only run once
        if(!done){
            getWeatherForCurrentLocation();
            getWeatherForCurrentLocation();
            mCityLabel.setText(tempUpdater+String.format(String.format("\nLocal Time: %s",time)));
            getWeatherForSimulatedCities(simCity1,simCity2,simCity3,simCity4);
            done=true;
            try{
                // try this piece of code
                if(!tt1.equals(time1)||!tt2.equals(time2)||!tt3.equals(time3)||!tt4.equals(time4)){
                    middleText1.setText(String.format("    %s    \nLocal Time: %s",simCityName1,time1));
                    middleText2.setText(String.format("    %s    \nLocal Time: %s",simCityName2,time2));
                    middleText3.setText(String.format("    %s    \nLocal Time: %s",simCityName3,time3));
                    middleText4.setText(String.format("    %s    \nLocal Time: %s",simCityName4,time4));
                    if(time1!=null)tt1=time1;
                    if(time2!=null)tt2=time2;
                    if(time3!=null)tt3=time3;
                    if(time4!=null)tt4=time4;
                }
            }
            catch(Exception e){

            }
        }
    }




    // TODO: Add onResume() here:
    @Override
    protected void onResume() {
        super.onResume();

        // when come back from change city activity this code will execute each time
        myIntent=getIntent();
        String value=myIntent.getStringExtra("value");
        boolean defaultMethod=myIntent.getBooleanExtra("defaultMethod",false);
        tt1=" ";tt2=" ";tt3=" ";tt4=" ";
        // if current location is changed then add the recent location to the stack
        if(CurrentLocation!=null&&!CurrentLocation.equals(simCity1)&&!simCity1.equals(simCity2)){
            if(CurrentLocation!=""){
                simCity4=simCity3;
                simCity3=simCity2;
                simCity2=simCity1;
                simCity1=CurrentLocation;
            }
            getWeatherForSimulatedCities(simCity1,simCity2,simCity3,simCity4);

        }

        // if default method i.e change by city
        if(defaultMethod){
            getWeatherForNewCity(value);
            CurrentLocation=value;
        }
        // Else change city by coordinates
        else if(!defaultMethod){
            try{
                String[] temp=value.split(",");
                String lat=temp[0];
                String lon=temp[1];
                RequestParams params=new RequestParams();
                params.put("lat",lat);
                params.put("lon",lon);
                Log.d("coord", "onSuccess: "+lat+lon);
                params.put("appId",APP_ID);
                // our http client
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

        // if current location is not null then find location by city
        if(CurrentLocation!=""){
            getWeatherForNewCity(CurrentLocation);
        }

        // on resume start repeating the code in function
        startRepeating();

        // execute this code on resume
        mCityLabel.setText(tempUpdater+String.format(String.format("\nLocal Time: %s",time)));
        weatherImage1.setImageResource(simResourceId1);
        weatherImage2.setImageResource(simResourceId2);
        weatherImage3.setImageResource(simResourceId3);
        weatherImage4.setImageResource(simResourceId4);
        tempText1.setText(sinTemp1);
        tempText2.setText(sinTemp2);
        tempText3.setText(sinTemp3);
        tempText4.setText(sinTemp4);
        try{
            if(!tt1.equals(time1)||!tt2.equals(time2)||!tt3.equals(time3)||!tt4.equals(time4)){
                middleText1.setText(String.format("    %s    \nLocal Time: %s",simCityName1,time1));
                middleText2.setText(String.format("    %s    \nLocal Time: %s",simCityName2,time2));
                middleText3.setText(String.format("    %s    \nLocal Time: %s",simCityName3,time3));
                middleText4.setText(String.format("    %s    \nLocal Time: %s",simCityName4,time4));
                if(time1!=null)tt1=time1;
                if(time2!=null)tt2=time2;
                if(time3!=null)tt3=time3;
                if(time4!=null)tt4=time4;
            }
        }
        catch(Exception e){

        }
    }

    // get weather for simulated cities by their names
    private void getWeatherForSimulatedCities(String city1,String city2,String city3,String city4){
        RequestParams parms1=new RequestParams();
        if(city1==null||city2==null||city3==null||city4==null){
            if(city1==null) city1="karnal";
            if(city2==null) city2="London";
            if(city3==null) city3="Paris";
            if(city4==null) city4="Goa";
            Log.d("climaif", "getWeatherForSimulatedCities: if part"+city1+city2+city3+city4);

        }

        // putting city names in each params
        parms1.put("q",city1);
        parms1.put("appid",APP_ID);
        Log.d("clima", "getWeatherForSimulatedCities: if part"+city1+city2+city3+city4);
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

    // networking for simulated cities.
    private void letsDoSomeNetworkingForSimulatedCities(RequestParams params1,RequestParams params2,RequestParams params3,RequestParams params4){

        // 4 slots for weather to compare atmost 5 locations at one time.
        AsyncHttpClient client1=new AsyncHttpClient();
        AsyncHttpClient client2=new AsyncHttpClient();
        AsyncHttpClient client3=new AsyncHttpClient();
        AsyncHttpClient client4=new AsyncHttpClient();
        client1.get(WEATHER_URL,params1,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                WeatherDataModel weatherData=WeatherDataModel.fromJson(response);
                try{
                    double temp1=response.getJSONObject("coord").getDouble("lat");
                    simLat1=String.valueOf(temp1);
                    double temp2=response.getJSONObject("coord").getDouble("lon");
                    simLon1=String.valueOf(temp2);
                }catch (Exception e){

                }

                updateUIForSimulatedCities1(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Log.d("clima", "failed1");
                Toast.makeText(WeatherController.this, "Loading",Toast.LENGTH_SHORT).show();
            }
        });
        client2.get(WEATHER_URL,params2,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                WeatherDataModel weatherData=WeatherDataModel.fromJson(response);
                try{
                    double temp1=response.getJSONObject("coord").getDouble("lat");
                    simLat2=String.valueOf(temp1);
                    double temp2=response.getJSONObject("coord").getDouble("lon");
                    simLon2=String.valueOf(temp2);
                }catch (Exception e){

                }
                updateUIForSimulatedCities2(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Log.d("clima", "failed2");
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
        client3.get(WEATHER_URL,params3,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                WeatherDataModel weatherData=WeatherDataModel.fromJson(response);
                try{
                    double temp1=response.getJSONObject("coord").getDouble("lat");
                    simLat3=String.valueOf(temp1);
                    double temp2=response.getJSONObject("coord").getDouble("lon");
                    simLon3=String.valueOf(temp2);
                }catch (Exception e){

                }
                updateUIForSimulatedCities3(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Log.d("clima", "failed3");
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
        client4.get(WEATHER_URL,params4,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                WeatherDataModel weatherData=WeatherDataModel.fromJson(response);
                try{
                    double temp1=response.getJSONObject("coord").getDouble("lat");
                    simLat4=String.valueOf(temp1);
                    double temp2=response.getJSONObject("coord").getDouble("lon");
                    simLon4=String.valueOf(temp2);
                }catch (Exception e){

                }
                updateUIForSimulatedCities4(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Log.d("clima", "failed4");
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: Add getWeatherForNewCity(String city) here:

    private void getWeatherForNewCity(String city){
        // if city is empty then get geolocation otherwise find weather by city
        if(city==""){
            getWeatherForCurrentLocation();
        }else{
            RequestParams parms=new RequestParams();
            parms.put("q",city);
            parms.put("appid",APP_ID);
            letsDoSomeNetworking(parms);
        }
    }

    // get time for city by city name by making the api call
    // this makes the time show up slow because of these calls
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
                        if(Double.valueOf(temp1[0])<=5 && Double.valueOf(temp1[0])>=0){
                            nd="Night";
                            layout.setBackgroundColor(0);
                        }else if(Double.valueOf(temp1[0])>=18 && Double.valueOf(temp1[0])<=23){
                            nd="Night";
                            layout.setBackgroundColor(0);
                        }else{
                            nd="Day";
                            layout.setBackgroundColor(0);
                        }
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

    // get time for our simulated cities
    private void getTimeByCityNameForSimulatedCities(){

        // total 4 slots so 4 parameters and 4 http clients
        RequestParams params1=new RequestParams();
        params1.put("key",TIME_APP_ID);
        params1.put("format","json");
        params1.put("by","position");
        params1.put("lng",simLon1);
        params1.put("lat",simLat1);
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

                        if(Double.valueOf(temp1[0])<=5 && Double.valueOf(temp1[0])>=0){
                            nd1="Night";
                        }
                        else if(Double.valueOf(temp1[0])>=18 && Double.valueOf(temp1[0])<=23){
                            nd1="Night";
                        }else{
                            nd1="Day";
                        }
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
        params2.put("lng",simLon2);
        params2.put("lat",simLat2);
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
                    if(Double.valueOf(temp1[0])<=5 && Double.valueOf(temp1[0])>=0){
                        nd2="Night";
                    }
                    else if(Double.valueOf(temp1[0])>=18 && Double.valueOf(temp1[0])<=23){
                        nd2="Night";
                    }else{
                        nd2="Day";
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
        RequestParams params3=new RequestParams();
        params3.put("key",TIME_APP_ID);
        params3.put("format","json");
        params3.put("by","position");
        params3.put("lng",simLon3);
        params3.put("lat",simLat3);
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
                    if(Double.valueOf(temp1[0])<=5 && Double.valueOf(temp1[0])>=0){
                        nd3="Night";
                    }
                    else if(Double.valueOf(temp1[0])>=18 && Double.valueOf(temp1[0])<=23){
                        nd3="Night";
                    }else{
                        nd3="Day";
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
        RequestParams params4=new RequestParams();
        params4.put("key",TIME_APP_ID);
        params4.put("format","json");
        params4.put("by","position");
        params4.put("lng",simLon4);
        params4.put("lat",simLat4);
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
                    if(Double.valueOf(temp1[0])<=5 && Double.valueOf(temp1[0])>=0){
                        nd4="Night";
                    }
                    else if(Double.valueOf(temp1[0])>=18 && Double.valueOf(temp1[0])<=23){
                        nd4="Night";
                    }else{
                        nd4="Day";
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
    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLocation() {

        // location manager finding location using fine location (GPS)
        // note current location provider is network Provider
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

        // Ask for permission
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

    // after permission is granted log message to logcat
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
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                // weatherData takes everthing needed from response (JSON format)
                WeatherDataModel weatherData=WeatherDataModel.fromJson(response);
                // finding real time location using parameters
                try {
                    RealTimeLocation=response.getString("name");
                    double latTemp=response.getJSONObject("coord").getDouble("lat");
                    lat=String.valueOf(latTemp);
                    double lngTemp=response.getJSONObject("coord").getDouble("lon");
                    lng=String.valueOf(lngTemp);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //After getting everthing in weatherData update the UI
                updateUI(weatherData);
            }


            // Onfailure give a toast "Request Failed"
            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Log.e("clima", "Fail: "+e.toString() );
                Log.d("Cligma", "Status Code: "+statusCode);
                Toast.makeText(WeatherController.this, "Request Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }


    // TODO: Add updateUI() here:

    // Updating the ui and static variables
    private void updateUI(WeatherDataModel weather){
        mTemperatureLabel.setText(weather.getmTemperature());
        mCityLabel.setText(weather.getmCity());
        tempUpdater=mCityLabel.getText().toString();
        int resourceID=getResources().getIdentifier(weather.getmIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourceID);
    }
    // Updating the ui and static variables
    private void updateUIForSimulatedCities1(WeatherDataModel weather){
        sinTemp1=" "+weather.getmTemperature();
        tempText1.setText(sinTemp1);
        simCityName1=weather.getmCity();
        middleText1.setText(String.format("    %s    \nLocal Time: ",simCityName1));
        simResourceId1=getResources().getIdentifier(weather.getmIconName(),"drawable",getPackageName());
        weatherImage1.setImageResource(simResourceId1);
    }
    // Updating the ui and static variables
    private void updateUIForSimulatedCities2(WeatherDataModel weather){
        sinTemp2=" "+weather.getmTemperature();
        tempText2.setText(sinTemp2);
        simCityName2=weather.getmCity();
        middleText2.setText(String.format("    %s    \nLocal Time: ",simCityName2));
        simResourceId2=getResources().getIdentifier(weather.getmIconName(),"drawable",getPackageName());
        weatherImage2.setImageResource(simResourceId2);
    }
    // Updating the ui and static variables
    private void updateUIForSimulatedCities3(WeatherDataModel weather){
        sinTemp3=" "+weather.getmTemperature();
        tempText3.setText(sinTemp3);
        simCityName3=weather.getmCity();
        middleText3.setText(String.format("    %s    \nLocal Time: ",simCityName3));
        simResourceId3=getResources().getIdentifier(weather.getmIconName(),"drawable",getPackageName());
        weatherImage3.setImageResource(simResourceId3);
    }
    // Updating the ui and static variables
    private void updateUIForSimulatedCities4(WeatherDataModel weather){
        sinTemp4=" "+weather.getmTemperature();
        tempText4.setText(sinTemp4);
        simCityName4=weather.getmCity();
        middleText4.setText(String.format("    %s    \nLocal Time: ",simCityName4));
        simResourceId4=getResources().getIdentifier(weather.getmIconName(),"drawable",getPackageName());
        weatherImage4.setImageResource(simResourceId4);
    }

    // TODO: Add onPause() here:

    // onpause stopRepeating
    protected void onPause(){
        super.onPause();
        if(mlocationMangaer!=null) mlocationMangaer.removeUpdates(mlocationListener);
        stopRepeating();
    }


}

package com.londonappbrewery.climapm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class changeCityController extends AppCompatActivity {
    private String newCity,latitude,longitude="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);
        final EditText editTextField=(EditText)findViewById(R.id.queryET);
        final EditText editLongitude=(EditText)findViewById(R.id.queryET8);
        final EditText editLatitude=(EditText)findViewById(R.id.queryET7);
        ImageButton backButton =(ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        editLatitude.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                latitude=editTextField.getText().toString();
                Intent newCityIntent =new Intent(changeCityController.this,WeatherController.class);
                newCityIntent.putExtra("latitude",latitude);
                if(longitude!=""&&latitude!="")startActivity(newCityIntent);

                return false;
            }
        });
        editLongitude.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                longitude=editTextField.getText().toString();
                Intent newCityIntent =new Intent(changeCityController.this,WeatherController.class);
                newCityIntent.putExtra("longitude",longitude);
                return false;
            }
        });
        editTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                newCity=editTextField.getText().toString();
                Intent newCityIntent =new Intent(changeCityController.this,WeatherController.class);
                newCityIntent.putExtra("City",newCity);
                if(newCity!="")startActivity(newCityIntent);
                return false;
            }
        });
    }
}

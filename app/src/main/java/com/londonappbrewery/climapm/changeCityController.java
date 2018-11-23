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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class changeCityController extends AppCompatActivity {
    // declaring variables
    private String value;
    Intent newCityIntent;
    Boolean defaultMethod=false;
    RadioButton setByCityName,setByCoordinates;
    EditText hint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);
        //Initializing our objects
        hint= findViewById(R.id.queryET);
        newCityIntent =new Intent(changeCityController.this,WeatherController.class);
        setByCityName= findViewById(R.id.radioButton3);
        setByCoordinates= findViewById(R.id.radioButton2);
        // setting listener to radio buttons
        setByCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(setByCoordinates.isChecked()){
                    hint.setText("");
                    hint.setHint("Enter latitude,longitude");
                    defaultMethod=false;
                }
            }
        });
        // setting listener
        setByCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(setByCityName.isChecked()){
                    hint.setText("");
                    hint.setHint("Enter City Name");
                    defaultMethod=true;
                }
            }
        });


        final EditText editTextField= findViewById(R.id.queryET);
        ImageButton backButton = findViewById(R.id.backButton);
        // back button listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // listener to our edit box
        editTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                value=editTextField.getText().toString();
                newCityIntent.putExtra("value",value);
                newCityIntent.putExtra("defaultMethod",defaultMethod);
                if(value!=null)startActivity(newCityIntent);
                return false;
            }
        });
    }
}

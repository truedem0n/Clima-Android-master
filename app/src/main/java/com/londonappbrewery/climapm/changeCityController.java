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
    private String value;
    Intent newCityIntent;
    Boolean defaultMethod=false;
    RadioButton setByCityName,setByCoordinates;
    EditText hint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);
        hint=(EditText)findViewById(R.id.queryET);
        newCityIntent =new Intent(changeCityController.this,WeatherController.class);
        setByCityName=(RadioButton)findViewById(R.id.radioButton3);
        setByCoordinates=(RadioButton)findViewById(R.id.radioButton2);
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


        final EditText editTextField=(EditText)findViewById(R.id.queryET);
        ImageButton backButton =(ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

package com.example.cashonwise.cashonwise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void successfulSignUp(View view){
        Toast.makeText(getApplicationContext(), "Your Account Has Been Registered Successfully.", Toast.LENGTH_LONG).show();
        finish();

    }
}

package com.example.cashonwise.cashonwise;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText password;
    private Button signUpButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryGreeny));
        }

        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        password = (EditText)findViewById(R.id.editTextPassword);
        signUpButton = (Button)findViewById(R.id.signUpButton);
        loginButton = (Button)findViewById(R.id.loginButton);

    }

    public void verifyAccount(View view){
        // check existing ID
        // Check the password

        // Success and proceed
        Intent goToMenuNavi = new Intent(this, MenuActivity.class);
        startActivity(goToMenuNavi);
    }
}

package com.example.cashonwise.cashonwise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinnerState;
    EditText editTextName, editTextIC, editTextContact, editTextAddress, editTextPosCode, editTextEmail, editTextPassword, editTextRePassword, editTextPin, editTextRepin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        spinnerState = (Spinner)findViewById(R.id.spinnerState);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Mstate_arr, android.R.layout.simple_spinner_item);
        spinnerState.setAdapter(adapter);
        spinnerState.setOnItemSelectedListener(SignupActivity.this);

        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextIC = (EditText)findViewById(R.id.editTextIC);
        editTextContact = (EditText)findViewById(R.id.editTextContact);
        editTextAddress = (EditText)findViewById(R.id.editTextAddress);
        editTextPosCode = (EditText)findViewById(R.id.editTextPosCode);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextRePassword = (EditText)findViewById(R.id.editTextRePassword);
        editTextPin = (EditText)findViewById(R.id.editTextPin);
        editTextRepin = (EditText)findViewById(R.id.editTextRePin);

    }

    public void successfulSignUp(){
        Toast.makeText(getApplicationContext(), "Your Account Has Been Registered Successfully.", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onClickCreate(View view){
        if (editTextName.getText().toString().isEmpty() || editTextIC.getText().toString().isEmpty() || editTextContact.getText().toString().isEmpty() || editTextAddress.getText().toString().isEmpty() || editTextPosCode.getText().toString().isEmpty() || editTextEmail.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty() || editTextRePassword.getText().toString().isEmpty() || editTextPin.getText().toString().isEmpty() || editTextRepin.getText().toString().isEmpty() || spinnerState.getSelectedItemPosition() == -1){
            Toast.makeText(getApplicationContext(), "Please Fill Up All the Detail.", Toast.LENGTH_LONG).show();
        }else{
            if(isEmailValid(editTextEmail.getText().toString())){
                if(editTextPassword.getText().toString().equals(editTextRePassword.getText().toString())){
                    if (editTextPin.getText().toString().equals(editTextRepin.getText().toString())){
                        successfulSignUp();
                    }else{
                        Toast.makeText(getApplicationContext(), "Your Pin is Not Matched, Please Confirm.", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Your Password is Not Matched, Please Confirm.", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Your Email Format is Wrong, Please Confirm.", Toast.LENGTH_LONG).show();
            }

        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

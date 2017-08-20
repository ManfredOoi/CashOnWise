package com.example.cashonwise.cashonwise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinnerState;
    EditText editTextName, editTextIC, editTextContact, editTextAddress, editTextPosCode, editTextEmail, editTextPassword, editTextRePassword, editTextPin, editTextRepin;
    String AES = "AES", password = "COW12345", homeAddress;
    String encryptedPassword, encryptedPin, decryptedPassword, decryptedPin;

    int numChar, numberM;
    String accountID = "C0001";
    String incrementAccountID = "";
    char checkChar;

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
                        try{
                            editTextName.getText().toString();
                            editTextIC.getText().toString();
                            editTextContact.getText().toString();
                            editTextEmail.getText().toString();
                            homeAddress = editTextAddress.getText().toString() + ", " + editTextPin.getText().toString() + ", " + spinnerState.getSelectedItem().toString();
                            encryptedPassword = encrypt(editTextPassword.getText().toString(), password);
                            encryptedPin = encrypt(editTextPin.getText().toString(), password);

                            decryptedPassword = decrypt(encryptedPassword, password);
                            decryptedPin = decrypt(encryptedPin, password);

                            autoIDGenerate();
                            //Toast.makeText(getApplicationContext(), "Password: " + decryptedPassword + " Pin: " + decryptedPin, Toast.LENGTH_LONG).show();
                            successfulSignUp();
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "Incorrect Password of AES", Toast.LENGTH_LONG).show();
                        }
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

    private String encrypt(String passwordForEncrypt, String password) throws Exception{
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(passwordForEncrypt.getBytes());
        String encryptValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptValue;
    }

    private String decrypt(String encryptedPassword, String password)throws Exception{
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(encryptedPassword, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void autoIDGenerate(){
        numChar = 0;
        for(int i = 0 ; i < accountID.length(); i++){
            checkChar = accountID.charAt(i);
            if(Character.isLetter(checkChar) || checkChar == '0'){
                numChar++;
            }else{
                break;
            }
        }

        if(numChar == 1){
            accountID = accountID.replaceAll("C", "");
            numberM = Integer.parseInt(accountID) + 1;
            incrementAccountID = 'C' + Integer.toString(numberM);
        }else if(numChar == 2){
            accountID = accountID.replaceAll("C0", "");
            numberM = Integer.parseInt(accountID) + 1;
            incrementAccountID = "C0" + Integer.toString(numberM);
            if(numberM == 1000){
                incrementAccountID = incrementAccountID.replaceAll("C0", "");
                incrementAccountID = "C" + incrementAccountID;
            }
        }else if(numChar == 3){
            accountID = accountID.replaceAll("C00", "");
            numberM = Integer.parseInt(accountID) + 1;
            incrementAccountID = "C00" + Integer.toString(numberM);
            if(numberM == 100){
                incrementAccountID = incrementAccountID.replaceAll("C00", "");
                incrementAccountID = "C0" + incrementAccountID;
            }
        }else if(numChar == 4){
            accountID = accountID.replaceAll("C000", "");
            numberM = Integer.parseInt(accountID) + 1;
            incrementAccountID = "C000" + Integer.toString(numberM);
            if(numberM == 10){
                incrementAccountID = incrementAccountID.replaceAll("C000", "");
                incrementAccountID = "C00" + incrementAccountID;
            }
        }
    }
}

package com.example.cashonwise.cashonwise;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ChangePinActivity extends AppCompatActivity {
    private EditText editTextCurrentPin;
    private EditText editTextNewPin;
    private EditText editTextConfirmPin;
    private Button buttonSubmit;
    private String AES = "AES", password = "COW12345";
    private ProgressDialog pDialog;
    RequestQueue queue;
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/account_detail.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);
        editTextCurrentPin = (EditText)findViewById(R.id.editTextCurrentPin);
        editTextNewPin = (EditText)findViewById(R.id.editTextNewPin);
        editTextConfirmPin = (EditText)findViewById(R.id.editTextConfirmPin);
        buttonSubmit = (Button)findViewById(R.id.button_submit_change_pin);
    }

    public void submit_changePin(View view){
        if(editTextConfirmPin.getText().toString().isEmpty() || editTextCurrentPin.getText().toString().isEmpty() || editTextNewPin.getText().toString().isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(ChangePinActivity.this);
            builder.setTitle("Warning");
            builder.setMessage("Please fill up the necessary detail.");
            builder.setPositiveButton("Okay", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else{ // after all fill up
            if(editTextNewPin.getText().toString().equals(editTextConfirmPin.getText().toString())){
                // check if the decrypted pin is correct


            }else{ // if the pin not identical
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePinActivity.this);
                builder.setTitle("Request Fail");
                builder.setMessage("New pin number is not same as confirm pin.");
                builder.setPositiveButton("Okay", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

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
}

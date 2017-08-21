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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ChangePinActivity extends AppCompatActivity {
    public static final String TAG = "com.example.cashonwise.cashonwise";

    private EditText editTextCurrentPin;
    private EditText editTextNewPin;
    private EditText editTextConfirmPin;
    private Button buttonSubmit;
    private String AES = "AES", password = "COW12345";
    private ProgressDialog pDialog;
    RequestQueue queue;
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/account_detail.php";
    String userid,PIN,decPIN,newPIN="",plainPIN="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);
        editTextCurrentPin = (EditText)findViewById(R.id.editTextCurrentPin);
        editTextNewPin = (EditText)findViewById(R.id.editTextNewPin);
        editTextConfirmPin = (EditText)findViewById(R.id.editTextConfirmPin);
        buttonSubmit = (Button)findViewById(R.id.button_submit_change_pin);

        userid = getIntent().getStringExtra("passID");
        pDialog = new ProgressDialog(this);
        retrivePIN(getApplicationContext(), GET_URL);
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
            if(editTextConfirmPin.getText().toString().length() == 4 && editTextCurrentPin.getText().toString().length() == 4 && editTextNewPin.getText().toString().length() == 4){
                if(editTextNewPin.getText().toString().equals(editTextConfirmPin.getText().toString())){
                    if(decPIN.matches(editTextCurrentPin.getText().toString())) {
                        // check if the decrypted pin is correct
                        Account account = new Account();
                        try {
                            plainPIN = editTextNewPin.getText().toString();
                            newPIN = encrypt(plainPIN, password);
                            account.setId(userid);
                            account.setPin(newPIN);
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                        try {
                            makeServiceCall(this, "https://cash-on-wise.000webhostapp.com/changePIN.php", account);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePinActivity.this);
                        builder.setTitle("Warning");
                        builder.setMessage("Current PIN is wrong..");
                        builder.setPositiveButton("Okay", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }else{ // if the pin not identical
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePinActivity.this);
                    builder.setTitle("Request Fail");
                    builder.setMessage("New pin number is not same as confirm pin.");
                    builder.setPositiveButton("Okay", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePinActivity.this);
                builder.setTitle("Request Fail");
                builder.setMessage("All Pin MUST have exactly 4-Digit.");
                builder.setPositiveButton("Okay", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }
    public void makeServiceCall(Context context, String url, final Account account) {
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success==0) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", account.getId());
                    params.put("pin", account.getPin());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
    private void retrivePIN(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing())
            pDialog.setMessage("Loading...");
        pDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject accountResponse = (JSONObject) response.get(i);

                                String id = accountResponse.getString("id");
                                if(id.matches(userid)){
                                    PIN = accountResponse.getString("pin");
                                    decPIN = decrypt(PIN,password);

                                }
                            }

                            if (pDialog.isShowing())
                                pDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
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

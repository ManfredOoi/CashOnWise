package com.example.cashonwise.cashonwise;

import android.app.ProgressDialog;
import android.content.Context;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "com.example.cashonwise.cashonwise";
    Spinner spinnerState;
    EditText editTextName, editTextIC, editTextContact, editTextAddress, editTextPosCode, editTextEmail, editTextPassword, editTextRePassword, editTextPin, editTextRepin;
    String AES = "AES", password = "COW12345", homeAddress;
    String encryptedPassword, encryptedPin, decryptedPassword, decryptedPin;
    int numChar, numberM;
    String accountID, LastID;
    String incrementAccountID = "";
    char checkChar;
    private ProgressDialog pDialog;
    RequestQueue queue;
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/account_detail.php";
    String pin = "";

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
        pDialog = new ProgressDialog(this);
        findLastID(getApplicationContext(), GET_URL);



    }

    public void successfulSignUp(){
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
                            autoIDGenerate();
                            editTextName.getText().toString();
                            editTextIC.getText().toString();
                            editTextContact.getText().toString();
                            editTextEmail.getText().toString();
                            homeAddress = editTextAddress.getText().toString() + ", " + editTextPosCode.getText().toString() + ", " + spinnerState.getSelectedItem().toString();
                            encryptedPassword = encrypt(editTextPassword.getText().toString(), password);
                            encryptedPin = encrypt(editTextPin.getText().toString(), password);

                            //decryptedPassword = decrypt(encryptedPassword, password);
                            //decryptedPin = decrypt(encryptedPin, password);

                            Account account = new Account();
                            account.setId(incrementAccountID);
                            account.setName(editTextName.getText().toString());
                            account.setIcnum(editTextIC.getText().toString());
                            account.setContactnum(editTextContact.getText().toString());
                            account.setAddress(homeAddress);
                            account.setEmail(editTextEmail.getText().toString());
                            account.setPassword(encryptedPassword);
                            account.setPin(encryptedPin);
                            successfulSignUp();

                            try {
                                makeServiceCall(this, "https://cash-on-wise.000webhostapp.com/signup.php", account);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
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
        accountID = LastID;
        if(accountID != null){
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
                if(numberM > 9999){
                    incrementAccountID = "";
                }
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
        }else{
            incrementAccountID = "C0001";
        }

    }


    private void findLastID(Context context, String url) {
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
                            //caList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject accountResponse = (JSONObject) response.get(i);
                                LastID = accountResponse.getString("id");
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
                    params.put("name", account.getName());
                    params.put("icnum", account.getIcnum());
                    params.put("contactnum", account.getContactnum());
                    params.put("address", account.getAddress());
                    params.put("email", account.getEmail());
                    params.put("password", account.getPassword());
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
}

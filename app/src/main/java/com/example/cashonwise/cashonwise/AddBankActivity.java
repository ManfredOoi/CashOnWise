package com.example.cashonwise.cashonwise;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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

public class AddBankActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "com.example.cashonwise.cashonwise";

    private ProgressDialog pDialog;
    private static String GET_BANK_URL = "https://cash-on-wise.000webhostapp.com/bankDetails.php";
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/account_detail.php";

    RequestQueue queue;

    Spinner spinnerState;
    String userid,bankid;
    CheckBox checkBoxAddress;
    EditText editTextPassword,editTextRePassword,editTextName, editTextIC, editTextContact, editTextAddress, editTextPosCode, editTextEmail,editTextFullAddress ;
    String AES = "AES", password = "COW12345", homeAddress;
    String encryptedPassword, encryptedPin, decryptedPassword, decryptedPin;
    int numChar, numberM;
    String accountID, LastID;
    String incrementAccountID = "";
    char checkChar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);
        pDialog = new ProgressDialog(this);
        userid = getIntent().getStringExtra("passID");

        spinnerState = (Spinner)findViewById(R.id.spinnerState);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Mstate_arr, android.R.layout.simple_spinner_item);
        spinnerState.setAdapter(adapter);
        spinnerState.setOnItemSelectedListener(AddBankActivity.this);
        checkBoxAddress = (CheckBox)findViewById(R.id.checkBoxAddress);

        checkBoxAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    editTextAddress.setEnabled(true);
                    editTextPosCode.setEnabled(true);
                    spinnerState.setEnabled(true);
                } else {
                    editTextAddress.setEnabled(false);
                    editTextPosCode.setEnabled(false);
                    spinnerState.setEnabled(false);
                }
            }
        });
        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextIC = (EditText)findViewById(R.id.editTextIC);
        editTextContact = (EditText)findViewById(R.id.editTextContact);
        editTextFullAddress = (EditText)findViewById(R.id.editTextFullAddress);
        editTextAddress = (EditText)findViewById(R.id.editTextAddress);
        editTextPosCode = (EditText)findViewById(R.id.editTextPosCode);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextRePassword = (EditText)findViewById(R.id.editTextRePassword);


        editTextAddress.setEnabled(false);
        editTextPosCode.setEnabled(false);
        spinnerState.setEnabled(false);
        editTextFullAddress.setEnabled(false);
        findLastID(getApplicationContext(), GET_BANK_URL);
        downloadDetails(getApplicationContext(), GET_URL);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private void downloadDetails(Context context, String url) {
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

                                String id = accountResponse.getString("id");

                                if(id.matches(userid)) {
                                    String name = accountResponse.getString("name");
                                    String icnum = accountResponse.getString("icnum");
                                    String contactnum = accountResponse.getString("contactnum");
                                    String address = accountResponse.getString("address");
                                    String email = accountResponse.getString("email");

                                    Account account = new Account(name, icnum, contactnum, address, email);

                                    editTextName.setText(account.getName());
                                    editTextIC.setText(account.getIcnum());
                                    editTextContact.setText(account.getContactnum());
                                    editTextFullAddress.setText(account.getAddress());
                                    editTextEmail.setText(account.getEmail());
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
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public void onClickSave(View view){
        if (editTextName.getText().toString().isEmpty() || editTextIC.getText().toString().isEmpty() || editTextContact.getText().toString().isEmpty()   || editTextEmail.getText().toString().isEmpty() ||editTextPassword.getText().toString().isEmpty()||editTextRePassword.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please Fill Up All the Detail.", Toast.LENGTH_LONG).show();
        }else{
            if(isEmailValid(editTextEmail.getText().toString())){
                if(editTextPassword.getText().toString().equals(editTextRePassword.getText().toString())) {
                    try {
                        BankAcc bankAcc = new BankAcc();
                        autoIDGenerate();
                        if (checkBoxAddress.isChecked()) {
                            homeAddress = editTextAddress.getText().toString() + ", " + editTextPosCode.getText().toString() + ", " + spinnerState.getSelectedItem().toString();
                            bankAcc.setAddress(homeAddress);
                        } else {
                            bankAcc.setAddress(editTextFullAddress.getText().toString());
                        }
                        encryptedPassword = encrypt(editTextPassword.getText().toString(), password);


                        bankAcc.setId(incrementAccountID);
                        bankAcc.setName(editTextName.getText().toString());
                        bankAcc.setIcnum(editTextIC.getText().toString());
                        bankAcc.setContactnum(editTextContact.getText().toString());
                        bankAcc.setEmail(editTextEmail.getText().toString());
                        bankAcc.setPassword(encryptedPassword);
                        bankAcc.setType("");
                        bankAcc.setCow_id(userid);

                        try {

                            makeServiceCall(this, "https://cash-on-wise.000webhostapp.com/AddBank.php", bankAcc);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Incorrect Password of AES", Toast.LENGTH_LONG).show();
                    }
                }else{
                        Toast.makeText(getApplicationContext(), "Your Password is Not Matched, Please Confirm.", Toast.LENGTH_LONG).show();
                    }
            }else{
                Toast.makeText(getApplicationContext(), "Your Email Format is Wrong, Please Confirm.", Toast.LENGTH_LONG).show();
            }

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
    public void makeServiceCall(Context context, String url, final BankAcc bankAcc) {
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
                    params.put("id", bankAcc.getId());
                    params.put("name", bankAcc.getName());
                    params.put("icnum", bankAcc.getIcnum());
                    params.put("contactnum", bankAcc.getContactnum());
                    params.put("address", bankAcc.getAddress());
                    params.put("email", bankAcc.getEmail());
                    params.put("password", bankAcc.getPassword());
                    params.put("type", bankAcc.getType());
                    params.put("cow_id", bankAcc.getCow_id());
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
    public void autoIDGenerate(){
        bankid = LastID;
        if(bankid != null){
            numChar = 0;
            for(int i = 0 ; i < bankid.length(); i++){
                checkChar = bankid.charAt(i);
                if(Character.isLetter(checkChar) || checkChar == '0'){
                    numChar++;
                }else{
                    break;
                }
            }

            if(numChar == 1){
                bankid = bankid.replaceAll("B", "");
                numberM = Integer.parseInt(bankid) + 1;
                incrementAccountID = 'B' + Integer.toString(numberM);
                if(numberM > 9999){
                    incrementAccountID = "";
                }
            }else if(numChar == 2){
                bankid = bankid.replaceAll("B0", "");
                numberM = Integer.parseInt(bankid) + 1;
                incrementAccountID = "B0" + Integer.toString(numberM);
                if(numberM == 1000){
                    incrementAccountID = incrementAccountID.replaceAll("B0", "");
                    incrementAccountID = "B" + incrementAccountID;
                }
            }else if(numChar == 3){
                bankid = bankid.replaceAll("B00", "");
                numberM = Integer.parseInt(bankid) + 1;
                incrementAccountID = "B00" + Integer.toString(numberM);
                if(numberM == 100){
                    incrementAccountID = incrementAccountID.replaceAll("B00", "");
                    incrementAccountID = "B0" + incrementAccountID;
                }
            }else if(numChar == 4){
                bankid = bankid.replaceAll("B000", "");
                numberM = Integer.parseInt(bankid) + 1;
                incrementAccountID = "B000" + Integer.toString(numberM);
                if(numberM == 10){
                    incrementAccountID = incrementAccountID.replaceAll("B000", "");
                    incrementAccountID = "B00" + incrementAccountID;
                }
            }
        }else{
            incrementAccountID = "B0001";
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
}

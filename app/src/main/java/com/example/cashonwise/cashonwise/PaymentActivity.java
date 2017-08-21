package com.example.cashonwise.cashonwise;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.example.cashonwise.cashonwise.SignupActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class PaymentActivity extends AppCompatActivity {
    public static final String TAG = "com.example.cashonwise.cashonwise";
    List<Account> acList;
    private String AES = "AES", password = "COW12345";
    private Button button_scan, button_proceed;
    private EditText editTextPin_payment, editTextAmountToPay;
    private double Money;
    private String date, place, decryptedPin, userid, account_Pin, decaccount_Pin, fullFormatDate;;

    private ProgressDialog pDialog;
    RequestQueue queue;
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/account_detail.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        editTextAmountToPay = (EditText)findViewById(R.id.editTextAmountToPay);
        editTextPin_payment = (EditText)findViewById(R.id.editTextPin_payment);
        button_scan = (Button)findViewById(R.id.button_scan);
        button_proceed = (Button)findViewById(R.id.button_proceed);
        editTextPin_payment.setEnabled(false);
        editTextAmountToPay.setEnabled(false);
        button_proceed.setEnabled(false);
        final Activity activity = this;
        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Be Patient.It will take you a moment.");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(true);
                integrator.initiateScan();
            }
        });
        userid = getIntent().getStringExtra("passID");

        pDialog = new ProgressDialog(this);
        //acList = new ArrayList<>();
        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }
        retriveIDPass(getApplicationContext(), GET_URL);

    }

    @Override
    public void onResume(){
        super.onResume();
        retriveIDPass(getApplicationContext(), GET_URL);

    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    private void retriveIDPass(Context context, String url) {
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
                            //acList.clear();

                            for (int i = 0; i < response.length(); i++) {
                                try{
                                    JSONObject accountResponse = (JSONObject) response.get(i);

                                    String id = accountResponse.getString("id");
                                    if(id.matches(userid)){
                                        account_Pin = accountResponse.getString("pin");
                                        decaccount_Pin = decrypt(account_Pin,password);

                                    }
                                    //Account account = new Account(userid, decaccount_Pin);
                                    //acList.add(account);
                                } catch (Exception e) {
                                    //e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Error: " , Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String resultQR = "";
        try{
            if(result != null){
                if(result.getContents() == null){
                    Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
                }
                else {
                    resultQR = result.getContents();
                    fullFormatDate = resultQR.substring(0, 10);
                    date = resultQR.substring(0, 10);
                    date = date.replace("/", "");
                    place = resultQR.substring(10, resultQR.length() - 6);
                    Money = Double.parseDouble(resultQR.substring(resultQR.length() - 6, resultQR.length())) / 100;

                    if(Money > 9999.99){
                        Toast.makeText(this, "Invalid QR Format. Please Refer To Cashier", Toast.LENGTH_SHORT).show();
                    }else{
                        editTextAmountToPay.setText("RM " + Money);
                        if(!editTextAmountToPay.getText().toString().isEmpty()){
                            editTextPin_payment.setEnabled(true);
                            button_proceed.setEnabled(true);
                        }
                    }
                }
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }catch (Exception e){
            Toast.makeText(this, "Invalid QR Format", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickProceedPayment(View view){ //---------------------------------------------------------------------------
        boolean check = false;
       // for (int i = 0; i < acList.size(); i++) {
            //userid = acList.get(i).getId();
            //decaccount_Pin = acList.get(i).getPassword();

            if(editTextPin_payment.getText().toString().equals(decaccount_Pin)){
                Intent successfulActivity = new Intent(this, SuccessfulActivity.class);
                successfulActivity.putExtra("FULLDATE", fullFormatDate);
                successfulActivity.putExtra("LOCATION", place);
                successfulActivity.putExtra("DATE", date);
                successfulActivity.putExtra("AMOUNTPAID", Money);
                successfulActivity.putExtra("passID", userid);
                startActivity(successfulActivity);
                check = true;
                finish();
                //break;
            }else{
                Toast.makeText(this, "Incorrect Pin Number, Please Enter Again.", Toast.LENGTH_LONG).show();
            }

        //}
        if (check == false){
            Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_LONG).show();
        }

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

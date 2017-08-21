package com.example.cashonwise.cashonwise;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SuccessfulActivity extends AppCompatActivity {
    public static final String TAG = "com.example.cashonwise.cashonwise";
    List<Account> acList;
    String userid, balance="0";
    private ProgressDialog pDialog;
    RequestQueue queue;
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/account_detail.php";
    private CircleProgressBar circleProgressBar;
    int progressValue = 0, numChar, numberT;
    Random increment = new Random();
    String transactionID = "20082017T000999"; // from DB
    String newTransactionID = "", dates, location, fullFormatDate;
    double amountPaid, accountAmount;
    char checkChar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful);
        circleProgressBar = (CircleProgressBar)findViewById(R.id.circleProgressBar);
        circleProgressBar.setColorSchemeColors(android.R.color.holo_blue_bright);

        Intent fromPaymentActivity = getIntent();
        fullFormatDate = fromPaymentActivity.getStringExtra("FULLDATE");
        amountPaid = fromPaymentActivity.getDoubleExtra("AMOUNTPAID", 0.00);
        dates = fromPaymentActivity.getStringExtra("DATE").toString(); // From QR
        location = fromPaymentActivity.getStringExtra("LOCATION").toString();
        autoTransactionID(dates);

        userid = fromPaymentActivity.getStringExtra("passID");

        pDialog = new ProgressDialog(this);
        retriveBalance(getApplicationContext(), GET_URL);

        saveTransactionRecord(userid, newTransactionID, location, dates, fullFormatDate, amountPaid);

        circleProgressBar.setVisibility(View.VISIBLE);
        CountDownTimer countDownTimer = new CountDownTimer(7000, 500) {
            @Override
            public void onTick(long l) {
                progressValue += increment.nextInt((15 - 5) + 1) + 5;
                circleProgressBar.setProgress(progressValue);
                if(progressValue >= 100){
                    circleProgressBar.setVisibility(View.INVISIBLE);
                    finish();
                }
            }

            @Override
            public void onFinish() {
                circleProgressBar.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void retriveBalance(Context context, String url) {
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
                                    accountAmount = Double.parseDouble(accountResponse.getString("balance"));
                                    saveAccountBalance();
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

    public void saveAccountBalance(){
        accountAmount -= amountPaid;
        Account account = new Account();
        try {
            account.setBalance("" + accountAmount);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        try {
            makeServiceCall(this, "https://cash-on-wise.000webhostapp.com/updateBalance.php", account);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                    params.put("balance", account.getBalance());
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

    public void autoTransactionID(String dateFromQR){
        //check if the database has record, else New ID
        // Example: DB retrieve 20082017T000100
        String dateFromDB = "20082017"; // after get from db and substring(0, 8)
        if(transactionID != null){
            if(dateFromQR.equals(dateFromDB)){
                transactionID = transactionID.substring(8, transactionID.length());
                numChar = 0;
                for(int i = 0 ; i < transactionID.length(); i++){
                    checkChar = transactionID.charAt(i);
                    if(Character.isLetter(checkChar) || checkChar == '0'){
                        numChar++;
                    }else{
                        break;
                    }
                }

                if(numChar == 1){
                    transactionID = transactionID.replaceAll("T", "");
                    numberT = Integer.parseInt(transactionID) + 1;
                    newTransactionID = dateFromDB + 'T' + Integer.toString(numberT);
                    if(numberT == 1000000){
                        newTransactionID = "";
                    }
                }else if(numChar == 2){
                    transactionID = transactionID.replaceAll("T0", "");
                    numberT = Integer.parseInt(transactionID) + 1;
                    newTransactionID = dateFromDB +"T0" + Integer.toString(numberT);
                    if(numberT == 100000){
                        newTransactionID = newTransactionID.replaceAll("T", "");
                        newTransactionID = dateFromDB +"T" + newTransactionID;
                    }
                }else if(numChar == 3){
                    transactionID = transactionID.replaceAll("T00", "");
                    numberT = Integer.parseInt(transactionID) + 1;
                    newTransactionID = dateFromDB +"T00" + Integer.toString(numberT);
                    if(numberT == 10000){
                        newTransactionID = newTransactionID.replaceAll("T0", "");
                        newTransactionID = dateFromDB +"T0" + newTransactionID;
                    }
                }else if(numChar == 4){
                    transactionID = transactionID.replaceAll("T000", "");
                    numberT = Integer.parseInt(transactionID) + 1;
                    newTransactionID = dateFromDB +"T000" + Integer.toString(numberT);
                    if(numberT == 1000){
                        newTransactionID = newTransactionID.replaceAll("T00", "");
                        newTransactionID = dateFromDB +"T00" + newTransactionID;
                    }
                }else if(numChar == 5){
                    transactionID = transactionID.replaceAll("T0000", "");
                    numberT = Integer.parseInt(transactionID) + 1;
                    newTransactionID = dateFromDB +"T0000" + Integer.toString(numberT);
                    if(numberT == 100){
                        newTransactionID = newTransactionID.replaceAll("T000", "");
                        newTransactionID = dateFromDB +"T000" + newTransactionID;
                    }
                }else if(numChar == 6){
                    transactionID = transactionID.replaceAll("T00000", "");
                    numberT = Integer.parseInt(transactionID) + 1;
                    newTransactionID = dateFromDB +"T00000" + Integer.toString(numberT);
                    if(numberT == 10){
                        newTransactionID = newTransactionID.replaceAll("T0000", "");
                        newTransactionID = dateFromDB +"T0000" + newTransactionID;
                    }
                } // end of process generate
            }else{
                newTransactionID = dateFromQR + "T000001";
            }// end checking the QR date whether match to DB date if not, then create a new one which is the QR generate to us the date
        }else{
            newTransactionID = dateFromQR + "T000001"; // check if the DB
        } // end checking if there is no ID in the DB
        //Toast.makeText(getApplicationContext(), "ID: " + newTransactionID, Toast.LENGTH_LONG).show();

    } // end of auto-generate

    public void saveTransactionRecord(String IDAccount, String newTransactionID, String location, String date, String fullDate, double amountPaid){

    }

}

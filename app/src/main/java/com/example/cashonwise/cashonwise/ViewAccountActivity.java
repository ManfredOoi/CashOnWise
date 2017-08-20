package com.example.cashonwise.cashonwise;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewAccountActivity extends AppCompatActivity {

    public static final String TAG = "com.example.cashonwise.cashonwise";

    private ProgressDialog pDialog;
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/account_detail.php";
    RequestQueue queue;
    TextView textViewName, textViewIC, textViewContact, textViewAddress, textViewEmail, textViewPassword, textViewPIN;
    EditText editTextName,editTextIC,editTextContact,editTextAddress,editTextEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);

        pDialog = new ProgressDialog(this);

        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
       }
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewIC = (TextView) findViewById(R.id.textViewIC);
        textViewContact = (TextView) findViewById(R.id.textViewContact);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        //textViewPassword = (TextView) findViewById(R.id.textViewPassword);
        //textViewPIN = (TextView) findViewById(R.id.textViewPIN);

        editTextName = (EditText) findViewById(R.id.editTextName) ;
        editTextIC = (EditText) findViewById(R.id.editTextIC) ;
        editTextContact = (EditText) findViewById(R.id.editTextContact) ;
        editTextAddress = (EditText) findViewById(R.id.editTextAddress) ;
        editTextEmail = (EditText) findViewById(R.id.editTextEmail) ;
        downloadCourse(getApplicationContext(), GET_URL);
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
    private void downloadCourse(Context context, String url) {
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

                                JSONObject courseResponse = (JSONObject) response.get(i);

                                String id = courseResponse.getString("id");

                                if(id.matches("1")) {
                                    String name = courseResponse.getString("name");
                                    String icnum = courseResponse.getString("icnum");
                                    String contactnum = courseResponse.getString("contactnum");
                                    String address = courseResponse.getString("address");
                                    String email = courseResponse.getString("email");
                                    String password = courseResponse.getString("password");
                                    String pin = courseResponse.getString("pin");

                                    Account account = new Account(name, icnum, contactnum, address, email, password, pin);
                                    //caList.add(account);
                                    //loadCourse();

                                    editTextName.setText(account.getName());
                                    editTextIC.setText(account.getIcnum());
                                    editTextContact.setText(account.getContactnum());
                                    editTextAddress.setText(account.getAddress());
                                    editTextEmail.setText(account.getEmail());


                                    textViewName.setText(textViewName.getText() + ":");
                                    textViewIC.setText(textViewIC.getText() + ":");
                                    textViewContact.setText(textViewContact.getText() + ":");
                                    textViewAddress.setText(textViewAddress.getText() + ":");
                                    textViewEmail.setText(textViewEmail.getText() + ":");
                                    //textViewPassword.setText(textViewPassword.getText() + ":");
                                    //textViewPIN.setText(textViewPIN.getText() + ":");
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
    protected void onPause() {
        super.onPause();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
    public void SaveChange(View v){
        Account account = new Account();

        account.setId("1");
        account.setName(editTextName.getText().toString());
        account.setIcnum(editTextIC.getText().toString());
        account.setContactnum(editTextContact.getText().toString());
        account.setAddress(editTextAddress.getText().toString());
        account.setEmail(editTextEmail.getText().toString());

        try {
            makeServiceCall(this, "https://cash-on-wise.000webhostapp.com/save_change.php", account);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    public void btncancel(View v){
        finish();
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

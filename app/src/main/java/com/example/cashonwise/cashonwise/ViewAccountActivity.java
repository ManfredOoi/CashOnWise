package com.example.cashonwise.cashonwise;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.cashonwise.cashonwise.R.id.spinnerState;

public class ViewAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "com.example.cashonwise.cashonwise";

    private ProgressDialog pDialog;
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/account_detail.php";
    RequestQueue queue;

    Spinner spinnerState;
    String homeAddress,userid;
    CheckBox checkBoxAddress;
    EditText editTextName, editTextIC, editTextContact, editTextAddress, editTextPosCode, editTextEmail,editTextFullAddress ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);

        pDialog = new ProgressDialog(this);
        userid = getIntent().getStringExtra("passID");
        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
       }
        spinnerState = (Spinner)findViewById(R.id.spinnerState);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Mstate_arr, android.R.layout.simple_spinner_item);
        spinnerState.setAdapter(adapter);
        spinnerState.setOnItemSelectedListener(ViewAccountActivity.this);
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
        editTextAddress.setEnabled(false);
        editTextPosCode.setEnabled(false);
        spinnerState.setEnabled(false);
        editTextFullAddress.setEnabled(false);

        downloadDetails(getApplicationContext(), GET_URL);
    }


    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

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

    @Override
    protected void onPause() {
        super.onPause();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void onClickSave(View view){
        if (editTextName.getText().toString().isEmpty() || editTextIC.getText().toString().isEmpty() || editTextContact.getText().toString().isEmpty()   || editTextEmail.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please Fill Up All the Detail.", Toast.LENGTH_LONG).show();
        }else{
            if(isEmailValid(editTextEmail.getText().toString())){
                Account account = new Account();


                if (checkBoxAddress.isChecked()) {
                    homeAddress = editTextAddress.getText().toString() + ", " + editTextPosCode.getText().toString() + ", " + spinnerState.getSelectedItem().toString();
                    account.setAddress(homeAddress);
                }else{
                    account.setAddress(editTextFullAddress.getText().toString());
                }
                            account.setId(userid);
                            account.setName(editTextName.getText().toString());
                            account.setIcnum(editTextIC.getText().toString());
                            account.setContactnum(editTextContact.getText().toString());

                            account.setEmail(editTextEmail.getText().toString());

                            try {

                                makeServiceCall(this, "https://cash-on-wise.000webhostapp.com/save_change.php", account);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

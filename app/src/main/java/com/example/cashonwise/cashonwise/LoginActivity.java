package com.example.cashonwise.cashonwise;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "com.example.cashonwise.cashonwise";
    List<Account> acList;
    private EditText editTextID;
    private EditText editTextPassword;
    private Button chkBoxRememberMe;
    private Button signUpButton;
    private Button loginButton;
    private Button forgotButton;
    private String AES = "AES", password = "COW12345", userid,account_password,decaccount_password;
    private ProgressDialog pDialog;
    RequestQueue queue;
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/account_detail.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryGreeny));
        }
        pDialog = new ProgressDialog(this);
        editTextID = (EditText)findViewById(R.id.editTextID);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        signUpButton = (Button)findViewById(R.id.signUpButton);
        loginButton = (Button)findViewById(R.id.loginButton);
        forgotButton = (Button)findViewById(R.id.buttonForgot);

        acList = new ArrayList<>();
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
    public void ForgotPassword(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_forget_password, null);
        EditText editTextLoginId = (EditText)mView.findViewById(R.id.editTextLoginId);
        Button buttonConfirm = (Button)mView.findViewById(R.id.buttonConfirm);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code to verify id and retrieve email
                Toast.makeText(LoginActivity.this,
                        "Password successful send to your email.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(mView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void verifyAccount(View view){

        if(editTextID.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please Fill In Your Account Detail", Toast.LENGTH_SHORT).show();
        }else {
            // check existing ID and decrypt password
            boolean check = false;
            for (int i = 0; i < acList.size(); i++) {

                userid = acList.get(i).getId();
                decaccount_password = acList.get(i).getPassword();
                if (editTextID.getText().toString().equalsIgnoreCase(userid) && editTextPassword.getText().toString().equals(decaccount_password)) {
                    Toast.makeText(getApplicationContext(), "Welcome "+userid, Toast.LENGTH_LONG).show();

                    // Success and proceed
                    Intent goToMenuNavi = new Intent(this, MenuActivity.class);
                    goToMenuNavi.putExtra("passID",userid );
                    startActivity(goToMenuNavi);
                    check = true;

                }

            }
            if (check == false){
                Toast.makeText(getApplicationContext(), "Invalid ID or Password", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void retriveIDPass(Context context, String url) {
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
                            acList.clear();

                            for (int i = 0; i < response.length(); i++) {
                                try{
                                JSONObject accountResponse = (JSONObject) response.get(i);

                                userid = accountResponse.getString("id");
                                account_password = accountResponse.getString("password");
                                decaccount_password = decrypt(account_password, password);
                                Account account = new Account(userid, decaccount_password);
                                acList.add(account);
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

    public void toSignUp(View view){
        Intent goToSignUp = new Intent(this, SignupActivity.class);
        startActivity(goToSignUp);
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

package com.example.cashonwise.cashonwise;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.AsyncListUtil;
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
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "com.example.cashonwise.cashonwise";
    List<Account> acList;
    private EditText editTextID;
    private EditText editTextPassword;
    private Button chkBoxRememberMe;
    private Button signUpButton;
    private Button loginButton;
    private Button forgotButton;
    private String AES = "AES", password = "COW12345", userid,account_password, decaccount_password, name = "", email = "", subject = "Cash On Wise Forget Password Request", content = "";
    private ProgressDialog pDialog;
    RequestQueue queue;
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/account_detail.php";
    private Session session = null;
    private Context context = null;
    private AlertDialog dialog;

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
        context = this;
    }
    @Override
    public void onResume(){
        super.onResume();
        retriveIDPass(getApplicationContext(), GET_URL);

    }

    @Override
    protected void onPause() {
        super.onPause();
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
        final EditText editTextLoginId = (EditText)mView.findViewById(R.id.editTextLoginId);
        Button buttonConfirm = (Button)mView.findViewById(R.id.buttonConfirm);
        Button buttonCancel = (Button)mView.findViewById(R.id.buttonCancel);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code to verify id and retrieve email
                if(editTextLoginId.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter your Login ID", Toast.LENGTH_SHORT).show();
                }else {
                    // check existing ID, get Email address and decrypt password
                    boolean check = false;

                    // Looping to check LoginID is valid or not
                    for (int i = 0; i < acList.size(); i++) {
                        userid = acList.get(i).getId();

                        if (editTextLoginId.getText().toString().equalsIgnoreCase(userid)) {
                            decaccount_password = acList.get(i).getPassword();
                            email = acList.get(i).getEmail();
                            name = acList.get(i).getName();
                            content = "Dear Mr./Ms. " + name + ", </br></br>Your password is : <b>" + decaccount_password + "</b>.</br></br>Best regards,</br>Teddy Chow</br>Senior Officer</br>Cash On Wise";

                            try{ // Code for email
                                Properties props = new Properties();
                                props.put("mail.smtp.host", "smtp.gmail.com");
                                props.put("mail.smtp.socketFactory.port", "465");
                                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                                props.put("mail.smtp.auth", "true");
                                props.put("mail.smtp.port", "465");

                                session = Session.getDefaultInstance(props, new Authenticator() {
                                    @Override
                                    protected PasswordAuthentication getPasswordAuthentication() {
                                        return new PasswordAuthentication("chowkm-pa13@student.tarc.edu.my", "rsd2grp2oct16");
                                    }
                                });
                                pDialog.setMessage("Sending your password to " + email);
                                pDialog.show();

                                RetrieveFeedTask task = new RetrieveFeedTask();
                                task.execute();
                                dialog.dismiss();
                            }catch (Exception ex){
                                Toast.makeText(getApplicationContext(),
                                        "Error : 149 " + ex.getMessage().toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            check = true;
                            break;
                        }
                    }

                    if (check == false){
                        Toast.makeText(getApplicationContext(), "Invalid ID or Password", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
        builder.setView(mView);
        dialog = builder.create();
        dialog.show();
    }

    private class RetrieveFeedTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("chowkm-pa13@student.tarc.edu.my"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject(subject);
                message.setContent(content, "text/html; charset=utf-8");

                Transport.send(message);

                Toast.makeText(getApplicationContext(),
                        "Password successful send to your email.",
                        Toast.LENGTH_SHORT).show();
            }catch (MessagingException ex){
                ex.printStackTrace();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Password sent", Toast.LENGTH_SHORT).show();
        }
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
                                name = accountResponse.getString("name");
                                email = accountResponse.getString("email");
                                decaccount_password = decrypt(account_password, password);
                                Account account = new Account(userid, decaccount_password, name, email);
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

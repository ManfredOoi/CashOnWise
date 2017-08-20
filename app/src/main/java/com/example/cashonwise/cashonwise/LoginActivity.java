package com.example.cashonwise.cashonwise;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class LoginActivity extends AppCompatActivity {
    private EditText editTextID;
    private EditText editTextPassword;
    private Button chkBoxRememberMe;
    private Button signUpButton;
    private Button loginButton;
    private String AES = "AES", password = "COW12345";
    private String decryptedPassword;

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

        editTextID = (EditText)findViewById(R.id.editTextID);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);

    }

    public void verifyAccount(View view){
        if(editTextID.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please Fill In Your Account Detail", Toast.LENGTH_SHORT).show();
        }else {
            // Get ID and Password from DB

            // decrypt password

            // check existing ID and decrypt password
            if(editTextID.getText().toString().equals("test") && editTextPassword.getText().toString().equals("test")){
                // Success and proceed
                Intent goToMenuNavi = new Intent(this, MenuActivity.class);
                startActivity(goToMenuNavi);
            }else{
                Toast.makeText(getApplicationContext(), "Invalid ID or Password", Toast.LENGTH_SHORT).show();
            }
        }
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

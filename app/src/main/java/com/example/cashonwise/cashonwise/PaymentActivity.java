package com.example.cashonwise.cashonwise;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class PaymentActivity extends AppCompatActivity {

    private Button button_scan, button_proceed;
    private EditText editTextPin_payment, editTextAmountToPay;
    private double Money;
    private String date, place;
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
                    date = resultQR.substring(0, 8);
                    place = resultQR.substring(8, resultQR.length() - 6);
                    //Toast.makeText(getApplicationContext(), "Your Purchase location: " + place + " on " + date, Toast.LENGTH_LONG).show();
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

    public void onClickProceedPayment(View view){
        if(editTextPin_payment.getText().toString().equals("1234")){
            Intent successfulActivity = new Intent(this, SuccessfulActivity.class);
            successfulActivity.putExtra("LOCATION", place);
            successfulActivity.putExtra("DATE", date);
            successfulActivity.putExtra("AMOUNTPAID", Money);
            startActivity(successfulActivity);
            finish();
        }else{
            Toast.makeText(this, "Incorrect Pin Number, Please Enter Again.", Toast.LENGTH_LONG).show();
        }
    }
}

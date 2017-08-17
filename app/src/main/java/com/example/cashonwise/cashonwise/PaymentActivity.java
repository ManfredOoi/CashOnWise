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
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class PaymentActivity extends AppCompatActivity {

    private Button button_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        button_scan = (Button)findViewById(R.id.button_scan);
        final Activity activity = this;
        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan QR Code Now");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(false); //
                integrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String resultQR = "";
        double Money;
            if(result != null){
                if(result.getContents() == null){
                    Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
                }
                else {
                    resultQR = result.getContents();
                    resultQR = resultQR.substring(14, 19); // no need 0, start 1
                    Money = Double.parseDouble(resultQR) / 100;
                    Toast.makeText(this, "Result: RM" + Money, Toast.LENGTH_LONG).show();
                    //Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(result.getContents()));
                    //startActivity(browse);
                }
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }

    }
}

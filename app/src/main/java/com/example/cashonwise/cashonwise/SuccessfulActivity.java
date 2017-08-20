package com.example.cashonwise.cashonwise;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.Random;

public class SuccessfulActivity extends AppCompatActivity {
    private CircleProgressBar circleProgressBar;
    int progressValue = 0, numChar, numberT;
    Random increment = new Random();
    String transactionID = "T100000";
    String newTransactionID = "", date, location;
    double amountPaid;
    char checkChar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful);
        circleProgressBar = (CircleProgressBar)findViewById(R.id.circleProgressBar);
        circleProgressBar.setColorSchemeColors(android.R.color.holo_blue_bright);

        autoTransactionID();

        Intent fromPaymentActivity = getIntent();
        amountPaid = Double.parseDouble(fromPaymentActivity.getStringExtra("AMOUNTPAID"));
        date = fromPaymentActivity.getStringExtra("DATE");
        location = fromPaymentActivity.getStringExtra("LOCATION");
        // get account balance

        // deduct the e-balance

        // save the update balance

        saveTransactionRecord(newTransactionID, newTransactionID, location, date, amountPaid);

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

    public void autoTransactionID(){
        //check if the database has record, else New ID
        if(transactionID != null){
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
                newTransactionID = 'T' + Integer.toString(numberT);
                if(numberT == 1000000){
                    newTransactionID = "";
                }
            }else if(numChar == 2){
                transactionID = transactionID.replaceAll("T0", "");
                numberT = Integer.parseInt(transactionID) + 1;
                newTransactionID = "T0" + Integer.toString(numberT);
                if(numberT == 100000){
                    newTransactionID = newTransactionID.replaceAll("T0", "");
                    newTransactionID = "T" + newTransactionID;
                }
            }else if(numChar == 3){
                transactionID = transactionID.replaceAll("T00", "");
                numberT = Integer.parseInt(transactionID) + 1;
                newTransactionID = "T00" + Integer.toString(numberT);
                if(numberT == 10000){
                    newTransactionID = newTransactionID.replaceAll("T00", "");
                    newTransactionID = "T0" + newTransactionID;
                }
            }else if(numChar == 4){
                transactionID = transactionID.replaceAll("T000", "");
                numberT = Integer.parseInt(transactionID) + 1;
                newTransactionID = "T000" + Integer.toString(numberT);
                if(numberT == 1000){
                    newTransactionID = newTransactionID.replaceAll("T000", "");
                    newTransactionID = "T00" + newTransactionID;
                }
            }else if(numChar == 5){
                transactionID = transactionID.replaceAll("T0000", "");
                numberT = Integer.parseInt(transactionID) + 1;
                newTransactionID = "T0000" + Integer.toString(numberT);
                if(numberT == 100){
                    newTransactionID = newTransactionID.replaceAll("T0000", "");
                    newTransactionID = "T000" + newTransactionID;
                }
            }else if(numChar == 6){
                transactionID = transactionID.replaceAll("T00000", "");
                numberT = Integer.parseInt(transactionID) + 1;
                newTransactionID = "T00000" + Integer.toString(numberT);
                if(numberT == 10){
                    newTransactionID = newTransactionID.replaceAll("T00000", "");
                    newTransactionID = "T0000" + newTransactionID;
                }
            } // end of process generate
        }else{
            newTransactionID = "T000001";
        }

    } // end of auto-generate

    public void saveTransactionRecord(String ID, String newID, String location, String date, double amountPaid){

    }
}

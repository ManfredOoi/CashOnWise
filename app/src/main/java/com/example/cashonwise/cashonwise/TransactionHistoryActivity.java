package com.example.cashonwise.cashonwise;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.RequestQueue;

public class TransactionHistoryActivity extends AppCompatActivity {

    public static final String TAG = "com.example.cashonwise.cashonwise";

    private ProgressDialog pDialog;
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/transactionDetail.php";
    private RequestQueue queue;

    private Spinner spinnerTransaction;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        spinnerTransaction = (Spinner)findViewById(R.id.spinnerTransaction);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.TransactionType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransaction.setAdapter(adapter);

        userId = getIntent().getStringExtra("passID");
    }
}

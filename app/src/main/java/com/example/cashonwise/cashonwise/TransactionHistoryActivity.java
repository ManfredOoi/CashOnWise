package com.example.cashonwise.cashonwise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class TransactionHistoryActivity extends AppCompatActivity {

    Spinner spinnerTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        spinnerTransaction = (Spinner)findViewById(R.id.spinnerTransaction);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.TransactionType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransaction.setAdapter(adapter);
    }
}

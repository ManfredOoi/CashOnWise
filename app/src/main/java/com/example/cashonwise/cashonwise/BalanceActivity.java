package com.example.cashonwise.cashonwise;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BalanceActivity extends AppCompatActivity {
    public static final String TAG = "com.example.cashonwise.cashonwise";
    private PieChart pieChart;
    private float[] moneyVolume;
    private String[] labelVolume = {"Occupied", "Un-occupied"};
    private String currentBalance="0", spaceAvailable;
    private ProgressDialog pDialog;
    RequestQueue queue;
    String balance="0";
    String userid;

    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/account_detail.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        userid = getIntent().getStringExtra("passID");
        pDialog = new ProgressDialog(this);
        retriveBalance(getApplicationContext(), GET_URL);

    }

    public void addDataSet(){
        ArrayList<PieEntry> volumeEntrys = new ArrayList<>();
        ArrayList<String> labelEntrys = new ArrayList<>();
        float Balance, Available;
        Balance = (Float.parseFloat(currentBalance) / 10000.0f) * 100.0f;
        Available = (100.0f - Balance);

        moneyVolume = new float[]{Balance, Available};
        for(int i = 0; i < moneyVolume.length; i++){
            volumeEntrys.add(new PieEntry(moneyVolume[i], i));
        }

        for(int i = 0; i < labelVolume.length; i++){
            labelEntrys.add(labelVolume[i]);
        }

        // create the data set
        PieDataSet pieDataSet = new PieDataSet(volumeEntrys, "Account Balance Chart."); // at 1st
        pieDataSet.setValueTextSize(18f);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //Add Colour to data set
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.LTGRAY);
        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //Create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
    private void retriveBalance(Context context, String url) {
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
                            for (int i = 0; i < response.length(); i++) {

                                    JSONObject accountResponse = (JSONObject) response.get(i);
                                balance="";
                                    String id = accountResponse.getString("id");
                                    if(id.matches(userid)){
                                        balance = accountResponse.getString("balance");
                                        currentBalance = balance;
                                        spaceAvailable = "" + (10000.00 - Double.parseDouble(currentBalance));
                                        pieChart = (PieChart)findViewById(R.id.pieChartMoneyVolume);
                                        pieChart.setDescription("You Still Have RM" + spaceAvailable + " Available Space To Top Up."); // at 3rd
                                        pieChart.setDescriptionTextSize(25f);
                                        pieChart.setRotationEnabled(true);
                                        pieChart.setHoleRadius(35f);
                                        pieChart.setTransparentCircleAlpha(0);
                                        pieChart.setCenterText("Current Balance: RM" + currentBalance); // at 2nd middle
                                        pieChart.setCenterTextSize(15);
                                        pieChart.setDrawEntryLabels(true);
                                        addDataSet();
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
}

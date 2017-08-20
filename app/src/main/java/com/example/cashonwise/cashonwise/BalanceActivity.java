package com.example.cashonwise.cashonwise;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class BalanceActivity extends AppCompatActivity {

    private PieChart pieChart;
    private static String TAG = "MenuActivity";
    private float[] moneyVolume;
    private String[] labelVolume = {"Occupied", "Un-occupied"};
    private String currentBalance, spaceAvailable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        currentBalance = "" + 2679.40;
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
}

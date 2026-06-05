package com.example.myelectrobill;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.slider.Slider;

public class UpdateCalculateBillActivity extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    Button buttonUpdate, buttonBack;
    EditText unit;
    TextView textViewRebateValue;
    Spinner month, year;
    Slider rebate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_calculatebill);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DataHelper(this);

// Initialize EditText components
        unit = (EditText) findViewById(R.id.editTextElectricity);
        month = (Spinner) findViewById(R.id.spinnerMonth);
        year = (Spinner) findViewById(R.id.spinnerYear);
        rebate = (Slider) findViewById(R.id.sliderRebate);

// Retrieve data from database based on selected name
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM bill WHERE no= '" +
                getIntent().getStringExtra("no") + "'", null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            // Month = column 1
            String selectedMonth = cursor.getString(1);
            // Year = column 2
            String selectedYear = cursor.getString(2);
            // Unit = column 3
            unit.setText(cursor.getString(3));
            // Rebate = column 4
            int rebateValue = cursor.getInt(4);
            rebate.setValue(rebateValue);

            //Spinner Month
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                                this,
                                                R.array.spinnerMonth,
                                                android.R.layout.simple_spinner_item);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            month.setAdapter(adapter);

            //Set spinner selection
            for (int i = 0; i < month.getCount(); i++) {
                if (month.getItemAtPosition(i).toString().equals(selectedMonth)) {
                    month.setSelection(i);
                    break;
                }
            }

            //Spinner Year
            ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(
                                                    this,
                                                    R.array.spinnerYear,
                                                    android.R.layout.simple_spinner_item);

            yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            year.setAdapter(yearAdapter);

            for (int i = 0; i < year.getCount(); i++) {
                if (year.getItemAtPosition(i).toString().equals(selectedYear)) {
                    year.setSelection(i);
                    break;
                }
            }
        }


        textViewRebateValue = findViewById(R.id.textViewRebateValue);
        rebate.addOnChangeListener((slider, value, fromUser) -> {
            textViewRebateValue.setText("Rebate: " + (int)value + "%");
        });

        //rebate.setValue(rebateValue);
        //textViewRebateValue.setText("Rebate: " + rebateValue + "%");

// Initialize Buttons
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonBack = (Button) findViewById(R.id.buttonBack);

// Update Button Logic
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(month.getSelectedItemPosition() == 0){
                    Toast.makeText(
                            UpdateCalculateBillActivity.this,
                            "Please select a month",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                if(year.getSelectedItemPosition() == 0){
                    Toast.makeText(
                            UpdateCalculateBillActivity.this,
                            "Please select a year",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                if(unit.getText().toString().isEmpty()){
                    unit.setError("Please enter electricity unit");
                    return;
                }

                int unitUsed = Integer.parseInt(unit.getText().toString());

                if(unitUsed < 1 || unitUsed > 1000){
                    unit.setError("Unit must be between 1 and 1000");
                    return;
                }

                int rebatePercent = (int) rebate.getValue();
                double totalCharge = calculateCharges(unitUsed);
                double finalCost = totalCharge - (totalCharge * rebatePercent / 100.0);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE bill SET " +
                            "month='" + month.getSelectedItem().toString() + "', " +
                            "year='" + year.getSelectedItem().toString() + "', " +
                            "unit='" + unitUsed + "', " +
                            "rebate='" + rebatePercent + "', " +
                            "totalCharge='" + totalCharge + "', " +
                            "finalCost='" + finalCost + "' " +
                            "WHERE no='" +
                            getIntent().getStringExtra("no") + "'"
                );

                Toast.makeText(getApplicationContext(), "Data Successfully Updated", Toast.LENGTH_SHORT
                ).show();

                MainActivity.ma.RefreshList();
                finish();
            }
        });

// Back Button Logic
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    private double calculateCharges(int unitUsed) {

        double total = 0;

        if (unitUsed <= 200) {
            total = unitUsed * 0.218;
        } else if (unitUsed <= 300) {
            total = (200 * 0.218) + ((unitUsed - 200) * 0.334);
        } else if (unitUsed <= 600) {
            total = (200 * 0.218) + (100 * 0.334) + ((unitUsed - 300) * 0.516);
        } else {
            total = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((unitUsed - 600) * 0.546);
        }

        return total;
    }
}
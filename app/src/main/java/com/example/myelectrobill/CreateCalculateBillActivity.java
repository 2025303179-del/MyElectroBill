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

public class CreateCalculateBillActivity extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    EditText unit;
    Spinner month, year;
    Slider rebate;
    TextView totalCharge, finalCost, textViewRebateValue;
    Button btnCalculate, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_calculatebill);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new DataHelper(this);

        unit = findViewById(R.id.editTextElectricity);
        month = findViewById(R.id.spinnerMonth);
        year = findViewById(R.id.spinnerYear);
        rebate = findViewById(R.id.sliderRebate);
        totalCharge = findViewById(R.id.textViewTotalCharge);
        finalCost = findViewById(R.id.textViewFinalCost);
        textViewRebateValue = findViewById(R.id.textViewRebateValue);
        btnCalculate = findViewById(R.id.buttonCalculate);
        btnBack = findViewById(R.id.buttonBack);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerMonth);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinnerMonth,
                android.R.layout.simple_spinner_item
        );
        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.spinnerYear,
                        android.R.layout.simple_spinner_item);

        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        year.setAdapter(yearAdapter);

        rebate.addOnChangeListener((slider, value, fromUser) -> {
            textViewRebateValue.setText("Rebate: " + (int)value + "%");
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Month Validity
                if(month.getSelectedItemPosition() == 0){
                    Toast.makeText(
                            CreateCalculateBillActivity.this,
                            "Please select a month",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                //Year Validity
                if(year.getSelectedItemPosition() == 0){
                    Toast.makeText(
                            CreateCalculateBillActivity.this,
                            "Please select a year",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                //Unit Validity
                if(unit.getText().toString().isEmpty()){
                    unit.setError("Please enter electricity unit");
                    return;
                }

                int unitUsed = Integer.parseInt(unit.getText().toString());
                int rebatePercent = (int) rebate.getValue();
                double charge = calculateCharges(unitUsed);
                double finalBill = charge - (charge * rebatePercent / 100.0);

                if(unitUsed < 1 || unitUsed > 1000){
                    unit.setError("Unit must be between 1 and 1000");
                    return;
                }

                totalCharge.setText("Total Charge: RM " + String.format("%.2f", charge));
                finalCost.setText("Final Cost: RM " + String.format("%.2f", finalBill));

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("INSERT INTO bill(month, year, unit, rebate, totalCharge, finalCost) VALUES('" +
                        month.getSelectedItem().toString() + "','" +
                        year.getSelectedItem().toString() + "','" +
                        unitUsed + "','" +
                        rebatePercent + "','" +
                        charge + "','" +
                        finalBill + "')"
                );

                Toast.makeText(getApplicationContext(), "Data Successfully Added", Toast.LENGTH_SHORT
                ).show();
                MainActivity.ma.RefreshList();
            }
        });

        //Reset form after submission and calculation
        unit.setText("");
        rebate.setValue(0);
        month.setSelection(0);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private double calculateCharges(int unitUsed) {
        double total = 0;
        if(unitUsed <= 200){
            total = unitUsed * 0.218;
        }
        else if(unitUsed <= 300){
            total = (200 * 0.218) + ((unitUsed - 200) * 0.334);
        }
        else if(unitUsed <= 600){
            total = (200 * 0.218) + (100 * 0.334) + ((unitUsed - 300) * 0.516);
        }
        else{
            total = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((unitUsed - 600) * 0.546);
        }
        return total;
    }
}
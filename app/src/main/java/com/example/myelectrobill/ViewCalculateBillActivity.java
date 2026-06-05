package com.example.myelectrobill;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewCalculateBillActivity extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    TextView unit, month, year, rebate, totalCharge, finalCost;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_calculatebill);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DataHelper(this);

        month = findViewById(R.id.textViewMonth);
        unit = findViewById(R.id.textViewUnit);
        year = findViewById(R.id.textViewYear);
        rebate = findViewById(R.id.textViewRebate);
        totalCharge = findViewById(R.id.textViewTotalCharge);
        finalCost = findViewById(R.id.textViewFinalCost);
        btnBack = findViewById(R.id.buttonBack);

        // Retrieve data from database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM bill WHERE no = '" +
                        getIntent().getStringExtra("no") + "'", null);

        if (cursor.moveToFirst()) {
            month.setText(cursor.getString(1));
            year.setText(cursor.getString(2));
            unit.setText(cursor.getString(3));
            rebate.setText(cursor.getString(4) + "%");
            totalCharge.setText("RM " + cursor.getString(5));
            finalCost.setText("RM " + cursor.getString(6));
        }

        cursor.close();

        // Back button action
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
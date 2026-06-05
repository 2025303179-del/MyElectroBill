package com.example.myelectrobill;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.myelectrobill.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    String[] register;
    String[] id;
    ListView ListView01;
    Menu menu;
    //access Database
    protected Cursor cursor;
    //use polymorphism to access DataHelper
    DataHelper dbcenter;
    //to access class CreateBio , UpdateBio , and ViewBio
    public static MainActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        //FAB
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateCalculateBillActivity.class);
            startActivity(intent);
        });

        ma = this;
        dbcenter = new DataHelper(this);
        RefreshList();

        //Bottom Navigation Menu
        BottomNavigationView nav = findViewById(R.id.bottomNavigation);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_home){
                return true;
            }
            if(item.getItemId() == R.id.nav_about){
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    public void RefreshList() {
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM bill", null);
        register = new String[cursor.getCount()];
        id = new String[cursor.getCount()];
        cursor.moveToFirst();

        ListView01 = (ListView) findViewById(R.id.listView1);
        ListView01.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, register));
        ListView01.setSelected(true);

        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);

            // Store primary key
            id[cc] = cursor.getString(0);

            // Display Month and Cost in ListView
            register[cc] = cursor.getString(1) + " " +
                            cursor.getString(2) + " - RM " +
                            String.format("%.2f", cursor.getDouble(6));

            //create alert dialog menu here
            ListView01.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                    final String selectedId = id[arg2]; //.getItemAtPosition(arg2).toString();
                    final CharSequence[] dialogitem = {"View Calculate Bill", "Update Calculate Bill", "Delete Calculate Bill"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Selection");
                    builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0:
                                    Intent i = new Intent(getApplicationContext(), ViewCalculateBillActivity.class);
                                    i.putExtra("no", selectedId);
                                    startActivity(i);
                                    break;
                                case 1:
                                    Intent in = new Intent(getApplicationContext(), UpdateCalculateBillActivity.class);
                                    in.putExtra("no", selectedId);
                                    startActivity(in);
                                    break;
                                case 2:
                                    SQLiteDatabase db = dbcenter.getWritableDatabase();
                                    db.execSQL("delete from bill where no = '" + selectedId + "'");
                                    Toast.makeText(getApplicationContext(), "Data Successfully Removed", Toast.LENGTH_SHORT).show();
                                    RefreshList();
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                }
            });
            ((ArrayAdapter) ListView01.getAdapter()).notifyDataSetInvalidated();
        }

    }
}
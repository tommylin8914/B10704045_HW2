package com.example.b10704045_hw2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.b10704045_hw2.data.WaitlistContract;
import com.example.b10704045_hw2.data.WaitlistDbHelper;

public class AddActivity extends AppCompatActivity {


    private SQLiteDatabase mDb;
    private EditText mNewGuestNameEditText;
    private EditText mNewPartySizeEditText;
    static String name;
    static String number;
    static int reqCode = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        Button OKBtn = findViewById(R.id.button);
        OKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewGuestNameEditText = findViewById(R.id.ET1);
                mNewPartySizeEditText = findViewById(R.id.ET2);
                Intent intent = new Intent(AddActivity.this,MainActivity.class);
                intent.putExtra("name",mNewGuestNameEditText.getText().toString());
                intent.putExtra("number",mNewPartySizeEditText.getText().toString());
                startActivityForResult(intent,reqCode);
        }
        });

        Button CancelBtn = findViewById(R.id.button2);
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==999){
            if(resultCode == AppCompatActivity.RESULT_OK){
                Log.d("ACTOR","成功");
            }
        }
    }
}

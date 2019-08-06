package com.example.checkdemo;

import android.Manifest;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Context context;

    private View.OnClickListener btn_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckIdentity process = new CheckIdentity(txt, MainActivity.this);
            process.execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitialComponent();

    }

    private void InitialComponent() {
        txt = findViewById(R.id.txt);
        lbl = findViewById(R.id.lbl);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(btn_click);
    }

    Button btn;
    EditText txt;
    TextView lbl;
}

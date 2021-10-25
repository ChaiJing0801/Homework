package com.swufestu.homework;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ErcListShow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erc_list_show);
        final Bundle bundle=getIntent().getExtras();    //接收Extras
        String Currency = bundle.getString("Currency");
        String ex_rate = bundle.getString("ex_rate");

        TextView title = (TextView) findViewById(R.id.currency);
        title.setText(Currency);

        EditText input = (EditText) findViewById(R.id.input);
        TextView result = (TextView) findViewById(R.id.convert_result);

        Button button=(Button)findViewById(R.id.convert);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rate = Float.parseFloat(ex_rate);
                float RMB = Float.parseFloat(input.getText().toString());
                float convert_result = rate * RMB;
                result.setText(input.getText().toString() + "RMB = " + convert_result + Currency);
            }
        });
    }
}
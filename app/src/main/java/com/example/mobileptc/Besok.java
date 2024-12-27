package com.example.mobileptc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Besok extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_besok);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        TextView txt, txt2, txt3, txt4, txt5, txt6, txt7;
        txt = findViewById(R.id.textView33);
        txt2 = findViewById(R.id.textView34);
        txt3 = findViewById(R.id.textView35);
        txt4 = findViewById(R.id.textView36);
        txt5 = findViewById(R.id.textView37);
        txt6 = findViewById(R.id.textView38);
        txt7 = findViewById(R.id.textView39);

        Intent intent = getIntent();
        float suhu, hum, appTemp, rain, CC, WS, WD;
        int WC;
        String nama, alamat, email, pass, suhuV, humV, appTempV, rainV, CCV, WSV, WDV;
        nama = intent.getStringExtra("nama");
        alamat = intent.getStringExtra("alamat");
        email = intent.getStringExtra("email");
        pass = intent.getStringExtra("pass");
        suhu = intent.getFloatExtra("suhu", 0);
        hum = intent.getFloatExtra("humidity", 0);
        appTemp = intent.getFloatExtra("apparentTemp", 0);
        rain = intent.getFloatExtra("rain", 0);
        CC = intent.getFloatExtra("CC", 0);
        WS = intent.getFloatExtra("WS", 0);
        WD = intent.getFloatExtra("WD", 0);
        WC = intent.getIntExtra("WC", 0);

        suhuV = String.format("%.1f", suhu);
        humV = String.format("%.1f", hum);
        appTempV = String.format("%.1f", appTemp);
        rainV = String.format("%.1f", rain);
        CCV = String.format("%.1f", CC);
        WSV = String.format("%.1f", WS);
        WDV = String.format("%.1f", WD);


        txt.setText(suhuV + "°C");
        txt2.setText(humV + "%");
        txt3.setText(CCV + "%");
        txt4.setText(appTempV + "°C");
        txt5.setText(rainV + "mm");
        txt6.setText(WSV + "km/h");
        txt7.setText(WDV + "°");
        kondisiCuaca(WC);

        ImageView back = findViewById(R.id.imageView14);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Besok.this, Home.class);
                intent.putExtra("nama", nama);
                intent.putExtra("alamat", alamat);
                intent.putExtra("email", email);
                intent.putExtra("pass", pass);
                startActivity(intent);
            }
        });
    }

    void kondisiCuaca(int kodeCuaca) {
        ImageView cuaca1 = findViewById(R.id.imageView15);
        if (kodeCuaca == 0) {
            cuaca1.setImageResource(R.drawable.sun);
        } else if (kodeCuaca <= 3) {
            cuaca1.setImageResource(R.drawable.cloudy);
        } else if (kodeCuaca == 45 || kodeCuaca == 48) {
            cuaca1.setImageResource(R.drawable.fog);
        } else if (kodeCuaca >= 51 && kodeCuaca <= 55) {
            cuaca1.setImageResource(R.drawable.drizzle);
        } else if (kodeCuaca >= 61 && kodeCuaca <= 65) {
            cuaca1.setImageResource(R.drawable.rainyday);
        } else if (kodeCuaca >= 80 && kodeCuaca <= 83) {
            cuaca1.setImageResource(R.drawable.shower);
        } else if (kodeCuaca >= 95 && kodeCuaca <= 99) {
            cuaca1.setImageResource(R.drawable.thunderstorm);
        }
    }
}
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

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView logout = findViewById(R.id.imageViewLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, MainActivity.class));
                finish();
            }
        });

        TextView txtNama, txtAlamat, txtEmail, txtPass;
        txtNama = findViewById(R.id.textView20);
        txtAlamat = findViewById(R.id.textView21);
        txtEmail = findViewById(R.id.textView24);
        txtPass = findViewById(R.id.textView26);

        String nama, email, alamat, pass;

        Intent intent = getIntent();
        nama = intent.getStringExtra("nama");
        email = intent.getStringExtra("email");
        alamat = intent.getStringExtra("alamat");
        pass = intent.getStringExtra("pass");
        txtNama.setText(nama);
        txtEmail.setText(email);
        txtAlamat.setText(alamat);
        txtPass.setText(pass);

        ImageView back = findViewById(R.id.imageView9);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Profile.this, Home.class);
                intent1.putExtra("nama", nama);
                intent1.putExtra("alamat", alamat);
                intent1.putExtra("email", email);
                intent1.putExtra("pass", pass);
                startActivity(intent1);
            }
        });

        ImageView edit = findViewById(R.id.imageView10);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Profile.this, EditProfile.class);
                intent1.putExtra("nama", nama);
                intent1.putExtra("alamat", alamat);
                intent1.putExtra("email", email);
                intent1.putExtra("pass", pass);
                startActivity(intent1);
            }
        });

    }
}
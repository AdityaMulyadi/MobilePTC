package com.example.mobileptc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    EditText edtEmail, edtPass, edtKonfPass, edtNama, edtAlamat;
    FirebaseDatabase db;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtEmail = findViewById(R.id.plainEmail);
        edtPass = findViewById(R.id.plainPass);
        edtKonfPass = findViewById(R.id.plainKonfPass);
        edtNama = findViewById(R.id.namaUser);
        edtAlamat = findViewById(R.id.alamatUser);
        Button btn1 = findViewById(R.id.button);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, pass, konfPass, nama, alamat;
                email = String.valueOf(edtEmail.getText());
                pass = String.valueOf(edtPass.getText());
                konfPass = String.valueOf(edtKonfPass.getText());
                nama = String.valueOf(edtNama.getText());
                alamat = String.valueOf(edtAlamat.getText());

                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Email kosong!");
                    edtEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    edtPass.setError("Password kosong!");
                    edtPass.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(konfPass)) {
                    edtKonfPass.setError("Password kosong!");
                    edtKonfPass.requestFocus();
                    return;
                }

                if (!pass.equals(konfPass)){
                    edtKonfPass.setError("Password berbeda!");
                    edtKonfPass.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(nama)) {
                    edtNama.setError("Nama kosong!");
                    edtNama.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(alamat)) {
                    edtAlamat.setError("Alamat kosong!");
                    edtAlamat.requestFocus();
                    return;
                }


                db = FirebaseDatabase.getInstance();
                myRef = db.getReference("users");

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount();
                        String jumlah = Long.toString(count);

                        HelperClass helperClass = new HelperClass(jumlah, nama, alamat, email, pass, konfPass);
                        myRef.child(MethodSupport.potongEmail(email)).setValue(helperClass);

                        Toast.makeText(Register.this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(Register.this, Login.class));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });






            }
        });

        TextView link1 = findViewById(R.id.textView18);
        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

    }

}
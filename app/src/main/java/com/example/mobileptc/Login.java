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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText edtPlainEmail, edtPlainPass;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtPlainEmail = findViewById(R.id.plainEmail);
        edtPlainPass = findViewById(R.id.plainPass);
        Button btn1 = findViewById(R.id.button);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, pass;

                email = String.valueOf(edtPlainEmail.getText());
                pass = String.valueOf(edtPlainPass.getText());

                if (TextUtils.isEmpty(email)) {
                    edtPlainEmail.setError("Email kosong!");
                    edtPlainEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    edtPlainPass.setError("Password kosong!");
                    edtPlainPass.requestFocus();
                    return;
                }


                checkUser();

            }
        });

        TextView link1 = findViewById(R.id.textView18);
        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });



    }

    private void checkUser() {
        String email, pass;
        email = String.valueOf(edtPlainEmail.getText());
        pass = String.valueOf(edtPlainPass.getText());

        ref = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = ref.orderByChild("email").equalTo(email);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    edtPlainEmail.setError(null);
                    String passfromDb = snapshot.child(MethodSupport.potongEmail(email)).child("pass").getValue(String.class);
                    if (passfromDb.equals(pass)) {
                        edtPlainEmail.setError(null);
                        Toast.makeText(Login.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                        String nama, alamat;
                        nama = snapshot.child(MethodSupport.potongEmail(email)).child("nama").getValue(String.class);
                        alamat = snapshot.child(MethodSupport.potongEmail(email)).child("alamat").getValue(String.class);
                        Intent intent = new Intent(Login.this, Home.class);
                        intent.putExtra("nama", nama);
                        intent.putExtra("alamat", alamat);
                        intent.putExtra("email", email);
                        intent.putExtra("pass", passfromDb);
                        startActivity(intent);
                    } else {
                        edtPlainPass.setError("Kata sandi salah!");
                        edtPlainPass.requestFocus();
                    }
                } else {
                    edtPlainEmail.setError("Email salah");
                    edtPlainEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
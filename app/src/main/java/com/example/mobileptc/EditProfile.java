package com.example.mobileptc;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        EditText edtNama, edtAlamat, edtPass;
        String nama, alamat, pass, email;
        edtNama = findViewById(R.id.editTextText);
        edtAlamat = findViewById(R.id.editTextText2);
        edtPass = findViewById(R.id.editTextText3);

        nama = intent.getStringExtra("nama");
        alamat = intent.getStringExtra("alamat");
        pass = intent.getStringExtra("pass");
        email = intent.getStringExtra("email");
        edtNama.setText(nama);
        edtAlamat.setText(alamat);
        edtPass.setText(pass);

        CheckBox cb = findViewById(R.id.checkBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edtPass.setTransformationMethod(null);
                } else {
                    edtPass.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        ImageView close = findViewById(R.id.imageView12);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(EditProfile.this, Profile.class);
                intent1.putExtra("nama", nama);
                intent1.putExtra("alamat", alamat);
                intent1.putExtra("pass", pass);
                intent1.putExtra("email", email);
                startActivity(intent1);
            }
        });

        Button btn1 = findViewById(R.id.button4);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = FirebaseDatabase.getInstance();
                ref = db.getReference("users");
                String valNama, valAlamat, valPass;
                valNama = String.valueOf(edtNama.getText());
                valAlamat = String.valueOf(edtAlamat.getText());
                valPass = String.valueOf(edtPass.getText());

                ref.child(MethodSupport.potongEmail(email)).child("nama").setValue(valNama);
                ref.child(MethodSupport.potongEmail(email)).child("alamat").setValue(valAlamat);
                ref.child(MethodSupport.potongEmail(email)).child("pass").setValue(valPass);
                ref.child(MethodSupport.potongEmail(email)).child("konfPass").setValue(valPass);

                Intent intent1 = new Intent(EditProfile.this, Profile.class);
                intent1.putExtra("nama", valNama);
                intent1.putExtra("alamat", valAlamat);
                intent1.putExtra("pass", valPass);
                intent1.putExtra("email", email);
                startActivity(intent1);

            }
        });
    }
}
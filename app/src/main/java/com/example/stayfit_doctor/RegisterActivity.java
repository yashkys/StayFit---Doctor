package com.example.stayfit_doctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayfit_doctor.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    private EditText etEmail, etPassword, etUserName;
    private Button btnSignup;
    private TextView tvLogin;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        tvLogin = binding.textViewLogin;
        etEmail = binding.signupEmail;
        etPassword = binding.signupPassword;
        etUserName = binding.edtUsername;
        btnSignup = binding.signupButton;

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference().child("doctors");

        tvLogin.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        btnSignup.setOnClickListener(view1 -> {
            PerformAuth();
        });

    }

    private void PerformAuth() {
        String username = etUserName.getText().toString();
        if(!isUserNameExists(username)) {
            String email = etEmail.getText().toString();
            String pass = etPassword.getText().toString();
            String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=. *[A-Z])(?=. *[ !@#&( )-[{}] : ; ', ?/ * ~$^+=<>]). {8,20}$";
            if (!email.matches(EMAIL_PATTERN)) {
                etEmail.setError("Enter correct email");
            }
            else {
                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Welcome User", Toast.LENGTH_SHORT).show();
                        mUser = mAuth.getCurrentUser();
                        assert mUser != null;
                        // Store the user email in the Firebase Realtime Database
                        userRef.child(username).child("email").setValue(email);
//                        userRef.child(username).child("uid").setValue(mUser.getUid());
                        sendToNextActivity();
                    } else {
                        Toast.makeText(this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else{
            etUserName.setError("Username already taken");
            //Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean  isUserNameExists( String userName) {
        final boolean[] exists = { false };
        Query query = userRef.orderByChild("userName").equalTo(userName).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    exists[0] = true;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegisterActivity.this, "An Error occurred, please try later", Toast.LENGTH_SHORT).show();
            }
        });
        return exists[0];
    }

    private void sendToNextActivity() {
        Intent intent = new Intent(this, AddDetailsActivity.class);
        intent.putExtra("username", etUserName.getText().toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
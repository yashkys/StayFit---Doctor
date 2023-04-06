package com.example.stayfit_doctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayfit_doctor.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private EditText etEmail, etPassword;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        TextView tvSignup = binding.textViewSignup;
        etEmail = binding.editTextEmail;
        etPassword = binding.editTextPassword;
        Button btnLogin = binding.buttonLogin;

        tvSignup.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(view1 -> {
            PerformLogin();
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToNextActivity();
        }
    }

    private void PerformLogin() {
        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();
        String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=. *[A-Z])(?=. *[ !@#&( )-[{}] : ; ', ?/ * ~$^+=<>]). {8,20}$";
        if(!email.matches(EMAIL_PATTERN)){
            etEmail.setError("Enter correct email");
        }
        else if(pass.length()<8 && !pass.matches(PASSWORD_PATTERN)){
            etPassword.setError("Password must contain at least : \n1. 8 characters \n2. One Uppercase letter \n3. One Lowercase letter \n4. A number \n5. One special character(@,$,_,etc)");
        }else{
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show();
                    sendToNextActivity();
                } else {
                    Toast.makeText(this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendToNextActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
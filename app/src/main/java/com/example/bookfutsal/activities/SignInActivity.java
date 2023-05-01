package com.example.bookfutsal.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bookfutsal.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends DrawerBaseActivity {
    ActivitySignInBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("SIGN IN");

        mAuth = FirebaseAuth.getInstance();
    }

    public void singInButtonClicked(View v){
        String email = binding.editViewSinginEmail.getText().toString().trim();
        String password = binding.editViewSinginPassword.getText().toString().trim();

        // verification
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.editViewSinginEmail.setError("Please Enter Valid Email");
            binding.editViewSinginEmail.requestFocus();
        }

        if (binding.editViewSinginPassword.length() < 6){
            binding.editViewSinginPassword.setError("please enter password containing at least 6 characters");
            binding.editViewSinginPassword.requestFocus();
        }

        binding.progressBarSignIn.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    showToast("SignIn successfully !");
                    // sa permet de cacher le bouton connexion
                    connected();
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                }else{
                    showToast("SignIn Failed !");
                }
                binding.progressBarSignIn.setVisibility(View.GONE);
            }
        });
    }

    public void signUptxtClicked(View v){
        startActivity(new Intent(this, SignUpActivity.class));
        overridePendingTransition(0, 0);
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

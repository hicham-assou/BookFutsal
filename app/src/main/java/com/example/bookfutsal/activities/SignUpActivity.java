package com.example.bookfutsal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookfutsal.R;
import com.example.bookfutsal.databinding.ActivityMainBinding;
import com.example.bookfutsal.databinding.ActivitySignUpBinding;
import com.example.bookfutsal.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends DrawerBaseActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore dbFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("SIGN UP");

        mAuth = FirebaseAuth.getInstance();
        dbFirestore = FirebaseFirestore.getInstance();
    }

    public void signupButtonClicked(View v){
        String username = binding.editViewUsername.getText().toString().trim();
        String password = binding.editViewPassword.getText().toString().trim();
        String email = binding.editViewEmail.getText().toString().trim();

        // verifications
        if (username.isEmpty()){
            binding.editViewUsername.setError("please enter username");
            binding.editViewUsername.requestFocus();
        }
        if (password.isEmpty() || password.length() < 6){
            binding.editViewPassword.setError("please enter password containing at least 6 characters");
            binding.editViewPassword.requestFocus();
        }
        if (email.isEmpty()){
            binding.editViewEmail.setError("please enter valid email");
            binding.editViewEmail.requestFocus();
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //enregister l'user dans Firestore
                            User user = new User(username, password, email);
                            dbFirestore.collection("users")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showToast("successfully !");
                                                binding.progressBar.setVisibility(View.GONE);
                                                // rediriger vers la page de connexion
                                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                                overridePendingTransition(0, 0);
                                            } else {
                                                showToast("Failed !");
                                                binding.progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else {
                            showToast("Failed !");
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });


    }

    public void signInButtonClicked(View v){
        startActivity(new Intent(this, SignInActivity.class));
        overridePendingTransition(0, 0);
    }
    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
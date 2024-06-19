package com.example.weighttrackingapp2.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.weighttrackingapp2.databinding.ActivitySignupBinding;
import com.example.weighttrackingapp2.model.DatabaseHelper;
import com.example.weighttrackingapp2.viewmodel.SignupViewModel;
import com.example.weighttrackingapp2.viewmodel.SignupViewModelFactory;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    DatabaseHelper databaseHelper;
    SignupViewModel signupViewModel; // Declare SignupViewModel variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize binding and set content view
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Create the SignupViewModelFactory
        SignupViewModelFactory factory = new SignupViewModelFactory(databaseHelper);

        // Initialize SignupViewModel using the factory
        signupViewModel = new ViewModelProvider(this, factory).get(SignupViewModel.class);

        // Handle signup button click
        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.signupEmail.getText().toString();
                String password = binding.signupPassword.getText().toString();
                String confirmPassword = binding.signupConfirm.getText().toString();

                // Call signUpUser method from the SignupViewModel
                boolean signUpSuccess = signupViewModel.signUpUser(email, password, confirmPassword);

                // Handle signup success or failure
                if (signUpSuccess) {
                    Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignupActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle login redirect text click
        binding.loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
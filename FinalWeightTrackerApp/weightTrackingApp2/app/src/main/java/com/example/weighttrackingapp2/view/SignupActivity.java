package com.example.weighttrackingapp2.view;

// Import statements
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // Import for TextUtils
import android.util.Patterns; // Import for Patterns
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

                // Check if the email is valid
                if (!isValidEmail(email)) {
                    Toast.makeText(SignupActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return; // Exit the method early if the email is invalid
                }

                // Check if the password meets security requirements
                if (!isValidPassword(password)) {
                    Toast.makeText(SignupActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    return; // Exit the method early if the password is not secure
                }

                // Call signUpUser method from the SignupViewModel
                int signUpResult = signupViewModel.signUpUser(email, password, confirmPassword);

                // Handle signup success or failure
                switch (signUpResult) {
                    case 5: // Signup successful
                        Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        break;
                    case 4: // User already exists
                        Toast.makeText(SignupActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                        break;
                    case 3: // Passwords don't match
                        Toast.makeText(SignupActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                        break;
                    case 2: // Invalid password
                        Toast.makeText(SignupActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        break;
                    case 1: // Invalid email
                        Toast.makeText(SignupActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                        break;
                    case 0: // All fields are mandatory
                        Toast.makeText(SignupActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SignupActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                        break;
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

    // Validate email format
    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Validate password format
    private boolean isValidPassword(String password) {
        // Password must be at least 8 characters long and contain at least one letter, one number, and one special character
        return password != null && password.length() >= 8 && password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$");
    }
}

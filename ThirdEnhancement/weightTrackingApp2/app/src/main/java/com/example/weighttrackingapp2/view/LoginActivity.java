package com.example.weighttrackingapp2.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighttrackingapp2.databinding.ActivityLoginBinding;
import com.example.weighttrackingapp2.model.DatabaseHelper;
import com.example.weighttrackingapp2.viewmodel.LoginViewModel;
import com.example.weighttrackingapp2.viewmodel.LoginViewModelFactory;

public class LoginActivity extends AppCompatActivity {

    static String loggedInEmail;
    ActivityLoginBinding binding;
    DatabaseHelper databaseHelper;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        // Use custom factory to create LoginViewModel
        LoginViewModelFactory factory = new LoginViewModelFactory(databaseHelper);
        loginViewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.loginEmail.getText().toString();
                String password = binding.loginPassword.getText().toString();

                if (email.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(email)) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                } else {
                    boolean checkCredentials = loginViewModel.loginUser(email, password);

                    if (checkCredentials) {
                        loggedInEmail = email; // Set loggedInEmail here
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Redirect to the appropriate activity based on user's data availability
                        Intent intent;
                        if (loginViewModel.checkUserGoalExistence(email)) {
                            intent = new Intent(getApplicationContext(), WeightActivity.class);
                        } else {
                            intent = new Intent(getApplicationContext(), GoalActivity.class);
                        }
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

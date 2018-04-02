package com.example.wendy.yenko;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register1 extends AppCompatActivity {

    AlertDialog.Builder builder;
    String newName, newLast, newUser, newEmail, newPassword, newConfirmPassword;
    private EditText firstName, lastName, userName, emailAddress, password, confirmPassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityregister1);

        firstName = (EditText) findViewById(R.id.regName);
        lastName = (EditText) findViewById(R.id.regLast);
        emailAddress = (EditText) findViewById(R.id.regEmail);
        userName = (EditText) findViewById(R.id.regUserName);
        password = (EditText) findViewById(R.id.regPassword);
        confirmPassword = (EditText) findViewById(R.id.regConfirm);

        Button regButtonN = (Button) findViewById(R.id.btnRegNext);
        builder = new AlertDialog.Builder(Register1.this);

        regButtonN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
                //Goes to validate first
            }
        });
    }

    public void next() {
        initialize();
        if (!Validate()) {
            Toast.makeText(this, "Cannot Proceed Data Missing Or Invalid Data Input", Toast.LENGTH_SHORT).show();
        } else {
            nextSuccess();
        }
    }

    public void nextSuccess() {
        SharedPreferences preferences = getSharedPreferences("MYP", MODE_PRIVATE);
        newName = firstName.getText().toString();
        newLast = lastName.getText().toString();

        String userReg = preferences.getString(newName + newLast + "data", newName + " " + newLast);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(newName + "data", newName);
        editor.putString(newLast + "data", newLast);
        editor.putString("display", userReg);
        editor.commit();

        Intent intent = new Intent(Register1.this, Register2.class);
        intent.putExtra("NAME", newName);
        intent.putExtra("LAST", newLast);
        intent.putExtra("EMAIL", newEmail);
        intent.putExtra("USER", newUser);
        intent.putExtra("PASSWORD", newPassword);
        startActivity(intent);
    }

    //validation process
    public boolean Validate() {
        boolean valid = true;
        if (newName.isEmpty() || newName.length() > 32) {
            firstName.setError("Please Enter Valid FirstName");
            valid = false;
        }

        if (newLast.isEmpty() || newLast.length() > 32) {
            lastName.setError("Please Enter Valid LastName");
            valid = false;
        }

        if (newUser.isEmpty() || newUser.length() > 32) {
            userName.setError("Please Enter A Unique User Name");
            valid = false;
        }

        if (newEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            emailAddress.setError("Please Enter Valid Email Address");
            valid = false;
        }

        if (newPassword.isEmpty()) {
            password.setError("Please Enter A Password");
            valid = false;
        }

        if (!(newPassword.equals(newConfirmPassword))) {
            builder.setTitle("Something Went Wrong...");
            builder.setMessage("Your passwords are not matching");
            displayAlert("input_error");
            valid = false;
        }

        if (newConfirmPassword.isEmpty()) {
            confirmPassword.setError("Please Enter A Confirm Password");
            valid = false;
        }
        return valid;
    }

    public void initialize() {
        //*********Passing data to new variables************
        newName = firstName.getText().toString().trim();
        newLast = lastName.getText().toString().trim();
        newUser = userName.getText().toString().trim();
        newEmail = emailAddress.getText().toString().trim();
        newPassword = password.getText().toString().trim();
        newConfirmPassword = confirmPassword.getText().toString().trim();
    }

    public void displayAlert(final String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (code.equals("input_error")) {
                    password.setText("");
                    confirmPassword.setText("");
                } else if (code.equals("reg_success")) {
                    Intent intent = new Intent(Register1.this, Login.class);
                    startActivity(intent);
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

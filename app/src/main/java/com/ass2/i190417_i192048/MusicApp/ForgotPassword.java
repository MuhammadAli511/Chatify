package com.ass2.i190417_i192048.MusicApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ass2.i190417_i192048.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPassword extends AppCompatActivity {
    EditText email, password, password1, password2;
    Button updatePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
        updatePass = findViewById(R.id.updatePass);

        updatePass.setOnClickListener(v -> {
            String pass1 = password1.getText().toString();
            String pass2 = password2.getText().toString();
            String emailStr = email.getText().toString();
            String pass = password.getText().toString();
            Log.d("Test", "onCreate: " + pass1 + " " + pass2 + " " + emailStr + " " + pass);


                AuthCredential credential = EmailAuthProvider.getCredential(emailStr, pass);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.reauthenticate(credential)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ForgotPassword.this, "Incorrect Email/Password", Toast.LENGTH_SHORT).show();
                            }
                        });
                if(pass1.equals(pass2)){

                    user.updatePassword(pass1)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ForgotPassword.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(ForgotPassword.this, Signin.class);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ForgotPassword.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else{
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }

        });


    }
}
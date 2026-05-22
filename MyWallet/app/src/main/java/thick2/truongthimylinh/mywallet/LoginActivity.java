package thick2.truongthimylinh.mywallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;

    TextView txtRegister, txtForgotPassword;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogin = findViewById(R.id.btnLogin);

        txtRegister = findViewById(R.id.txtRegister);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        mAuth = FirebaseAuth.getInstance();

        // LOGIN
        btnLogin.setOnClickListener(v -> {

            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email)){
                edtEmail.setError("Enter email");
                return;
            }

            if(TextUtils.isEmpty(password)){
                edtPassword.setError("Enter password");
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if(task.isSuccessful()){

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Chào mừng bạn đến với MyWallet",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    LoginActivity.this,
                                    MainActivity.class
                            );

                            startActivity(intent);
                            finish();

                        }else{

                            String error = task.getException().getMessage();

                            Toast.makeText(
                                    LoginActivity.this,
                                    error,
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    });

        });

        // REGISTER
        txtRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);

        });

        // FORGOT PASSWORD
        txtForgotPassword.setOnClickListener(v -> {

            String email = edtEmail.getText().toString().trim();

            if(email.isEmpty()){
                edtEmail.setError("Enter email");
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {

                        if(task.isSuccessful()){

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Reset email sent",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }else{

                            String error = task.getException().getMessage();

                            Toast.makeText(
                                    LoginActivity.this,
                                    error,
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    });

        });

    }
}
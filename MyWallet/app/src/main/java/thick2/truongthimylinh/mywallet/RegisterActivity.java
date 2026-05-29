package thick2.truongthimylinh.mywallet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnRegister;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> {

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

            if(password.length() < 6){
                edtPassword.setError("Password >= 6 characters");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if(task.isSuccessful()){

                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Đăng ký tài khoản thành công",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();

                        }else{

                            String error = task.getException().getMessage();

                            Toast.makeText(
                                    RegisterActivity.this,
                                    error,
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    });
        });
    }
}
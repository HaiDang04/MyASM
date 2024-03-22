package com.example.myasm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DangKiActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText edt_tenDangKi, edtPassDK, edtEndPassDK;
    Button btnDangKy;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ki);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        edt_tenDangKi =findViewById(R.id.edt_Tendangki);
        edtPassDK = findViewById(R.id.edtPassDK);
        edtEndPassDK = findViewById(R.id.edtEndPassDK);
        btnDangKy  =findViewById(R.id.btnDangKy);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edt_tenDangKi.getText().toString();
                String password = edtPassDK.getText().toString();
                String confirmPassword = edtEndPassDK.getText().toString();

                if (!password.equals(confirmPassword)) {
                    // Hiển thị thông báo khi mật khẩu và xác nhận mật khẩu không khớp
                    Toast.makeText(DangKiActivity.this, "Mật khẩu và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(DangKiActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Đăng kí thành công
                                    Log.d("DangKy", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(DangKiActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                    editor.apply();

                                    // Chuyển sang màn hình đăng nhập
                                    Intent intent = new Intent(DangKiActivity.this, DangNhapActivity.class);
                                    startActivity(intent);
                                } else {
                                    // Đăng kí thất bại
                                    Log.w("DangKy", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(DangKiActivity.this, "Đăng kí thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
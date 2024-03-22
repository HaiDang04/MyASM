package com.example.myasm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class DangNhapActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText edt_tenDangNhap, edtPassDN;
    TextView txtSign, txtQuenMK;
    Button btnDangNhap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        edt_tenDangNhap =findViewById(R.id.edt_tenDangNhap);
        edtPassDN = findViewById(R.id.edtPassDN);
        txtSign = findViewById(R.id.txtSign);
        txtQuenMK = findViewById(R.id.txtQuenMK);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

// Sử dụng savedEmail và savedPassword để điền vào ô EditText
        edt_tenDangNhap.setText(savedEmail);
        edtPassDN.setText(savedPassword);

        btnDangNhap =  findViewById(R.id.btnDangNhap);
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy giá trị từ EditText
                String tenDangNhap = edt_tenDangNhap.getText().toString();
                String matKhau = edtPassDN.getText().toString();

                // Thực hiện đăng nhập
                mAuth.signInWithEmailAndPassword(tenDangNhap, matKhau)
                        .addOnCompleteListener(DangNhapActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Đăng nhập thành công
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(DangNhapActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                                    // Thực hiện các hành động cần thiết sau khi đăng nhập thành công
                                    // Ví dụ: chuyển sang màn hình chính
                                    Intent intent = new Intent(DangNhapActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Đăng nhập thất bại
                                    Toast.makeText(DangNhapActivity.this, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        txtQuenMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenDangNhap = edt_tenDangNhap.getText().toString();
                if (!tenDangNhap.isEmpty()) {
                    mAuth.sendPasswordResetEmail(tenDangNhap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(DangNhapActivity.this, "Vui lòng kiểm tra hộp thư.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DangNhapActivity.this, "Lỗi gửi yêu cầu đặt lại mật khẩu.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(DangNhapActivity.this, "Vui lòng nhập địa chỉ email.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        txtSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DangNhapActivity.this, DangKiActivity.class);
                startActivity(intent);
            }
        });

    }
}
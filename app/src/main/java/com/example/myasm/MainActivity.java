package com.example.myasm;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rcvSanPham;
    private FloatingActionButton fltAdd;
    List<CarModel> listCarModel;
    CarAdapter carAdapter = new CarAdapter(listCarModel, MainActivity.this);
    // Khởi tạo đối tượng ApiService
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ
        rcvSanPham = findViewById(R.id.rcvSanPham);
        fltAdd = findViewById(R.id.fltAdd);

        // Tạo LinearLayoutManager cho RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rcvSanPham.setLayoutManager(layoutManager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIService.class);

        Call<List<CarModel>> call = apiService.getCars();

        call.enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                if (response.isSuccessful()) {
                    listCarModel = response.body();

                    carAdapter = new CarAdapter(listCarModel, MainActivity.this);

                    // Gán adapter cho RecyclerView
                    rcvSanPham.setAdapter(carAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });


        fltAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogThem();
            }
        });
    }
    private void openDialogThem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.item_add, null);
        builder.setView(view);
        Dialog dialog = builder.create();

        EditText edtTen = view.findViewById(R.id.edtTenOTO);
        EditText edtNamSX = view.findViewById(R.id.edtNamSX);
        EditText edtHang = view.findViewById(R.id.edtHang);
        EditText edtGia = view.findViewById(R.id.edtGia);
        Button btnThem = view.findViewById(R.id.btnADD);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ten = edtTen.getText().toString();
                String namSX = edtNamSX.getText().toString();
                String hang = edtHang.getText().toString();
                String gia = edtGia.getText().toString();

                int namSXInt = Integer.parseInt(namSX);
                double giaDouble = Double.parseDouble(gia);
                // Tạo đối tượng CarModel mới
                CarModel newCarModel = new CarModel(ten, namSXInt, hang, giaDouble);

                // Gửi yêu cầu thêm CarModel mới
                Call<CarModel> call = apiService.addCar(newCarModel);
                call.enqueue(new Callback<CarModel>() {
                    @Override
                    public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                        if (response.isSuccessful()) {
                            CarModel addedCarModel = response.body();

                            // Thêm xe mới vào danh sách
                            listCarModel.add(addedCarModel);

                            // Cập nhật hiển thị trong RecyclerView
                            carAdapter.notifyDataSetChanged();

                            Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CarModel> call, Throwable t) {
                        Log.e("Main", t.getMessage());
                        Toast.makeText(MainActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.show();
    }
}
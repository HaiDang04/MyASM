package com.example.myasm;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.common.net.MediaType;

import okhttp3.MediaType;
//import okhttp3.RequestBody;
//import okhttp3.MultipartBody;
import android.content.ContentResolver;
import android.net.Uri;
//import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rcvSanPham;
    private FloatingActionButton fltAdd;
    private EditText timkiem;
    private Button tangDan, giamDan, search;
    List<CarModel> listCarModel;
    CarAdapter carAdapter = new CarAdapter(listCarModel, MainActivity.this);
    // Khởi tạo đối tượng ApiService
    private APIService apiService;
    private Uri selectedImageUri;
    ImageView imgaddAnhXe;

    private File file;

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
        timkiem = findViewById(R.id.etSearch);
        tangDan = findViewById(R.id.btnSortAsc);
        giamDan = findViewById(R.id.btnSortDesc);
        search = findViewById(R.id.btn_Search);
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

        tangDan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sortByPriceAscending();
                sortByPrice(true);
            }
        });

        giamDan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sortByPriceDescending();
                sortByPrice(false);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = timkiem.getText().toString().trim();
                searchCars(keyword);
            }
        });

//        timkiem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    String keyword = timkiem.getText().toString().trim();
//                    searchCars(keyword);
//                    return true;
//                }
//                return false;
//            }
//        });
    }
    private void openDialogThem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.item_add, null);
        builder.setView(view);
        Dialog dialog = builder.create();

         imgaddAnhXe = view.findViewById(R.id.imgaddAnhXe);
        EditText edtTen = view.findViewById(R.id.edtTenOTO);
        EditText edtNamSX = view.findViewById(R.id.edtNamSX);
        EditText edtHang = view.findViewById(R.id.edtHang);
        EditText edtGia = view.findViewById(R.id.edtGia);
        Button btnThem = view.findViewById(R.id.btnADD);

//        imgaddAnhXe.setOnClickListener(v -> chooseImage.launch("image/*"));

        imgaddAnhXe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi phương thức để chọn ảnh
                chooseImage.launch("image/*");
            }
        });
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ten = edtTen.getText().toString();
                String namSX = edtNamSX.getText().toString();
                String hang = edtHang.getText().toString();
                String gia = edtGia.getText().toString();


                int namSXInt = Integer.parseInt(namSX);
                double giaDouble = Double.parseDouble(gia);

                // Đảm bảo tất cả thông tin đã được nhập đầy đủ trước khi tiếp tục
                if (ten.isEmpty() || namSX.isEmpty() || hang.isEmpty() || gia.isEmpty() || selectedImageUri == null) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin và chọn ảnh!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thực hiện upload thông tin xe và ảnh
                uploadCarWithImage(ten, namSX, hang, gia, selectedImageUri);

                dialog.dismiss(); // Đóng dialog sau khi đã bắt đầu quá trình upload

                // Tạo đối tượng CarModel mới
                CarModel newCarModel = new CarModel(ten, namSXInt, hang, giaDouble, "");

                // Gửi yêu cầu thêm CarModel mới
                Call<CarModel> call = apiService.addCar(newCarModel);
                call.enqueue(new Callback<CarModel>() {
                    @Override
                    public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                        if (response.isSuccessful()) {
                            CarModel addedCarModel = response.body();
                            uploadCarWithImage(ten, namSX, hang, gia, selectedImageUri);
                            // Thêm xe mới vào danh sách
                            listCarModel.add(addedCarModel);
                            // Cập nhật hiển thị trong RecyclerView
                            carAdapter.notifyDataSetChanged();

                            Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Thêmthất bại", Toast.LENGTH_SHORT).show();
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

    private void uploadCarWithImage(String ten, String namSX, String hang, String gia, Uri imageUri) {
        // Chuyển đổi thông tin xe thành RequestBody
        RequestBody tenBody = RequestBody.create(ten, okhttp3.MultipartBody.FORM);
        RequestBody namSXBody = RequestBody.create(namSX, okhttp3.MultipartBody.FORM);
        RequestBody hangBody = RequestBody.create(hang, okhttp3.MultipartBody.FORM);
        RequestBody giaBody = RequestBody.create(gia, okhttp3.MultipartBody.FORM);

        // Tạo RequestBody từ các giá trị chuỗi
//        RequestBody tenBody = RequestBody.create(MediaType.parse("text/plain"), ten);
//        RequestBody namSXBody = RequestBody.create(MediaType.parse("text/plain"), namSX);
//        RequestBody hangBody = RequestBody.create(MediaType.parse("text/plain"), hang);
//        RequestBody giaBody = RequestBody.create(MediaType.parse("text/plain"), gia);

        // Chuyển đổi Uri thành File và sau đó thành RequestBody
        try {
            File file = createFileFromUri(imageUri);
            RequestBody requestFile = RequestBody.create(file, MediaType.parse(getContentResolver().getType(imageUri)));
            MultipartBody.Part body = MultipartBody.Part.createFormData("anh", file.getName(), requestFile);

            // Gọi API
            Call<CarModel> call = apiService.addCarWithImage(tenBody, namSXBody, hangBody, giaBody, body);
            call.enqueue(new Callback<CarModel>() {
                @Override
                public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Thêm xe thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Thêm xe thất bại.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CarModel> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Lỗi khi kết nối với server.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Không thể tạo file từ URI", Toast.LENGTH_SHORT).show();
        }
    }


    private File createFileFromUri(Uri uri) throws IOException {
        // Tạo một file tạm thời trong bộ nhớ cache của ứng dụng
        File tempFile = File.createTempFile("image", ".png", getCacheDir());
//        File _file = new File(MainActivity.this.getCacheDir(), ten + ".png");
        tempFile.deleteOnExit();

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream out = new FileOutputStream(tempFile)) {

            // Copy dữ liệu từ InputStream sang FileOutputStream
            if (inputStream != null) {
                byte[] buffer = new byte[4 * 1024]; // hoặc kích thước buffer khác tùy bạn
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
            }
        }

        return tempFile;
    }



    // Phương thức để chọn ảnh từ thư viện
    private final ActivityResultLauncher<String> chooseImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    selectedImageUri = result;
                    try {
                        // Hiển thị ảnh đã chọn trong ImageView
                        Glide.with(this)
                                .asBitmap()
                                .load(selectedImageUri)
                                .centerCrop()
                                .into(new BitmapImageViewTarget(imgaddAnhXe) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        imgaddAnhXe.setImageDrawable(circularBitmapDrawable);
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    private void sortByPriceAscending() {
        // Gửi yêu cầu sắp xếp danh sách xe theo giá tăng dần
        Call<List<CarModel>> call = apiService.sortCars("price_asc");
        call.enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                if (response.isSuccessful()) {
                    List<CarModel> sortedList = response.body();
                    updateCarList(sortedList);
                }
            }

            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });
    }

    private void sortByPriceDescending() {
        // Gửi yêu cầu sắp xếp danh sách xe theo giá giảm dần
        Call<List<CarModel>> call = apiService.sortCars("price_desc");
        call.enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                if (response.isSuccessful()) {
                    List<CarModel> sortedList = response.body();
                    updateCarList(sortedList);
                }
            }

            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });
    }


    private void sortByPrice(boolean ascending) {
        String sortBy = ascending ? "asc" : "desc";
        apiService.sortCars(sortBy).enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listCarModel = response.body();
                    carAdapter.updateList(listCarModel);
                    carAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Không thể sắp xếp danh sách xe.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {
                Log.e("MainActivity", "Lỗi khi sắp xếp: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Lỗi khi sắp xếp danh sách xe.", Toast.LENGTH_SHORT).show();
            }
        });
    }


//    private void searchCars(String keyword) {
//        // Gửi yêu cầu tìm kiếm xe dựa trên từ khóa
//        Call<List<CarModel>> call = apiService.searchCarsByName(keyword);
//        call.enqueue(new Callback<List<CarModel>>() {
//            @Override
//            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
//                if (response.isSuccessful()) {
//                    List<CarModel> searchResults = response.body();
//                    updateCarList(searchResults);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<CarModel>> call, Throwable t) {
//                Log.e("Main", t.getMessage());
//            }
//        });
//    }

    private void searchCars(String keyword) {
        // Gửi yêu cầu tìm kiếm xe dựa trên từ khóa
        Call<List<CarModel>> call = apiService.searchCarsByName(keyword);
        call.enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                if (response.isSuccessful()) {
                    List<CarModel> searchResults = response.body();
                    updateCarList(searchResults);
                }
            }

            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });
    }

    private void updateCarList(List<CarModel> updatedList) {
        listCarModel.clear();
        listCarModel.addAll(updatedList);
        carAdapter.notifyDataSetChanged();
    }
}
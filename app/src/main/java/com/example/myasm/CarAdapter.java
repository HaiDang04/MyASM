package com.example.myasm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    List<CarModel> carModelList;
    Context context;
    private APIService apiService;
    private CarModel carModel;

    private ActivityResultLauncher<String> activityResultLauncher;

    public CarAdapter(List<CarModel> carModelList, Context context) {
        this.carModelList = carModelList;
        this.context = context;

    }
    @NonNull
    @Override
    public CarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view =inflater.inflate(R.layout.item_oto,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(carModelList.get(position).getTen());
        holder.tvNamSX.setText(String.valueOf(carModelList.get(position).getNamSX()));
        holder.tvHang.setText(carModelList.get(position).getHang());
        holder.tvGia.setText(String.valueOf(carModelList.get(position).getGia()));

        String imageUrl = carModelList.get(position).getHinhAnh();
        String newUrl = imageUrl.replace("localhost", "10.0.2.2");
        Glide.with(context)
                .load(newUrl)
                .thumbnail(Glide.with(context).load(R.drawable.anh))
                .into(holder.imgAvatar);


        holder.btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialogsua(position);
            }
        });

        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarModel car = carModelList.get(position);
                String carId = car.get_id();
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Cảnh báo"); // Set tiêu đề cho hộp thoại
                builder.setIcon(R.drawable.baseline_wasing); // Icon cho hộp thoại
                builder.setMessage("Bạn có muốn xóa không?"); // Chuỗi thông báo
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        APIService apiService = RetrofitClientInstance.getRetrofitInstance().create(APIService.class);
                        Call<Response<Void>> call = apiService.deleteCarById(carId);
                        call.enqueue(new Callback<Response<Void>>() {
                            @Override
                            public void onResponse(Call<Response<Void>> call, Response<Response<Void>> response) {
                                if (response.isSuccessful()) {
                                    carModelList.remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "xóa thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Response<Void>> call, Throwable t) {
                                Log.e("Delete Error", t.getMessage());
                                Toast.makeText(context, "Lỗi khi xóa dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                // Nút "Không"
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Không xóa", Toast.LENGTH_SHORT).show();
                    }
                });
                // Tạo và hiển thị hộp thoại
                AlertDialog dialog = builder.create(); // Tạo hộp thoại
                dialog.show(); // Hiển thị hộp thoại
            }
        });
    }

    @Override
    public int getItemCount() {
        return carModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvNamSX, tvHang, tvGia, btnXoa, btnSua;
        ImageView imgAvatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAnhXe);
            tvName = itemView.findViewById(R.id.txtTenOto);
            tvNamSX = itemView.findViewById(R.id.txtNamSX);
            tvHang = itemView.findViewById(R.id.txtHang);
            tvGia = itemView.findViewById(R.id.txtGia);
            btnXoa = itemView.findViewById(R.id.btnXoa);
            btnSua = itemView.findViewById(R.id.btnSua);
        }
    }

    // open dialog SUA
    public void opendialogsua( int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_sua, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

            EditText edtTen = view.findViewById(R.id.edtTenOTO_sua);
            EditText edtNamSX = view.findViewById(R.id.edtNamSX_sua);
            EditText edtHang = view.findViewById(R.id.edtHang_sua);
            EditText edtGia = view.findViewById(R.id.edtGia_sua);
            Button btnSuas = view.findViewById(R.id.btnupdate);
            ImageView anhXe = view.findViewById(R.id.imgUPdateAnhXe);


            edtTen.setText(carModelList.get(position).getTen());
            edtNamSX.setText(String.valueOf(carModelList.get(position).getNamSX()));
            edtHang.setText(carModelList.get(position).getHang());
            edtGia.setText(String.valueOf(carModelList.get(position).getGia()));
            String imageUrl = carModelList.get(position).getHinhAnh();
            String newUrl = imageUrl.replace("localhost", "10.0.2.2"); // Thay đổi nếu cần
            Glide.with(context)
                .load(newUrl)
                .into(anhXe);

            apiService = RetrofitClientInstance.getRetrofitInstance().create(APIService.class);

            // Xử lý sự kiện khi người dùng nhấn nút "Sửa"
            btnSuas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 String tenXe =   edtTen.getText().toString();
                 int namSX = Integer.parseInt(edtNamSX.getText().toString());
                 String hang =    edtHang.getText().toString();
                 Double gia =  Double.parseDouble(edtGia.getText().toString());

                    CarModel carWithoutImageRequest = new CarModel(tenXe, namSX, hang, gia, "");
                    Call<Response<CarModel>> call = apiService.updateCarById(carModelList.get(position).get_id(), carWithoutImageRequest);

                    call.enqueue(new Callback<Response<CarModel>>() {
                        @Override
                        public void onResponse(Call<Response<CarModel>> call, Response<Response<CarModel>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Thành công cập nhật", Toast.LENGTH_SHORT).show();
                                CarModel updatedCar = carModelList.get(position);
                                updatedCar.setTen(tenXe);
                                updatedCar.setGia(gia);
                                updatedCar.setHang(hang);
                                updatedCar.setNamSX(namSX);
                                notifyItemChanged(position);
                            } else {
                                Toast.makeText(context, "Cập Nhật thất bại", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<Response<CarModel>> call, Throwable t) {
                            Toast.makeText(context, "Cập Nhật thất bại", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                });
                }
            });

    }

}

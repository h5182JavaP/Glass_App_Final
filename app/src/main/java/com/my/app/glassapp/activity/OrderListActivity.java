package com.my.app.glassapp.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.my.app.glassapp.R;
import com.my.app.glassapp.adapter.AllGlassAdapter;
import com.my.app.glassapp.databinding.ActivityOrderListBinding;
import com.my.app.glassapp.viewmodel.OrderListViewModel;

import java.util.Collections;

public class OrderListActivity extends AppCompatActivity {

    private ActivityOrderListBinding binding;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST = 1888;
    private AllGlassAdapter allGlassAdapter;
    private OrderListViewModel orderListViewModel;
    private boolean isPermitted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityOrderListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        orderListViewModel = new ViewModelProvider(this).get(OrderListViewModel.class);

        allGlassAdapter= new AllGlassAdapter(this,this);
        binding.ivAddOrder.setOnClickListener(view -> {
            checkRunTimePermission();
        });

/*
        binding.cvAddSGU.setOnClickListener(view -> {
            Intent intent = new Intent(this, InquiryFormEditActivity.class);
            intent.putExtra("GlassType", "SGU");
            intent.putExtra("TablePosition", 0);
            intent.putExtra("AddTypeFlag", true);
            intent.putExtra("AddTypeFlag1", true);

            startActivity(intent);
            finish();
        });
        binding.cvAddDGU.setOnClickListener(view -> {
            Intent intent = new Intent(this, InquiryFormEditActivity.class);
            intent.putExtra("GlassType", "DGU");
            intent.putExtra("TablePosition", 0);
            intent.putExtra("AddTypeFlag", true);
            intent.putExtra("AddTypeFlag1", true);

            startActivity(intent);
            finish();
        });
        binding.cvAddLamination.setOnClickListener(view -> {
            Intent intent = new Intent(this, InquiryFormEditActivity.class);
            intent.putExtra("GlassType", "Lamination");
            intent.putExtra("TablePosition", 0);
            intent.putExtra("AddTypeFlag", true);
            intent.putExtra("AddTypeFlag1", true);

            startActivity(intent);
            finish();
        });
        binding.cvAddAnnealed.setOnClickListener(view -> {
            Intent intent = new Intent(this, InquiryFormEditActivity.class);
            intent.putExtra("GlassType", "Annealed");
            intent.putExtra("TablePosition", 0);
            intent.putExtra("AddTypeFlag", true);
            intent.putExtra("AddTypeFlag1", true);

            startActivity(intent);
            finish();
        });
        binding.cvAddLaminatedDGU.setOnClickListener(view -> {
            Intent intent = new Intent(this, InquiryFormEditActivity.class);
            intent.putExtra("GlassType", "LaminatedDGU");
            intent.putExtra("TablePosition", 0);
            intent.putExtra("AddTypeFlag", true);
            intent.putExtra("AddTypeFlag1", true);

            startActivity(intent);
            finish();
        });
*/
        binding.cvOrder.setOnClickListener(view -> {
            startActivity(new Intent(OrderListActivity.this, AddressActivity.class));
            finish();
        });

        binding.imgLogout.setOnClickListener(view -> {

            final Dialog dialog = new Dialog(OrderListActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_logout);

            TextView tvYes = (TextView) dialog.findViewById(R.id.tv_yes);
            TextView tvNo = (TextView) dialog.findViewById(R.id.tv_no);

            tvYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    SharedPreferences sharedPreferences = getSharedPreferences("LoginPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putBoolean("login", false);
                    myEdit.commit();
                    startActivity(new Intent(OrderListActivity.this, MainActivity.class));
                    finish();
                }
            });
            tvNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        binding.rvGlassItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvGlassItems.setAdapter(allGlassAdapter);
        orderListViewModel.getMediatorLiveData().observe(this, objects -> {
            allGlassAdapter.submitList(null);
            allGlassAdapter.submitList(objects);
        });
//        orderListViewModel.getSguLiveData().observe(this, sguTable -> {
//            allGlassAdapter.submitList(sguTable);
//        });
    }



    private void checkRunTimePermission() {
        String[] permissionArrays = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, PERMISSION_REQUEST_CODE);
        } else {
            startActivity(new Intent(this, InquiryFormActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
            }
        }

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                String permission = permissions[i];

                isPermitted = grantResults[i] == PackageManager.PERMISSION_GRANTED;

                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        //execute when 'never Ask Again' tick and permission dialog not show
                    } else {
                    }
                }
            }

            if (isPermitted) {
                startActivity(new Intent(this, InquiryFormActivity.class));
                finish();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.rvGlassItems.setAdapter(allGlassAdapter);
        orderListViewModel.getMediatorLiveData().observe(this, objects -> {
            allGlassAdapter.submitList(null);
            allGlassAdapter.submitList(objects);
        });
    }

}
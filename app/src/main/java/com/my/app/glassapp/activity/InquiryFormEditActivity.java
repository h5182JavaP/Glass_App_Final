package com.my.app.glassapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.my.app.glassapp.PathUtil;
import com.my.app.glassapp.utils.DataConverter;
import com.my.app.glassapp.R;
import com.my.app.glassapp.database.DBDao;
import com.my.app.glassapp.database.DBRoom;
import com.my.app.glassapp.databinding.ActivityInquiryFormEditBinding;
import com.my.app.glassapp.model.AnnealedTable;
import com.my.app.glassapp.model.DGUTable;
import com.my.app.glassapp.model.LaminatedDGUTable;
import com.my.app.glassapp.model.LaminationTable;
import com.my.app.glassapp.model.SGUTable;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InquiryFormEditActivity extends AppCompatActivity {

    public static final int SELECT_PICTURE = 101;

    ActivityInquiryFormEditBinding binding;
    String[] glassTypeCategories = {"SGU", "DGU", "Lamination", "Annealed", "LaminatedDGU"};
    String[] gapCategories = {"06", "08", "10", "12", "14", "15", "16", "18", "20", "22"};
    String[] pvbCategories = {"0.38", "0.16", "1.14", "1.52"};

    int glassTypePosition = 0;
    int gapPosition = 0;
    int pvbPosition = 0;
    int standardId = 0;
    String thicknessText;
    String materialDetailsText;
    String glassWidthText;
    String glassHeightText;
    String quantityText;
    String noteText;
    String glass1;
    String glass2;
    String glass3;
    //    String gap;
    private DBDao dao;
    String selectedStandard = "inch";
    private int sguTableDataPosition;
    private String glassType;
    private List<SGUTable> sgudata;
    private List<DGUTable> dgudata;
    private List<LaminationTable> laminationdata;
    private List<AnnealedTable> annealeddata;
    private List<LaminatedDGUTable> laminatedDgudata;
    public static Bitmap bmpImage = null;
    String imgepath;
    private boolean addTypeFlag;
    private boolean addTypeFlag1;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private boolean cameraFlag = false;
    private ArrayAdapter<String> dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityInquiryFormEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sguTableDataPosition = getIntent().getIntExtra("TablePosition", 1);
        glassType = getIntent().getStringExtra("GlassType");
        addTypeFlag = getIntent().getBooleanExtra("AddTypeFlag", false);
        addTypeFlag1 = getIntent().getBooleanExtra("AddTypeFlag1", false);

        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                standardId = i;
                if (standardId == R.id.radioInch) {
                    selectedStandard = "inch";
                } else if (standardId == R.id.radioMm) {
                    selectedStandard = "mm";
                }
            }
        });

        if (glassType.equals("SGU")) {
            binding.spGlassType.setEnabled(false);
            binding.spGlassType.setClickable(false);
            binding.tvGap.setVisibility(View.GONE);
            binding.cvGap.setVisibility(View.GONE);
            binding.tvGlass1.setVisibility(View.GONE);
            binding.tvGlass2.setVisibility(View.GONE);
            binding.cvGlass1.setVisibility(View.GONE);
            binding.cvGlass2.setVisibility(View.GONE);
            binding.cvThickness.setVisibility(View.VISIBLE);
            binding.cvMaterialDetails.setVisibility(View.VISIBLE);
            binding.tvThickness.setVisibility(View.VISIBLE);
            binding.tvMaterialDetails.setVisibility(View.VISIBLE);
            binding.cvPvb.setVisibility(View.GONE);
            binding.spPvb.setVisibility(View.GONE);
            binding.tvPvb.setVisibility(View.GONE);
        } else if (glassType.equals("DGU")) {
            binding.spGlassType.setEnabled(false);
            binding.spGlassType.setClickable(false);
            binding.tvGap.setVisibility(View.VISIBLE);
            binding.cvGap.setVisibility(View.VISIBLE);
            binding.tvGlass1.setVisibility(View.VISIBLE);
            binding.tvGlass2.setVisibility(View.VISIBLE);
            binding.cvGlass1.setVisibility(View.VISIBLE);
            binding.cvGlass2.setVisibility(View.VISIBLE);
            binding.cvThickness.setVisibility(View.GONE);
            binding.cvMaterialDetails.setVisibility(View.GONE);
            binding.tvThickness.setVisibility(View.GONE);
            binding.tvMaterialDetails.setVisibility(View.GONE);
            binding.cvPvb.setVisibility(View.GONE);
            binding.spPvb.setVisibility(View.GONE);
            binding.tvPvb.setVisibility(View.GONE);
        } else if (glassType.equals("Lamination")) {
            binding.spGlassType.setEnabled(false);
            binding.spGlassType.setClickable(false);
            /*binding.cvPvb.setVisibility(View.VISIBLE);
            binding.spPvb.setVisibility(View.VISIBLE);
            binding.tvPvb.setVisibility(View.VISIBLE);
            binding.cvGap.setVisibility(View.GONE);
            binding.spGap.setVisibility(View.GONE);
            binding.tvGap.setVisibility(View.GONE);*/

            binding.tvGap.setVisibility(View.GONE);
            binding.cvGap.setVisibility(View.GONE);
            binding.spGap.setVisibility(View.GONE);
            binding.tvGlass1.setVisibility(View.VISIBLE);
            binding.tvGlass2.setVisibility(View.VISIBLE);
            binding.tvGlass3.setVisibility(View.GONE);
            binding.cvGlass1.setVisibility(View.VISIBLE);
            binding.cvGlass2.setVisibility(View.VISIBLE);
            binding.cvGlass3.setVisibility(View.GONE);
            binding.cvThickness.setVisibility(View.GONE);
            binding.cvMaterialDetails.setVisibility(View.GONE);
            binding.tvThickness.setVisibility(View.GONE);
            binding.tvMaterialDetails.setVisibility(View.GONE);

            binding.cvPvb.setVisibility(View.VISIBLE);
            binding.spPvb.setVisibility(View.VISIBLE);
            binding.tvPvb.setVisibility(View.VISIBLE);
        } else if (glassType.equals("Annealed")) {
            binding.spGlassType.setEnabled(false);
            binding.spGlassType.setClickable(false);
            binding.tvGap.setVisibility(View.GONE);
            binding.cvGap.setVisibility(View.GONE);
            binding.tvGlass1.setVisibility(View.GONE);
            binding.tvGlass2.setVisibility(View.GONE);
            binding.cvGlass1.setVisibility(View.GONE);
            binding.cvGlass2.setVisibility(View.GONE);
            binding.cvThickness.setVisibility(View.VISIBLE);
            binding.cvMaterialDetails.setVisibility(View.VISIBLE);
            binding.tvThickness.setVisibility(View.VISIBLE);
            binding.tvMaterialDetails.setVisibility(View.VISIBLE);
            binding.cvPvb.setVisibility(View.GONE);
            binding.spPvb.setVisibility(View.GONE);
            binding.tvPvb.setVisibility(View.GONE);
        } else if (glassType.equals("LaminatedDGU")) {
            binding.spGlassType.setEnabled(false);
            binding.spGlassType.setClickable(false);
            binding.tvGap.setVisibility(View.VISIBLE);
            binding.cvGap.setVisibility(View.VISIBLE);
            binding.spGap.setVisibility(View.VISIBLE);
            binding.tvGlass1.setVisibility(View.VISIBLE);
            binding.tvGlass2.setVisibility(View.VISIBLE);
            binding.tvGlass3.setVisibility(View.VISIBLE);
            binding.cvGlass1.setVisibility(View.VISIBLE);
            binding.cvGlass2.setVisibility(View.VISIBLE);
            binding.cvGlass3.setVisibility(View.VISIBLE);
            binding.cvThickness.setVisibility(View.GONE);
            binding.cvMaterialDetails.setVisibility(View.GONE);
            binding.tvThickness.setVisibility(View.GONE);
            binding.tvMaterialDetails.setVisibility(View.GONE);
            binding.cvPvb.setVisibility(View.VISIBLE);
            binding.spPvb.setVisibility(View.VISIBLE);
            binding.tvPvb.setVisibility(View.VISIBLE);
        }

        setGlassTypeSpinnerAdapter();
        setGapSpinnerAdapter();
        setPVBSpinnerAdapter();

        standardId = binding.radioGroup.getCheckedRadioButtonId();

        dao = DBRoom.getInstance(this).dao();
        if (addTypeFlag1) {

            if (glassType.equals("SGU")) {

                SharedPreferences sh = getSharedPreferences("FormDataSguPref", MODE_APPEND);

                if (sh.getString("standard", null).equals("inch")) {
                    binding.radioInch.setChecked(true);
                } else {
                    binding.radioMm.setChecked(true);
                }

                if (!sh.getString("image", "").isEmpty()) {
                    binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
                    binding.ivUploadPhoto.setVisibility(View.GONE);
//                            bmpImage = DataConverter.convertByteArray2Image(sgudata.get(i).getSgu_image());
                    File file = new File(sh.getString("image", ""));
                    imgepath = file.getAbsolutePath();
                    try {
                        bmpImage = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                } else {
                    binding.ivUploadPhoto1.setVisibility(View.GONE);
                    binding.ivUploadPhoto.setVisibility(View.VISIBLE);
                }
                binding.spGlassType.setSelection(0);
                binding.etThickness.setText(sh.getString("thikness", ""));
                binding.etMaterialDetail.setText(sh.getString("material", ""));
                binding.etWidth.setText(sh.getString("width", ""));
                binding.etHeight.setText(sh.getString("height", ""));
                binding.etQuantity.setText(sh.getString("quantity", ""));
                binding.etNote.setText(sh.getString("note", ""));
            } else if (glassType.equals("DGU")) {
                SharedPreferences sh = getSharedPreferences("FormDataDguPref", MODE_APPEND);
                String glassType = sh.getString("glassType", null);
                String shGapPosition = sh.getString("gapPosition", "");
                String shPvbPosition = sh.getString("pvbPosition", "");


                if (sh.getString("standard", null).equals("inch")) {
                    binding.radioInch.setChecked(true);
                } else {
                    binding.radioMm.setChecked(true);
                }

                if (!sh.getString("image", "").isEmpty()) {
                    binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
                    binding.ivUploadPhoto.setVisibility(View.GONE);
//                            bmpImage = DataConverter.convertByteArray2Image(sgudata.get(i).getSgu_image());
                    File file = new File(sh.getString("image", ""));
                    imgepath = file.getAbsolutePath();
                    try {
                        bmpImage = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                } else {
                    binding.ivUploadPhoto1.setVisibility(View.GONE);
                    binding.ivUploadPhoto.setVisibility(View.VISIBLE);
                }

                binding.spGlassType.setSelection(1);
                binding.etGlass1.setText(sh.getString("glass1", ""));
                binding.etGlass2.setText(sh.getString("glass2", ""));

                if (shGapPosition.equals("0")) {
                    binding.spGap.setSelection(0);
                    gapPosition = 0;
                } else if (shGapPosition.equals("1")) {
                    binding.spGap.setSelection(1);
                    gapPosition = 1;
                } else if (shGapPosition.equals("2")) {
                    binding.spGap.setSelection(2);
                    gapPosition = 2;
                } else if (shGapPosition.equals("3")) {
                    binding.spGap.setSelection(3);
                    gapPosition = 3;
                } else if (shGapPosition.equals("4")) {
                    binding.spGap.setSelection(4);
                    gapPosition = 4;
                } else if (shGapPosition.equals("5")) {
                    binding.spGap.setSelection(5);
                    gapPosition = 5;
                } else if (shGapPosition.equals("6")) {
                    binding.spGap.setSelection(6);
                    gapPosition = 6;
                } else if (shGapPosition.equals("7")) {
                    binding.spGap.setSelection(7);
                    gapPosition = 7;
                } else if (shGapPosition.equals("8")) {
                    binding.spGap.setSelection(8);
                    gapPosition = 8;
                } else if (shGapPosition.equals("9")) {
                    binding.spGap.setSelection(9);
                    gapPosition = 9;
                }

                binding.etWidth.setText(sh.getString("width", ""));
                binding.etHeight.setText(sh.getString("height", ""));
                binding.etQuantity.setText(sh.getString("quantity", ""));
                binding.etNote.setText(sh.getString("note", ""));

            } else if (glassType.equals("Lamination")) {
                SharedPreferences sh = getSharedPreferences("FormDataLaminationPref", MODE_APPEND);
                String glassType = sh.getString("glassType", null);
                String shGapPosition = sh.getString("gapPosition", "");
                String shPvbPosition = sh.getString("pvbPosition", "");

                if (sh.getString("standard", null).equals("inch")) {
                    binding.radioInch.setChecked(true);
                } else {
                    binding.radioMm.setChecked(true);
                }

                if (!sh.getString("image", "").isEmpty()) {
                    binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
                    binding.ivUploadPhoto.setVisibility(View.GONE);
//                            bmpImage = DataConverter.convertByteArray2Image(sgudata.get(i).getSgu_image());
                    File file = new File(sh.getString("image", ""));
                    imgepath = file.getAbsolutePath();
                    try {
                        bmpImage = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                } else {
                    binding.ivUploadPhoto1.setVisibility(View.GONE);
                    binding.ivUploadPhoto.setVisibility(View.VISIBLE);
                }

                binding.spGlassType.setSelection(2);

                binding.etGlass1.setText(sh.getString("glass1", ""));
                binding.etGlass2.setText(sh.getString("glass2", ""));

                if (shPvbPosition.equals("0")) {
                    binding.spPvb.setSelection(0);
                } else if (shPvbPosition.equals("1")) {
                    binding.spPvb.setSelection(1);
                } else if (shPvbPosition.equals("2")) {
                    binding.spPvb.setSelection(2);
                } else if (shPvbPosition.equals("3")) {
                    binding.spPvb.setSelection(3);
                }

                binding.etWidth.setText(sh.getString("width", ""));
                binding.etHeight.setText(sh.getString("height", ""));
                binding.etQuantity.setText(sh.getString("quantity", ""));
                binding.etNote.setText(sh.getString("note", ""));
            } else if (glassType.equals("Annealed")) {
                SharedPreferences sh = getSharedPreferences("FormDataAnnealedPref", MODE_APPEND);
                String glassType = sh.getString("glassType", null);
                String shGapPosition = sh.getString("gapPosition", "");
                String shPvbPosition = sh.getString("pvbPosition", "");

                if (sh.getString("standard", null).equals("inch")) {
                    binding.radioInch.setChecked(true);
                } else {
                    binding.radioMm.setChecked(true);
                }

                if (!sh.getString("image", "").isEmpty()) {
                    binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
                    binding.ivUploadPhoto.setVisibility(View.GONE);
//                            bmpImage = DataConverter.convertByteArray2Image(sgudata.get(i).getSgu_image());
                    File file = new File(sh.getString("image", ""));
                    imgepath = file.getAbsolutePath();
                    try {
                        bmpImage = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                } else {
                    binding.ivUploadPhoto1.setVisibility(View.GONE);
                    binding.ivUploadPhoto.setVisibility(View.VISIBLE);
                }
                binding.spGlassType.setSelection(3);
                binding.etThickness.setText(sh.getString("thikness", ""));
                binding.etMaterialDetail.setText(sh.getString("material", ""));
                binding.etWidth.setText(sh.getString("width", ""));
                binding.etHeight.setText(sh.getString("height", ""));
                binding.etQuantity.setText(sh.getString("quantity", ""));
                binding.etNote.setText(sh.getString("note", ""));
            } else if (glassType.equals("LaminatedDGU")) {
                SharedPreferences sh = getSharedPreferences("FormDataLDguPref", MODE_APPEND);
                String glassType = sh.getString("glassType", null);
                String shGapPosition = sh.getString("gapPosition", "");
                String shPvbPosition = sh.getString("pvbPosition", "");

                if (sh.getString("standard", null).equals("inch")) {
                    binding.radioInch.setChecked(true);
                } else {
                    binding.radioMm.setChecked(true);
                }

                if (!sh.getString("image", "").isEmpty()) {
                    binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
                    binding.ivUploadPhoto.setVisibility(View.GONE);
//                            bmpImage = DataConverter.convertByteArray2Image(sgudata.get(i).getSgu_image());
                    File file = new File(sh.getString("image", ""));
                    imgepath = file.getAbsolutePath();
                    try {
                        bmpImage = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                } else {
                    binding.ivUploadPhoto1.setVisibility(View.GONE);
                    binding.ivUploadPhoto.setVisibility(View.VISIBLE);
                }
                binding.spGlassType.setSelection(4);
                binding.etGlass1.setText(sh.getString("glass1", ""));
                binding.etGlass2.setText(sh.getString("glass2", ""));
                binding.etGlass3.setText(sh.getString("glass3", ""));

                if (shGapPosition.equals("0")) {
                    binding.spGap.setSelection(0);
                } else if (shGapPosition.equals("1")) {
                    binding.spGap.setSelection(1);
                } else if (shGapPosition.equals("2")) {
                    binding.spGap.setSelection(2);
                } else if (shGapPosition.equals("3")) {
                    binding.spGap.setSelection(3);
                } else if (shGapPosition.equals("4")) {
                    binding.spGap.setSelection(4);
                } else if (shGapPosition.equals("5")) {
                    binding.spGap.setSelection(5);
                } else if (shGapPosition.equals("6")) {
                    binding.spGap.setSelection(6);
                } else if (shGapPosition.equals("7")) {
                    binding.spGap.setSelection(7);
                } else if (shGapPosition.equals("8")) {
                    binding.spGap.setSelection(8);
                } else if (shGapPosition.equals("9")) {
                    binding.spGap.setSelection(9);
                }

                if (shPvbPosition.equals("0")) {
                    binding.spPvb.setSelection(0);
                } else if (shPvbPosition.equals("1")) {
                    binding.spPvb.setSelection(1);
                } else if (shPvbPosition.equals("2")) {
                    binding.spPvb.setSelection(2);
                } else if (shPvbPosition.equals("3")) {
                    binding.spPvb.setSelection(3);
                }

                binding.etWidth.setText(sh.getString("width", ""));
                binding.etHeight.setText(sh.getString("height", ""));
                binding.etQuantity.setText(sh.getString("quantity", ""));
                binding.etNote.setText(sh.getString("note", ""));

            }

            binding.spGlassType.setEnabled(false);
            binding.radioGroup.setEnabled(false);
            binding.radioInch.setEnabled(false);
            binding.radioMm.setEnabled(false);
            binding.etThickness.setEnabled(false);
            binding.etMaterialDetail.setEnabled(false);
            binding.etGlass1.setEnabled(false);
            binding.etGlass2.setEnabled(false);
            binding.spGap.setEnabled(false);
            binding.spPvb.setEnabled(false);
            binding.etGlass3.setEnabled(false);
        }
        if (addTypeFlag) {
            if (sguTableDataPosition != 0) {
                if (glassType.equals("SGU")) {
                    sgudata = dao.getSGUData();
                    for (int i = 0; i < sgudata.size(); i++) {
                        if (sgudata.get(i).getSgu_id() == sguTableDataPosition) {
                            //Standard
                            if (sgudata.get(i).getSgu_standard().equals("inch")) {
                                binding.radioGroup.check(R.id.radioInch);
                            } else if (sgudata.get(i).getSgu_standard().equals("mm")) {
                                binding.radioGroup.check(R.id.radioMm);
                            }
                            //Thickness
                            binding.etThickness.setText(sgudata.get(i).getSgu_thickness());
                            //Material Details
                            binding.etMaterialDetail.setText(sgudata.get(i).getSgu_materialDetails());
                            //Glass Width
                            binding.etWidth.setText(sgudata.get(i).getSgu_glassWidth());
                            //Glass Height
                            binding.etHeight.setText(sgudata.get(i).getSgu_glassHeight());
                            //Quantity
                            binding.etQuantity.setText(sgudata.get(i).getSgu_quantity());
                            //Notes
                            binding.etNote.setText(sgudata.get(i).getSgu_note());
                            //Image
                            if (sgudata.get(i).getSgu_path() != null) {
                                binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
                                binding.ivUploadPhoto.setVisibility(View.GONE);
//                            bmpImage = DataConverter.convertByteArray2Image(sgudata.get(i).getSgu_image());
                                File file = new File(sgudata.get(i).getSgu_path());
                                imgepath = file.getAbsolutePath();
                                try {
                                    bmpImage = MediaStore.Images.Media
                                            .getBitmap(getContentResolver(), Uri.fromFile(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                            } else {
                                binding.ivUploadPhoto1.setVisibility(View.GONE);
                                binding.ivUploadPhoto.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else if (glassType.equals("DGU")) {
                    dgudata = dao.getDGUData();
                    for (int i = 0; i < dgudata.size(); i++) {
                        if (dgudata.get(i).getDgu_id() == sguTableDataPosition) {
                            //Standard
                            if (dgudata.get(i).getDgu_standard().equals("inch")) {
                                binding.radioGroup.check(R.id.radioInch);
                            } else if (dgudata.get(i).getDgu_standard().equals("mm")) {
                                binding.radioGroup.check(R.id.radioMm);
                            }
                            //Thickness
                            binding.etGlass1.setText(dgudata.get(i).getDgu_glass_1());
                            //Material Details
                            binding.etGlass2.setText(dgudata.get(i).getDgu_glass_2());
                            //Gap
                            if (dgudata.get(i).getDgu_gap().equals("0")) {
                                binding.spGap.setSelection(0);
                            } else if (dgudata.get(i).getDgu_gap().equals("1")) {
                                binding.spGap.setSelection(1);
                            } else if (dgudata.get(i).getDgu_gap().equals("2")) {
                                binding.spGap.setSelection(2);
                            } else if (dgudata.get(i).getDgu_gap().equals("3")) {
                                binding.spGap.setSelection(3);
                            } else if (dgudata.get(i).getDgu_gap().equals("4")) {
                                binding.spGap.setSelection(4);
                            } else if (dgudata.get(i).getDgu_gap().equals("5")) {
                                binding.spGap.setSelection(5);
                            } else if (dgudata.get(i).getDgu_gap().equals("6")) {
                                binding.spGap.setSelection(6);
                            } else if (dgudata.get(i).getDgu_gap().equals("7")) {
                                binding.spGap.setSelection(7);
                            } else if (dgudata.get(i).getDgu_gap().equals("8")) {
                                binding.spGap.setSelection(8);
                            } else if (dgudata.get(i).getDgu_gap().equals("9")) {
                                binding.spGap.setSelection(9);
                            }
                            //Glass Width
                            binding.etWidth.setText(dgudata.get(i).getDgu_glassWidth());
                            //Glass Height
                            binding.etHeight.setText(dgudata.get(i).getDgu_glassHeight());
                            //Quantity
                            binding.etQuantity.setText(dgudata.get(i).getDgu_quantity());
                            //Notes
                            binding.etNote.setText(dgudata.get(i).getDgu_note());
                            //Image
                        /*if (dgudata.get(i).getDgu_image() != null) {
                            bmpImage = DataConverter.convertByteArray2Image(dgudata.get(i).getDgu_image());
                            binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                        }*/
                            if (dgudata.get(i).getDgu_path() != null) {
                                binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
                                binding.ivUploadPhoto.setVisibility(View.GONE);
//                            bmpImage = DataConverter.convertByteArray2Image(dgudata.get(i).getDgu_image());
//                            binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                                File file = new File(dgudata.get(i).getDgu_path());
                                imgepath = file.getAbsolutePath();
                                try {
                                    bmpImage = MediaStore.Images.Media
                                            .getBitmap(getContentResolver(), Uri.fromFile(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                            } else {
                                binding.ivUploadPhoto1.setVisibility(View.GONE);
                                binding.ivUploadPhoto.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else if (glassType.equals("Lamination")) {
                    laminationdata = dao.getLaminationData();
                    for (int i = 0; i < laminationdata.size(); i++) {
                        if (laminationdata.get(i).getLamination_id() == sguTableDataPosition) {
                            //Standard
                            if (laminationdata.get(i).getLamination_standard().equals("inch")) {
                                binding.radioGroup.check(R.id.radioInch);
                            } else if (laminationdata.get(i).getLamination_standard().equals("mm")) {
                                binding.radioGroup.check(R.id.radioMm);
                            }
                            //Thickness
                            binding.etGlass1.setText(laminationdata.get(i).getLamination_glass_1());
                            //Material Details
                            binding.etGlass2.setText(laminationdata.get(i).getLamination_glass_2());
                            //PVB
                            if (laminationdata.get(i).getLamination_pvb().equals("0")) {
                                binding.spPvb.setSelection(0);
                            } else if (laminationdata.get(i).getLamination_pvb().equals("1")) {
                                binding.spPvb.setSelection(1);
                            } else if (laminationdata.get(i).getLamination_pvb().equals("2")) {
                                binding.spPvb.setSelection(2);
                            } else if (laminationdata.get(i).getLamination_pvb().equals("3")) {
                                binding.spPvb.setSelection(3);
                            }
                            //Glass Width
                            binding.etWidth.setText(laminationdata.get(i).getLamination_glassWidth());
                            //Glass Height
                            binding.etHeight.setText(laminationdata.get(i).getLamination_glassHeight());
                            //Quantity
                            binding.etQuantity.setText(laminationdata.get(i).getLamination_quantity());
                            //Notes
                            binding.etNote.setText(laminationdata.get(i).getLamination_note());
                            //Image
                            if (laminationdata.get(i).getLamination_path() != null) {
                                binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
                                binding.ivUploadPhoto.setVisibility(View.GONE);
//                            bmpImage = DataConverter.convertByteArray2Image(laminationdata.get(i).getLamination_image());
//                            binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                                File file = new File(laminationdata.get(i).getLamination_path());
                                imgepath = file.getAbsolutePath();
                                try {
                                    bmpImage = MediaStore.Images.Media
                                            .getBitmap(getContentResolver(), Uri.fromFile(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                            } else {
                                binding.ivUploadPhoto1.setVisibility(View.GONE);
                                binding.ivUploadPhoto.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else if (glassType.equals("Annealed")) {
                    annealeddata = dao.getAnnealedData();
                    for (int i = 0; i < annealeddata.size(); i++) {
                        if (annealeddata.get(i).getAnnealed_id() == sguTableDataPosition) {
                            //Standard
                            if (annealeddata.get(i).getAnnealed_standard().equals("inch")) {
                                binding.radioGroup.check(R.id.radioInch);
                            } else if (annealeddata.get(i).getAnnealed_standard().equals("mm")) {
                                binding.radioGroup.check(R.id.radioMm);
                            }
                            //Thickness
                            binding.etThickness.setText(annealeddata.get(i).getAnnealed_thickness());
                            //Material Details
                            binding.etMaterialDetail.setText(annealeddata.get(i).getAnnealed_materialDetails());
                            //Glass Width
                            binding.etWidth.setText(annealeddata.get(i).getAnnealed_glassWidth());
                            //Glass Height
                            binding.etHeight.setText(annealeddata.get(i).getAnnealed_glassHeight());
                            //Quantity
                            binding.etQuantity.setText(annealeddata.get(i).getAnnealed_quantity());
                            //Notes
                            binding.etNote.setText(annealeddata.get(i).getAnnealed_note());
                            //Image
                            if (annealeddata.get(i).getAnnealed_path() != null) {
                                binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
                                binding.ivUploadPhoto.setVisibility(View.GONE);
//                            bmpImage = DataConverter.convertByteArray2Image(annealeddata.get(i).getAnnealed_image());
//                            binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                                File file = new File(annealeddata.get(i).getAnnealed_path());
                                imgepath = file.getAbsolutePath();
                                try {
                                    bmpImage = MediaStore.Images.Media
                                            .getBitmap(getContentResolver(), Uri.fromFile(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                            } else {
                                binding.ivUploadPhoto1.setVisibility(View.GONE);
                                binding.ivUploadPhoto.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else if (glassType.equals("LaminatedDGU")) {
                    laminatedDgudata = dao.getLDGUData();
                    for (int i = 0; i < laminatedDgudata.size(); i++) {
                        if (laminatedDgudata.get(i).getLdgu_id() == sguTableDataPosition) {
                            //Standard
                            if (laminatedDgudata.get(i).getLdgu_standard().equals("inch")) {
                                binding.radioGroup.check(R.id.radioInch);
                            } else if (laminatedDgudata.get(i).getLdgu_standard().equals("mm")) {
                                binding.radioGroup.check(R.id.radioMm);
                            }
                            //Thickness
                            binding.etGlass1.setText(laminatedDgudata.get(i).getLdgu_glass_1());
                            //Material Details
                            binding.etGlass2.setText(laminatedDgudata.get(i).getLdgu_glass_2());
                            binding.etGlass3.setText(laminatedDgudata.get(i).getLdgu_glass_3());
                            //Gap
                            if (laminatedDgudata.get(i).getLdgu_gap().equals("0")) {
                                binding.spGap.setSelection(0);
                            } else if (laminatedDgudata.get(i).getLdgu_gap().equals("1")) {
                                binding.spGap.setSelection(1);
                            } else if (laminatedDgudata.get(i).getLdgu_gap().equals("2")) {
                                binding.spGap.setSelection(2);
                            } else if (laminatedDgudata.get(i).getLdgu_gap().equals("3")) {
                                binding.spGap.setSelection(3);
                            } else if (laminatedDgudata.get(i).getLdgu_gap().equals("4")) {
                                binding.spGap.setSelection(4);
                            } else if (laminatedDgudata.get(i).getLdgu_gap().equals("5")) {
                                binding.spGap.setSelection(5);
                            } else if (laminatedDgudata.get(i).getLdgu_gap().equals("6")) {
                                binding.spGap.setSelection(6);
                            } else if (laminatedDgudata.get(i).getLdgu_gap().equals("7")) {
                                binding.spGap.setSelection(7);
                            } else if (laminatedDgudata.get(i).getLdgu_gap().equals("8")) {
                                binding.spGap.setSelection(8);
                            } else if (laminatedDgudata.get(i).getLdgu_gap().equals("9")) {
                                binding.spGap.setSelection(9);
                            }
                            //PVB
                            if (laminatedDgudata.get(i).getLdgu_pvb().equals("0")) {
                                binding.spPvb.setSelection(0);
                            } else if (laminatedDgudata.get(i).getLdgu_pvb().equals("1")) {
                                binding.spPvb.setSelection(1);
                            } else if (laminatedDgudata.get(i).getLdgu_pvb().equals("2")) {
                                binding.spPvb.setSelection(2);
                            } else if (laminatedDgudata.get(i).getLdgu_pvb().equals("3")) {
                                binding.spPvb.setSelection(3);
                            }

                            //Glass Width
                            binding.etWidth.setText(laminatedDgudata.get(i).getLdgu_glassWidth());
                            //Glass Height
                            binding.etHeight.setText(laminatedDgudata.get(i).getLdgu_glassHeight());
                            //Quantity
                            binding.etQuantity.setText(laminatedDgudata.get(i).getLdgu_quantity());
                            //Notes
                            binding.etNote.setText(laminatedDgudata.get(i).getLdgu_note());
                            //Image
                        /*if (laminatedDgudata.get(i).getDgu_image() != null) {
                            bmpImage = DataConverter.convertByteArray2Image(laminatedDgudata.get(i).getDgu_image());
                            binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                        }*/
                            if (laminatedDgudata.get(i).getLdgu_path() != null) {
                                binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
                                binding.ivUploadPhoto.setVisibility(View.GONE);
//                            bmpImage = DataConverter.convertByteArray2Image(laminatedDgudata.get(i).getLdgu_image());
//                            binding.ivUploadPhoto1.setImageBitmap(bmpImage);

                                File file = new File(laminatedDgudata.get(i).getLdgu_path());
                                imgepath = file.getAbsolutePath();
                                try {
                                    bmpImage = MediaStore.Images.Media
                                            .getBitmap(getContentResolver(), Uri.fromFile(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                            } else {
                                binding.ivUploadPhoto1.setVisibility(View.GONE);
                                binding.ivUploadPhoto.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
            binding.spGlassType.setEnabled(false);
            binding.radioGroup.setEnabled(false);
            binding.radioInch.setEnabled(false);
            binding.radioMm.setEnabled(false);
            binding.etThickness.setEnabled(false);
            binding.etMaterialDetail.setEnabled(false);
            binding.etGlass1.setEnabled(false);
            binding.etGlass2.setEnabled(false);
            binding.spGap.setEnabled(false);
            binding.spPvb.setEnabled(false);
            binding.etGlass3.setEnabled(false);
        }
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.cvCancel.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.imgLogout.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(InquiryFormEditActivity.this);
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
                    startActivity(new Intent(InquiryFormEditActivity.this, MainActivity.class));
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

        binding.ivUploadPhoto.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        });

        binding.ivUploadPhoto1.setOnClickListener(view -> {
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            if (bmpImage != null) {
//                Intent intent = new Intent(this, ViewImageActivity.class);
//                intent.putExtra("img", DataConverter.convertImage2ByteArray(bmpImage));
//                startActivity(intent);
            }
        });

        binding.cvSubmit.setOnClickListener(view -> {
//            if (bmpImage != null) {
            if (standardId == R.id.radioInch) {
                selectedStandard = "inch";
            } else if (standardId == R.id.radioMm) {
                selectedStandard = "mm";
            }
            {
                startActivity(new Intent(this, OrderListActivity.class));
                finish();
                thicknessText = binding.etThickness.getText().toString();
                materialDetailsText = binding.etMaterialDetail.getText().toString();
                glassWidthText = binding.etWidth.getText().toString();
                glassHeightText = binding.etHeight.getText().toString();
                quantityText = binding.etQuantity.getText().toString();
                noteText = binding.etNote.getText().toString();
                glass1 = binding.etGlass1.getText().toString();
                glass2 = binding.etGlass2.getText().toString();
                glass3 = binding.etGlass3.getText().toString();
//            gap = binding.etGlass2.getText().toString();
                Log.i("TAG", "onCreate: " + "Glass Type Position=> " + glassTypePosition +
                        "  Standard=> " + standardId + "  Thickness=> " + thicknessText + "  Material=> " + materialDetailsText +
                        "  width=> " + glassWidthText + "  Height=> " + glassWidthText +
                        "  Quantity=> " + quantityText + "  Note=> " + noteText);

                if (glassType.equals("SGU")) {
                    SGUTable sguData = new SGUTable();
                    sguData.setSgu_standard(selectedStandard);
                    sguData.setSgu_thickness(thicknessText);
                    sguData.setSgu_materialDetails(materialDetailsText);
                    sguData.setSgu_glassWidth(glassWidthText);
                    sguData.setSgu_glassHeight(glassHeightText);
                    sguData.setSgu_quantity(quantityText);
                    sguData.setSgu_note(noteText);
                    if (bmpImage != null) {
                        sguData.setSgu_image(DataConverter.convertImage2ByteArray(bmpImage));
                        if (cameraFlag) {
                            File sd = getCacheDir();
                            File folder = new File(sd, "/myfolder/");
                            if (!folder.exists()) {
                                if (!folder.mkdir()) {
                                    Log.e("ERROR", "Cannot create a directory!");
                                } else {
                                    folder.mkdirs();
                                }
                            }

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                            String timeStamp = dateFormat.format(new Date());
                            String imageFileName = "picture_" + timeStamp + ".jpg";

                            File fileName = new File(folder, imageFileName);
                            imgepath = fileName.getAbsolutePath();

                            try {
                                FileOutputStream outputStream = new FileOutputStream(String.valueOf(fileName));
                                bmpImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                outputStream.close();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        sguData.setSgu_path(imgepath);
                    }
                    if (sguTableDataPosition != 0) {
                        if (bmpImage == null) {
                            dao.updateSGUTable1(sguTableDataPosition, selectedStandard, thicknessText, materialDetailsText,
                                    glassWidthText, glassHeightText, quantityText, noteText);
                        } else {
                            dao.updateSGUTable(sguTableDataPosition, selectedStandard, thicknessText, materialDetailsText,
                                    glassWidthText, glassHeightText, quantityText, noteText, imgepath, DataConverter.convertImage2ByteArray(bmpImage));
                        }
                    } else {
                        dao.insertSGUData(sguData);
                    }
                } else if (glassType.equals("DGU")) {
                    DGUTable dguData = new DGUTable();
                    dguData.setDgu_standard(selectedStandard);
                    dguData.setDgu_glass_1(glass1);
                    dguData.setDgu_glass_2(glass2);
                    dguData.setDgu_gap(String.valueOf(gapPosition));
                    dguData.setDgu_glassWidth(glassWidthText);
                    dguData.setDgu_glassHeight(glassHeightText);
                    dguData.setDgu_quantity(quantityText);
                    dguData.setDgu_note(noteText);
                    if (bmpImage != null) {
                        dguData.setDgu_image(DataConverter.convertImage2ByteArray(bmpImage));
                        if (cameraFlag) {
                            File sd = getCacheDir();
                            File folder = new File(sd, "/myfolder/");
                            if (!folder.exists()) {
                                if (!folder.mkdir()) {
                                    Log.e("ERROR", "Cannot create a directory!");
                                } else {
                                    folder.mkdirs();
                                }
                            }

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                            String timeStamp = dateFormat.format(new Date());
                            String imageFileName = "picture_" + timeStamp + ".jpg";

                            File fileName = new File(folder, imageFileName);
                            imgepath = fileName.getAbsolutePath();

                            try {
                                FileOutputStream outputStream = new FileOutputStream(String.valueOf(fileName));
                                bmpImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                outputStream.close();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        dguData.setDgu_path(imgepath);
                    }
//                dao.insertDGUData(sguData);
                    if (sguTableDataPosition != 0) {
                        if (bmpImage == null) {
                            dao.updateDGUTable1(sguTableDataPosition, selectedStandard, glass1, glass2, String.valueOf(gapPosition),
                                    glassWidthText, glassHeightText, quantityText, noteText);
                        } else {
                            dao.updateDGUTable(sguTableDataPosition, selectedStandard, glass1, glass2, String.valueOf(gapPosition),
                                    glassWidthText, glassHeightText, quantityText, noteText, imgepath, DataConverter.convertImage2ByteArray(bmpImage));
                        }
                    } else {
                        dao.insertDGUData(dguData);
                    }

                }
                else if (glassType.equals("Lamination")) {
                    LaminationTable laminationData = new LaminationTable();
                    laminationData.setLamination_standard(selectedStandard);
                    laminationData.setLamination_glass_1(glass1);
                    laminationData.setLamination_glass_2(glass2);
                    laminationData.setLamination_pvb(String.valueOf(pvbPosition));
                    laminationData.setLamination_glassWidth(glassWidthText);
                    laminationData.setLamination_glassHeight(glassHeightText);
                    laminationData.setLamination_quantity(quantityText);
                    laminationData.setLamination_note(noteText);
                    if (bmpImage != null) {
                        laminationData.setLamination_image(DataConverter.convertImage2ByteArray(bmpImage));
                        if (cameraFlag) {
                            File sd = getCacheDir();
                            File folder = new File(sd, "/myfolder/");
                            if (!folder.exists()) {
                                if (!folder.mkdir()) {
                                    Log.e("ERROR", "Cannot create a directory!");
                                } else {
                                    folder.mkdirs();
                                }
                            }

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                            String timeStamp = dateFormat.format(new Date());
                            String imageFileName = "picture_" + timeStamp + ".jpg";

                            File fileName = new File(folder, imageFileName);
                            imgepath = fileName.getAbsolutePath();

                            try {
                                FileOutputStream outputStream = new FileOutputStream(String.valueOf(fileName));
                                bmpImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                outputStream.close();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        laminationData.setLamination_path(imgepath);
                    }
                    if (sguTableDataPosition != 0) {
                        if (bmpImage == null) {
                            dao.updateLaminationTable1(sguTableDataPosition, selectedStandard, thicknessText,
                                    String.valueOf(pvbPosition), materialDetailsText, glassWidthText,
                                    glassHeightText, quantityText, noteText);
                        } else {
                            dao.updateLaminationTable(sguTableDataPosition, selectedStandard, thicknessText,
                                    String.valueOf(pvbPosition), materialDetailsText, glassWidthText,
                                    glassHeightText, quantityText, noteText, imgepath,
                                    DataConverter.convertImage2ByteArray(bmpImage));
                        }
                    } else {
                        dao.insertLaminationData(laminationData);
                    }
                } else if (glassType.equals("Annealed")) {
                    AnnealedTable annealedData = new AnnealedTable();
                    annealedData.setAnnealed_standard(selectedStandard);
                    annealedData.setAnnealed_thickness(thicknessText);
                    annealedData.setAnnealed_materialDetails(materialDetailsText);
                    annealedData.setAnnealed_glassWidth(glassWidthText);
                    annealedData.setAnnealed_glassHeight(glassHeightText);
                    annealedData.setAnnealed_quantity(quantityText);
                    annealedData.setAnnealed_note(noteText);
                    if (bmpImage != null) {
                        annealedData.setAnnealed_image(DataConverter.convertImage2ByteArray(bmpImage));
                        if (cameraFlag) {
                            File sd = getCacheDir();
                            File folder = new File(sd, "/myfolder/");
                            if (!folder.exists()) {
                                if (!folder.mkdir()) {
                                    Log.e("ERROR", "Cannot create a directory!");
                                } else {
                                    folder.mkdirs();
                                }
                            }

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                            String timeStamp = dateFormat.format(new Date());
                            String imageFileName = "picture_" + timeStamp + ".jpg";

                            File fileName = new File(folder, imageFileName);
                            imgepath = fileName.getAbsolutePath();

                            try {
                                FileOutputStream outputStream = new FileOutputStream(String.valueOf(fileName));
                                bmpImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                outputStream.close();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        annealedData.setAnnealed_path(imgepath);
                    }
                    if (sguTableDataPosition != 0) {
                        if (bmpImage == null) {
                            dao.updateAnnealedTable1(sguTableDataPosition, selectedStandard, thicknessText, materialDetailsText,
                                    glassWidthText, glassHeightText, quantityText, noteText);
                        } else {
                            dao.updateAnnealedTable(sguTableDataPosition, selectedStandard, thicknessText, materialDetailsText,
                                    glassWidthText, glassHeightText, quantityText, noteText, imgepath, DataConverter.convertImage2ByteArray(bmpImage));
                        }
                    } else {
                        dao.insertAnnealedData(annealedData);
                    }
                }
                else if (glassType.equals("LaminatedDGU")) {
                    LaminatedDGUTable lDguData = new LaminatedDGUTable();
                    lDguData.setLdgu_standard(selectedStandard);
                    lDguData.setLdgu_glass_1(glass1);
                    lDguData.setLdgu_glass_2(glass2);
                    lDguData.setLdgu_glass_3(glass3);
                    lDguData.setLdgu_gap(String.valueOf(gapPosition));
                    lDguData.setLdgu_pvb(String.valueOf(pvbPosition));
                    lDguData.setLdgu_glassWidth(glassWidthText);
                    lDguData.setLdgu_glassHeight(glassHeightText);
                    lDguData.setLdgu_quantity(quantityText);
                    lDguData.setLdgu_note(noteText);
                    if (bmpImage != null) {
                        lDguData.setLdgu_image(DataConverter.convertImage2ByteArray(bmpImage));
                        if (cameraFlag) {
                            File sd = getCacheDir();
                            File folder = new File(sd, "/myfolder/");
                            if (!folder.exists()) {
                                if (!folder.mkdir()) {
                                    Log.e("ERROR", "Cannot create a directory!");
                                } else {
                                    folder.mkdirs();
                                }
                            }

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                            String timeStamp = dateFormat.format(new Date());
                            String imageFileName = "picture_" + timeStamp + ".jpg";

                            File fileName = new File(folder, imageFileName);
                            imgepath = fileName.getAbsolutePath();

                            try {
                                FileOutputStream outputStream = new FileOutputStream(String.valueOf(fileName));
                                bmpImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                outputStream.close();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        lDguData.setLdgu_path(imgepath);
                    }
//                dao.insertDGUData(sguData);
                    if (sguTableDataPosition != 0) {
                        if (bmpImage == null) {
                            dao.updateLDGUTable1(sguTableDataPosition, selectedStandard, glass1, glass2, glass3, String.valueOf(gapPosition),
                                    String.valueOf(pvbPosition), glassWidthText, glassHeightText, quantityText, noteText);
                        } else {
                            dao.updateLDGUTable(sguTableDataPosition, selectedStandard, glass1, glass2, glass3, String.valueOf(gapPosition),
                                    String.valueOf(pvbPosition), glassWidthText, glassHeightText, quantityText, noteText, imgepath, DataConverter.convertImage2ByteArray(bmpImage));
                        }
                    } else {
                        dao.insertLaminatedDGUData(lDguData);
                    }

                }
            }
//            } else {
//                Toast.makeText(InquiryFormEditActivity.this, "Select Photo", Toast.LENGTH_SHORT).show();
//            }
        });

        binding.ivUploadPhoto1.setOnClickListener(view -> {
            final BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(R.layout.btm_sheet_dialog);
            dialog.setCanceledOnTouchOutside(true);

            CardView cvViewImage = (CardView) dialog.findViewById(R.id.cv_view_imag);
            CardView cvCamera = (CardView) dialog.findViewById(R.id.cv_camera);
            CardView cvGallery = (CardView) dialog.findViewById(R.id.cv_gallery);

            cvViewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bmpImage != null) {
                        Intent intent = new Intent(InquiryFormEditActivity.this, ViewImageActivity.class);
                        intent.putExtra("img1", true);
//                            intent.putExtra("img", DataConverter.convertImage2ByteArray(bmpImage));
                        startActivity(intent);
                    }
                    dialog.dismiss();
                }
            });
            cvCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    } else {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                    dialog.dismiss();
                }
            });
            cvGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_PICTURE);

                    dialog.dismiss();
                }
            });
            dialog.show();
        });
        if (dataAdapter != null) {
            dataAdapter.notifyDataSetChanged();
        }
    }

    public void setGlassTypeSpinnerAdapter() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item, glassTypeCategories);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item1);
        binding.spGlassType.setAdapter(dataAdapter);
        if (glassType.equals("SGU")) {
            binding.spGlassType.setSelection(0);
        } else if (glassType.equals("DGU")) {
            binding.spGlassType.setSelection(1);
        } else if (glassType.equals("Lamination")) {
            binding.spGlassType.setSelection(2);
        } else if (glassType.equals("Annealed")) {
            binding.spGlassType.setSelection(3);
        } else if (glassType.equals("LaminatedDGU")) {
            binding.spGlassType.setSelection(4);
        }
    }

    public void setGapSpinnerAdapter() {
        dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item, gapCategories);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item1);
        binding.spGap.setAdapter(dataAdapter);
    }

    public void setPVBSpinnerAdapter() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item, pvbCategories);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item1);
        binding.spPvb.setAdapter(dataAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
                Uri uri = data.getData();
//                File finalFile = new File(getRealPathFromURI(uri));
                try {
                    imgepath = PathUtil.getPath(this, uri);
//                    imgepath = uri.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    bmpImage = getBitmapFromUri(uri);
                    if (bmpImage != null) {
                        binding.ivUploadPhoto.setVisibility(View.GONE);
                        binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
                        binding.ivUploadPhoto1.setImageBitmap(bmpImage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            binding.ivUploadPhoto.setVisibility(View.GONE);
            binding.ivUploadPhoto1.setVisibility(View.VISIBLE);
            bmpImage = (Bitmap) data.getExtras().get("data");
            Uri uri = data.getData();
            try {
                imgepath = PathUtil.getPath(this, uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
//                bmpImage = getBitmapFromUri(uri);
                cameraFlag = true;
                binding.ivUploadPhoto1.setImageBitmap(bmpImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, OrderListActivity.class));
        finish();
    }
}
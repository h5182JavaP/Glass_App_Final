package com.my.app.glassapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.my.app.glassapp.R;
import com.my.app.glassapp.api.model.InquiryData;
import com.my.app.glassapp.database.DBDao;
import com.my.app.glassapp.database.DBRoom;
import com.my.app.glassapp.databinding.ActivityAddressBinding;
import com.my.app.glassapp.model.AnnealedTable;
import com.my.app.glassapp.model.DGUTable;
import com.my.app.glassapp.model.LaminatedDGUTable;
import com.my.app.glassapp.model.LaminationTable;
import com.my.app.glassapp.model.SGUTable;
import com.my.app.glassapp.model.glass.GlassGroup;
import com.my.app.glassapp.viewmodel.InquiryViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddressActivity extends AppCompatActivity {

    final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final int SHARE_CODE = 101;
    ActivityAddressBinding binding;
    private List<SGUTable> sguData;
    private List<DGUTable> dguData;
    private List<LaminationTable> laminationData;
    private List<AnnealedTable> annealedData;
    private List<LaminatedDGUTable> lDGUData;
    private DBDao dao;
    HashMap<String, RequestBody> params;
    private String accessToken;
    private InquiryViewModel viewModel;
    //    File fileSgu;
    List<String> imageKeyList = new ArrayList<>();
    List<File> imageFileList = new ArrayList<>();
    int j = 0;
    String[] gapCategories = {"06", "08", "10", "12", "14", "15", "16", "18", "20", "22"};
    String[] pvbCategories = {"0.38", "0.16", "1.14", "1.52"};
    String dguGap;
    String lDguGap;
    String laminatedPVB;
    String laminationPVB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModel();

        dao = DBRoom.getInstance(this).dao();
//        inquirySaveTable = new InquirySaveTable();

        binding.cardSave.setOnClickListener(view -> {
//
            sguData = dao.getSGUData();
            dguData = dao.getDGUData();
            laminationData = dao.getLaminationData();
            annealedData = dao.getAnnealedData();
            lDGUData = dao.getLDGUData();

            params = new HashMap<>();

            params.put("address[street_no]", createPartFromString(binding.etStreetNo.getText().toString()));
            params.put("address[address]", createPartFromString(binding.etAddress.getText().toString()));
            params.put("address[state]", createPartFromString(binding.etState.getText().toString()));
            params.put("address[city]", createPartFromString(binding.etCity.getText().toString()));
            params.put("address[pincode]", createPartFromString(binding.etPincode.getText().toString()));
//            for (int j = 0; j < 1; j++) {
            if (!sguData.isEmpty()) {
                Log.i("TAG", "saveOrder: " + "order[" + j + "][glass_type]");
                params.put("order[" + j + "][glass_type]", createPartFromString("SGU"));
                for (int i = 0; i < sguData.size(); i++) {
                    params.put("order[" + j + "][standard]", createPartFromString(sguData.get(i).getSgu_standard()));
                    params.put("order[" + j + "][thickness]", createPartFromString(sguData.get(i).getSgu_thickness()));
                    params.put("order[" + j + "][material_details]", createPartFromString(sguData.get(i).getSgu_materialDetails()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[width]", createPartFromString(sguData.get(i).getSgu_glassWidth()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[height]", createPartFromString(sguData.get(i).getSgu_glassHeight()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[quantity]", createPartFromString(sguData.get(i).getSgu_quantity()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[notes]", createPartFromString(sguData.get(i).getSgu_note()));
                    if (sguData.get(i).getSgu_path() != null) {
//                            if (fileSgu != null) {
                        imageFileList.add(new File(sguData.get(i).getSgu_path()));
                        imageKeyList.add("order[" + j + "][order_items][" + i + "][image]");
//                            }
                    } else {
                    }
                }
                j++;
            }
            if (!dguData.isEmpty()) {
                Log.i("TAG", "saveOrder: " + "order[" + 1 + "][glass_type]");
                params.put("order[" + j + "][glass_type]", createPartFromString("DGU"));
                for (int i = 0; i < dguData.size(); i++) {
                    params.put("order[" + j + "][standard]", createPartFromString(dguData.get(i).getDgu_standard()));
                    params.put("order[" + j + "][glass1]", createPartFromString(dguData.get(i).getDgu_glass_1()));
                    params.put("order[" + j + "][glass2]", createPartFromString(dguData.get(i).getDgu_glass_2()));

                    if (dguData.get(i).getDgu_gap().equals("0")) {
                        params.put("order[" + j + "][gap]", createPartFromString("06"));
                    } else if (dguData.get(i).getDgu_gap().equals("1")) {
                        params.put("order[" + j + "][gap]", createPartFromString("08"));
                    } else if (dguData.get(i).getDgu_gap().equals("2")) {
                        params.put("order[" + j + "][gap]", createPartFromString("10"));
                    } else if (dguData.get(i).getDgu_gap().equals("3")) {
                        params.put("order[" + j + "][gap]", createPartFromString("12"));
                    } else if (dguData.get(i).getDgu_gap().equals("4")) {
                        params.put("order[" + j + "][gap]", createPartFromString("14"));
                    } else if (dguData.get(i).getDgu_gap().equals("5")) {
                        params.put("order[" + j + "][gap]", createPartFromString("15"));
                    } else if (dguData.get(i).getDgu_gap().equals("6")) {
                        params.put("order[" + j + "][gap]", createPartFromString("16"));
                    } else if (dguData.get(i).getDgu_gap().equals("7")) {
                        params.put("order[" + j + "][gap]", createPartFromString("18"));
                    } else if (dguData.get(i).getDgu_gap().equals("8")) {
                        params.put("order[" + j + "][gap]", createPartFromString("20"));
                    } else if (dguData.get(i).getDgu_gap().equals("9")) {
                        params.put("order[" + j + "][gap]", createPartFromString("22"));
                    }

                    params.put("order[" + j + "][order_items][" + i + "]" + "[width]", createPartFromString(dguData.get(i).getDgu_glassWidth()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[height]", createPartFromString(dguData.get(i).getDgu_glassHeight()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[quantity]", createPartFromString(dguData.get(i).getDgu_quantity()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[notes]", createPartFromString(dguData.get(i).getDgu_note()));

                    if (dguData.get(i).getDgu_path() != null) {
//                            if (fileSgu != null) {
                        imageFileList.add(new File(dguData.get(i).getDgu_path()));
                        imageKeyList.add("order[" + j + "][order_items][" + i + "][image]");
//                            }
                    } else {
                    }
                }
                j++;
            }
            if (!laminationData.isEmpty()) {
                Log.i("TAG", "saveOrder: " + "order[" + j + "][glass_type]");
                params.put("order[" + j + "][glass_type]", createPartFromString("Lamination Glass"));
                for (int i = 0; i < laminationData.size(); i++) {
                    params.put("order[" + j + "][standard]", createPartFromString(laminationData.get(i).getLamination_standard()));
                    params.put("order[" + j + "][thickness]", createPartFromString(laminationData.get(i).getLamination_glass_1()));
                    params.put("order[" + j + "][material_details]", createPartFromString(laminationData.get(i).getLamination_glass_2()));
                    if (laminationData.get(i).getLamination_pvb().equals("0"))
                        params.put("order[" + j + "][PVB]", createPartFromString("0.38"));
                    else if (laminationData.get(i).getLamination_pvb().equals("1"))
                        params.put("order[" + j + "][PVB]", createPartFromString("0.16"));
                    else if (laminationData.get(i).getLamination_pvb().equals("2"))
                        params.put("order[" + j + "][PVB]", createPartFromString("1.14"));
                    else if (laminationData.get(i).getLamination_pvb().equals("3"))
                        params.put("order[" + j + "][PVB]", createPartFromString("1.52"));

                    params.put("order[" + j + "][order_items][" + i + "]" + "[width]", createPartFromString(laminationData.get(i).getLamination_glassWidth()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[height]", createPartFromString(laminationData.get(i).getLamination_glassHeight()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[quantity]", createPartFromString(laminationData.get(i).getLamination_quantity()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[notes]", createPartFromString(laminationData.get(i).getLamination_note()));
                    if (laminationData.get(i).getLamination_path() != null) {
//                            if (fileSgu != null){
                        imageFileList.add(new File(laminationData.get(i).getLamination_path()));
                        imageKeyList.add("order[" + j + "][order_items][" + i + "][image]");
//                        }
                    } else {
                    }

                }
                j++;
            }
            if (!annealedData.isEmpty()) {
                Log.i("TAG", "saveOrder: " + "order[" + j + "][glass_type]");
                params.put("order[" + j + "][glass_type]", createPartFromString("Annealed Glass"));
//                inquirySaveTable.setData("SGU");
                for (int i = 0; i < annealedData.size(); i++) {
                    params.put("order[" + j + "][standard]", createPartFromString(annealedData.get(i).getAnnealed_standard()));
                    params.put("order[" + j + "][thickness]", createPartFromString(annealedData.get(i).getAnnealed_thickness()));
                    params.put("order[" + j + "][material_details]", createPartFromString(annealedData.get(i).getAnnealed_materialDetails()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[width]", createPartFromString(annealedData.get(i).getAnnealed_glassWidth()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[height]", createPartFromString(annealedData.get(i).getAnnealed_glassHeight()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[quantity]", createPartFromString(annealedData.get(i).getAnnealed_quantity()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[notes]", createPartFromString(annealedData.get(i).getAnnealed_note()));
                    if (annealedData.get(i).getAnnealed_path() != null) {
//                            if (fileSgu != null) {
                        imageFileList.add(new File(annealedData.get(i).getAnnealed_path()));
                        imageKeyList.add("order[" + j + "][order_items][" + i + "][image]");
//                            }
                    } else {
                    }
                }
                j++;
            }
            if (!lDGUData.isEmpty()) {
                Log.i("TAG", "saveOrder: " + "order[" + j + "][glass_type]");
                params.put("order[" + j + "][glass_type]", createPartFromString("Laminated DGU Glass"));
                for (int i = 0; i < lDGUData.size(); i++) {
                    params.put("order[" + j + "][standard]", createPartFromString(lDGUData.get(i).getLdgu_standard()));
                    params.put("order[" + j + "][glass1]", createPartFromString(lDGUData.get(i).getLdgu_glass_1()));
                    params.put("order[" + j + "][glass2]", createPartFromString(lDGUData.get(i).getLdgu_glass_2()));
                    params.put("order[" + j + "][glass3]", createPartFromString(lDGUData.get(i).getLdgu_glass_3()));

                    if (lDGUData.get(i).getLdgu_gap().equals("0")) {
                        params.put("order[" + j + "][gap]", createPartFromString("06"));
                    } else if (lDGUData.get(i).getLdgu_gap().equals("1")) {
                        params.put("order[" + j + "][gap]", createPartFromString("08"));
                    } else if (lDGUData.get(i).getLdgu_gap().equals("2")) {
                        params.put("order[" + j + "][gap]", createPartFromString("10"));
                    } else if (lDGUData.get(i).getLdgu_gap().equals("3")) {
                        params.put("order[" + j + "][gap]", createPartFromString("12"));
                    } else if (lDGUData.get(i).getLdgu_gap().equals("4")) {
                        params.put("order[" + j + "][gap]", createPartFromString("14"));
                    } else if (lDGUData.get(i).getLdgu_gap().equals("5")) {
                        params.put("order[" + j + "][gap]", createPartFromString("15"));
                    } else if (lDGUData.get(i).getLdgu_gap().equals("6")) {
                        params.put("order[" + j + "][gap]", createPartFromString("16"));
                    } else if (lDGUData.get(i).getLdgu_gap().equals("7")) {
                        params.put("order[" + j + "][gap]", createPartFromString("18"));
                    } else if (lDGUData.get(i).getLdgu_gap().equals("8")) {
                        params.put("order[" + j + "][gap]", createPartFromString("20"));
                    } else if (lDGUData.get(i).getLdgu_gap().equals("9")) {
                        params.put("order[" + j + "][gap]", createPartFromString("22"));
                    }

                    if (lDGUData.get(i).getLdgu_pvb().equals("0"))
                        params.put("order[" + j + "][PVB]", createPartFromString("0.38"));
                    else if (lDGUData.get(i).getLdgu_pvb().equals("1"))
                        params.put("order[" + j + "][PVB]", createPartFromString("0.16"));
                    else if (lDGUData.get(i).getLdgu_pvb().equals("2"))
                        params.put("order[" + j + "][PVB]", createPartFromString("1.14"));
                    else if (lDGUData.get(i).getLdgu_pvb().equals("3"))
                        params.put("order[" + j + "][PVB]", createPartFromString("1.52"));
//                    params.put("order[" + j + "][gap]", createPartFromString(lDGUData.get(i).getLdgu_gap()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[width]", createPartFromString(lDGUData.get(i).getLdgu_glassWidth()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[height]", createPartFromString(lDGUData.get(i).getLdgu_glassHeight()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[quantity]", createPartFromString(lDGUData.get(i).getLdgu_quantity()));
                    params.put("order[" + j + "][order_items][" + i + "]" + "[notes]", createPartFromString(lDGUData.get(i).getLdgu_note()));
                    if (lDGUData.get(i).getLdgu_path() != null) {
//                            if (fileSgu != null) {
                        imageFileList.add(new File(lDGUData.get(i).getLdgu_path()));
                        imageKeyList.add("order[" + j + "][order_items][" + i + "][image]");
//                            }
                    } else {
                    }
                }
                j++;
            }
//            }
            Log.i("TAG", "onCreate: " + params.size());
            if (TextUtils.isEmpty(binding.etStreetNo.getText().toString())) {
                Toast.makeText(AddressActivity.this, "Please enter Street No", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(binding.etAddress.getText().toString())) {
                Toast.makeText(AddressActivity.this, "Please enter Address", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(binding.etState.getText().toString())) {
                Toast.makeText(AddressActivity.this, "Please enter state", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(binding.etCity.getText().toString())) {
                Toast.makeText(AddressActivity.this, "Please enter city", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(binding.etPincode.getText().toString())) {
                Toast.makeText(AddressActivity.this, "Please enter pincode", Toast.LENGTH_SHORT).show();
                return;
            } else if (binding.etPincode.getText().toString().length() < 6) {
                Toast.makeText(AddressActivity.this, "Please enter valid pincode", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                showLoading();
                SharedPreferences sh = getSharedPreferences("TokenPref", MODE_APPEND);
                if (sh != null)
                    accessToken = sh.getString("token", "");


                MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[imageKeyList.size()];

                RequestBody requestFile;
                for (int i = 0; i < imageKeyList.size(); i++) {
                    requestFile = RequestBody.create(MediaType.parse("*/*"), imageFileList.get(i));
                    surveyImagesParts[i] = MultipartBody.Part.createFormData(imageKeyList.get(i),
                            imageFileList.get(i).getName(),
                            requestFile);

                }
//                if (fileSgu != null) {

//                    MultipartBody.Part fphoto = MultipartBody.Part.createFormData("order[0][order_items][0][image]", fileSgu.getName(), requestFile);
//                }
                viewModel.apiCall(params, "Bearer " + accessToken, surveyImagesParts);
            }
        });

        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.ivLogout.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(AddressActivity.this);
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
                    startActivity(new Intent(AddressActivity.this, MainActivity.class));
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

    public void createPdf() throws FileNotFoundException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        String timeStamp = dateFormat.format(new Date());
        String pdfFileName = "pdf_" + timeStamp + ".pdf";

        File file = new File(pdfPath, pdfFileName);

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        float columnWidth[] = {100f, 100f, 100f, 100f, 200f, 60f};

        Paragraph streetNoP = new Paragraph("Street No. : " + binding.etStreetNo.getText().toString());
        Paragraph addressP = new Paragraph("Address : " + binding.etAddress.getText().toString());
        Paragraph stateP = new Paragraph("State : " + binding.etState.getText().toString());
        Paragraph cityP = new Paragraph("City : " + binding.etCity.getText().toString());
        Paragraph pincodeP = new Paragraph("Pincode : " + binding.etPincode.getText().toString());
        Paragraph par1 = new Paragraph(" ");
        Paragraph par2 = new Paragraph(" ");

        document.add(streetNoP);
        document.add(addressP);
        document.add(cityP);
        document.add(stateP);
        document.add(pincodeP);
        document.add(par1);
        document.add(par2);


        List<SGUTable> sguList = dao.getSGUData();
        if (!sguList.isEmpty()) {

            Paragraph sguParagraph1 = new Paragraph("");
            Paragraph sguParagraph = new Paragraph("Glass : SGU");

            Table sguTable = new Table(columnWidth);

            sguTable.addCell(new Cell(1, 6).add(new Paragraph(sguList.get(0).getSgu_thickness() + "   " + sguList.get(0).getSgu_materialDetails())));

            sguTable.addCell("No");
            sguTable.addCell("Width");
            sguTable.addCell("Height");
            sguTable.addCell("Quantity");
            sguTable.addCell("Note");
            sguTable.addCell("Image");

            for (int i = 0; i < sguList.size(); i++) {
                sguTable.addCell(String.valueOf(sguList.get(i).getSgu_id()));
                sguTable.addCell(sguList.get(i).getSgu_glassWidth());
                sguTable.addCell(sguList.get(i).getSgu_glassHeight());
                sguTable.addCell(sguList.get(i).getSgu_quantity());
                sguTable.addCell(sguList.get(i).getSgu_note());

                if (sguList.get(i).getSgu_path() != null) {
                    File imgFile = new File(sguList.get(i).getSgu_path());
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] bitmapData = byteArrayOutputStream.toByteArray();

                    ImageData imageData = ImageDataFactory.create(bitmapData);
                    Image image = new Image(imageData);
                    image.setWidth(50);
                    image.setHeight(50);
                    sguTable.addCell(image);
                } else {
                    sguTable.addCell("-");
                }
            }
            document.add(sguParagraph1);
            document.add(sguParagraph);
            document.add(sguTable);
        }

        List<DGUTable> dguList = dao.getDGUData();
        if (!dguList.isEmpty()) {
            Paragraph dguParagraph1 = new Paragraph("");
            Paragraph dguParagraph = new Paragraph("Glass : DGU");

            Table dguTable = new Table(columnWidth);

            if (dguList.get(0).getDgu_gap().equals("0")) {
                dguGap = gapCategories[0];
            } else if (dguList.get(0).getDgu_gap().equals("1")) {
                dguGap = gapCategories[1];
            } else if (dguList.get(0).getDgu_gap().equals("2")) {
                dguGap = gapCategories[2];
            } else if (dguList.get(0).getDgu_gap().equals("3")) {
                dguGap = gapCategories[3];
            } else if (dguList.get(0).getDgu_gap().equals("4")) {
                dguGap = gapCategories[4];
            } else if (dguList.get(0).getDgu_gap().equals("5")) {
                dguGap = gapCategories[5];
            } else if (dguList.get(0).getDgu_gap().equals("6")) {
                dguGap = gapCategories[6];
            } else if (dguList.get(0).getDgu_gap().equals("7")) {
                dguGap = gapCategories[7];
            } else if (dguList.get(0).getDgu_gap().equals("8")) {
                dguGap = gapCategories[8];
            } else if (dguList.get(0).getDgu_gap().equals("9")) {
                dguGap = gapCategories[9];
            }

            dguTable.addCell(new Cell(1, 6).add(
                    new Paragraph(dguList.get(0).getDgu_glass_1()
                            + "   "
                            + dguGap
                            + "   "
                            + dguList.get(0).getDgu_glass_2())));

            dguTable.addCell("No");
            dguTable.addCell("Width");
            dguTable.addCell("Height");
            dguTable.addCell("Quantity");
            dguTable.addCell("Note");
            dguTable.addCell("Image");

            for (int i = 0; i < dguList.size(); i++) {
                dguTable.addCell(String.valueOf(dguList.get(i).getDgu_id()));
                dguTable.addCell(dguList.get(i).getDgu_glassWidth());
                dguTable.addCell(dguList.get(i).getDgu_glassHeight());
                dguTable.addCell(dguList.get(i).getDgu_quantity());
                dguTable.addCell(dguList.get(i).getDgu_note());

                if (dguList.get(i).getDgu_path() != null) {
                    File imgFile = new File(dguList.get(i).getDgu_path());
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] bitmapData = byteArrayOutputStream.toByteArray();

                    ImageData imageData = ImageDataFactory.create(bitmapData);
                    Image image = new Image(imageData);
                    image.setWidth(50);
                    image.setHeight(50);
                    dguTable.addCell(image);
                } else {
                    dguTable.addCell("-");
                }
            }

            document.add(dguParagraph1);
            document.add(dguParagraph);
            document.add(dguTable);
        }

        List<LaminatedDGUTable> lDguList = dao.getLDGUData();
        if (!lDguList.isEmpty()) {
            Paragraph lDguParagraph1 = new Paragraph("");
            Paragraph lDguParagraph = new Paragraph("Glass : Laminated DGU");
            Table lDguTable = new Table(columnWidth);

            if (lDguList.get(0).getLdgu_gap().equals("0")) {
                lDguGap = gapCategories[0];
            } else if (lDguList.get(0).getLdgu_gap().equals("1")) {
                lDguGap = gapCategories[1];
            } else if (lDguList.get(0).getLdgu_gap().equals("2")) {
                lDguGap = gapCategories[2];
            } else if (lDguList.get(0).getLdgu_gap().equals("3")) {
                lDguGap = gapCategories[3];
            } else if (lDguList.get(0).getLdgu_gap().equals("4")) {
                lDguGap = gapCategories[4];
            } else if (lDguList.get(0).getLdgu_gap().equals("5")) {
                lDguGap = gapCategories[5];
            } else if (lDguList.get(0).getLdgu_gap().equals("6")) {
                lDguGap = gapCategories[6];
            } else if (lDguList.get(0).getLdgu_gap().equals("7")) {
                lDguGap = gapCategories[7];
            } else if (lDguList.get(0).getLdgu_gap().equals("8")) {
                lDguGap = gapCategories[8];
            } else if (lDguList.get(0).getLdgu_gap().equals("9")) {
                lDguGap = gapCategories[9];
            }

            if (lDguList.get(0).getLdgu_pvb().equals("0")) {
                laminatedPVB = pvbCategories[0];
            } else if (lDguList.get(0).getLdgu_pvb().equals("1")) {
                laminatedPVB = pvbCategories[1];
            } else if (lDguList.get(0).getLdgu_pvb().equals("2")) {
                laminatedPVB = pvbCategories[2];
            } else if (lDguList.get(0).getLdgu_pvb().equals("3")) {
                laminatedPVB = pvbCategories[3];
            }

            lDguTable.addCell(new Cell(1, 6).add(
                    new Paragraph(lDguList.get(0).getLdgu_glass_1()
                            + "   "
                            + laminatedPVB
                            + "   "
                            + lDguList.get(0).getLdgu_glass_2()
                            + "   "
                            + lDguGap
                            + "   "
                            + lDguList.get(0).getLdgu_glass_3())));

            lDguTable.addCell("No");
            lDguTable.addCell("Width");
            lDguTable.addCell("Height");
            lDguTable.addCell("Quantity");
            lDguTable.addCell("Note");
            lDguTable.addCell("Image");
            for (int i = 0; i < lDguList.size(); i++) {

                lDguTable.addCell(String.valueOf(lDguList.get(i).getLdgu_id()));
                lDguTable.addCell(lDguList.get(i).getLdgu_glassWidth());
                lDguTable.addCell(lDguList.get(i).getLdgu_glassHeight());
                lDguTable.addCell(lDguList.get(i).getLdgu_quantity());
                lDguTable.addCell(lDguList.get(i).getLdgu_note());

                if (lDguList.get(i).getLdgu_path() != null) {
                    File imgFile = new File(lDguList.get(i).getLdgu_path());
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] bitmapData = byteArrayOutputStream.toByteArray();

                    ImageData imageData = ImageDataFactory.create(bitmapData);
                    Image image = new Image(imageData);
                    image.setWidth(50);
                    image.setHeight(50);
                    lDguTable.addCell(image);
                } else {
                    lDguTable.addCell("-");
                }
            }

            document.add(lDguParagraph1);
            document.add(lDguParagraph);
            document.add(lDguTable);
        }

        List<AnnealedTable> annealedList = dao.getAnnealedData();
        if (!annealedList.isEmpty()) {

            Paragraph annealedParagraph1 = new Paragraph("");
            Paragraph annealedParagraph = new Paragraph("Glass : Annealed");

            Table annealedTable = new Table(columnWidth);

            annealedTable.addCell(new Cell(1, 6).add(
                    new Paragraph(annealedList.get(0).getAnnealed_thickness()
                            + "   "
                            + annealedList.get(0).getAnnealed_materialDetails())));

            annealedTable.addCell("No");
            annealedTable.addCell("Width");
            annealedTable.addCell("Height");
            annealedTable.addCell("Quantity");
            annealedTable.addCell("Note");
            annealedTable.addCell("Image");
            for (int i = 0; i < annealedList.size(); i++) {

                annealedTable.addCell(String.valueOf(annealedList.get(i).getAnnealed_id()));
                annealedTable.addCell(annealedList.get(i).getAnnealed_glassWidth());
                annealedTable.addCell(annealedList.get(i).getAnnealed_glassHeight());
                annealedTable.addCell(annealedList.get(i).getAnnealed_quantity());
                annealedTable.addCell(annealedList.get(i).getAnnealed_note());

                if (annealedList.get(i).getAnnealed_path() != null) {
                    File imgFile = new File(annealedList.get(i).getAnnealed_path());
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] bitmapData = byteArrayOutputStream.toByteArray();

                    ImageData imageData = ImageDataFactory.create(bitmapData);
                    Image image = new Image(imageData);
                    image.setWidth(50);
                    image.setHeight(50);
                    annealedTable.addCell(image);
                } else {
                    annealedTable.addCell("-");
                }
            }

            document.add(annealedParagraph1);
            document.add(annealedParagraph);
            document.add(annealedTable);
        }

        List<LaminationTable> laminationList = dao.getLaminationData();
        if (!laminationList.isEmpty()) {

            Paragraph laminationParagraph1 = new Paragraph("");
            Paragraph laminationParagraph = new Paragraph("Glass : Lamination");

            if (laminationList.get(0).getLamination_pvb().equals("0")) {
                laminationPVB = pvbCategories[0];
            } else if (laminationList.get(0).getLamination_pvb().equals("1")) {
                laminationPVB = pvbCategories[1];
            } else if (laminationList.get(0).getLamination_pvb().equals("2")) {
                laminationPVB = pvbCategories[2];
            } else if (laminationList.get(0).getLamination_pvb().equals("3")) {
                laminationPVB = pvbCategories[3];
            }

            Table laminationTable = new Table(columnWidth);

            laminationTable.addCell(new Cell(1, 6).add(
                    new Paragraph(laminationList.get(0).getLamination_glass_1()
                            + "   "
                            + laminationPVB
                            + "   "
                            + laminationList.get(0).getLamination_glass_2())));

            laminationTable.addCell("No");
            laminationTable.addCell("Width");
            laminationTable.addCell("Height");
            laminationTable.addCell("Quantity");
            laminationTable.addCell("Note");
            laminationTable.addCell("Image");
            for (int i = 0; i < laminationList.size(); i++) {

                laminationTable.addCell(String.valueOf(laminationList.get(i).getLamination_id()));
                laminationTable.addCell(laminationList.get(i).getLamination_glassWidth());
                laminationTable.addCell(laminationList.get(i).getLamination_glassHeight());
                laminationTable.addCell(laminationList.get(i).getLamination_quantity());
                laminationTable.addCell(laminationList.get(i).getLamination_note());

                if (laminationList.get(i).getLamination_path() != null) {
                    File imgFile = new File(laminationList.get(i).getLamination_path());
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] bitmapData = byteArrayOutputStream.toByteArray();

                    ImageData imageData = ImageDataFactory.create(bitmapData);
                    Image image = new Image(imageData);
                    image.setWidth(50);
                    image.setHeight(50);
                    laminationTable.addCell(image);
                } else {
                    laminationTable.addCell("-");
                }
            }
            document.add(laminationParagraph1);
            document.add(laminationParagraph);
            document.add(laminationTable);
        }

        document.close();

        new shareFileWhatsaap(file).execute();

//        Toast.makeText(AddressActivity.this, "Pdf Created", Toast.LENGTH_SHORT).show();
    }

    public class shareFileWhatsaap extends AsyncTask<Void, Void, Void> {

        File file;

        public shareFileWhatsaap(File file) {
            this.file = file;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String toNumber = "+91 81410 03334"; // contains spaces.
                toNumber = toNumber.replace("+", "").replace(" ", "");

                Uri uri = FileProvider.getUriForFile(AddressActivity.this, AddressActivity.this.getPackageName() + ".provider", file);

                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("application/pdf");
                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sendIntent.setPackage("com.whatsapp");
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                startActivity(sendIntent);
                startActivityForResult(sendIntent, SHARE_CODE);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            dao.deleteSguData();
            dao.deleteDguData();
            dao.deleteLaminationData();
            dao.deleteAnnealedData();
            dao.deleteLDGUData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SHARE_CODE) {
            startActivity(new Intent(AddressActivity.this, OrderListActivity.class));
            finish();
        }
    }

    public RequestBody createPartFromString(String desString) {
        return RequestBody.create(MediaType.parse("text/plain"), desString);
    }


    private void showLoading() {
        binding.formProgress.setVisibility(View.VISIBLE);
        binding.formScroll.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.formProgress.setVisibility(View.GONE);
        binding.formScroll.setVisibility(View.VISIBLE);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(InquiryViewModel.class);
        viewModel.getCreatedUserObserver().observe(this, new Observer<InquiryData>() {
            @Override
            public void onChanged(InquiryData loginUser) {
                hideLoading();
                if (loginUser == null) {
                    Toast.makeText(AddressActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddressActivity.this, loginUser.getMsg(), Toast.LENGTH_SHORT).show();
                   /* dao.deleteSguData();
                    dao.deleteDguData();
                    dao.deleteLaminationData();
                    dao.deleteAnnealedData();
                    dao.deleteLDGUData();
                    startActivity(new Intent(AddressActivity.this, OrderListActivity.class));
                    finish();*/
                    try {
                        createPdf();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
            startActivity(new Intent(this, OrderListActivity.class));
            finish();
        }

    }
}
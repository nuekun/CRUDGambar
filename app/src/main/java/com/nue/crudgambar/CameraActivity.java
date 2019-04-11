package com.nue.crudgambar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {
    private EditText txtCamNama;
    private Button btnAmbil , btnUpload ;
    private ImageView gbrCamPriview;
    private CameraKitView cameraKitView;
    private Bitmap file ;
    private StorageReference mStorageRef;
    private DatabaseReference database;
    private ProgressDialog prolog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        txtCamNama = findViewById(R.id.txtCameraNama);
        btnAmbil = findViewById(R.id.btnCameraAmbil);
        btnUpload = findViewById(R.id.btnCameraUpload);
        cameraKitView = findViewById(R.id.camera);
        gbrCamPriview = findViewById(R.id.gbrCameraPriview);
        prolog = new ProgressDialog(CameraActivity.this);


        mStorageRef = FirebaseStorage.getInstance().getReference();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!TextUtils.isEmpty(txtCamNama.getText().toString())){
                    prolog.setTitle("upload");
                    prolog.setMessage("mengupload gambar,  harap tunggu!....");
                    prolog.setCanceledOnTouchOutside(false);
                    prolog.show();

                    Bitmap bitmap =  getFile();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final String waktu = new SimpleDateFormat("yyyy-MM-dd:HH.mm.ss", Locale.getDefault()).format(new Date());

                    byte[] data = baos.toByteArray();
                    final StorageReference riversRef = mStorageRef.child("gambar").child(waktu+".jpg");
                    UploadTask uploadTask = riversRef.putBytes(data);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return riversRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                database = FirebaseDatabase.getInstance().getReference().child("gambar");

                                HashMap<String, String> foto = new HashMap<>();

                                foto.put("url", downloadUri.toString());
                                foto.put("waktu", waktu);
                                foto.put("nama", txtCamNama.getText().toString());
                                database.push().setValue(foto);
                                prolog.dismiss();
                                Intent main = new Intent(CameraActivity.this, MainActivity.class);
                                startActivity(main);

                            } else {
                                // Handle failures
                                // ...
                                prolog.dismiss();
                            }
                        }
                    });

                }
            }
        });

        btnAmbil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, byte[] capturedImage) {

                        Bitmap bitmap = BitmapFactory.decodeByteArray(capturedImage , 0, capturedImage.length);
                        setFile(bitmap);
                        gbrCamPriview.setImageBitmap(bitmap);
                        cameraKitView.setVisibility(View.INVISIBLE);
                        btnAmbil.setVisibility(View.INVISIBLE);
                        btnUpload.setVisibility(View.VISIBLE);
                        gbrCamPriview.setVisibility(View.VISIBLE);
                        txtCamNama.setVisibility(View.VISIBLE);


                    }
                });

            }
        });



    }
    public Bitmap getFile() {
        return file;
    }

    public void setFile(Bitmap file) {
        this.file = file;
    }
    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}


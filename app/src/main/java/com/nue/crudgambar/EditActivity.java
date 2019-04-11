package com.nue.crudgambar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {
    private EditText txtEditNama;
    private TextView txtNama , txtTanggal;
    private Button btnHapus , btnEditNama , btnEditGambar , btnKonfirmNama , btnKomfirmGambar;
    private ImageView gambar ;
    private CameraKitView camera;
    private Bitmap file ;
    private StorageReference mStorageRef;
    private ProgressDialog prolog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        Intent intent = getIntent();
        String IDgambar = intent.getStringExtra("IDgambar");

        camera = findViewById(R.id.cameraEdit);
        txtEditNama = findViewById(R.id.txtEditUbahNama);
        txtNama = findViewById(R.id.txtEditNama);
        txtTanggal = findViewById(R.id.txtEditTanggal);
        btnEditGambar = findViewById(R.id.btnEditGambar);
        btnEditNama = findViewById(R.id.btnEditNama);
        btnHapus = findViewById(R.id.btnEditHapus);
        gambar = findViewById(R.id.gbrEditGambar);
        btnKonfirmNama = findViewById(R.id.btnEditKonfirmNama);
        btnKomfirmGambar = findViewById(R.id.btnEditKonfirmGambar);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        prolog = new ProgressDialog(EditActivity.this);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference datagambar = database.getReference
                ("gambar/" + IDgambar);

        datagambar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    // code if data exists
                    String nama = dataSnapshot.child("nama").getValue().toString();
                    String waktu = dataSnapshot.child("waktu").getValue().toString();
                    String gambarUrl = dataSnapshot.child("url").getValue().toString();

                    txtNama.setText(nama);
                    txtTanggal.setText(waktu);
                    Picasso.with(EditActivity.this).load(gambarUrl).into(gambar);
                } else {
                    // code if data does not  exists
                    Intent main = new Intent(EditActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prolog.setTitle("menghapus");
                prolog.setMessage("menghapus gambar,  harap tunggu!....");
                prolog.setCanceledOnTouchOutside(false);
                prolog.show();
                mStorageRef.child("gambar").child(txtTanggal.getText().toString()+".jpg").delete();
                datagambar.removeValue();
                prolog.dismiss();


            }
        });

        btnEditNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtNama.setVisibility(View.INVISIBLE);
                txtEditNama.setVisibility(View.VISIBLE);
                btnEditNama.setVisibility(View.INVISIBLE);
                btnKonfirmNama.setVisibility(View.VISIBLE);

            }
        });

        btnKonfirmNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gantiNama = txtEditNama.getText().toString();
                txtNama.setText(gantiNama);
                txtEditNama.setVisibility(View.INVISIBLE);
                txtNama.setVisibility(View.VISIBLE);
                datagambar.child("nama").setValue(gantiNama);
                btnKonfirmNama.setVisibility(View.INVISIBLE);
                btnEditNama.setVisibility(View.VISIBLE);

            }
        });

        btnEditGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gambar.setVisibility(View.INVISIBLE);
                camera.setVisibility(View.VISIBLE);
                btnEditGambar.setVisibility(View.INVISIBLE);
                btnKomfirmGambar.setVisibility(View.VISIBLE);




            }
        });

        btnKomfirmGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                camera.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, byte[] capturedImage) {

                        prolog.setTitle("sabar bos !");
                        prolog.setMessage("update gambar,  harap tunggu!....");
                        prolog.setCanceledOnTouchOutside(false);
                        prolog.show();

                        Bitmap bitmap = BitmapFactory.decodeByteArray(capturedImage , 0, capturedImage.length);
                        setFile(bitmap);
                        gambar.setImageBitmap(bitmap);
                        cameraKitView.setVisibility(View.INVISIBLE);
                        gambar.setVisibility(View.VISIBLE);
                        camera.setVisibility(View.INVISIBLE);
                        btnKomfirmGambar.setVisibility(View.INVISIBLE);
                        btnEditGambar.setVisibility(View.VISIBLE);


                        Bitmap upload =  getFile();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        upload.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                                    String url = downloadUri.toString();

                                    datagambar.child("url").setValue(url);
                                    datagambar.child("waktu").setValue(waktu);
                                    mStorageRef.child("gambar").child(txtTanggal.getText().toString()+".jpg").delete();


                                    prolog.dismiss();

                                } else {
                                    // Handle failures
                                    // ...
                                    prolog.dismiss();
                                }
                            }
                        });








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
        camera.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.onResume();
    }

    @Override
    protected void onPause() {
        camera.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        camera.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        camera.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




}

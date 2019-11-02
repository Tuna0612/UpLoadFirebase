package com.tuna.uploadfirebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.tuna.uploadfirebase.model.UpLoad;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private CircleImageView imgChooseImage;
    private EditText edName;
    private EditText edDiaChi;
    private EditText edDienTich;
    private EditText edGia;
    private EditText edPhone;
    private EditText edDescription;
    private Button btnUpLoad, btnList;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mStorageRef = FirebaseStorage.getInstance().getReference("homestays");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("homestays");
        imgChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnUpLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(MainActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListUploadActivity.class));
            }
        });
    }

    private void uploadFile() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        final StorageReference imgName = mStorageRef.child("image" + mImageUri.getLastPathSegment());

        imgName.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("homestays");

                        UpLoad upload = new UpLoad(String.valueOf(uri),
                                edName.getText().toString(),
                                edDiaChi.getText().toString(),
                                edDescription.getText().toString(),
                                edPhone.getText().toString(),
                                Double.parseDouble(edGia.getText().toString()),
                                Double.parseDouble(edDienTich.getText().toString()));

                        String uploadId = mDatabaseRef.push().getKey();
                        mDatabaseRef.child(uploadId).setValue(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, ListUploadActivity.class));
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }
        });


    }

    private void initView() {
        imgChooseImage = (CircleImageView) findViewById(R.id.imgChooseImage);
        edName = (EditText) findViewById(R.id.edName);
        edDiaChi = (EditText) findViewById(R.id.edDiaChi);
        edDienTich = (EditText) findViewById(R.id.edDienTich);
        edGia = (EditText) findViewById(R.id.edGia);
        edPhone = (EditText) findViewById(R.id.edPhone);
        edDescription = (EditText) findViewById(R.id.edDescription);
        btnUpLoad = (Button) findViewById(R.id.btnUpLoad);
        btnList = (Button) findViewById(R.id.btnList);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null & data.getData() != null) {
            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(imgChooseImage);
            Log.d("IMAGEEE", data.getData().toString());
        }

    }


}

package com.tuna.uploadfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tuna.uploadfirebase.adapter.UpLoadAdapter;
import com.tuna.uploadfirebase.model.UpLoad;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListUploadActivity extends AppCompatActivity {
    private RecyclerView rcView;
    private UpLoadAdapter adapter;
    private List<UpLoad> upLoadList;
    private CircleImageView imgChooseImage;
    private EditText edName;
    private EditText edDiaChi;
    private EditText edDienTich;
    private EditText edGia;
    private EditText edPhone;
    private EditText edDescription;
    private Button btnUpdate, btnList;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_upload);
        rcView = findViewById(R.id.rcViewList);
        rcView.setHasFixedSize(true);
        rcView.setLayoutManager(new GridLayoutManager(this, 2));

        upLoadList = new ArrayList<>();
        mStorageRef = FirebaseStorage.getInstance().getReference("homestays");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("homestays");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UpLoad upLoad = postSnapshot.getValue(UpLoad.class);
                    upLoadList.add(upLoad);
                }
                adapter = new UpLoadAdapter(ListUploadActivity.this, upLoadList, new UpLoadAdapter.AdapterListener() {
                    @Override
                    public void OnClick(final int position) {
                        View view = LayoutInflater.from(ListUploadActivity.this).inflate(R.layout.activity_main, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListUploadActivity.this);
                        builder.setView(view);
                        builder.setTitle("Sửa Homestay");

                        imgChooseImage = view.findViewById(R.id.imgChooseImage);
                        edName = view.findViewById(R.id.edName);
                        edPhone = view.findViewById(R.id.edPhone);
                        edDiaChi = view.findViewById(R.id.edDiaChi);
                        edDescription = view.findViewById(R.id.edDescription);
                        edDienTich = view.findViewById(R.id.edDienTich);
                        edGia = view.findViewById(R.id.edGia);
                        btnUpdate = view.findViewById(R.id.btnUpLoad);

                        edName.setText(upLoadList.get(position).getTitle());
                        edPhone.setText(upLoadList.get(position).getPhone());
                        edDiaChi.setText(upLoadList.get(position).getAddress());
                        edDescription.setText(upLoadList.get(position).getDescription());
                        edDienTich.setText(String.valueOf(upLoadList.get(position).getArearoom()));
                        edGia.setText(String.valueOf(upLoadList.get(position).getPrice()));
                        Glide.with(ListUploadActivity.this).load(upLoadList.get(position).getImages()).into(imgChooseImage);

                        imgChooseImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openFileChooser();
                            }
                        });

                        btnUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadFile(position);
                            }
                        });
                        Dialog dialog = builder.create();
                        dialog.show();
                    }
                });

                rcView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListUploadActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void uploadFile(final int positon) {
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
                        mDatabaseRef.child("homestays").setValue(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ListUploadActivity.this, "OK", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ListUploadActivity.this, ListUploadActivity.class));
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
}

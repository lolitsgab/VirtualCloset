package com.example.virtualcloset;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class CameraActivity extends AppCompatActivity {
    private CameraKitView cameraKitView;
    private ImageView captureButton;
    private ImageView galleryButton;
    private ImageView imageView;
    private ImageView flashButton;
    private ImageView acceptButton;
    private ImageView cancelButton;
    private ImageView exitCameraButton;
    private StorageReference storageRef;
    private String UserUID;
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Create a storage reference from our app
        storageRef = FirebaseStorage.getInstance().getReference();

        // Get a unique identifier for the currently logged in user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserUID = user.getUid();

        // Initialize all buttons
        cameraKitView = this.findViewById(R.id.camera);
        captureButton = this.findViewById(R.id.cameraButton);
        flashButton = this.findViewById(R.id.flashButton);
        imageView = this.findViewById(R.id.capturedView);
        galleryButton = this.findViewById(R.id.galleryButton);
        cancelButton = this.findViewById(R.id.cancelImage);
        acceptButton = this.findViewById(R.id.acceptImage);
        exitCameraButton = this.findViewById(R.id.exitCamera);

        // Set handlers for all buttons
        captureButton.setOnClickListener(captureListener);
        galleryButton.setOnClickListener(galleryListener);
        cancelButton.setOnClickListener(cancelImageViewListener);
        acceptButton.setOnClickListener(acceptListener);

        exitCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraKitView.getFlash() == CameraKit.FLASH_ON) {
                    cameraKitView.setFlash(CameraKit.FLASH_OFF);
                    flashButton.clearColorFilter();
                } else {
                    cameraKitView.setFlash(CameraKit.FLASH_ON);
                    flashButton.setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                }
            }
        });
    }

    private View.OnClickListener galleryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    };

    private View.OnClickListener cancelImageViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            imageView.setBackground(null);

            // Remove the ACCEPT and CANCEL buttons into the view.
            imageView.setVisibility(View.GONE);
            acceptButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);

            // Display the gallery, capture, and flash buttons from the view.
            captureButton.setVisibility(View.VISIBLE);
            galleryButton.setVisibility(View.VISIBLE);
            flashButton.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener acceptListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Create the path we want to upload our file to.
            // @TODO(Gabriel): Integrate this with Cloud Vision to dynamically get the clothing type
            // {shirt or pants or shoes or etc...}.
            String uniqueImageName = UUID.randomUUID().toString();
            String savePath = "users/" + UserUID + "/clothes/shirts/" + uniqueImageName;

            // Put image from out ImageView into a bitmap.
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            // Convert the bitmap into a ByteStream and compress into a JPEG (as a byte array).
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] data = outputStream.toByteArray();
            startUpload(new ByteArrayInputStream(data), savePath);
        }
    };

    private View.OnClickListener captureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView cameraKitView, byte[] captured) {
                    // Convert captured image from a byte[] to a BitMap, and set to our ImageView.
                    Bitmap bmp = BitmapFactory.decodeByteArray(captured, 0, captured.length);
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(),
                            imageView.getHeight(), false));

                    // Remove the gallery, capture, and flash buttons from the view.
                    captureButton.setVisibility(View.GONE);
                    galleryButton.setVisibility(View.GONE);
                    flashButton.setVisibility(View.GONE);

                    // Display the ACCEPT and CANCEL buttons into the view.
                    imageView.setVisibility(View.VISIBLE);
                    acceptButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);
                }
            });
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Unable to retrieve image.", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                String uniqueImageName = UUID.randomUUID().toString();
                String savePath = ("users/" + UserUID + "/clothes/shirts/" + uniqueImageName);
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                startUpload(inputStream, savePath);
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Unable to upload image", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    public void startUpload(InputStream inputStream, String savePath) {
        // Start uploading, and set listeners to treat a successful/failed upload.
        StorageReference uploadRef = storageRef.child(savePath);
        UploadTask uploadTask = uploadRef.putStream(inputStream);
        final ProgressBar progressBar = CameraActivity.this.findViewById(R.id.uploadProgress);
        progressBar.setVisibility(View.VISIBLE);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(CameraActivity.this, "Failed to upload image.",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CameraActivity.this, "Upload Successful!",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
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

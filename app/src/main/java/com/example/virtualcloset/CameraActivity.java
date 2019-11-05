package com.example.virtualcloset;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CameraActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;
    private CameraKitView cameraKitView;
    private ImageView captureButton;
    private ImageView galleryButton;
    private DrawableImageView imageView;
    private ImageView flashButton;
    private ImageView acceptButton;
    private ImageView cancelButton;
    private ImageView exitCameraButton;
    private TextView drawMode;
    private StorageReference storageRef;
    private String UserUID;
    private Animation animRotate;
    private ObjectAnimator CHANGE_MODE;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.galleryButton:
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                    break;
                case R.id.cancelImage:
                    imageView.setBackground(null);

                    // Remove the ACCEPT and CANCEL buttons into the view.
                    imageView.setVisibility(View.GONE);
                    acceptButton.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.GONE);
                    drawMode.setVisibility(View.GONE);

                    // Display the gallery, capture, and flash buttons from the view.
                    captureButton.setVisibility(View.VISIBLE);
                    galleryButton.setVisibility(View.VISIBLE);
                    flashButton.setVisibility(View.VISIBLE);
                    break;
                case R.id.acceptImage:
                    // Get image out of ImageView into a bitmap.
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    labelAndUpload(bitmap);
                    break;
                case R.id.cameraButton:
                    cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                        @Override
                        public void onImage(CameraKitView cameraKitView, byte[] captured) {
                            // Convert captured image from a byte[] to a BitMap, and set to our ImageView.
                            Bitmap bmp = BitmapFactory.decodeByteArray(captured, 0, captured.length);
                            loadIntoDrawableImageView(bmp);
                        }
                    });
                    break;
                case R.id.flashButton:
                    if (cameraKitView.getFlash() == CameraKit.FLASH_ON) {
                        cameraKitView.setFlash(CameraKit.FLASH_OFF);
                        flashButton.clearColorFilter();
                    } else {
                        cameraKitView.setFlash(CameraKit.FLASH_ON);
                        flashButton.setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                    }
                    break;
                case R.id.exitCamera:
                    startActivity(new Intent(CameraActivity.this, MainActivity.class));
                    finish();
                    break;
                case R.id.drawMode:
                    drawMode.startAnimation(animRotate);

                    // If the drawMode button is set to the FG color, then we want the opposite.
                    // This applies for the opposite too.
                    if (drawMode.getCurrentTextColor() != Color.parseColor("#1de9b6")) {
                        drawMode.setText("FG");
                        CHANGE_MODE.reverse();
                        imageView.paint.setColor(getResources().getColor(R.color.FG_MODE));
                    } else {
                        drawMode.setText("BG");
                        CHANGE_MODE.start();
                        imageView.paint.setColor(getResources().getColor(R.color.BG_MODE));
                    }
                    break;
            }
        }
    };

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
        (cameraKitView = this.findViewById(R.id.camera)).setOnClickListener(onClickListener);
        (captureButton = this.findViewById(R.id.cameraButton)).setOnClickListener(onClickListener);
        (flashButton = this.findViewById(R.id.flashButton)).setOnClickListener(onClickListener);
        (galleryButton = this.findViewById(R.id.galleryButton)).setOnClickListener(onClickListener);
        (cancelButton = this.findViewById(R.id.cancelImage)).setOnClickListener(onClickListener);
        (acceptButton = this.findViewById(R.id.acceptImage)).setOnClickListener(onClickListener);
        (exitCameraButton = this.findViewById(R.id.exitCamera)).setOnClickListener(onClickListener);
        (drawMode = this.findViewById(R.id.drawMode)).setOnClickListener(onClickListener);
        imageView = this.findViewById(R.id.capturedView);

        animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        CHANGE_MODE = ObjectAnimator.ofInt(drawMode, "textColor",
                Color.parseColor("#1de9b6"), Color.parseColor("#c62828"));
        CHANGE_MODE.setEvaluator(new ArgbEvaluator());
    }

    private void loadIntoDrawableImageView(Bitmap bmp) {
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(),
                imageView.getHeight(), false));

        // Create a bitmap to store the drawn-on result
        Bitmap alteredBitmap = Bitmap.createBitmap(bmp.getWidth(),
                bmp.getHeight(), bmp.getConfig());
        imageView.setNewImage(alteredBitmap, bmp);


        // Remove the gallery, capture, and flash buttons from the view.
        captureButton.setVisibility(View.GONE);
        galleryButton.setVisibility(View.GONE);
        flashButton.setVisibility(View.GONE);

        // Display the ACCEPT and CANCEL buttons into the view.
        imageView.setVisibility(View.VISIBLE);
        imageView.paint.setColor(drawMode.getCurrentTextColor() == Color.parseColor("#1de9b6") ?
                getResources().getColor(R.color.FG_MODE) :
                getResources().getColor(R.color.BG_MODE));
        drawMode.setVisibility(View.VISIBLE);
        acceptButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // This is where we arrive from the Gallery activity (part of Android). Make sure that the
        // user picked and image and that there was no error.
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Unable to retrieve image.", Toast.LENGTH_LONG).show();
            } else {
                try {
                    // If we got an image, display it in our "View Finder" imageView.
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    loadIntoDrawableImageView(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    public void labelAndUpload(final Bitmap bitmap) {
        // Label here
        FirebaseVisionImage imageToLabel = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                .getCloudImageLabeler();
        labeler.processImage(imageToLabel)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        // Possible labels
                        Set<String> bottom_labels = new HashSet<>(Arrays.asList("trousers", "pants",
                                "skirt", "jeans", "sweatpants", "trunks", "khaki", "joggers", "shorts",
                                "skinny jeans", "mini skirt", "chinos", "cargo pants", "cargo shorts"));
                        Set<String> top_labels = new HashSet<>(Arrays.asList("t-shirt", "shirt",
                                "polo shirt", "polo", "dress shirt", "blouse",
                                "sleeveless shirt", "jacket", "sherpa", "sweatshirt", "windbreaker",
                                "sweater", "hoodie", "Blazer"));
                        String type = "";
                        String metaType = "";
                        for (FirebaseVisionImageLabel label : labels) {
                            metaType = label.getText();
                            if (top_labels.contains(metaType.toLowerCase())) {
                                type = "top";
                                break;
                            } else if (bottom_labels.contains(metaType.toLowerCase())) {
                                type = "bottom";
                                break;
                            }
                        }
                        if (type.length() == 0) {
                            Toast.makeText(CameraActivity.this, "Unrecognizable!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(CameraActivity.this, "Type: " + type +
                                "\nMeta: " + metaType, Toast.LENGTH_LONG).show();
                        upload(bitmap, type);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CameraActivity.this,
                                "Failed to label & upload!",
                                Toast.LENGTH_LONG).show();
                        Log.e("MYAPP", "exception", e);
                    }
                });
    }

    public void upload(Bitmap bitmap, String type) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
        byte[] data = baos.toByteArray();

        // Define where we will save the image
        String uniqueImageName = UUID.randomUUID().toString();
        String savePath = "users/" + UserUID + "/clothes/" + type + "/" + uniqueImageName;

        // Start uploading, and set listeners to treat a successful/failed upload.
        StorageReference uploadRef = storageRef.child(savePath);
        UploadTask uploadTask = uploadRef.putBytes(data);
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
                finish();
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

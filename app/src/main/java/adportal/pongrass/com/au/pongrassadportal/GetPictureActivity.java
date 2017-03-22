package adportal.pongrass.com.au.pongrassadportal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetPictureActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView mImageView;
    Button mOpenCameraButton;
    Uri mCurrentPhotoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_picture);

        // create the intent..
        mImageView = (ImageView)findViewById(R.id.imageView);

        mOpenCameraButton = (Button) findViewById(R.id.open_camera);
        mOpenCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });



    }

    /**
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
     **/




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            // rename the image..
            File path = new File(mCurrentPhotoPath);
            Bitmap bm = BitmapFactory.decodeFile(path.getAbsolutePath());
            // to 200 by 200..
            int new_x = bm.getWidth();
            int new_y = bm.getHeight();

            double scale = 1.0;
            if (new_x > new_y)
            {
                // width greater than height
                scale = (float)new_x / 400.0;
            }
            else {
                scale = (float)new_y / 400.0;
            }

            new_x = (int)((float)new_x / scale);
            new_y = (int)((float)new_y / scale);


            Bitmap smaller_bm = Bitmap.createScaledBitmap(bm, new_x, new_y, true);




            mImageView.setImageBitmap(smaller_bm);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {

                // upload the file...
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference rootRef = storage.getReferenceFromUrl(getString(R.string.storage_url));
                // get the user ID..
                StorageReference userStorageRef = rootRef.child(user.getUid());
                StorageReference userImageStorageRef = userStorageRef.child("images");
                StorageReference photoRef = userImageStorageRef.child(mCurrentPhotoName);

                    //InputStream stream = new FileInputStream(new File(mCurrentPhotoPath));

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                smaller_bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                UploadTask uploadTask = photoRef.putBytes(stream.toByteArray());
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + "% done");
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Upload is paused");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d("Upload Failed", exception.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        @SuppressWarnings("VisibleForTests")
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(getApplicationContext(), downloadUrl.getPath(), Toast.LENGTH_SHORT).show();
                    }
                });






            };

        }
    }

    String mCurrentPhotoPath;
    String mCurrentPhotoName;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        mCurrentPhotoName = imageFileName;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "adportal.pongrass.com.au.pongrassadportal.fileprovider",
                        photoFile);
                mCurrentPhotoURI = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
}

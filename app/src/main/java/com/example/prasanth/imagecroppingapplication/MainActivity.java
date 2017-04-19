package com.example.prasanth.imagecroppingapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button selectPhotoBtn;
    private AlertDialog.Builder alertDialog;
    private ImageView image;
    private String imagePath;
    private Uri selectedImage;
    private String imageName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.image);
        selectPhotoBtn = (Button) findViewById(R.id.selectPhotoBtn);
        selectPhotoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        selectPhoto();
    }

    private void selectPhoto() {
        final String[] dialogItems = {"Take Photo", "Upload Photo"};
        alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(getString(R.string.dialog_tile));
        alertDialog.setItems(dialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (dialogItems[item].equals(getString(R.string.dialog_takePhoto))) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File folderUrl = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
                    if (!folderUrl.exists()) {
                        folderUrl.mkdirs();
                    }

                    String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    imageName = timestamp + "_" + "image.jpg";
                    File imageUrl = new File(folderUrl, imageName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageUrl));
                    startActivityForResult(intent, 1);
                } else if (dialogItems[item].equals(getString(R.string.dialog_uploadPhoto))) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                File file = new File(Environment.getExternalStorageDirectory().toString());
                for (File f : file.listFiles()) {
                    if (f.getName().equals(getString(R.string.app_name))) {
                        file = f;
                        File url = new File(file, imageName);
                        imagePath = file.getAbsolutePath() + "/" + imageName;
                        selectedImage = Uri.fromFile(url);
                        break;
                    }
                }

                BitmapFactory.Options bitmapFactory = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bitmapFactory);
                image.setImageBitmap(bitmap);
                imageCropFunction();
            } else if (requestCode == 2) {
                selectedImage = data.getData();
                String[] filePaths = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePaths, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePaths[0]);
                String filePathss = cursor.getString(columnIndex);
                cursor.close();
                BitmapFactory.Options bitmapFactoryGallery = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(filePathss, bitmapFactoryGallery);
                image.setImageBitmap(bitmap);
                imageCropFunction();
            } else if (requestCode == 3) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                image.setImageBitmap(thePic);
            }
        }
    }

    private void imageCropFunction() {
        Intent viewMediaIntent = new Intent();
        viewMediaIntent.setAction("com.android.camera.action.CROP");
        viewMediaIntent.setDataAndType(selectedImage, "image/*");
        viewMediaIntent.putExtra("crop", "true");
        viewMediaIntent.putExtra("aspectX", 1);
        viewMediaIntent.putExtra("aspectY", 1);
        viewMediaIntent.putExtra("outputX", 256);
        viewMediaIntent.putExtra("outputY", 256);
        viewMediaIntent.putExtra("scaleUpIfNeeded", true);
        viewMediaIntent.putExtra("return-data", true);
        startActivityForResult(viewMediaIntent, 3);
    }
}




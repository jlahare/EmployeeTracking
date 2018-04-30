package com.yash.employeetrack;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;

/**
 * Created by vipin.jain on 4/18/2018.
 */
public class SignUpActivity extends AppCompatActivity {


    private static final int REQUEST_CAMERA = 200;
    private static final int SELECT_FILE = 201;
    public static String imagepath = null;

    Bitmap myBitmap;
    String  picUri;
    ImageView profile_pic;
    TextView image_picker;
    Button save;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        save = findViewById(R.id.signUp);
        image_picker = findViewById(R.id.image_picker);
        profile_pic = findViewById(R.id.profile_pic);
        image_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               // startActivityForResult(getPickImageChooserIntent(), 200);
                //showPopup();
                selectImage();
            }
        });
        permissions.add(CAMERA);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BeaconNotifier.show(SignUpActivity.this.getApplicationContext());
                startService();
            }
        });
        //startService();
    }

    PopupWindow popupWindow;
    private void showPopup()
    {

        LayoutInflater layoutInflater = (LayoutInflater) SignUpActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_layout,null);

        Button cameraBtn = (Button) customView.findViewById(R.id.cameraBtn);
        Button galleryBtn = (Button) customView.findViewById(R.id.galleryBtn);
        Button cancelBtn = (Button) customView.findViewById(R.id.cancelBtn);

        //instantiate popup window
        popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //display the popup window
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        //close the popup window on button click
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void selectImage()
    {
        final CharSequence[] items =
                { "Camera", "Choose from Gallery" };

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Select Picture");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera"))
                {
                    File getImage = getExternalCacheDir();
                    File file = new File(getImage.getPath(), "EmployeeProfile.png");

                   /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(intent, REQUEST_CAMERA);*/

                    PackageManager packageManager = getPackageManager();
                    Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);// size 2(Camera,youcamperfect)

                    List<Intent> allIntents = new ArrayList<>();
                    for (ResolveInfo res : listCam) {
                        Intent newIntent = new Intent(captureIntent);
                        newIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                        newIntent.setPackage(res.activityInfo.packageName);
                        newIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

                        allIntents.add(newIntent);
                    }

                    Intent mainIntent = allIntents.get(allIntents.size() - 1);
                    for (Intent nintent : allIntents) {
                        if (nintent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                            mainIntent = nintent;
                            break;
                        }
                    }
                    Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
                    // Add all other intents
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
                    startActivityForResult(chooserIntent, REQUEST_CAMERA);


                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                }
            }
        });
        builder.show();
    }


    public void startService() {
        startService(new Intent(getBaseContext(), Sender.class));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {

            if (requestCode == REQUEST_CAMERA)
            {
                File getImage = getExternalCacheDir();
                File file = new File(getImage.getPath(), "EmployeeProfile.png");
                saveBitmap(file.getPath());
            }
            else if (requestCode == SELECT_FILE)
            {
                Uri selectedImageUri = data.getData();
                String[] projection =
                        { MediaStore.MediaColumns.DATA };
                Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String bmpPath  = cursor.getString(column_index);
                saveBitmap(bmpPath);
            }
        }


    }
    public void saveBitmap(String filePath)
    {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(filePath);

        bitmap = getResizedBitmap(bitmap, Utils.THUMB_SIZE);

        CircleImageView croppedImageView = (CircleImageView) findViewById(R.id.profile_pic);
        croppedImageView.setImageBitmap(bitmap);
    }



    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {

                    } else
                    {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    public static String encodeFromString(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
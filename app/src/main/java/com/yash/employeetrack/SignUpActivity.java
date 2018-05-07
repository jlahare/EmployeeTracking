package com.yash.employeetrack;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.yash.employeetrack.http.JNetworkConstants;
import com.yash.employeetrack.http.JNetworkHandler;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * Created by vipin.jain on 4/18/2018.
 */
public class SignUpActivity extends AppCompatActivity {


    private static final int REQUEST_CAMERA = 200;
    private static final int SELECT_FILE = 201;

    ImageView profile_pic;
    Button save;
    private EditText firstName, lastName, businessUnit, empId, designation, emailId, phoneNo;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    String selectedGender;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Getting the bluetooth adapter object
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Checking if bluetooth is supported by device or not
       /* if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth Not Supported", Toast.LENGTH_LONG).show();
        } else {
            // if bluetooth is supported but not enabled then enable it
            if (!mBluetoothAdapter.isEnabled()) {
                Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                bluetoothIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(bluetoothIntent);
            } else {
                Toast.makeText(getApplicationContext(), "For proper working of this application bluetooth needs to TURN ON.", Toast.LENGTH_LONG).show();
            }
        }*/
        setContentView(R.layout.activity_signup);
        save = findViewById(R.id.signUp);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        emailId = findViewById(R.id.email);
        phoneNo = findViewById(R.id.contact_no);
        businessUnit = findViewById(R.id.business_unit);
        designation = findViewById(R.id.designation);
        empId = findViewById(R.id.emp_id);
        profile_pic = findViewById(R.id.profile_pic);

        final String[] gender = {"Male", "Female",};

        Spinner spin = (Spinner) findViewById(R.id.gender_spinner);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedGender = gender[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter aa = new ArrayAdapter(this, R.layout.spinner_item, R.id.textview, gender);
        aa.setDropDownViewResource(R.layout.spinner_item);
        spin.setAdapter(aa);


        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        permissions.add(CAMERA);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
       /* findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BeaconNotifier.show(SignUpActivity.this.getApplicationContext());
                startService();
            }
        });*/
        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Bluetooth Not Supported", Toast.LENGTH_LONG).show();
                } else {
                    // if bluetooth is supported but not enabled then enable it
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        bluetoothIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(bluetoothIntent);
                    }
                    /*else {
                        Toast.makeText(getApplicationContext(), "For proper working of this application bluetooth needs to TURN ON.", Toast.LENGTH_LONG).show();
                    }*/
                }
                if (Utils.base64Image == null) {
                    Toast.makeText(getApplicationContext(), "Please select profile pic", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(firstName.getText())) {
                    firstName.setError("First name is required!");
                } else if (TextUtils.isEmpty(lastName.getText())) {
                    lastName.setError("Last name is required!");
                } else if (TextUtils.isEmpty(emailId.getText())) {
                    emailId.setError("Email is required!");
                } else if (TextUtils.isEmpty(empId.getText())) {
                    empId.setError("Employee Id is required!");
                } else if (TextUtils.isEmpty(phoneNo.getText())) {
                    phoneNo.setError("Phone no is required!");
                } else if (TextUtils.isEmpty(businessUnit.getText())) {
                    businessUnit.setError("Business unit is required!");
                } else if (TextUtils.isEmpty(designation.getText())) {
                    designation.setError("Designation is required!");
                } else {
                    setSharedPrefernces();
                    sendToServer();

                }
            }
        });

        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //SignUpActivity.this.startService(new Intent(SignUpActivity.this.getApplicationContext(), Sender.class));
                aws = new AWSSubscriber(getApplicationContext());
                aws.connectToAws();

            }
        });

        findViewById(R.id.stopBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               // SignUpActivity.this.stopService(new Intent(SignUpActivity.this.getApplicationContext(), Sender.class));
                aws.disconnect();
            }
        });

        findViewById(R.id.testBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //SignUpActivity.this.startService(new Intent(SignUpActivity.this.getApplicationContext(), Sender.class));

                aws.sendMessage("bletracking" , "Test msg : " + (new Date()).toString());

            }
        });

        //startService();
    }
    AWSSubscriber aws;

    public void setSharedPrefernces() {

        sh_Pref = getSharedPreferences("Credentials", MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        toEdit.putString("businessUnit", getStr(R.id.business_unit));
        toEdit.putString("contactNo", getStr(R.id.contact_no));
        toEdit.putString("designation", getStr(R.id.designation));
        toEdit.putString("deviceId", Utils.getDeviceId(this));
        toEdit.putString("email", getStr(R.id.email));
        toEdit.putString("empId", getStr(R.id.emp_id));
        //  toEdit.putString("empPhoto", getStr(R.id.contact_no));
        toEdit.putString("firstName", getStr(R.id.first_name));
        toEdit.putString("gender", selectedGender);
        toEdit.putString("lastName", getStr(R.id.last_name));
        toEdit.commit();
    }


    public String getStr(int rid) {
        return ((EditText) findViewById(rid)).getText().toString();
    }


    public void getSharedPreferences() {
        sh_Pref = getSharedPreferences("Credentials", MODE_PRIVATE);
        sh_Pref.getString("businessUnit", "NA");
        sh_Pref.getString("contactNo", "NA");
        sh_Pref.getString("designation", "NA");
        sh_Pref.getString("deviceId", "NA");
        sh_Pref.getString("email", "NA");
        sh_Pref.getString("empId", "NA");
        sh_Pref.getString("firstName", "NA");
        sh_Pref.getString("gender", "NA");
        sh_Pref.getString("lastName", "NA");
    }


    private void sendToServer() {

        try {
            String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String url = JNetworkConstants.BASE_URL + JNetworkConstants.REGISTER_URL;

            JSONObject json = new JSONObject();
            json.put("businessUnit", getStr(R.id.business_unit));
            json.put("contactNo", getStr(R.id.contact_no));
            json.put("designation", getStr(R.id.designation));
            json.put("deviceId", Utils.getDeviceId(this));
            json.put("email", getStr(R.id.email));
            json.put("empId", Integer.parseInt(getStr(R.id.emp_id)));
            json.put("empPhoto", Utils.base64Image);
            json.put("firstName", getStr(R.id.first_name));
            json.put("gender", selectedGender);
            json.put("lastName", getStr(R.id.last_name));

            Log.i("JSON", json.toString());

            JNetworkHandler network = new JNetworkHandler(this, url, json.toString(), networkListener);
            network.execute("");
        } catch (Throwable throwable) {

        }
    }

    JNetworkHandler.NetworkListener networkListener = new JNetworkHandler.NetworkListener() {
        @Override
        public void onNetworkResponse(Pair<String, String> response) {
            Log.i("FIRST", response.first);
            Log.i("SECOND", response.second);
            if (response.first.equalsIgnoreCase(JNetworkConstants.NETWORK_SUCCESS)) {
                Toast.makeText(getApplicationContext(), "Employee Info updated successfully !!!", Toast.LENGTH_LONG).show();
                //START SERVICE.
                //SignUpActivity.this.startService(intent);
                SignUpActivity.this.startService(new Intent(SignUpActivity.this.getApplicationContext(), Sender.class));

                Intent i = new Intent(SignUpActivity.this, ThanksActivity.class);
                SignUpActivity.this.startActivity(i);

                SignUpActivity.this.finish();

            } else if (response.first.equalsIgnoreCase(JNetworkConstants.NETWORK_ERROR)) {
                Toast.makeText(getApplicationContext(), "Network Error : " + response.second + "\nPlease try again !!!", Toast.LENGTH_LONG).show();

            }
        }
    };

    private void selectImage() {
        final CharSequence[] items =
                {"Camera", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Select Picture");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                File getImage = getExternalCacheDir();
                File file = new File(getImage.getPath(), "EmployeeProfile.png");
                saveBitmap(file.getPath());
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection =
                        {MediaStore.MediaColumns.DATA};
                Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String bmpPath = cursor.getString(column_index);
                saveBitmap(bmpPath);
            }
        }


    }

    public void saveBitmap(String filePath) {


        BitmapFactory.Options options;
        Bitmap bitmap = null;

        try {


            String imageInSD = filePath;
            bitmap = BitmapFactory.decodeFile(imageInSD);

        } catch (Throwable t) {
            try {
                options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeFile(filePath, options);

            } catch(Throwable e) {
               Log.e("Error" , "Bitmao Error : " + e.toString());
            }
        }

        bitmap = getResizedBitmap(bitmap, Utils.THUMB_SIZE);

        CircleImageView croppedImageView = (CircleImageView) findViewById(R.id.profile_pic);
        croppedImageView.setImageBitmap(bitmap);

        Utils.byteBitmap = bitmap;
        ByteTask task = new ByteTask(SignUpActivity.this);
        task.execute("");
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

                    } else {
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

    private static Bitmap byteBitmap = null;

    public static String encodeFromString(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    class ByteTask extends AsyncTask<String, String, String> {

        //ProgressDialog dialog;
        Context context;


        public ByteTask(Context ctx) {

            // dialog = new ProgressDialog(ctx);
            // dialog.setMessage("Loading Image, Please wait..");
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                //   dialog.show();


            } catch (Throwable t) {
            }

        }


        @Override
        protected String doInBackground(String... strings) {
            try {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Utils.byteBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                baos.close();

                return Base64.encodeToString(b, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s == null) {
                    Toast.makeText(context, "Error converting image, try again !!!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Picture captured successfully !!!", Toast.LENGTH_LONG).show();
                }
                Utils.base64Image = s;
                //dialog.hide();
            } catch (Throwable t) {
            }
        }
    }

}
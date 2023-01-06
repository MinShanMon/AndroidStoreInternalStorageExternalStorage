package com.workshop.workshop1c;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    EditText mInputTxt;
    Button mSaveBtn;
    Button mReadBtn;

    File mTargetFile;

    // STEP 3. handle the permission
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputTxt = findViewById(R.id.inputTxt);

        //save btn to write
        mSaveBtn = findViewById(R.id.btnSave);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check whether this app has write external storage permission or not.
                //STEP 4 check permission is granted or not
                int writeExternalStoragePermission = ContextCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                //permission is granted write to file
                if (writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
                    writeToFile();
                }

                else {
                    // Not grant write external storage permission
                    // Request user to grant write external storage permission.
                    String [] permissions = new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };
                    //Step 2 Request for the dangerous permission in activity(permission)
                    ActivityCompat.requestPermissions(
                            MainActivity.this, permissions,
                            REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                }
            }
        });

        mReadBtn = findViewById(R.id.btnRead);
        mReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFromFile();
            }
        });

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            mSaveBtn.setEnabled(false);
        } else {
            //STEP 1 get a file object
            String filePath = "SampleFolder";
            String fileName = "SampleFile.txt";
//            App specific - Internal storage
            mTargetFile = new File(getFilesDir(), filePath + "/" + fileName);

//            App specific - External storage
//            mTargetFile = new File(getExternalFilesDir(filePath), fileName);

//            Public external storage
//            File publicPath = Environment.getExternalStorageDirectory();
//            mTargetFile = new File(publicPath, filePath + "/" + fileName);
        }
    }

    protected void writeToFile() {
        try {
            //STEP 2 access file to write
            // Make sure that the parent folder exists
            File parent = mTargetFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }

            // Write to file
            FileOutputStream fos = new FileOutputStream(mTargetFile);
            fos.write(mInputTxt.getText().toString().getBytes());
            fos.close();

            mInputTxt.setText("");

            Toast.makeText(this, "Write file ok!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void readFromFile() {
        String data = "";
        try {
            //Step 1 Read File
            //get file location
            FileInputStream fis = new FileInputStream(mTargetFile);
            //get data location from file
            DataInputStream in = new DataInputStream(fis);
            //reade each value
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            //set value in strLin
            String strLine;
            while ((strLine = br.readLine()) != null) {
                data = data + strLine;
            }
            in.close();

            mInputTxt.setText(data);

            Toast.makeText(this, "Read file ok!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //verify external storage available (read only)
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    //verify external storage available (read and write)
    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    // This method is invoked after user click buttons in permission grant popup dialog.
    // STEP 3. handle the permission(Permission)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //if permission is granted writ to file
                writeToFile();
            } else {
                //if permission is denied toast message
                Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
            }
        }
    }
}

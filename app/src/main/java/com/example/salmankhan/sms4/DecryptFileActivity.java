package com.example.salmankhan.sms4;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import SMS4.Exceptions.EmptyFileException;
import SMS4.Exceptions.InvalidFilePathException;
import SMS4.Exceptions.NullDataException;
import SMS4.Interfaces.Notifiable;
import SMS4.SMS4;
import SMS4.Utility.Converter;


public class DecryptFileActivity extends ActionBarActivity {

    Button btnSelectSourceFile;
    Button btnSelectDestFile;
    Button btnDecrypt;
    EditText etDecryptKey;

    private final int SOURCE_FILE_TAG = 110;
    private final int DEST_FILE_TAG = 120;

    private String SourceFilePath = null;
    private String DestFilePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt_file);

        btnSelectSourceFile = (Button)findViewById(R.id.btnSourceFileDecryption);
        btnSelectDestFile = (Button)findViewById(R.id.btnDestFileDecryption);
        btnDecrypt = (Button)findViewById(R.id.btnDecrypt);
        etDecryptKey = (EditText)findViewById(R.id.etDecryptKey);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {

            switch (requestCode) {
                case SOURCE_FILE_TAG:
                    SourceFilePath = data.getData().getPath();
                    break;

                case DEST_FILE_TAG:
                    DestFilePath = data.getData().getPath();
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClickSelectSourceFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, SOURCE_FILE_TAG);
    }

    public void onClickSelectDestinationFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, DEST_FILE_TAG);
    }

    public void onClickEncrypt(View view) {
        String plainDecryptionKey = etDecryptKey.getText().toString();
        String hashedKey = null;

        if (SourceFilePath == null || SourceFilePath.length() <= 0) {
            Toast.makeText(this, "Please select source file.", Toast.LENGTH_SHORT).show();
        } else if (DestFilePath == null || DestFilePath.length() <= 0) {
            Toast.makeText(this, "Please select destination file.", Toast.LENGTH_SHORT).show();
        } else if (plainDecryptionKey == null || plainDecryptionKey.length() <= 0) {
            Toast.makeText(this, "Please enter encryption key.", Toast.LENGTH_SHORT).show();
        } else {
            DecryptAsyncTask task = new DecryptAsyncTask();
            task.execute(SourceFilePath, DestFilePath, plainDecryptionKey);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_decrypt_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class DecryptAsyncTask extends AsyncTask<String, Void, Void>{

        ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(DecryptFileActivity.this);
            progressDialog.setMessage("Decrypting...Please wait!");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String sourceFilePos = params[0];
            String destFilesPos = params[1];
            String enKey = params[2];
            String hashedKey = null;
            //try {
                //  Hashes the string with SHA1 algorithm.
                //hashedKey = SHA1.hash(enKey);
                //hashedKey = Hashing.sha1().hashString(enKey, Charsets.UTF_8).toString();
            try {
                hashedKey = SHA1.hash(enKey);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //} catch (NoSuchAlgorithmException e) {
                //Toast.makeText(EncryptFileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            //} catch (UnsupportedEncodingException e) {
                //Toast.makeText(EncryptFileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            //}
            //  Converts the 40 character hex values to 20 bytes.
            //byte[] hashedBytes = new BigInteger(hashedKey, 16).toByteArray();

            byte[] hashedBytes = hexStringToByteArray(hashedKey);
            long[] hashedB = hexStringToIntArray(hashedKey);

            char[] hash = new char[hashedBytes.length];

            for(int i=0;i < hashedBytes.length;i++){
                hash[i]=(char)hashedBytes[i];
            }

            long[] encryptionKey = new long[4];
            int lastByte = 0;
            for (int i = 0; i < 4; i++) {
                long result = (hashedB[lastByte] << 24) | (hashedB[lastByte + 1] << 16) | (hashedB[lastByte+2] << 8) | (hashedB[lastByte+3]);
                encryptionKey[i] = result;
                lastByte +=4;
            }

            SMS4 algorithm = new SMS4();
            try {
                algorithm.decryptDataByPos(sourceFilePos, destFilesPos, encryptionKey);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
        }


    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byte b = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
            data[i / 2] = (byte) (b & 0xFF);
        }
        return data;
    }

    public static long[] hexStringToIntArray(String s) {
        int len = s.length();
        long[] data = new long[len / 2];
        for (int i = 0; i < len; i += 2) {
            int b = ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
            data[i / 2] = b & 0xFF;
        }
        return data;
    }

}

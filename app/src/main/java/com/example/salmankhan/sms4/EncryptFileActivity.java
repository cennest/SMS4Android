package com.example.salmankhan.sms4;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import SMS4.Interfaces.UrlReadable;
import SMS4.SMS4;
import SMS4.Utility.Converter;


public class EncryptFileActivity extends ActionBarActivity implements UrlReadable{

    Button btnSelectSourceFile;
    Button btnSelectDestFile;
    Button btnEncrypt;
    EditText etEncryptKey;

    private final int SOURCE_FILE_TAG = 110;
    private final int DEST_FILE_TAG = 120;

    private String SourceFilePath = null;
    private String DestFilePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSelectSourceFile = (Button)findViewById(R.id.btnSourceFile);
        btnSelectDestFile = (Button)findViewById(R.id.btnDestFile);
        btnEncrypt = (Button)findViewById(R.id.btnEncrypt);
        etEncryptKey = (EditText)findViewById(R.id.etEncryptKey);

//        SMS4 algo = new SMS4();
//        int[] encryptionKey = Utils.convertCharArrayToIntArray("salmankhangoogle".toCharArray());
////        algo.encryptData("test".getBytes(),dataArray, this);
////        byte[] charArray = algo.convertIntArrayToByteArray(dataArray);
//        try {
//            algo.encryptFileFromUrl("http://docutils.sourceforge.net/docs/user/rst/demo.txt",
//                    encryptionKey,"", this, new UrlReadable() {
//                        @Override
//                        public void onSuccess() {
//                            Log.d("","");
//                        }
//
//                        @Override
//                        public void onError(String error) {
//                            Log.d("","");
//                        }
//                    });
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

        String plainEncryptionKey = etEncryptKey.getText().toString();

        EncryptAsyncTask task = new EncryptAsyncTask();
        task.execute(SourceFilePath, DestFilePath, plainEncryptionKey);

        //String hashedKey = null;

        /*if (SourceFilePath == null || SourceFilePath.length() <= 0) {
            Toast.makeText(this, "Please select source file.", Toast.LENGTH_SHORT).show();
        } else if (DestFilePath == null || DestFilePath.length() <= 0) {
            Toast.makeText(this, "Please select destination file.", Toast.LENGTH_SHORT).show();
        } else if (plainEncryptionKey == null || plainEncryptionKey.length() <= 0) {
            Toast.makeText(this, "Please enter encryption key.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                //  Hashes the string with SHA1 algorithm.
                hashedKey = SHA1.hash(plainEncryptionKey);
            } catch (NoSuchAlgorithmException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            //  Converts the 40 character hex values to 20 bytes.
            byte[] hashedBytes = new BigInteger(hashedKey, 16).toByteArray();

            //  Converts the byte array to char array.
            char[] charArray = new String(hashedBytes).toCharArray();

            //  Releasing memory
            hashedBytes = null;
            hashedKey = null;
            plainEncryptionKey = null;

            //  Converts the char array to int array.
            int[] encryptionKey = Utils.convertCharArrayToIntArray(charArray);

            //  Releasing memory
            charArray = null;

            /*SMS4 algorithm = new SMS4();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Encrypting...Please wait!");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            //progressDialog.show();
            /*algorithm.encryptFile(SourceFilePath, encryptionKey, DestFilePath, new Notifiable() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                }

                @Override
                public void onErr(String message) {
                    Log.e("Error", message);
                }
            });*/

        //}
    }

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

            /*String filePath = data.getData().getPath();
            String enKey = null;
            try {
                enKey = SHA1("test123");
            } catch (NoSuchAlgorithmException e) {
                Log.e("", "");
            } catch (UnsupportedEncodingException e) {
                Log.e("", "");
            }

            //enKey = Hashing.sha1().hashString("test123", Charsets.UTF_8).toString();
            byte[] byteData = new BigInteger(enKey, 16).toByteArray();
            char[] chardatas = new String(byteData).toCharArray();

            int[] encryptionKey = convertCharArrayToIntArray(chardatas);
            //String dataString = getRealPathFromURI(this, myFile.getAbsolutePath());
            SMS4 algo = new SMS4();
            try {
                algo.encryptFile(filePath, encryptionKey);
            } catch (InvalidFilePathException e) {
                e.printStackTrace();
            } catch (EmptyFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullDataException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("", "");
            }*/
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(String error) {

    }

    class EncryptAsyncTask extends AsyncTask<String, Void, Void> {
        ProgressDialog progressDialog = null;


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EncryptFileActivity.this);
            progressDialog.setMessage("Encrypting...Please wait!");
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

            //  Hashes the string with SHA1 algorithm.
            try {
                hashedKey = SHA1.hash(enKey);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

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
                algorithm.encryptDataByPos(sourceFilePos, destFilesPos, encryptionKey);
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
}


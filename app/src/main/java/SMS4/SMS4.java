package SMS4;

import android.content.Context;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.apache.commons.lang.ArrayUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import SMS4.Constants.Constant;
import SMS4.Exceptions.EmptyFileException;
import SMS4.Exceptions.InvalidFilePathException;
import SMS4.Exceptions.NullDataException;
import SMS4.Interfaces.Notifiable;
import SMS4.Interfaces.UrlReadable;
import SMS4.Utility.Converter;
import SMS4.Utility.DataPadder;

/**
 * @author salmankhan
 *
 */
public class SMS4 {

    private final int READ_SIZE = 1; // 16 bytes
    //==============================================================================================
    //   PRIVATE METHODS FOR ALGORITHM
    //==============================================================================================
    /**
     * S-Box for bit shifting.
     */
    private short sbox[] = {
            0xd6, 0x90, 0xe9, 0xfe, 0xcc, 0xe1, 0x3d, 0xb7, 0x16, 0xb6, 0x14, 0xc2, 0x28, 0xfb, 0x2c, 0x05,
            0x2b, 0x67, 0x9a, 0x76, 0x2a, 0xbe, 0x04, 0xc3, 0xaa, 0x44, 0x13, 0x26, 0x49, 0x86, 0x06, 0x99,
            0x9c, 0x42, 0x50, 0xf4, 0x91, 0xef, 0x98, 0x7a, 0x33, 0x54, 0x0b, 0x43, 0xed, 0xcf, 0xac, 0x62,
            0xe4, 0xb3, 0x1c, 0xa9, 0xc9, 0x08, 0xe8, 0x95, 0x80, 0xdf, 0x94, 0xfa, 0x75, 0x8f, 0x3f, 0xa6,
            0x47, 0x07, 0xa7, 0xfc, 0xf3, 0x73, 0x17, 0xba, 0x83, 0x59, 0x3c, 0x19, 0xe6, 0x85, 0x4f, 0xa8,
            0x68, 0x6b, 0x81, 0xb2, 0x71, 0x64, 0xda, 0x8b, 0xf8, 0xeb, 0x0f, 0x4b, 0x70, 0x56, 0x9d, 0x35,
            0x1e, 0x24, 0x0e, 0x5e, 0x63, 0x58, 0xd1, 0xa2, 0x25, 0x22, 0x7c, 0x3b, 0x01, 0x21, 0x78, 0x87,
            0xd4, 0x00, 0x46, 0x57, 0x9f, 0xd3, 0x27, 0x52, 0x4c, 0x36, 0x02, 0xe7, 0xa0, 0xc4, 0xc8, 0x9e,
            0xea, 0xbf, 0x8a, 0xd2, 0x40, 0xc7, 0x38, 0xb5, 0xa3, 0xf7, 0xf2, 0xce, 0xf9, 0x61, 0x15, 0xa1,
            0xe0, 0xae, 0x5d, 0xa4, 0x9b, 0x34, 0x1a, 0x55, 0xad, 0x93, 0x32, 0x30, 0xf5, 0x8c, 0xb1, 0xe3,
            0x1d, 0xf6, 0xe2, 0x2e, 0x82, 0x66, 0xca, 0x60, 0xc0, 0x29, 0x23, 0xab, 0x0d, 0x53, 0x4e, 0x6f,
            0xd5, 0xdb, 0x37, 0x45, 0xde, 0xfd, 0x8e, 0x2f, 0x03, 0xff, 0x6a, 0x72, 0x6d, 0x6c, 0x5b, 0x51,
            0x8d, 0x1b, 0xaf, 0x92, 0xbb, 0xdd, 0xbc, 0x7f, 0x11, 0xd9, 0x5c, 0x41, 0x1f, 0x10, 0x5a, 0xd8,
            0x0a, 0xc1, 0x31, 0x88, 0xa5, 0xcd, 0x7b, 0xbd, 0x2d, 0x74, 0xd0, 0x12, 0xb8, 0xe5, 0xb4, 0xb0,
            0x89, 0x69, 0x97, 0x4a, 0x0c, 0x96, 0x77, 0x7e, 0x65, 0xb9, 0xf1, 0x09, 0xc5, 0x6e, 0xc6, 0x84,
            0x18, 0xf0, 0x7d, 0xec, 0x3a, 0xdc, 0x4d, 0x20, 0x79, 0xee, 0x5f, 0x3e, 0xd7, 0xcb, 0x39, 0x48
    };

    /**
     * System Key.
     */
    private int fk[] = {0xa3b1bac6, 0x56aa3350, 0x677d9197, 0xb27022dc};

    /**
     * Circular Key.
     */
    private int ck[] = {
            0x00070e15, 0x1c232a31, 0x383f464d, 0x545b6269,
            0x70777e85, 0x8c939aa1, 0xa8afb6bd, 0xc4cbd2d9,
            0xe0e7eef5, 0xfc030a11, 0x181f262d, 0x343b4249,
            0x50575e65, 0x6c737a81, 0x888f969d, 0xa4abb2b9,
            0xc0c7ced5, 0xdce3eaf1, 0xf8ff060d, 0x141b2229,
            0x30373e45, 0x4c535a61, 0x686f767d, 0x848b9299,
            0xa0a7aeb5, 0xbcc3cad1, 0xd8dfe6ed, 0xf4fb0209,
            0x10171e25, 0x2c333a41, 0x484f565d, 0x646b7279
    };


    private int rotateLeft(int x, int n) {
        return ((x << n) | x >>> (32 - n));
    }

    private long rotateLeft(long x, long n) {
        return ((x << n) | x >>> (32 - n));
    }

    private int ltrans(int b) {
        return b ^ (rotateLeft(b, 2)) ^ (rotateLeft(b, 10)) ^ (rotateLeft(b, 18)) ^ (rotateLeft(b, 24));
    }

    private long ltrans(long b) {
        return b ^ (rotateLeft(b, 2)) ^ (rotateLeft(b, 10)) ^ (rotateLeft(b, 18)) ^ (rotateLeft(b, 24));
    }

    private int keyLtrans(int b) {
        return (b ^ rotateLeft(b, 13) ^ rotateLeft(b, 23));
    }

    private long keyLtrans(long b) {
        return (b ^ rotateLeft(b, 13) ^ rotateLeft(b, 23));
    }

    private int substitute(int x) {
        return (sbox[(x >>> 24) & 0xff] << 24)
                ^ (sbox[(x >>> 16) & 0xff] << 16)
                ^ (sbox[(x >>> 8) & 0xff] << 8)
                ^ (sbox[(x) & 0xff]);
    }

    private long substitute(long x) {
        return (sbox[((int) ((x >>> 24) & 0xff))] << 24)
                ^ (sbox[((int) ((x >>> 16) & 0xff))] << 16)
                ^ (sbox[((int) ((x >>> 8) & 0xff))] << 8)
                ^ (sbox[((int) ((x) & 0xff))]);
    }

    private int ttrans(int x) {
        return ltrans(substitute(x));
    }

    private long ttrans(long x) {
        return ltrans(substitute(x));
    }

    private int keyTtrans(int x) {
        return keyLtrans(substitute(x));
    }

    private long keyTtrans(long x) {
        return keyLtrans(substitute(x));
    }

    /**
     * Process a word (128 bits).
     */
    private void processWord(int[] input, int[] rk, int index) {

        // Buffer array to store 36 values.
        int[] buf = new int[36];

        // Position Variables.
        int first = index;
        int second = first + 1;
        int third = second + 1;
        int forth = third + 1;

        //	Store values in buffer according to the supplied position.
        buf[0] = input[first];
        buf[1] = input[second];
        buf[2] = input[third];
        buf[3] = input[forth];

        //	Loops 32 times and stores the XOR expression after every 4 position.
        for (int position = 0; position < Constant.ROUND; position++) {
            buf[position + 4] = buf[position] ^ ttrans(buf[position + 1] ^ buf[position + 2] ^ buf[position + 3] ^ rk[position]);
        }

        //	Stores the reverse value.
        input[first] = buf[35];
        input[second] = buf[34];
        input[third] = buf[33];
        input[forth] = buf[32];
    }

    private void processWord(int[] input, long[] rk, int index) {

        // Buffer array to store 36 values.
        long[] buf = new long[36];

        // Position Variables.
        int first = index;
        int second = first + 1;
        int third = second + 1;
        int forth = third + 1;

        //	Store values in buffer according to the supplied position.
        buf[0] = input[first];
        buf[1] = input[second];
        buf[2] = input[third];
        buf[3] = input[forth];

        //	Loops 32 times and stores the XOR expression after every 4 position.
        for (int position = 0; position < Constant.ROUND; position++) {
            buf[position + 4] = buf[position] ^ ttrans(buf[position + 1] ^ buf[position + 2] ^ buf[position + 3] ^ rk[position]);
        }

        //	Stores the reverse value.
        input[first] = (int)buf[35];
        input[second] = (int)buf[34];
        input[third] = (int)buf[33];
        input[forth] = (int)buf[32];
    }

    /**
     * Calculates Round key.
     */
    private int[] calculateRoundKey(int[] mainKey) {

        // Round Key.
        int roundKey[] = {0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0
        };

        //	Buffer array.
        int[] key = new int[4];
        key[0] = mainKey[0];
        key[1] = mainKey[1];
        key[2] = mainKey[2];
        key[3] = mainKey[3];

        //	XORing the buffer array with system key.
        key[0] ^= fk[0];
        key[1] ^= fk[1];
        key[2] ^= fk[2];
        key[3] ^= fk[3];

        for (int i = 0; i < Constant.ROUND; i++) {
            roundKey[i] = key[0] ^ keyTtrans(key[1] ^ key[2] ^ key[3] ^ ck[i]);
            key[0] = key[1];
            key[1] = key[2];
            key[2] = key[3];
            key[3] = roundKey[i];
        }
        return roundKey;
    }

    private long[] calculateRoundKey(long[] mainKey) {

        // Round Key.
        long roundKey[] = {0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0
        };

        //	Buffer array.
        long[] key = new long[4];
        key[0] = mainKey[0];
        key[1] = mainKey[1];
        key[2] = mainKey[2];
        key[3] = mainKey[3];

        //	XORing the buffer array with system key.
        key[0] ^= fk[0];
        key[1] ^= fk[1];
        key[2] ^= fk[2];
        key[3] ^= fk[3];

        for (int i = 0; i < Constant.ROUND; i++) {
            roundKey[i] = key[0] ^ keyTtrans(key[1] ^ key[2] ^ key[3] ^ ck[i]);
            key[0] = key[1];
            key[1] = key[2];
            key[2] = key[3];
            key[3] = roundKey[i];
        }
        return roundKey;
    }

    /**
     * Encrypts data of type Integer array with a specific encryption key.
     *
     * @param    data    Data to be encrypted.
     * @param    encryptionKey    Key used to encryptData data.
     * @return Encrypted data in integer array format.
     */
    private int[] cipher(int[] data, int[] encryptionKey) {

        int rk[] = calculateRoundKey(encryptionKey);

        for (int i = 0; i < data.length; i = i + 4) {

            processWord(data, rk, i);
        }
        return data;
    }

    private int[] cipher(int[] data, long[] encryptionKey) {

        long rk[] = calculateRoundKey(encryptionKey);

        for (int i = 0; i < data.length; i = i + 4) {

            processWord(data, rk, i);
        }
        return data;
    }

    /**
     * Decrypts an encrypted data with a specific encryption key.
     *
     * @param encryptionKey Key for decrypting data.
     * @param    encryptedData    Encrypted data which needs to be decrypted.
     * @return Decrypted data in integer array format.
     */
    private int[] decipher(int[] encryptedData, int[] encryptionKey) {

        int rk[] = calculateRoundKey(encryptionKey);
        int drk[] = new int[Constant.ROUND];

        int wordLength = 4;
        int wordCount = encryptedData.length / wordLength;
        int wordMod = encryptedData.length % wordLength;
        wordCount += wordMod > 0 ? 1 : 0;

        int dataSize = wordCount * wordLength;
        int[] datBuff = new int[dataSize];

        System.arraycopy(encryptedData, 0, datBuff, 0, encryptedData.length);

        for (int position = 0; position < Constant.ROUND; position++) {
            drk[position] = rk[31 - position];
        }

        for (int index = 0; index < datBuff.length; index = index + 4) {
            processWord(datBuff, drk, index);
        }

        return datBuff;
    }

    private int[] decipher(int[] encryptedData, long[] encryptionKey) {

        long rk[] = calculateRoundKey(encryptionKey);
        long drk[] = new long[Constant.ROUND];

        int wordLength = 4;
        int wordCount = encryptedData.length / wordLength;
        int wordMod = encryptedData.length % wordLength;
        wordCount += wordMod > 0 ? 1 : 0;

        int dataSize = wordCount * wordLength;
        int[] datBuff = new int[dataSize];

        System.arraycopy(encryptedData, 0, datBuff, 0, encryptedData.length);

        for (int position = 0; position < Constant.ROUND; position++) {
            drk[position] = rk[31 - position];
        }

        for (int index = 0; index < datBuff.length; index = index + 4) {
            processWord(datBuff, drk, index);
        }

        return datBuff;
    }


    //==============================================================================================
    //   PUBLIC METHODS - Encryption
    //==============================================================================================

    /**
     * This method checks the last block of the data array and adds padding data if last block is less than 16 bits.
     * It saves the record of this padding data in a byte and appends it to the data after encrypting the data.
     *
     * @param    byteData    Data array in byte format.
     * @param    encryptionKey    Key to encrypt data.
     * @return Encrypted data with appended meta data.
     */
    public byte[] encryptData(byte[] byteData, int[] encryptionKey) {

        //	Number of data to pad.
        int noOfDataToPad = (int) DataPadder.dataToPad(byteData.length);

        //	Metadata to append in the encrypted data.
        byte metaData = (byte) noOfDataToPad;

        // Pad byte data.
        byte[] paddedData = DataPadder.padData(byteData);

        //	Convert byte data to int data.
        int[] dataForEncryption = Converter.convertByteArraytoIntArray(paddedData);

        //	Encrypt the data.
        int[] cipherData = cipher(dataForEncryption, encryptionKey);

        // Converts int data to byte data
        byte[] byteBuffer = Converter.convertIntArrayToByteArray(cipherData);
        byte[] encryptedData = new byte[byteBuffer.length + 1];
        System.arraycopy(byteBuffer, 0, encryptedData, 0, byteBuffer.length);

        int metaDataPosition = byteBuffer.length;

        //	Append Meta Data.
        encryptedData[metaDataPosition] = metaData;

        return encryptedData;
    }


    public void encryptData(byte[] data, int[] encryptionKey, String destinationPath) throws InvalidFilePathException, IOException, NullDataException {

        //  Validate Path
        validatePath(destinationPath);

        //  Encrypts the plain data.
        byte[] encryptedData = encryptData(data, encryptionKey);

        //  Writes the encrypted data to destination file.
        writeData(destinationPath, encryptedData);
    }

    public void encryptFile(String sourcePath, int[] encryptionKey, String destinationPath, Notifiable completion) {

        try {
            if (destinationPath == null) {
                destinationPath = sourcePath;
            }

            //  Validate Path
            validatePath(sourcePath, destinationPath);

            //  Read and save data from file.
            byte[] plainData = readData(sourcePath);

            //  Encrypt the plain data.
            byte[] encryptedData = encryptData(plainData, encryptionKey);

            //  Writes the encrypted data to destination file.
            writeData(destinationPath, encryptedData);

            completion.onSuccess();
        } catch (Exception e) {
            completion.onErr(e.getMessage());
        }
    }

    /*public void encryptFile(String sourcePath, int[] encryptionKey, Notifiable completion) throws InvalidFilePathException, EmptyFileException, IOException, NullDataException {

        try {
            //  Validate Path.
            validatePath(sourcePath);

            //  Read and save data from file.
            byte[] plainData = readData(sourcePath);

            //  Encrypt the plain data.
            byte[] encryptedData = encryptData(plainData, encryptionKey);

            //  Writes the encrypted data to destination file.
            writeData(sourcePath, encryptedData);

            completion.onSuccess();
        } catch (Exception e) {
            completion.onErr(e.getMessage());
        }
    }*/

    public byte[] encryptFile(String sourcePath, int[] encryptionKey) throws InvalidFilePathException, EmptyFileException, IOException, NullDataException {

        //  Validate Path.
        validatePath(sourcePath);

        //  Read and save data from file.
        byte[] plainData = readData(sourcePath);

        return encryptData(plainData, encryptionKey);
    }

    public void encryptData(byte[] data, int[] encryptionKey, String destinationPath, Notifiable completion) {

        try {
            //  Validate Path
            validatePath(destinationPath);

            byte[] encryptedData = encryptData(data, encryptionKey);

            //  Write Data to File.
            writeData(destinationPath, encryptedData);
            completion.onSuccess();
        } catch (Exception e) {
            completion.onErr(e.getMessage());
        }
    }

    public void encryptFileFromUrl(String downloadUrl, final int[] encryptionKey, final String destinationPath, Context context, final UrlReadable completion) throws IOException, InvalidFilePathException {
        Ion.with(context)
                .load(downloadUrl)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        byte[] data = result.getResult().getBytes();
                        try {
                            encryptData(data, encryptionKey, destinationPath);
                            completion.onSuccess();
                        } catch (Exception exception) {
                            completion.onError(exception.getMessage());
                        }
                    }
                });
    }

    /*public byte[] encryptData(byte[] byteData, int[] encryptionKey) {

        //TODO: RETURN & CALLBACK
        //	Number of data to pad.
        int noOfDataToPad = DataPadder.dataToPad(byteData);

        //	Metadata to append in the encrypted data.
        byte metaData = (byte)noOfDataToPad;

        // Pad byte data.
        byte[] paddedData = DataPadder.padData(byteData);

        //	Convert byte data to int data.
        int[] dataForEncryption = Converter.convertByteArraytoIntArray(paddedData);

        //	Encrypt the data.
        int[] cipherData = cipher(dataForEncryption, encryptionKey);

        // Converts int data to byte data
        byte[] byteBuffer = Converter.convertIntArrayToByteArray(cipherData);
        byte[] encryptedData = new byte[byteBuffer.length+1];
        System.arraycopy(byteBuffer, 0, encryptedData, 0, byteBuffer.length);

        int metaDataPosition = byteBuffer.length;

        //	Append Meta Data.
        encryptedData[metaDataPosition] = metaData;

        return encryptedData;
    }*/


    //==============================================================================================
    //   PUBLIC METHODS - Decryption
    //==============================================================================================


    /**
     * This method checks the metadata in the last block of the data array. It further removes the appended metadata
     * and interprets the number of byte to remove from the last block of data array and return the decrypted data.
     *
     * @param    encryptedData    Encrypted data in byte array format.
     * @param    encryptionKey    Key to decrypt data.
     * @return Returns the
     */
    public byte[] decryptData(byte[] encryptedData, int[] encryptionKey) {

        int metaDataPosition = encryptedData.length - 1;

        //	Gets metadata from the data.
        int metaData = (int) encryptedData[metaDataPosition];

        //	Removes metadata byte from encrypted data.
        byte[] plainEncryptedData = ArrayUtils.remove(encryptedData, metaDataPosition);

        encryptedData = null;

        //	Decrypts the data.
        byte[] decryptedData = Converter.convertIntArrayToByteArray(
                decipher(Converter.convertByteArraytoIntArray(plainEncryptedData), encryptionKey)
        );
        plainEncryptedData = null;
//        if (metaData > 0) {
//            return DataPadder.unPadData(decryptedData);
//        } else {
            return decryptedData;
        //}
    }

    public void decryptFile(String sourcePath, int[] encryptionKey, String destinationPath, Notifiable completion) {

        try {
            if (destinationPath == null) {
                destinationPath = sourcePath;
            }

            //  Validate Path
            validatePath(sourcePath, destinationPath);

            //  Read and save data from file.
            //byte[] encryptedData = readData(sourcePath);

            //  Decrypt the encrypted data.
            byte[] plainData = decryptData(readBufferData(sourcePath), encryptionKey);

            //encryptedData = null;
            //  Writes the plain data to destination file.
            writeData(destinationPath, plainData);

            completion.onSuccess();
        } catch (Exception e) {
            completion.onErr(e.getMessage());
        }
    }

    public void decryptFile(String sourcePath, int[] encryptionKey, Notifiable completion) {

        try {
            //  Validate Path
            validatePath(sourcePath);

            String destinationPath = sourcePath;

            //  Decrypt the encrypted data.
            decryptFile(sourcePath, encryptionKey, destinationPath, completion);
        } catch (Exception e) {
            completion.onErr(e.getMessage());
        }
    }

    public byte[] decryptFile(String sourcePath, int[] encryptionKey) throws InvalidFilePathException, IOException, EmptyFileException {

        //  Validate Path
        validatePath(sourcePath);

        //  Read and save data from file.
        byte[] encryptedData = readData(sourcePath);

        byte[] decryptedData = decryptData(encryptedData, encryptionKey);

        return decryptedData;
    }

    public void writeData(String filePath, byte[] data) throws IOException, NullDataException {

        if (data == null) {
            throw new NullDataException();
        }
        File file = new File(filePath);
        FileOutputStream outputStream = new FileOutputStream(file, false);
        outputStream.write(data);
        outputStream.flush();
        outputStream.close();
    }

    public byte[] readData(String filePath) throws IOException, EmptyFileException {

        File plainFile = new File(filePath);

        int fileLength = (int) plainFile.length();
        byte[] data = new byte[fileLength];

        FileInputStream fileInputStream = new FileInputStream(plainFile);
        int status = fileInputStream.read(data);

        if (status == 0) {
            throw new EmptyFileException();
        }
        fileInputStream.close();

        return data;
    }

    public byte[] readBufferData(String filePath) throws IOException, EmptyFileException {
        StringBuffer line = new StringBuffer();
        String data = null;
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while ((data = reader.readLine()) != null) {
            line.append(data);
        }
        return data.getBytes();
    }

    public void validatePath(String path) throws InvalidFilePathException {
        File file = new File(path);

        if (!file.exists()) {
            throw new InvalidFilePathException("Invalid file path.");
        } else if (!file.isFile()) {
            throw new InvalidFilePathException("Supplied path is not of type file.");
        }
    }

    public void validatePath(String sourcePath, String destinationPath) throws InvalidFilePathException {

        File sourceFile = new File(sourcePath);
        File destinationFile = new File(destinationPath);

        if (!sourceFile.exists()) {
            throw new InvalidFilePathException("Path for source file is not valid.");
        } else if (!sourceFile.isFile()) {
            throw new InvalidFilePathException("Supplied source path is not of type file.");
        } else if (!destinationFile.exists()) {
            throw new InvalidFilePathException("Path for destination path is not valid");
        } else if (!destinationFile.isFile()) {
            throw new InvalidFilePathException("Supplied destination path is not of type file.");
        }
    }

    public void encryptDataByPos(String sourceFile, String destinationFile, int[] encryptionKey) throws FileNotFoundException, IOException{

        File file = new File(sourceFile);
        InputStream inputStream = new FileInputStream(file);
        FileOutputStream outputStream = new FileOutputStream(destinationFile, true);

        int bytesToWrite = 2 * 1024; // 500 kb
        int fileLen = (int)file.length() - 1;
        int remainingLen = fileLen;

        for (int pos = 0; pos<fileLen; ) {
            if (remainingLen <= bytesToWrite) {
                bytesToWrite = remainingLen;
            }
            byte[] dataArray = new byte[bytesToWrite];
            int byteRead = inputStream.read(dataArray, 0, bytesToWrite);
            int[] data = Converter.convertByteArraytoIntArray(DataPadder.padData(dataArray));
            int[] enData = cipher(data, encryptionKey);
            data = null;
            outputStream.write(Converter.convertIntArrayToByteArray(enData));
            enData = null;
            pos += byteRead;
            remainingLen -= byteRead;
        }
        outputStream.write((int) DataPadder.dataToPad(file.length()));
        inputStream.close();
        outputStream.close();
    }

    public void encryptDataByPos(String sourceFile, String destinationFile, long[] encryptionKey) throws FileNotFoundException, IOException{

        File file = new File(sourceFile);
        InputStream inputStream = new FileInputStream(file);
        FileOutputStream outputStream = new FileOutputStream(destinationFile, true);

        int bytesToWrite = 2 * 1024; // 500 kb
        int fileLen = (int)file.length() - 1;
        int remainingLen = fileLen;

        for (int pos = 0; pos<fileLen; ) {
            if (remainingLen <= bytesToWrite) {
                bytesToWrite = remainingLen;
            }
            byte[] dataArray = new byte[bytesToWrite];
            int byteRead = inputStream.read(dataArray, 0, bytesToWrite);
            int[] data = Converter.convertByteArraytoIntArray(DataPadder.padData(dataArray));
            int[] enData = cipher(data, encryptionKey);
            data = null;
            outputStream.write(Converter.convertIntArrayToByteArray(enData));
            enData = null;
            pos += byteRead;
            remainingLen -= byteRead;
        }
        outputStream.write((int) DataPadder.dataToPad(file.length()));
        inputStream.close();
        outputStream.close();
    }

    public void decryptDataByPos(String sourceFile, String destinationFile, int[] encryptionKey) throws FileNotFoundException, IOException {

        File file = new File(sourceFile);
        InputStream inputStream = new FileInputStream(file);
        FileOutputStream outputStream = new FileOutputStream(destinationFile, true);

        int bytesToWrite = 2 * 1024; // 500 kb
        int fileLen = (int) file.length() - 17;
        int remainingLen = fileLen;

        for (int pos = 0; pos < fileLen; ) {
            if (remainingLen <= bytesToWrite) {
                bytesToWrite = remainingLen;
            }
            byte[] dataArray = new byte[bytesToWrite];
            int byteRead = inputStream.read(dataArray, 0, bytesToWrite);
            byte[] enData = Converter.convertIntArrayToByteArray(decipher(Converter.convertByteArraytoIntArray(dataArray), encryptionKey));
            outputStream.write(enData);
            enData = null;
            pos += byteRead;
            remainingLen -= byteRead;
        }
        byte[] remainData = new byte[17];
        int remainDataRead = inputStream.read(remainData, 0, remainData.length);


        byte[] decryptedData = Converter.convertIntArrayToByteArray(decipher(Converter.convertByteArraytoIntArray(remainData), encryptionKey));
        Log.e("ERROR", Arrays.toString(remainData));

        byte[] unPadData;
        if (remainData[16] > 0) {
            unPadData = DataPadder.unPadData(remainData);
        } else {
            unPadData = decryptedData;
        }

        //byte[] unpadData = DataPadder.unPadData(decryptedData);
        Log.v("INFO", Arrays.toString(unPadData));
        outputStream.write(unPadData);
        inputStream.close();
        outputStream.close();
    }

    public void decryptDataByPos(String sourceFile, String destinationFile, long[] encryptionKey) throws FileNotFoundException, IOException {

        File file = new File(sourceFile);
        InputStream inputStream = new FileInputStream(file);
        FileOutputStream outputStream = new FileOutputStream(destinationFile, true);

        int bytesToWrite = 2 * 1024; // 500 kb
        int fileLen = (int) file.length();
        int remainingLen = fileLen - 17;

        int abc = 0;
        for (int pos = 0; pos < fileLen; ) {
            if (remainingLen <= bytesToWrite) {
                bytesToWrite = remainingLen;

            }
            byte[] dataArray = new byte[bytesToWrite];
            int byteRead = inputStream.read(dataArray, 0, bytesToWrite);
            byte[] enData = Converter.convertIntArrayToByteArray(decipher(Converter.convertByteArraytoIntArray(dataArray), encryptionKey));
            outputStream.write(enData);
            enData = null;
            pos += byteRead;
            remainingLen -= byteRead;
            abc++;
        }
        byte[] remainData = new byte[17];
        int remainDataRead = inputStream.read(remainData, 0, remainData.length);


        byte[] decryptedData = Converter.convertIntArrayToByteArray(decipher(Converter.convertByteArraytoIntArray(remainData), encryptionKey));
        Log.e("ERROR", Arrays.toString(remainData));

        byte[] unPadData;
        if (remainData[16] > 0) {
            unPadData = DataPadder.unPadData(remainData);
        } else {
            unPadData = decryptedData;
        }

        //byte[] unpadData = DataPadder.unPadData(decryptedData);
        Log.v("INFO", Arrays.toString(unPadData));
        outputStream.write(unPadData);
        inputStream.close();
        outputStream.close();
    }
}

package SMS4.Utility;

import java.util.Arrays;

/**
 * Created by salmankhan on 4/14/15.
 */
public class DataPadder {


    /**
     *	This method returns the number of padding data to add in last block of data array.
     *
     *	@param : Data in byte array.
     *	@return: Number of padding data to add in last block of data array.
     */
    public static long dataToPad(long dataLength) {

        int wordSize = 4; //  bytes
        int blockLength = 4; // size
        int blockSize = wordSize * blockLength; // 4*4 bytes
        int wordMod = (int )dataLength % blockSize;

        if(wordMod > 0) {
            return blockSize - wordMod;
        }
        return wordMod;
    }

    /**
     *	This method removes the padding data from last block of data array.
     *
     *	@param : Data in byte array.
     *	@return: Byte array with no padding data.
     */
    public static byte[] unPadData(byte[] data)
    {
        byte lastWord = data[data.length - 1];
        int wordSize = 4; //  bytes
        int blocklen = 4; // size
        int blockSize = wordSize * blocklen; // 4*4 bytes
        if(lastWord < blockSize){
            byte[] paddata = new byte[lastWord];
            System.arraycopy(data, data.length-lastWord, paddata, 0, lastWord);
            boolean padFlag = true;
            for(int index = 0; index<lastWord; index++){
                if(padFlag){
                    padFlag = paddata[index] == lastWord;
                } else {
                    break;
                }
            }
            if(padFlag){
                data = Arrays.copyOf(data, data.length - lastWord);
            }
        }
        return data;
    }

    /**
     *	This method takes data in byte array format and ensures that each block of data is of 16 bits and
     *	appends padding data if last block is smaller then 16 bits and returns the new array.
     *
     *	@param : Data in byte array.
     *	@return: Data in byte array with appended padding data if last block is less than 16 bits.
     */
    public static byte[] padData(byte[] data)
    {
        int wordSize = 4; //  bytes
        int blocklen = 4; // size
        int blockSize = wordSize * blocklen; // 4*4 bytes

        int wordMod = data.length % blockSize;

        if(wordMod > 0) {
            int padNumber = (blockSize - wordMod);
            byte[] dataList = new byte[padNumber];
            for(int i=0; i< padNumber; i++){
                dataList[i] = (byte)padNumber;
            }

            byte[] dataBuf = new byte[dataList.length+data.length];
            System.arraycopy(data, 0, dataBuf, 0, data.length);
            System.arraycopy(dataList, 0, dataBuf, dataBuf.length-dataList.length, dataList.length);

            return dataBuf;
        } else {
            return data;
        }
    }
}

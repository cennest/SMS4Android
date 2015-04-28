package SMS4.Utility;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

/**
 * Created by salmankhan on 4/14/15.
 */
public class Converter {
    /**
     *	Converts Byte Array to Integer Array.
     *
     *	@param byteArray Byte Array.
     *	@return Integer Array.
     */
    public static int[] convertByteArraytoIntArray(byte byteArray[]) {
        int intArr[] = new int[byteArray.length / 4];
        int offset = 0;
        for(int i = 0; i < intArr.length; i++) {
            intArr[i] = (byteArray[3 + offset] & 0xFF) | ((byteArray[2 + offset] & 0xFF) << 8) |
                    ((byteArray[1 + offset] & 0xFF) << 16) | ((byteArray[0 + offset] & 0xFF) << 24);
            offset += 4;
        }
        return intArr;
    }

    /**
     *	Converts Integer Array to Byte Array
     *
     *	@param intArray Integer Array.
     *	@return Byte Array.
     */
    public static byte[] convertIntArrayToByteArray(int intArray[]) {

        int[] data = intArray;

        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(data);

        byte[] array = byteBuffer.array();
        return array;
    }

    public static long[] convertByteArrayToLongArray(byte byteArray[]) {
        long intArr[] = new long[byteArray.length / 4];
        int offset = 0;
        for(int i = 0; i < intArr.length; i++) {
            intArr[i] = (byteArray[3 + offset] & 0xFF) | ((byteArray[2 + offset] & 0xFF) << 8) |
                    ((byteArray[1 + offset] & 0xFF) << 16) | ((byteArray[0 + offset] & 0xFF) << 24);
            offset += 4;
        }
        return intArr;
    }

    public static byte[] convertLongArrayToByteArray(long longArray[]) {
        long[] data = longArray;

        ByteBuffer byteBuffer = ByteBuffer.allocate(longArray.length * 4);
        for (int i = 0; i < longArray.length; i++) {
            byteBuffer.putLong(0, longArray[i]);
        }
        return byteBuffer.array();
    }
}

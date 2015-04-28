package com.example.salmankhan.sms4;

/**
 * Created by salmankhan on 4/17/15.
 */
public class Utils {

    public static int[] convertCharArrayToIntArray(char charArray[]) {
        int intArr[] = new int[charArray.length / 4];
        int offset = 0;
        for(int i = 0; i < intArr.length; i++) {
            intArr[i] = (charArray[3 + offset] & 0xFF) | ((charArray[2 + offset] & 0xFF) << 8) |
                    ((charArray[1 + offset] & 0xFF) << 16) | ((charArray[0 + offset] & 0xFF) << 24);
            offset += 4;
        }
        return intArr;
    }

}

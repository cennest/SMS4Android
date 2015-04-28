package SMS4.Exceptions;

import SMS4.Constants.Constant;

/**
 * Created by salmankhan on 4/15/15.
 */
public class EmptyFileException extends Exception{

    public EmptyFileException() {
        super(Constant.EMPTY_FILE);
    }

    public EmptyFileException(String message) {
        super(message);
    }

}

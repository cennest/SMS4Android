package SMS4.Exceptions;

import SMS4.Constants.Constant;

/**
 * Created by salmankhan on 4/15/15.
 */
public class NullDataException extends Exception{

    public NullDataException() {
        super(Constant.DATA_IS_NULL_ERROR_SUGGESTION);
    }

    public NullDataException(String message) {
        super(message);
    }
}

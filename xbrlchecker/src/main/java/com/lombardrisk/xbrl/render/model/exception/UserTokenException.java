package com.lombardrisk.xbrl.render.model.exception;

import javax.ejb.ApplicationException;

/**
 * Created by Cesar on 06/06/2014.
 */
@ApplicationException(rollback = true)
public class UserTokenException extends LrmException{

    public UserTokenException(String message) {
        super(message);
    }

    public UserTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserTokenException(Throwable cause) {
        super(cause);
    }
}

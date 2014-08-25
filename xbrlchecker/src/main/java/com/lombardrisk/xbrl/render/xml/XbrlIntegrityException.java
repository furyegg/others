package com.lombardrisk.xbrl.render.xml;

/**
 * Created by Cesar on 06/06/2014.
 */
public class XbrlIntegrityException extends Exception{

    public XbrlIntegrityException() {
    }

    public XbrlIntegrityException(String message) {
        super(message);
    }

    public XbrlIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }

    public XbrlIntegrityException(Throwable cause) {
        super(cause);
    }

    public XbrlIntegrityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

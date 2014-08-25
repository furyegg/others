package com.lombardrisk.xbrl.render.model.exception;

import javax.ejb.ApplicationException;

/**
 * Created by Cesar on 10/06/2014.
 */
@ApplicationException(rollback = true)
public class XbrlRenderException extends LrmException {

    public XbrlRenderException(String message) {
        super(message);
    }

    public XbrlRenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public XbrlRenderException(Throwable cause) {
        super(cause);
    }
}

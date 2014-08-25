package com.lombardrisk.xbrl.render.model.exception;

import com.lombardrisk.xbrl.render.util.UUIDUtils;

/**
 * Created by Cesar on 17/06/2014.
 */
public abstract class LrmException extends Exception{
    private final String id;

    protected LrmException(String message, Throwable cause) {
        super(message, cause);
        this.id = UUIDUtils.smallHexUUIDHumanReadable();
    }

    protected LrmException(String message) {
        this(message, null);
    }

    protected LrmException(Throwable cause) {
        this(null, cause);
    }

    public String getId() {
        return id;
    }
}

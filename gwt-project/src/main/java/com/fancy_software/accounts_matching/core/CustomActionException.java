package com.fancy_software.accounts_matching.core;

import com.google.gwt.user.client.rpc.IsSerializable;
import net.customware.gwt.dispatch.shared.ActionException;

/**
 * @author John Khandygo
 */

public class CustomActionException extends ActionException implements IsSerializable {
    private ExceptionCode exceptionCode;

    public CustomActionException(String msg) {
        super(msg);
    }

    public CustomActionException(Throwable cause) {
        super(cause);
    }

    public CustomActionException() {
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public CustomActionException(ExceptionCode exceptionCode) {
        super(exceptionCode.name());
        this.exceptionCode = exceptionCode;
    }

    public CustomActionException(ExceptionCode exceptionCode, String message) {
        super(message);
        this.exceptionCode = exceptionCode;
    }
}

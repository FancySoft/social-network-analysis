package com.fancy_software.accounts_matching.webapp.application.client;

import com.fancy_software.accounts_matching.core.results.EmptyResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;

/**
 * @author John Khandygo
 */

public abstract class AbstractAsyncCallback <T extends EmptyResult> implements AsyncCallback<T> {
    public void onFailure(Throwable caught) {
        //TODO implement handling failures
    }
}

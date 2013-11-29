package com.fancy_software.accounts_matching.webapp.application.server.handlers;

import com.fancy_software.accounts_matching.core.CustomActionException;
import com.fancy_software.accounts_matching.core.ExceptionCode;
import com.fancy_software.accounts_matching.core.results.EmptyResult;
import com.fancy_software.accounts_matching.core.results.MatchAccountsResult;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.DispatchException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author John Khandygo
 */

@Component
public abstract class AbstractActionHandler<A extends Action<R>, R extends EmptyResult> implements ActionHandler<A, R> {
    private static final Logger log = Logger.getLogger(AbstractActionHandler.class.getName());

    public R execute(A action, ExecutionContext context) throws DispatchException {
        try {
            R result = safeExecute(action, context);
            log.info(". Successfully executed action: " + action.toString());
            return result;
        } catch (CustomActionException ex) {
            log.error(". Failed to execute action: " + action.toString(), ex);
            throw ex;
        } catch (Throwable ex) {
            log.error(". Failed to execute action: " + action.toString(), ex);
            throw new CustomActionException(getExceptionCode());
        }
    }

    /**
     * Implements rollback logic in this method.
     */
    public void rollback(A action, R result, ExecutionContext context) throws DispatchException {
        //Do nothing
    }

    /**
     * Implements execution logic in this method
     */
    protected abstract R safeExecute(A action, ExecutionContext context) throws CustomActionException;

    /**
     * Returns exception code which is appropriate executed action
     */
    protected abstract ExceptionCode getExceptionCode();
}


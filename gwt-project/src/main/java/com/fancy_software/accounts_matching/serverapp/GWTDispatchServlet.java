package com.fancy_software.accounts_matching.serverapp;

import net.customware.gwt.dispatch.server.spring.SpringStandardDispatchServlet;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.DispatchException;
import net.customware.gwt.dispatch.shared.Result;
import org.gwtwidgets.server.spring.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author John Khandygo
 */

public class GWTDispatchServlet extends SpringStandardDispatchServlet {
    @Override
    protected void checkPermutationStrongName() throws SecurityException {
        return;
    }

    @Override
    public Result execute(Action<?> action) throws DispatchException {
        ServletUtils.setRequest(getThreadLocalRequest());
        ServletUtils.setResponse(getThreadLocalResponse());
        return super.execute(action);

    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.setRequest(request);
        ServletUtils.setResponse(response);
    }
}

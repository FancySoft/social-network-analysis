package com.fancy_software.accounts_matching.webapp.application.server;

import net.customware.gwt.dispatch.server.ActionHandlerRegistry;
import net.customware.gwt.dispatch.server.SimpleDispatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author John Khandygo
 */

@Component
public class DispatchBean extends SimpleDispatch {
    @Autowired
    public DispatchBean(ActionHandlerRegistry registry) {
        super(registry);
    }
}

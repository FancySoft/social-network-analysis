package com.fancy_software.accounts_matching.webapp.application.server;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.DefaultActionHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author John Khandygo
 */

@Component
public class ActionHandlerRegistryBean extends DefaultActionHandlerRegistry {
    @Autowired
    public void setHandlers(List<ActionHandler<?, ?>> handlers) {
        for (ActionHandler<?, ?> actionHandler : handlers) {
            addHandler(actionHandler);
        }
    }
}

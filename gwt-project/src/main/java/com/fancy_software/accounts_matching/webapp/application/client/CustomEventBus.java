package com.fancy_software.accounts_matching.webapp.application.client;

import com.fancy_software.accounts_matching.webapp.application.client.ui.RootPresenter;
import com.mvp4g.client.annotation.*;
import com.mvp4g.client.event.EventBusWithLookup;

/**
 * @author John Khandygo
 */

@Events(startPresenter = RootPresenter.class)
@Debug
public interface CustomEventBus extends EventBusWithLookup {
    @Start
    @Event(handlers = {RootPresenter.class})
    @InitHistory
    void start();

    /*@NotFoundHistory
    @Event(handlers = ErrorPresenter.class)
    void notFound();*/
}

package com.fancy_software.accounts_matching.webapp.application.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.Mvp4gModule;

/**
 * @author John Khandygo
 */

public class StartPoint implements EntryPoint {
    private static Mvp4gModule mvp4gModule;

    public void onModuleLoad() {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            public void onUncaughtException(Throwable e) {
                GWT.log("unhandled exception", e);
                alertStackTrace(e);
                if (e instanceof UmbrellaException) {
                    UmbrellaException ue = (UmbrellaException) e;
                    for (Throwable t : ue.getCauses()) {
                        alertStackTrace(t);
                    }
                }
            }
        });
        
        mvp4gModule = (Mvp4gModule) GWT.create(Mvp4gModule.class);
        mvp4gModule.createAndStartModule();
        RootPanel.get().add((Widget) mvp4gModule.getStartView());
    }

    private void alertStackTrace(Throwable e) {
        Window.alert("Unknown exception " + e.getMessage() + " " + e.getClass().getName());
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : e.getStackTrace()) {
            sb.append(ste.toString()).append("\n");
        }
        //getEventBus().showError(sb.toString());
    }

    public static CustomEventBus getEventBus() {
        return (CustomEventBus)mvp4gModule.getEventBus();
    }
}

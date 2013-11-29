package com.fancy_software.accounts_matching.webapp.application.client;

import com.mvp4g.client.annotation.History;
import com.mvp4g.client.history.HistoryConverter;

/**
 * @author John Khandygo
 */

@History(type = History.HistoryConverterType.SIMPLE)
public class CustomHistoryConverter implements HistoryConverter<CustomEventBus> {
    @Override
    public void convertFromToken(String historyName, String param, final CustomEventBus eventBus) {
        if (historyName.equals("") || historyName.equals(null)) {
            eventBus.start();
        } else {
            return;
            //eventBus.notFound();
        }
    }
    
    @Override
    public boolean isCrawlable() {
        return false;
    }
}

package com.fancy_software.accounts_matching.webapp.application.client.ui;

import com.fancy_software.accounts_matching.core.actions.MatchAccountsAction;
import com.fancy_software.accounts_matching.core.entities.AccountVector;
import com.fancy_software.accounts_matching.core.entities.BirthDate;
import com.fancy_software.accounts_matching.core.results.MatchAccountsResult;
import com.fancy_software.accounts_matching.webapp.application.client.AbstractAsyncCallback;
import com.fancy_software.accounts_matching.webapp.application.client.CustomEventBus;
import com.google.gwt.text.client.DoubleRenderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.BasePresenter;
import net.customware.gwt.dispatch.client.DefaultExceptionHandler;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.client.standard.StandardDispatchAsync;

import java.util.Map;

/**
 * @author John Khandygo
 */

@Presenter(view = RootView.class)
public class RootPresenter extends BasePresenter<RootView, CustomEventBus> {
    private DispatchAsync dispatchAsync = new StandardDispatchAsync(new DefaultExceptionHandler());

    public void onStart() {}

    @Override
    public void setView(RootView view) {
        super.setView(view);
        view.setPresenter(this);
    }

    public void onLaunchButtonPressed() {
        if(view.validate()) {
            AccountVector av = new AccountVector();
            av.setFirstName(view.firstNameTB.getValue());
            av.setLastName(view.lastNameTB.getValue());
            av.setSex(view.maleSexRB.getValue() ? AccountVector.Sex.MALE : AccountVector.Sex.FEMALE);
            av.setBdate(BirthDate.generateBirthDate(view.getFormattedBirthDate()));

            MatchAccountsAction a = new MatchAccountsAction();
            a.setAccountVector(av);
            Window.alert("start execute");
            dispatchAsync.execute(a, new AbstractAsyncCallback<MatchAccountsResult>() {
                @Override
                public void onSuccess(MatchAccountsResult result) {
                    for (Double key : result.getBestMatches().keySet()) {
                        Label l1 = new Label("Measure: " + String.valueOf(key));
                        Label l2 = new Label("Account: " + result.getBestMatches().get(key).toString());
                        RootPanel.get().add(l1);
                        RootPanel.get().add(l2);
                    }
                }

                @Override
                public void onFailure( Throwable e ) {
                    Window.alert( "Error: " + e.getMessage() );
                }
            });
        } else {
            Window.alert("Some fields empty!");
        }
    }
}

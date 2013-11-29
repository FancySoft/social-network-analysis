package com.fancy_software.accounts_matching.webapp.application.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;

import java.util.Date;

/**
 * @author John Khandygo
 */

public class RootView extends AbstractView{
    interface RootViewUiBinder extends UiBinder<Widget, RootView> {
    }

    private static RootViewUiBinder uiBinder = GWT.create(RootViewUiBinder.class);

    @UiField
    HTMLPanel rootPanel;
    @UiField
    TextBox firstNameTB;
    @UiField
    TextBox lastNameTB;
    @UiField
    RadioButton maleSexRB;
    @UiField
    RadioButton femaleSexRB;
    @UiField
    DateBox birthDateDB;
    @UiField
    Button launchB;

    RootPresenter presenter;

    public void setPresenter(RootPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget getWidget() {
        return rootPanel;
    }
    
    public RootView() {
        initWidget(uiBinder.createAndBindUi(this));
        final DateTimeFormat format = DateTimeFormat.getFormat("dd.mm.yyyy");
        birthDateDB.setFormat(new DateBox.DefaultFormat(format));
    }

    public boolean validate() {
        return !(firstNameTB.getValue().equals(null) || lastNameTB.getValue().equals(null) || !(maleSexRB.getValue() || femaleSexRB.getValue()) || birthDateDB.getValue().equals(null));
    }

    public String getFormattedBirthDate() {
        return DateTimeFormat.getFormat("dd.mm.yyyy").format(birthDateDB.getValue());
    }
    
    @UiHandler("launchB")
    void onLaunchButtonPressed(ClickEvent e) {
        presenter.onLaunchButtonPressed();
    }
}

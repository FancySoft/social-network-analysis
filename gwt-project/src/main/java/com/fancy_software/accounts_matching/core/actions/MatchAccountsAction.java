package com.fancy_software.accounts_matching.core.actions;

import com.fancy_software.accounts_matching.core.entities.AccountVector;
import com.fancy_software.accounts_matching.core.results.MatchAccountsResult;
import net.customware.gwt.dispatch.shared.Action;

/**
 * @author John Khandygo
 */

public class MatchAccountsAction implements Action<MatchAccountsResult> {
    private AccountVector accountVector;

    public AccountVector getAccountVector() {
        return accountVector;
    }

    public void setAccountVector(AccountVector accountVector) {
        this.accountVector = accountVector;
    }

    @Override
    public String toString() {
        return "MatchAccountsAction{" +
                "accountVector=" + accountVector +
                '}';
    }
}

package com.fancy_software.accounts_matching.webapp.application.server.handlers;

import com.fancy_software.accounts_matching.core.CustomActionException;
import com.fancy_software.accounts_matching.core.ExceptionCode;
import com.fancy_software.accounts_matching.core.actions.MatchAccountsAction;
import com.fancy_software.accounts_matching.core.entities.AccountVector;
import com.fancy_software.accounts_matching.core.results.MatchAccountsResult;
import com.fancy_software.accounts_matching.serverapp.localio.LocalAccountReader;
import com.fancy_software.accounts_matching.serverapp.match.AccountMeasurer;
import net.customware.gwt.dispatch.server.ExecutionContext;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author John Khandygo
 */

@Component
public class MatchAccountsActionHandler extends AbstractActionHandler<MatchAccountsAction, MatchAccountsResult>{
    private static final Logger log = Logger.getLogger(MatchAccountsActionHandler.class.getName());

    @Override
    protected MatchAccountsResult safeExecute(MatchAccountsAction action, ExecutionContext context) throws CustomActionException{
        MatchAccountsResult result = new MatchAccountsResult();
        AccountMeasurer am = new AccountMeasurer(action.getAccountVector());
        HashMap<Long,AccountVector> storedAccounts = LocalAccountReader.readAllAccounts("enter path to your accounts folber");
        TreeMap<Double, AccountVector> bestMeasures = new TreeMap<Double, AccountVector>();
        for (AccountVector value : storedAccounts.values()) {
            am.setVector2(value);
            try {
                double m = am.measure(false);
                bestMeasures.put(m, value);
            } catch (Exception e) {
                log.error(e);
            }
        }
        TreeMap<Double, AccountVector> resultBestMeasures = new TreeMap<Double, AccountVector>();
        int count = 0;
        for (Double key : bestMeasures.keySet()) {
            resultBestMeasures.put(key, bestMeasures.get(key));
            ++count;
            if (count == 10) {
                break;
            }
        }
        result.setBestMatches(resultBestMeasures);
        return result;
    }

    @Override
    protected ExceptionCode getExceptionCode() {
        return ExceptionCode.CANT_MATCH_ACCOUNTS;
    }

    public Class<MatchAccountsAction> getActionType() {
        return MatchAccountsAction.class;
    }
}

import com.fancy_software.accounts_matching.matcher.namematching.NameMatcher;
import org.junit.Assert;
import org.junit.Test;

/**
 * ************************************************************************
 * Created by akirienko on 11.11.13
 * Copyright (c) 2013 Artem Kirienko. All rights reserved.
 * ************************************************************************
 */
public class TNameMatching {
    @Test
    public void nameTest() {
        NameMatcher matcher = NameMatcher.getInstance();
        Assert.assertTrue(matcher.match("Ася", "Нюша", 1));
        Assert.assertTrue(matcher.match("Анна", "Ася"));
        Assert.assertFalse(matcher.match("Анна", "Саша"));
    }
}

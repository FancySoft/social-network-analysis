import com.fancy_software.accounts_matching.matcher.Utils;
import com.fancy_software.accounts_matching.matcher.namematching.NameMatcher;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.LinkedList;

/**
 * ************************************************************************
 * Created by akirienko on 11.11.13
 * Copyright (c) 2013 Artem Kirienko. All rights reserved.
 * ************************************************************************
 */
public class TNameMatching {
    @Test
    public void match1Test() {
        NameMatcher matcher = NameMatcher.getInstance();
        Assert.assertTrue(matcher.match("Ася", "Нюша"));
        Assert.assertTrue(matcher.match("Анна", "Ася"));
        Assert.assertFalse(matcher.match("Анна", "Саша"));
        Assert.assertTrue(matcher.match("Владимир", "Вова"));
        Assert.assertFalse(matcher.match("Саша", "Женя"));
    }

    @Test
    public void match2Test() {
        NameMatcher matcher = NameMatcher.getInstance();
        Assert.assertFalse(matcher.match("Анна", "Ася", 2));
        Assert.assertFalse(matcher.match("Анна", "Саша", 2));
        Assert.assertFalse(matcher.match("Владимир", "Вова", 1));
        Assert.assertFalse(matcher.match("Саша", "Женя", 2));
        Assert.assertFalse(matcher.match("саша", "Александр",1));
        Assert.assertFalse(matcher.match("Анна", "Саша", 1));
        Assert.assertTrue(matcher.match("Маша", "Маша", 1));
        Assert.assertFalse(matcher.match("Маша", "Маша", 2));
        Assert.assertFalse(matcher.match("Собака", "Собака", 1));
    }

    private String normalize(String name) {
        return Utils.transliterate(name.toUpperCase());
    }

    @Test
    public void allFormsTest() {
        NameMatcher matcher = NameMatcher.getInstance();
        List<String> firstGroup = matcher.allForms("Саша", 1);
        Assert.assertTrue(firstGroup.contains(normalize("Александра")));
        Assert.assertFalse(firstGroup.contains(normalize("Александр")));
        Assert.assertFalse(firstGroup.contains(normalize("Маша")));
        Assert.assertTrue(firstGroup.contains(normalize("Саня")));
        Assert.assertEquals(16 * 2, firstGroup.size());

        List<String> secondGroup = matcher.allForms("Маша", 2);
        Assert.assertEquals(secondGroup, null);
        List<String> thirdGroup = matcher.allForms("Жека", 2);
        Assert.assertTrue(thirdGroup.contains(normalize("Евгений")));
        Assert.assertFalse(thirdGroup.contains(normalize("Евгения")));
        Assert.assertTrue(thirdGroup.contains(normalize("Женя")));
        Assert.assertEquals(10 * 2, thirdGroup.size());
    }
}

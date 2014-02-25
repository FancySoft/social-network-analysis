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
        Assert.assertTrue(matcher.match("Ася", "Нюша", 1));
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

    @Test
    public void allFormsTest() {
        NameMatcher matcher = NameMatcher.getInstance();
        List<String> firstGroup = matcher.allForms("Саша", 1);
        Assert.assertTrue(firstGroup.contains("Александра"));
        Assert.assertFalse(firstGroup.contains("Александр"));
        Assert.assertFalse(firstGroup.contains("Маша"));
        Assert.assertTrue(firstGroup.contains("Саня"));
        Assert.assertEquals(firstGroup.size(), 16);

        List<String> secondGroup = matcher.allForms("Маша", 2);
        Assert.assertEquals(secondGroup, null);
        List<String> thirdGroup = matcher.allForms("Жека", 2);
        Assert.assertTrue(thirdGroup.contains("Евгений"));
        Assert.assertFalse(thirdGroup.contains("Евгения"));
        Assert.assertTrue(thirdGroup.contains("Женя"));
        Assert.assertEquals(thirdGroup.size(), 10);
    }
    @Test
    public void allFormsTest2() {
        NameMatcher matcher = NameMatcher.getInstance();
        List<String> testGroup = matcher.allForms("Жека", 2);
        Assert.assertTrue(testGroup.contains("Женечка"));
    }

    @Test
    public void allFormsTest3() {
        NameMatcher matcher = NameMatcher.getInstance();
        List<String> testGroup = matcher.allForms("Лилия", 1);
        Assert.assertNotNull(testGroup);
    }

}

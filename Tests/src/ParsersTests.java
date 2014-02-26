
import com.fancy_software.accounts_matching.io_local_base.Settings;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.BirthDate;
import org.junit.Test;

/**
 * ************************************************************************
 * Created by akirienko on 28.10.13
 * Copyright (c) 2013 Desman, Inc. All rights reserved.
 * ************************************************************************
 */
public class ParsersTests {

    @Test
    public void vkSearch() {
//        AbstractParser parser = ParserFactory.getApiWorkerInstance(SocialNetworkId.VK);
        Settings settings = Settings.getInstance();
//        parser.auth(settings.get("vk_login"), settings.get("vk_password"));
        AccountVector goal = new AccountVector();
        goal.setFirst_name("Artem");
        goal.setLast_name("Kirienko");
        goal.setBdate(new BirthDate(16, 3, 1992));
//        Assert.assertNotNull(parser.match(goal));
    }

    @Test
    public void testMatch() {
//        AbstractParser parser = ParserFactory.getApiWorkerInstance(SocialNetworkId.VK);
        Settings settings = Settings.getInstance();
//        parser.auth(settings.get("vk_login"), settings.get("vk_password"));
        AccountVector goal = new AccountVector();
        goal.setFirst_name("Artem");
        goal.setLast_name("Kirienko");
        goal.setBdate(new BirthDate(16, 3, 1992));
        BirthDate resDate = new BirthDate(16, 3, 1992);
//        Assert.assertEquals(parser.match(goal).getFirst_name(), "Artem");
//        Assert.assertEquals(parser.match(goal).getLast_name(), "Kirienko");
//        Assert.assertEquals(parser.match(goal).getBdate(), resDate);
//        Assert.assertEquals(parser.match(goal).getSex(), 2);
    }
}

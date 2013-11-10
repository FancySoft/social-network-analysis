import com.fancy_software.accounts_matching.io_local_base.Settings;
import org.junit.Assert;
import org.junit.Test;

/**
 * ************************************************************************
 * Created by akirienko on 04.11.13
 * Copyright (c) 2013 Desman, Inc. All rights reserved.
 * ************************************************************************
 */
public class TSettings {
    @Test
    public void vkData() {
        Settings settings = Settings.getInstance();
        Assert.assertNotNull(settings.get("vk_login"));
    }

    @Test
    public void writeTest() {
        Settings settings = Settings.getInstance();
        if (settings.get("test_key") != null) {
            Assert.assertSame("test_value", settings.get("test_key"));
        } else {
            settings.put("test_key", "test_value");
        }
    }
}

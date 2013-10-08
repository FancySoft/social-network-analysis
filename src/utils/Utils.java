package utils;

public class Utils {

    private static final String pathToAccounts = "accounts/";
    private static final String pathToVkAccounts = "vk/";

    public static String generatePathToAccounts(int socialNetworkId, long accountId) {
        StringBuilder builder = new StringBuilder(pathToAccounts);
        if(socialNetworkId==0)
            builder.append(pathToVkAccounts);
        return String.valueOf(builder.append(accountId).append(".xml"));
    }
}

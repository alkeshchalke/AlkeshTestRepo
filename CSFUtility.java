package cwalletdetails;

import com.oracle.retail.integration.common.security.credential.CredentialStoreManager;
import java.io.File;
import java.util.Set;

public class CSFUtility
{
    private static CredentialStoreManager csm = null;

    private static CSFUtility csfUtil = null;

    private CSFUtility()
    {
        csm = new CredentialStoreManager();
    }

    private CSFUtility(String walletLocation, String mapName)
    {
        File wallet = new File(walletLocation);
        if (mapName == null)
            mapName = "DEFAULT_KEY_PARTITION_NAME";
        csm = new CredentialStoreManager(wallet, mapName);
    }

    public char[] getPassword(String userNameAlias) throws RuntimeException
    {
        return csm.getPassword(userNameAlias);
    }

    public boolean doesUserAliasExist(String userNameAlias)
    {
        return csm.doesUserAliasExist(userNameAlias);
    }

    public void savePassword(String userNameAlias, String userName, String password)
    {
        csm.save(userNameAlias, userName, password.toCharArray());
    }

    public static CSFUtility getInstance()
    {
        if (csfUtil == null)
        {
            csfUtil = new CSFUtility();
        }
        return csfUtil;
    }

    public static CSFUtility getInstance(String walletLocation, String mapName)
    {
        csfUtil = null;
        if (csfUtil == null)
        {
            csfUtil = new CSFUtility(walletLocation, mapName);
        }
        return csfUtil;
    }

    public Set<?> getAllUserNameAliases()
    {
        if (csfUtil == null)
        {
            csfUtil = new CSFUtility();
        }

        return csm.getAllUserNameAliases();
    }
}

package cwalletdetails;

import java.util.Iterator;
import java.util.Set;

public class RetrieveCwalletDetails
{
    public static void main(String[] args)
    {
        CSFUtility csfObject = CSFUtility.getInstance(System.getProperty("user.dir"), null);
        
        Set<?> allUserNameAliases = csfObject.getAllUserNameAliases();
        Iterator<?> iterator = allUserNameAliases.iterator();

        while (iterator.hasNext())
        {
            String alias = (String)iterator.next();
            System.out.println(alias + " = " + String.valueOf(csfObject.getPassword(alias)));
        }
    }
}

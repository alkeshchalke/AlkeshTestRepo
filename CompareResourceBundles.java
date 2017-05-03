package findpattern;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CompareResourceBundles
{
    public static void main(String[] args) throws IOException
    {
        FileInputStream fstream1 = new FileInputStream("D:\\temp\\custom.properties");

        FileInputStream fstream2 = new FileInputStream("D:\\temp\\base.properties");

        DataInputStream in1 = new DataInputStream(fstream1);
        BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));

        DataInputStream in2 = new DataInputStream(fstream2);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));

        String strLine1 = null, strLine2 = null;
        String val1, val2;
        String key1, key2;

        while ((strLine1 = br1.readLine()) != null)
        {
            if (!(strLine1.isEmpty()) && (strLine1.charAt(0) != '#'))
            {
                while ((strLine2 = br2.readLine()) != null)
                {
                    if (!(strLine2.isEmpty()) && strLine1.contains("="))
                    {
                        System.out.println("strLine1 " + strLine1);
                        System.out.println("strLine2 " + strLine2);
                        System.out.println("*****************************");

                        if (strLine1.equals(strLine2))
                        {
                            break;
                        }
                        else
                        {
                            int startIndex = strLine1.indexOf("=");
                            key1 = strLine1.substring(0, startIndex);
                            val1 = strLine1.substring(startIndex + 1);

                            if (strLine2.startsWith(key1))
                            {
                                //System.out.println(strLine2);
                                break;
                            }

                        }
                    }

                }
                strLine2 = null;
                br2 = new BufferedReader(new InputStreamReader(in2));
            }

        }
        br1.close();
        br2.close();

    }
}

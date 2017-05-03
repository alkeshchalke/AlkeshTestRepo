package findpattern;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CompareTwoFiles
{
    public static void main(String[] args) throws IOException
    {

        FileInputStream fstream1 = new FileInputStream("D:\\TEST\\ORG_Items.properties");
        FileInputStream fstream2 = new FileInputStream("D:\\TEST\\DEL_Items.properties");

        DataInputStream in1 = new DataInputStream(fstream1);
        BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));

        DataInputStream in2 = new DataInputStream(fstream2);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));

        String strLine1 = null, strLine2 = null;

        while ((strLine1 = br1.readLine()) != null && (strLine2 = br2.readLine()) != null)
        {
            if (strLine1.equals(strLine2))
            {
                System.out.println(strLine1);

            }
        }
        br1.close();
        br2.close();

    }
}

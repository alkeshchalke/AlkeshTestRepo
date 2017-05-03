package xlfconverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class KeyValueToXlfConverter
{

    public static void main(String[] args)
    {
        String inFileName = "D:\\AEO_Upgrade\\Localization\\Alkesh_Chinese_Translations\\AE_BO_Reports_V2.xlsx";
        File inFile = new File(inFileName);
        
        String outFileName = "D:\\AEO_Upgrade\\Localization\\Alkesh_Chinese_Translations\\AE_BO_Reports_V3.xlsx";
        File outfile = new File(outFileName);
        Writer writer = null;

        try
        {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "UTF-8"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFileName), "UTF-8"));

            String line = "";

            while ((line = reader.readLine()) != null)
            {
                writer.write(line + "\n");
                System.out.println(line);
            }
            reader.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

}

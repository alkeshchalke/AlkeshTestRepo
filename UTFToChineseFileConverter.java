package asciitochinese;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;

import net.janino.UnicodeUnescapeReader;

public class UTFToChineseFileConverter
{
    public static void main(String[] args)
    {
        try
        {
            File filepath = new File("D:\\AEO_Upgrade\\Localization\\1_Dec\\1401_unicode\\");

            File[] listOfFiles = filepath.listFiles();
            for (int i = 0; i < listOfFiles.length; i++)
            {
                System.out.println(listOfFiles[i].getName());

                String inputFileName = filepath.toString() + "\\" + listOfFiles[i].getName();
                File inputfile = new File(inputFileName);

                BufferedReader reader = new BufferedReader(new FileReader(inputfile));
                String line = "";
                String val;
                String key;
                StringBuffer chineseValue;

                String outFileName = "D:\\AEO_Upgrade\\Localization\\1_Dec\\1401_zh\\" + listOfFiles[i].getName();
                File outfile = new File(outFileName);

                Writer writer = null;
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));

                while ((line = reader.readLine()) != null)
                {
                    if ((line.length() == 0) || (line.charAt(0) == '#'))
                    {
                        writer.write(line + "\n");
                    }
                    else
                    {
                        boolean equalsContains = line.contains("=");

                        if (equalsContains)
                        {
                            int startIndex = line.indexOf("=");
                            key = line.substring(0, startIndex);
                            val = line.substring(startIndex + 1);

                            writer.write(key + "=");
                            /* writer.write(val + "\t"); */
                            chineseValue = getChineseValueForAscii(val);
                            writer.write(chineseValue + "\n");

                            System.out.println("Key : " + key + "\tAscii Value : " + val + "\tChinese value : "
                                    + chineseValue);
                        }
                    }
                }

                reader.close();
                writer.flush();
                writer.close();
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    private static StringBuffer getChineseValueForAscii(String str)
    {
        StringReader sr = new StringReader(str);
        UnicodeUnescapeReader uur = new UnicodeUnescapeReader(sr);

        StringBuffer buf = new StringBuffer();
        try
        {
            for (int c = uur.read(); c != -1; c = uur.read())
            {
                buf.append((char)c);
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buf;
    }
}

package asciitochinese;
import java.io.IOException;
import java.io.StringReader;

import net.janino.UnicodeUnescapeReader;

public class UTFToChineseConverter
{
    public static void main(String[] args)
    {
        String str = "\u2191";
        System.out.println(str);
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
        System.out.println(buf.toString());

        /*try
        {

            File file = new File(
                    "D:\\AEO_UK\\Workspace\\BO_Workspace\\AEO_ASIA_BO_14_3.2\\applications\\backoffice\\i18n\\zh\\parameters.properties");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";

            Writer writer = null;

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\PathTrversed.txt"), "utf-8"));

            while ((line = reader.readLine()) != null)
                System.out.println(line);

            reader.close();
            writer.close();

        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }*/

    }
}

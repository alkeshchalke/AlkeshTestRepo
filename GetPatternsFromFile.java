package findpattern;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.TreeMap;

public class GetPatternsFromFile
{
    public static void main(String[] args) throws IOException
    {
        String outFileName = "D:\\test.properties";
        File outfile = new File(outFileName);

        Writer writer = null;
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));

        String currentFileName = "D:\\pos.txt";

        File file = new File(currentFileName);

        String line = "";
        TreeMap<String, String> map = new TreeMap<String, String>();

        final int lhs = 0;
        final int rhs = 1;

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            while ((line = reader.readLine()) != null)
            {
                if (!line.startsWith("#") && !line.isEmpty())
                {
                    String[] pair = line.trim().split("=");
                    map.put(pair[lhs].trim(), pair[rhs].trim());
                    //System.out.println(pair[lhs].trim());
                    System.out.println(pair[rhs].trim());
                }
            }
            
            reader.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}

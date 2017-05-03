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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GetPatternsFromDirectory
{

    public static void main(String[] args) throws IOException
    {
        File folder = new File("D:\\TEST");

        ArrayList<String> files = new ArrayList<String>();
        try
        {
            files = (ArrayList<String>)searchFiles(folder, ".bpt", files);
        }
        catch (FileNotFoundException e1)
        {
        }

        String outFileName = "D:\\TEST\\DEL_Items.properties";
        File outfile = new File(outFileName);

        Writer writer = null;
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));

        for (int i = 0; i < files.size(); i++)
        {
            writer.write("\n\n\n");
            String currentFileName = folder.getPath() + "\\" + files.get(i);

            System.out.println(folder.getPath() + "\\" + files.get(i));

            File file = new File(currentFileName);

            String line = "";

            try
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                while ((line = reader.readLine()) != null)
                {
                    if (line.contains("ID=\"00"))
                    {
                        System.out.println(currentFileName + " \t " + line);
                        writer.write(line);
                        writer.write("\n");
                        writer.flush();
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
        writer.close();
    }

    private static List<String> searchFiles(File file, String pattern, List<String> result)
            throws FileNotFoundException
    {

        if (!file.isDirectory())
        {
            throw new IllegalArgumentException("file has to be a directory");
        }

        if (result == null)
        {
            result = new ArrayList<String>();
        }

        File[] files = file.listFiles();

        if (files != null)
        {
            for (File currentFile : files)
            {
                if (currentFile.isDirectory())
                {
                    searchFiles(currentFile, pattern, result);
                }
                else
                {
                    Scanner scanner = new Scanner(currentFile);
                    if (scanner.findWithinHorizon(pattern, 0) != null)
                    {
                        result.add(currentFile.getName());
                    }
                    scanner.close();
                }
            }
        }
        return result;
    }

}

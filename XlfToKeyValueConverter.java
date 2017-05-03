package xlfconverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class XlfToKeyValueConverter
{
    public static void main(String[] args)
    {
        try
        {
            File filepath = new File(
                    "D:\\AEO_US\\1401Workspace\\BO_Workspace\\ORBO-14.0.1_TempBuildArea\\applications\\backoffice\\reports\\");

            ArrayList<File> files = new ArrayList<File>();
            files = listofFiles(filepath.getAbsolutePath(), files);
            System.out.println(files.size());

            String outFileName = "D:\\AEO_Mexico\\Localization\\Required_Translations\\BO_Reports_EN.properties";
            File outfile = new File(outFileName);

            Writer writer = null;
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));

            TreeMap<String, String> sourceTargetMap = new TreeMap<String, String>();
            String source = null;
            String target = null;

            for (int i = 0; i < files.size(); i++)
            {
                String inputFileName = files.get(i).getAbsolutePath();
                File inputfile = new File(inputFileName);

                BufferedReader reader = new BufferedReader(new FileReader(inputfile));
                String line = "";
                
                while ((line = reader.readLine()) != null)
                {
                    if (line.contains("<source"))
                    {
                        int startIndex = line.indexOf(">");
                        int endIndex = line.indexOf("</");
                        source = line.substring(startIndex + 1, endIndex).trim();
                    }

                    if (line.contains("<target"))
                    {
                        int startIndex = line.indexOf(">");
                        int endIndex = line.indexOf("</");
                        target = line.substring(startIndex + 1, endIndex).trim();
                        sourceTargetMap.put(source, target);
                    }
                }
                reader.close();
            }

            for (Map.Entry<String, String> entry : sourceTargetMap.entrySet())
            {
                System.out.println("Source: " + entry.getKey());
                System.out.println("Target: " + entry.getValue());
                
                if(entry.getKey().endsWith(":"))
                {
                     
                }
                
                writer.write(entry.getKey() + "=");
                writer.write(entry.getValue() + "\n");
            }
            writer.flush();
            writer.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    private static ArrayList<File> listofFiles(String directoryName, ArrayList<File> files)
    {
        File filepath = new File(directoryName);

        File[] listOfFiles = filepath.listFiles();

        String requiredFileNamePattern = "_en.xlf";

        for (File file : listOfFiles)
        {
            String fileName = file.getAbsolutePath();

            if (file.isFile())
            {
                if (fileName.contains(requiredFileNamePattern))
                {
                    files.add(file);
                    System.out.println(file.getAbsolutePath());
                }
            }
            else if (file.isDirectory())
            {
                listofFiles(file.getAbsolutePath(), files);
            }
        }
        return files;

    }

}

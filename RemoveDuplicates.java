package findpattern;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedHashSet;

public class RemoveDuplicates
{
    public static void main(String[] args)
    {
        try
        {
            FileInputStream fstream = new FileInputStream("D:\\AEO_Mexico\\Localization\\Received_Translations\\Temp3.sql");
            String currentOutFileName = "D:\\AEO_Mexico\\Localization\\Received_Translations\\Temp4.sql";
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            LinkedHashSet<String> set = new LinkedHashSet<String>();
            
            PrintWriter pw = new PrintWriter(new FileWriter(currentOutFileName));
            int counter=0;
            // Read File Line By Line
            while ((strLine = br.readLine()) != null)
            {
                // Print the content on the console
                set.add(strLine);
                counter++;
                
                
                // System.out.println (strLine);
            }
            System.out.println("Set :" + set.size());
            
            for(String s:set)
            {
                pw.println(s);
                pw.flush();
            }
            
            System.out.println("Counter :" + counter);
            // Close the input stream
            pw.close();
            in.close();
        }
        catch (Exception e)
        {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}

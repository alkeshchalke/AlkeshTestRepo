package xlfconverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class XLSToXlfConverter2
{
    static ArrayList<String> files = new ArrayList<String>();

    static File folder = null;

    static String outFileName = "D:\\XLSToXlfConverterLog.txt";

    static Writer logWriter = null;

    static TreeMap<String, ArrayList<String>> tempFileExtract = null;

    public static void main(String[] args) throws Exception
    {

        File outfile = new File(outFileName);
        logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));

        tempFileExtract = readXLSFile("D:\\AEO_Mexico\\Localization\\Received_Translations\\BO_Reports_V1 SP.xlsx");

        folder = new File("D:\\TEST\\Mexico_Files");

        try
        {
            files = (ArrayList<String>)searchFiles(folder, "xlf", files);
        }
        catch (FileNotFoundException e1)
        {
        }

        for (int i = 0; i < files.size(); i++)
        {
            String currentFileName = folder.getPath() + "\\" + files.get(i);
            String currentOutFileName = "D:\\tmp\\" + files.get(i);
            File file = new File(currentFileName);

            Writer writer = null;
            String line = "";
            String match = null;
            String splitValue[] = null;
            
            searchPattern();

            try
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                PrintWriter pw = new PrintWriter(new FileWriter(currentOutFileName));

                while ((line = reader.readLine()) != null)
                {
                    for (Entry<String, ArrayList<String>> entry : tempFileExtract.entrySet())
                    {
                        splitValue = entry.getValue().toString().split("~");

                        if (splitValue[1].isEmpty())
                        {
                            splitValue[1] = splitValue[0].substring(1, splitValue[0].length());
                        }

                        if (line.contains(splitValue[1]) && line.contains("<target>"))
                        {
                            System.out.println(currentFileName + " \t " + line);

                           line = line.trim();

                            int endIndex = line.lastIndexOf("<");

                            match = line.substring(8, endIndex);

                            System.out.println("Exact Match Found at :" + match);

                            String replacement = splitValue[2].substring(0, splitValue[2].length()-1);
                            line = line.replace(match, replacement);

                            logWriter.write(match + "\t" + replacement);
                            logWriter.write("\n");
                            logWriter.flush();

                        }
                    }
                    
                    pw.println(line);
                    pw.flush();

                    /* writer.close(); */
                }
                reader.close();
                pw.close();
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

        logWriter.close();
    }

    protected static String[] searchPattern() throws IOException
    {
        String splitValue[] = null;
        TreeMap<String, ArrayList<String>> newTempFileExtract = null;

        for (Entry<String, ArrayList<String>> entry : tempFileExtract.entrySet())
        {
            splitValue = entry.getValue().toString().split("~");

            if (splitValue[1].isEmpty())
            {
                splitValue[1] = splitValue[0].substring(1, splitValue[0].length());
            }
            
            logWriter.write(entry.getKey() + "\t" + splitValue[0] + "\t" + splitValue[0] + "\t" + splitValue[1] + "\t" + splitValue[2] +"\n");
            logWriter.flush();
            
            //newTempFileExtract.put(entry.getKey(), value);

        }
        
        logWriter.write("*************************************************************************************************************");
        logWriter.flush();
        
        return splitValue;

    }

    protected static void searchPattern(String pattern, String replacement) throws UnsupportedEncodingException,
            FileNotFoundException
    {
        // System.out.println("Total Files : " + files.size());

        System.out.println("******************************************************");

        System.out.println("Pattern to be searched : " + pattern);

        for (int i = 0; i < files.size(); i++)
        {
            String currentFileName = folder.getPath() + "\\" + files.get(i);
            String currentOutFileName = "D:\\tmp\\" + files.get(i);

            // System.out.println(folder.getPath() + "\\" + files.get(i));

            File file = new File(currentFileName);

            Writer writer = null;
            String line = "";
            String match = null;

            try
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                // writer = new BufferedWriter(new OutputStreamWriter(new
                // FileOutputStream(file), "utf-8"));
                PrintWriter pw = new PrintWriter(new FileWriter(currentOutFileName));

                while ((line = reader.readLine()) != null)
                {
                    if (line.contains(pattern) && line.contains("<target>"))
                    {
                        System.out.println(currentFileName + " \t " + line);

                        line = line.trim();

                        int endIndex = line.lastIndexOf("<");

                        match = line.substring(8, endIndex);

                        System.out.println("Exact Match Found at :" + match);

                        line = line.replace(match, replacement);

                        logWriter.write(match);
                        logWriter.flush();

                        /*
                         * Path original =
                         * FileSystems.getDefault().getPath(currentOutFileName);
                         * Path target =
                         * FileSystems.getDefault().getPath(currentFileName);
                         * Files.move(original,
                         * target,StandardCopyOption.REPLACE_EXISTING);
                         */
                    }

                    /*
                     * writer.write(line); writer.write("\n"); writer.flush();
                     */

                    pw.println(line);
                    pw.flush();
                }

                /* writer.close(); */
                pw.close();
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

    protected static TreeMap<String, ArrayList<String>> readXLSFile(String fileName) throws Exception
    {

        InputStream inputStream = null;
        String sheetName1 = fileName;

        TreeMap<String, ArrayList<String>> tempFileExtract = new TreeMap<String, ArrayList<String>>();
        try
        {
            inputStream = new FileInputStream(fileName);
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
        Workbook workBook;
        try
        {
            workBook = WorkbookFactory.create(inputStream);
            Sheet sheet = workBook.getSheetAt(0);

            String bptFileName = "", addToUpdate = "";
            Cell myCell;

            for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++)
            {
                if (j > 0)
                {
                    Row myRow = sheet.getRow(j);
                    for (int i = 0; i < myRow.getLastCellNum(); i++)
                    {
                        myCell = myRow.getCell(i);
                        myCell.setCellType(Cell.CELL_TYPE_STRING);
                        if (i == 0)
                        {
                            if ((myCell.getStringCellValue() != null) && (myCell.getStringCellValue().length() > 0))
                            {
                                bptFileName = myCell.getStringCellValue();
                            }
                            else
                            {
                                bptFileName = "";
                            }

                        }
                        if ((i > 0) && (i < ((myRow.getLastCellNum()) - 1)))
                        {

                            myCell = myRow.getCell(i);
                            if ((myCell.getStringCellValue() != null) && (myCell.getStringCellValue().length() > 0))
                            {
                                addToUpdate += myCell.getStringCellValue() + "~";
                            }
                            else
                            {
                                addToUpdate += "" + "~";
                            }
                        }
                        if ((i == (myRow.getLastCellNum() - 1)))
                        {
                            myCell = myRow.getCell(i);
                            if ((myCell.getStringCellValue() != null) && (myCell.getStringCellValue().length() > 0))
                            {
                                addToUpdate += myCell.getStringCellValue();
                            }
                            else
                            {
                                addToUpdate += "";
                            }
                        }

                    }
                    // bptUdates.add(addToUpdate);
                    if (tempFileExtract.containsKey(bptFileName))
                    {
                        ArrayList<String> temp = (ArrayList<String>)tempFileExtract.get(bptFileName);
                        temp.add(addToUpdate);
                        tempFileExtract.put(bptFileName, temp);

                    }
                    else
                    {

                        ArrayList<String> temp = new ArrayList<String>();
                        temp.add(addToUpdate);
                        tempFileExtract.put(bptFileName, temp);

                    }
                    addToUpdate = "";
                }

            }
        }
        catch (Exception e)
        {
            System.out.println("Exception while reading Sheet ::" + sheetName1 + "");
            e.printStackTrace();
            throw new Exception("Error in resding XLS file !");
        }
        catch (Error e)
        {
            System.out.println("Error while reading Sheet (In Error Block)::" + sheetName1 + "");
            e.printStackTrace();
            throw new Exception("Error in resding XLS file !");
        }

        return tempFileExtract;

    }
}

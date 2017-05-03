package xlfconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class XLSToSqlReasonCodeConvertor
{
    static TreeMap<String, ArrayList<String>> tempFileExtract = null;

    public static void main(String[] args) throws Exception
    {
        tempFileExtract = readXLSFile("D:\\AEO_Mexico\\Localization\\Received_Translations\\Test_Reason_Codes.xls");
        String[] splitValue = null;

        String pattern = null;
        String replacement = null;
        String line = "";

        String currentFileName = "D:\\AEO_Mexico\\Localization\\Received_Translations\\Temp.sql";
        String currentOutFileName = "D:\\AEO_Mexico\\Localization\\Received_Translations\\Temp5.sql";
        File file = new File(currentFileName);
        String newLine=null;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        PrintWriter pw = new PrintWriter(new FileWriter(currentOutFileName));
        
        LinkedHashSet<String> set = new LinkedHashSet<String>();

        while ((line = reader.readLine()) != null)
        {
            for (Entry<String, ArrayList<String>> entry : tempFileExtract.entrySet())
            {
                // System.out.println("" + entry.getKey() + "\t" +
                // entry.getValue());

                pattern = entry.getKey();
                replacement = entry.getValue().toString();

                replacement = replacement.substring(1, replacement.length() - 1);

                if (line.contains(pattern))
                {
                    newLine = line.replace(pattern, replacement);
                    
                    if(!(pattern.equals(replacement)))
                    {
                        System.out.println(pattern + "       Replace with             " + replacement);
                    }
                    
                    set.add(newLine);
                    
                    /*pw.println(newLine);
                    pw.flush();*/
                    
                    newLine = null;
                }
            }
        }
        
        for(String s:set)
        {
            pw.println(s);
            pw.flush();
        }
        pw.close();
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

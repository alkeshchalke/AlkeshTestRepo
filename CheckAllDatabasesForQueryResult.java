package dbdata;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CheckAllDatabasesForQueryResult
{
    private static Logger logger = Logger.getLogger(CheckAllDatabasesForQueryResult.class);

    private static TreeMap<Integer, TransactionObject> transactionInfo = new TreeMap<Integer, TransactionObject>();

    public static void main(String[] args)
    {
        Connection connection = null;

        CheckAllDatabasesForQueryResult obj = new CheckAllDatabasesForQueryResult();

        // Read excel file for database details.
        ArrayList<String> columndata = obj.readDBInfoFromFile();

        // Create database url string along with user details.
        for (String s : columndata)
        {
            String[] databaseDetails = s.split(",");

            connection = obj.getConnection(databaseDetails[0], databaseDetails[1], databaseDetails[2]);

            obj.checkQueryResults(connection, databaseDetails[0], databaseDetails[1]);

            try
            {
                if (!(connection.isClosed()))
                {
                    obj.closeConnection(connection);
                }
            }
            catch (SQLException e)
            {
                logger.error("SQLException occurred.", e);
            }
        }

        if (transactionInfo.size() == 0)
        {
            logger.info("No records found...");
        }

    }

    private void checkQueryResults(Connection connection, String url, String user)
    {
        PreparedStatement stmt = null;
        int counter = 0;

        if (connection != null)
        {
            String query = "select * from co_dta_bmc";

            try
            {
                stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                while (rs.next())
                {
                    logger.info("Getting records from Back Office database from url " + url + " for user " + user);
                    
                    logger.info("\t" + rs.getString("DE_IMP_EXCP"));
                    
                    /*transactionInfo.put(
                            counter++,
                            new TransactionObject(rs.getString("DC_DY_BSN"), rs.getString("ID_STR_RT"), rs
                                    .getString("ID_WS"), rs.getInt("AI_TRN")));*/
                }

                rs.close();
                stmt.close();
            }
            catch (SQLException e)
            {
                logger.error("SQLException occurred..", e);
            }
            catch (Exception e)
            {
                logger.error("Unable to connect database as there was a problem in creating connections", e);
            }
            finally
            {
                closeConnection(connection);
            }
        }

    }

    public Connection getConnection(String url, String user, String password)
    {
        Connection conn = null;
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        catch (ClassNotFoundException e)
        {
            logger.error("JDBC driver not found " + e);
        }

        try
        {
            logger.info("Creating connection for user " + user + " for url " + url);
            conn = DriverManager.getConnection(url, user, password);
        }

        catch (SQLException e)
        {
            logger.error("Unable to connect to database for store " + e);
        }
        return conn;
    }

    public void closeConnection(Connection conn)
    {
        if (conn != null)
        {
            try
            {
                logger.info("Closing connection for " + conn);
                conn.close();
            }
            catch (SQLException e)
            {
                logger.error("Failed to close connections", e);
            }
        }
    }

    /**
     * This method will read file excel file and get database
     * connection details for databases.
     */

    private ArrayList<String> readDBInfoFromFile()
    {
        ArrayList<String> columndata = null;
        String fileLocation = "D:\\AEO_NorthAmerica\\Workspace\\BO_Workspace\\TestProject\\bin\\AEO_Environments_Sheet.xlsx";

        try
        {
            File f = new File(fileLocation);
            FileInputStream ios = new FileInputStream(f);
            XSSFWorkbook workbook = new XSSFWorkbook(ios);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            columndata = new ArrayList<>();
            StringBuilder databaseDetails = new StringBuilder();

            while (rowIterator.hasNext())
            {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext())
                {
                    Cell cell = cellIterator.next();

                    if (row.getRowNum() > 0)
                    {
                        if (cell.getColumnIndex() == 13)
                        {
                            databaseDetails.append("jdbc:oracle:thin:@" + cell.getStringCellValue() + ":1521:orcl,");
                        }
                        
                        //  Database Owner
                        
                        if (cell.getColumnIndex() == 14)
                        {
                            databaseDetails.append(cell.getStringCellValue().substring(0, cell.getStringCellValue().lastIndexOf("/")) + ",posbo");
                        }
                        
                        //  Data Source User
                        
                        /*if (cell.getColumnIndex() == 15)
                        {
                            databaseDetails.append(cell.getStringCellValue().substring(0, cell.getStringCellValue().lastIndexOf("/")) + ",dsbo");
                        }*/
                        
                        //  Scratchpad
                        
                        /*if (cell.getColumnIndex() == 16)
                        {
                            if(!(cell.getStringCellValue().endsWith("scratch")))
                            {
                                databaseDetails.append(cell.getStringCellValue().substring(0, cell.getStringCellValue().lastIndexOf("/")) + ",scratchdb");
                            }
                        }*/
                    }
                }
                if (databaseDetails.length() > 0)
                {
                    columndata.add(databaseDetails.toString());
                }
                databaseDetails = new StringBuilder();
            }
            ios.close();

        }
        catch (Exception e)
        {
            logger.error("Exception occurred while reading file data.", e);
        }
        return columndata;
    }

    /**
     * This inner class is created for storing transaction data in the TreeMap.
     */

    class TransactionObject
    {
        String businessDate;

        String storeID;

        String workstationID;

        int transactionNumber;

        public TransactionObject(String businessDate, String storeID, String workstationID, int transactionNumber)
        {
            this.businessDate = businessDate;
            this.storeID = storeID;
            this.workstationID = workstationID;
            this.transactionNumber = transactionNumber;
        }

        public String getBusinessDate()
        {
            return businessDate;
        }

        public void setBusinessDate(String businessDate)
        {
            this.businessDate = businessDate;
        }

        public String getStoreID()
        {
            return storeID;
        }

        public void setStoreID(String storeID)
        {
            this.storeID = storeID;
        }

        public String getWorkstationID()
        {
            return workstationID;
        }

        public void setWorkstationID(String workstationID)
        {
            this.workstationID = workstationID;
        }

        public int getTransactionNumber()
        {
            return transactionNumber;
        }

        public void setTransactionNumber(int transactionNumber)
        {
            this.transactionNumber = transactionNumber;
        }
    }
}

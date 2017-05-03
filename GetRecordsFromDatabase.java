package dbdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class GetRecordsFromDatabase
{
    private static TreeMap<String, TransactionEntity> dbInfo = new TreeMap<String, TransactionEntity>();

    private static Logger logger = Logger.getLogger(GetRecordsFromDatabase.class);

    public static void main(String[] args)
    {
        Connection conn_bo = null;
        Connection conn_co = null;
        String businessDate = null;

        GetRecordsFromDatabase object = new GetRecordsFromDatabase();

        // Read dbdetails.properties and load db details.
        object.loadDBInfo();

        // Get Business date from the user.

        System.out.print("Enter Business Date <yyyy-mm-dd>: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try
        {
            businessDate = br.readLine();
        }
        catch (IOException ioe)
        {
            logger.error("IO error trying to read business date!");
            System.exit(1);
        }

        conn_bo = dbInfo.get("bo_db").getConnection();
        conn_co = dbInfo.get("co_db").getConnection();

        object.getTransactionData(conn_bo, conn_co, businessDate);
    }

    /**
     * This method will read file dbdetails.properties and get database
     * connection details for BO and CO databases.
     */

    private void loadDBInfo()
    {
        Properties prop = new Properties();
        String dbpropertiesurl="D:\\AEO_NorthAmerica\\Workspace\\BO_Workspace\\TestProject\\dbdetails.properties";
        URL url = ClassLoader.getSystemResource("dbdetails.properties");
        try
        {
            prop.load(url.openStream());
            Set<Object> keys = prop.keySet();

            for (Object key : keys)
            {
                String value = prop.get(key).toString();
                String[] split = value.split(",");

                try
                {
                    if (key.equals("bo_db"))
                    {
                        String password = "posbo";
                        dbInfo.put(key.toString(), new TransactionEntity(split[0], split[1], password, key.toString()));
                    }
                    if (key.equals("co_db"))
                    {
                        String password = "posco";
                        dbInfo.put(key.toString(), new TransactionEntity(split[0], split[1], password, key.toString()));
                    }
                }
                catch (Exception e)
                {
                    logger.error("Got exception while retrieving passwords");
                }
            }
        }
        catch (IOException e)
        {
            logger.error("Error reading dbdetails.properties file. " + e);
        }
    }

    /**
     * This inner class will create database connections based on data read by
     * method loadDBInfo().
     */

    class TransactionEntity
    {
        String user;

        String password;

        String url;

        String store;

        public TransactionEntity(String url, String user, String password, String store)
        {
            this.url = url;
            this.user = user;
            this.password = password;
            this.store = store;
        }

        public Connection getConnection()
        {
            try
            {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            }
            catch (ClassNotFoundException e)
            {
                logger.error("JDBC driver not found " + e);
            }

            Connection conn = null;

            try
            {
                logger.info("Creating connection for user " + user);
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
    }

    /**
     * This method will get Transaction data for given business date.
     * 
     * @param Connection
     * @param Connection
     * @param String
     */

    public void getTransactionData(Connection conn_bo, Connection conn_co, String businessDate)
    {
        PreparedStatement stmt_bo = null;
        PreparedStatement stmt_co = null;

        TreeMap<Integer, TransactionObject> bo_transactionInfo = new TreeMap<Integer, TransactionObject>();
        TreeMap<Integer, TransactionObject> co_transactionInfo = new TreeMap<Integer, TransactionObject>();

        int counter = 0;

        try
        {
            if (conn_bo != null && conn_co != null && businessDate != null)
            {
                String bo_query = "SELECT * FROM TR_TRN WHERE DC_DY_BSN = '" + businessDate + "' ORDER BY TS_CRT_RCRD";
                stmt_bo = conn_bo.prepareStatement(bo_query);

                ResultSet bo_rs = stmt_bo.executeQuery();

                logger.info("Getting records from Back Office database..");

                while (bo_rs.next())
                {
                    bo_transactionInfo.put(
                            counter++,
                            new TransactionObject(bo_rs.getString("DC_DY_BSN"), bo_rs.getString("ID_STR_RT"), bo_rs
                                    .getString("ID_WS"), bo_rs.getInt("AI_TRN")));
                }

                logger.info("Total records from Back Office database are: " + counter);
                counter = 0;

                bo_rs.close();
                stmt_bo.close();

                if (!(bo_transactionInfo.isEmpty()))
                {
                    String co_query = "SELECT * FROM TR_TRN WHERE DC_DY_BSN = '" + businessDate + "' AND ID_STR_RT= '"
                            + bo_transactionInfo.get(0).getStoreID() + "' ORDER BY TS_CRT_RCRD";
                    stmt_co = conn_co.prepareStatement(co_query);

                    ResultSet co_rs = stmt_co.executeQuery();

                    logger.info("Getting records from Central Office database..");

                    while (co_rs.next())
                    {
                        co_transactionInfo.put(
                                counter++,
                                new TransactionObject(co_rs.getString("DC_DY_BSN"), co_rs.getString("ID_STR_RT"), co_rs
                                        .getString("ID_WS"), co_rs.getInt("AI_TRN")));
                    }

                    logger.info("Total records from Central Office database are: " + counter);

                    co_rs.close();
                    stmt_co.close();

                    writeTransactionDataToFile("D:\\BO_Records_" + businessDate + ".properties", bo_transactionInfo);
                    writeTransactionDataToFile("D:\\CO_Records_" + businessDate + ".properties", co_transactionInfo);
                }
            }
            else
            {
                logger.info("Unable to connect database as there was a problem in creating connections");
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occurred", e);
        }
        finally
        {
            try
            {
                if (stmt_bo != null)
                {
                    stmt_bo.close();
                }

                if (stmt_co != null)
                {
                    stmt_co.close();
                }

                if (conn_bo != null)
                {
                    conn_bo.close();
                }

                if (conn_co != null)
                {
                    conn_co.close();
                }

            }
            catch (Exception e)
            {
                logger.error("Exception occurred while closing connection", e);
            }
        }

    }

    /**
     * This method will write transaction data into a text file for comparison.
     * 
     * @param String
     * @param TreeMap
     */

    private void writeTransactionDataToFile(String outFileName, TreeMap<Integer, TransactionObject> transactionInfo)
    {
        File outfile = new File(outFileName);

        Writer writer = null;
        try
        {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));

            for (Map.Entry<Integer, TransactionObject> entry : transactionInfo.entrySet())
            {
                System.out.print("Index: " + entry.getKey());
                System.out.println("\tTransaction: " + entry.getValue().getStoreID() + "\t"
                        + entry.getValue().getWorkstationID() + "\t" + entry.getValue().getTransactionNumber() + "\t"
                        + entry.getValue().getBusinessDate());

                writer.write(entry.getValue().getStoreID() + "\t" + entry.getValue().getWorkstationID() + "\t"
                        + entry.getValue().getTransactionNumber() + "\t" + entry.getValue().getBusinessDate() + "\n");
            }
            writer.flush();
            writer.close();
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("Getting UnsupportedEncodingException", e);
        }
        catch (FileNotFoundException e)
        {
            logger.error("Getting FileNotFoundException", e);
        }
        catch (IOException e)
        {
            logger.error("Getting IOException", e);
        }
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

package dbdata;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public class SpecialCharacterInDatabase
{

    public static void main(String[] args)
    {
        boolean isUpdated = false;
        String value = readPropertiesFile();

        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("alkesh", value);

        if (value != null)
        {
            isUpdated = insertData(dataMap);

            // isUpdated=updateData(value);

            if (isUpdated)
            {
                readData(value);
            }
        }
    }

    private static String readPropertiesFile()
    {
        Properties prop = new Properties();
        URL url = ClassLoader.getSystemResource("dbdetails.properties");
        String value = null;
        try
        {
            prop.load(url.openStream());
            Set<Object> keys = prop.keySet();

            for (Object key : keys)
            {
                value = prop.get(key).toString();
                System.out.println("Key:" + key + " Value: " + value);
            }
        }
        catch (IOException e)
        {
            System.out.println("Error reading dbdetails.properties file. " + e);
        }

        return value;
    }

    private static boolean insertData(Map<String, String> dataMap)
    {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream output;
        try
        {
            output = new ObjectOutputStream(b);
            output.writeObject(dataMap);
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }

        boolean isUpdated = false;
        Connection conn = getConnection();

        String insertQuery = "INSERT INTO CO_APP_LOG(CTGY_APP_LOG,DC_MSG_LOG,TY_MSG_LOG,ID_MSG_LOG, DE_MSG_LOG) VALUES (?,?,?,?,?)";

        try
        {
            PreparedStatement pstmt = conn.prepareStatement(insertQuery);
            pstmt.setInt(1, 1);
            pstmt.setDate(2, new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
            pstmt.setString(3, "column3");
            pstmt.setString(4, "column4");
            pstmt.setClob(5, new InputStreamReader(new ByteArrayInputStream(b.toByteArray())));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0)
            {
                System.out.println("\nDone!!!");
                isUpdated = true;
            }

            pstmt.close();
        }

        catch (SQLException e)
        {
            e.printStackTrace();
        }

        closeConnection(conn);

        return isUpdated;
    }

    private static boolean updateData(String value)
    {
        boolean isUpdated = false;
        Connection conn = getConnection();

        String updateQuery = "UPDATE PA_EM SET ID_ALT = ? WHERE ID_LOGIN='posa'";

        try
        {
            PreparedStatement pstmt = conn.prepareStatement(updateQuery);
            pstmt.setString(1, value);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0)
            {
                System.out.println("\nDone!!!");
                isUpdated = true;
            }

            pstmt.close();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        closeConnection(conn);

        return isUpdated;
    }

    private static void readData(String value)
    {
        Connection conn = getConnection();

        String updateQuery = "SELECT DE_MSG_LOG FROM CO_APP_LOG";

        try
        {
            PreparedStatement pstmt = conn.prepareStatement(updateQuery);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {

                Clob clob = rs.getClob(1);
                byte[] st = IOUtils.toByteArray(clob.getCharacterStream());

                ByteArrayInputStream baip = new ByteArrayInputStream(st);
                ObjectInputStream ois = new ObjectInputStream(baip);
                Map<String, String> dataMap2 = (Map<String, String>)ois.readObject();

                System.out.println("\n\nChecking data : " + dataMap2.get("alkesh"));

            }

            pstmt.close();
            rs.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        closeConnection(conn);

    }

    private static Connection getConnection()
    {
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("JDBC driver not found " + e);
        }

        Connection conn = null;

        try
        {
            System.out.println("\nCreating connection for user aeoglobalch");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "aeoglobalch", "aeoglobalch");
        }

        catch (SQLException e)
        {
            System.out.println("Unable to connect to database for store " + e);
        }
        return conn;
    }

    private static void closeConnection(Connection conn)
    {
        if (conn != null)
        {
            try
            {
                System.out.println("\nClosing connection for " + conn);
                conn.close();
            }
            catch (SQLException e)
            {
                System.out.println("Failed to close connections");
            }
        }
    }
}

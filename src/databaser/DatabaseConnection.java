/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rcher
 */
public class DatabaseConnection {

    // Variable Declaration
    // Java Native Classes
    private Connection connection;
    private Savepoint startPoint;
    // Data Types
    private String connDatabase;
    private String connHost;
    private String connPassword;
    private String connTable;
    private String connUser;
    private long connPort;
    // End of Variable Declaration

    public DatabaseConnection(String connHost, long connPort, String connDatabase, String connUser, String connPassword) {

        //
        this.connHost = connHost;
        this.connPort = connPort;
        this.connDatabase = connDatabase;
        this.connUser = connUser;
        this.connPassword = connPassword;

        //
        try {

            // Create a new connection
            connection = DriverManager.getConnection("jdbc:mysql://" + connHost + ":" + connPort + "/" + connDatabase, connUser, connPassword);
            startPoint = connection.setSavepoint();
        } catch (SQLException sqle) {

            //
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, sqle.getMessage());
        }
    }

    public Statement createStatement() throws SQLException {

        //
        if (connection == null) {

            //
            try {

                // Create a new connection
                connection = DriverManager.getConnection("jdbc:mysql://" + connHost + ":" + connPort + "/" + connDatabase, connUser, connPassword);

                // If after creating that connection no successful connection was made then throw an error
            } catch (SQLException sqle) {

                //
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, sqle.getMessage());
            }
        }

        //
        return connection.createStatement();
    }

    public Object[] addPrefilledRow(DefaultTableModel model) throws SQLException {

        //
        final Statement statement = createStatement();

        // Return nothing if the connection wasn't establish.
        if (connection == null) {
            return null;
        }

        final Object[] data = new Object[getRowCount()];

        // Now query up the information we want which is the data type for each column
        ResultSet result = statement.executeQuery("SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA ='" + connDatabase + "' AND TABLE_NAME = '" + connTable + "'");
        result.first();
        final String primaryColumnName = getPKeyColumnName();
        final StringBuilder values = new StringBuilder();
        final StringBuilder columns = new StringBuilder();

        // There's definitely a more efficient way to do this.
        for (int i = 0; i < data.length; i++) {

            //
            final String dataType = result.getString(1);
            final String columnName = model.getColumnName(i);

            // If the data type for the column is that of a timestamp, auto fill with the current time;
            // even though once in the database it will be auto filled if the default_value is set to CURRENT_TIMESTAMP.
            if (dataType.equalsIgnoreCase("timestamp")) {

                // Create the date and format it for the format that MYSQL expects.
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                data[i] = dateFormat.format(new Date()).replace('/', '-');
            } else if (dataType.equalsIgnoreCase("varchar(45)") || dataType.equalsIgnoreCase("varchar")) {
                data[i] = "'null'";
            } else if (dataType.equalsIgnoreCase("double")) {
                data[i] = "0";
            } else if (dataType.equalsIgnoreCase("int")) {
                data[i] = "0";
            } else {
                data[i] = "'undef'";
            }

            // In the case that we're on the primary key, skip it.
            if (model.getColumnName(i).equalsIgnoreCase(primaryColumnName)) {

                // Advance
                result.next();
                continue;
            }

            // All the columns we're adding values to
            columns.append(i == data.length - 1 || i == data.length - 2 && model.getColumnName(i + 1).equalsIgnoreCase(primaryColumnName) ? columnName : columnName + ",");

            // Appending the values for those columns aboves.
            values.append(i == data.length - 1 || i == data.length - 2 && model.getColumnName(i + 1).equalsIgnoreCase(primaryColumnName) ? data[i] : data[i] + ",");

            // Advance
            result.next();
        }

        // Complete the query string with the bells and whistles
        final String query = "INSERT INTO " + connTable + " (" + columns + ") VALUES (" + values + ");";

        // Execute that on the database
        statement.executeUpdate(query);

        //
        return data;
    }

    public void sendChanges(DefaultTableModel model) {

        // Now after the additions and removals, process the remaining information.
        try {

            // Convience Data Types
            final String[] columnNames = prefetchColumnNames();

            String primaryKeyName = getPKeyColumnName();

            //  We only want to skip the Primary Key, but we can't always assume it's the first column in the database so search for it.
            int primaryKeyIndex = -1;

            // Determine the index of the Primary Key column
            for (int i = 0; i < columnNames.length; i++) {
                
                //
                if (columnNames[i].equalsIgnoreCase(primaryKeyName)) {
                    primaryKeyIndex = i;
                    break;
                }
            }

            // Our string builder
            final StringBuilder fields = new StringBuilder();

            // Consider every field in the table.
            for (int row = 0; row < model.getRowCount(); row++) {

                // Create one for every column
                for (int col = 0; col < columnNames.length; col++) {

                    //
                    if (col == primaryKeyIndex) {
                        continue;
                    }

                    // If we're the last column in the table or the column in front of us is the primary key and is the last column; don't add a comma.
                    fields.append((col == columnNames.length - 1 || (col == columnNames.length - 2 && col + 1 == primaryKeyIndex)) ? columnNames[col] + " = ? " : columnNames[col] + " = ?, ");
                }

                // Update the fields in the database where the primary key matches.
                final String query = "UPDATE " + connDatabase + "." + connTable + " SET " + fields + " WHERE " + columnNames[primaryKeyIndex] + " = '" + String.valueOf(model.getValueAt(row, primaryKeyIndex)).replace('/', '-') + "';";

                // We use prepared statement because we're going to fill in the "?"'s with values.
                final PreparedStatement state = connection.prepareStatement(query);

                // Applying those values in the database
                for (int paramIndex = 0; paramIndex < columnNames.length; paramIndex++) {

                    // Don't let the skip index get processed.
                    if (paramIndex != primaryKeyIndex) {

                        // The object in the table model
                        final Object value = model.getValueAt(row, paramIndex);

                        // Now change in database depending on class. <-- Update here if your value of [type] doesn't change in database.
                        if (value instanceof String) {
                            state.setString(paramIndex + 1, String.valueOf(value));
                        } else if (value instanceof Integer) {
                            state.setInt(paramIndex + 1, (int) value);
                        } else if (value instanceof Double) {
                            state.setDouble(paramIndex + 1, (double) value);
                        } else if (value instanceof Float) {
                            state.setFloat(paramIndex + 1, (float) value);
                        } else {
                            state.setObject(paramIndex + 1, value);
                        }
                    }
                }

                // Execute the changes to the database.
                state.executeUpdate();

                // Reset the substring.
                fields.setLength(0);
            }
        } catch (SQLException ex) {
            
            //
            java.util.logging.Logger.getLogger(DatabaseJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DefaultTableModel feedStatement(String str) throws SQLException {

        //
        final Statement statement = connection.createStatement();

        //
        final ResultSet result = statement.executeQuery(str);

        // Model and meta data.
        final DefaultTableModel model = new DefaultTableModel();
        final ResultSetMetaData meta = result.getMetaData();

        // Convience Data Types
        final int columns = meta.getColumnCount();
        final int rows = prefetchRowCount(result);
        final Object[][] data = new Object[rows][columns];

        //
        final String[] columnNames = prefetchColumnNames();

        // We put the result set to the end with the prefetching. So reset it.
        result.first();

        // For every row in the result set
        for (int i = 0; i < rows; i++) {

            // Create an object representing the value in the database.
            final Object[] arr = new Object[columns];

            // For every column add that as a collection of data.
            for (int j = 1; j <= columns; j++) {
                arr[j - 1] = result.getString(j);
            }

            // The data for the row is the column data from code above.
            data[result.getRow() - 1] = arr;

            // Move forward in row.
            result.next();
        }

        // Apply that data vector to the model
        model.setDataVector(data, columnNames);

        //
        return model;
    }

    public int feedUpdate(String str) throws SQLException {
        return connection.createStatement().executeUpdate(str);
    }

    // Mutators
    public void setDatabase(String connDatabase) {
        this.connDatabase = connDatabase;
    }

    public void setHost(String connHost) {
        this.connHost = connHost;
    }

    public void setTable(String connTable) {
        this.connTable = connTable;
    }

    public void setPort(long connPort) {
        this.connPort = connPort;
    }

    public void setUser(String connUser) {
        this.connUser = connUser;
    }

    public void setPassword(String connPassword) {
        this.connPassword = connPassword;
    }

    // Accessors
    public Connection getConnection() {
        return connection;
    }

    public String getDatabase() {
        return connDatabase;
    }

    public String getHost() {
        return connHost;
    }

    public String getUser() {
        return connUser;
    }

    public String getTable() {
        return connTable;
    }

    public long getPort() {
        return connPort;
    }

    public int getRowCount() throws SQLException {

        // First get the number of rows for the loop below
        ResultSet result = createStatement().executeQuery("SELECT COUNT(DATA_TYPE) FROM INFORMATION_SCHEMA.COLUMNS  WHERE TABLE_SCHEMA ='" + connDatabase + "' AND TABLE_NAME = '" + connTable + "'");
        result.first();

        // Our number of rows
        return result.getInt(1);
    }

    public int prefetchRowCount(ResultSet result) throws SQLException {

        // Output value
        int count = 0;

        // result.next() would be the value in the row
        while (result.next()) {
            count++;
        }

        // Reset the result set.
        result.first();

        //
        return count;
    }

    public String[] prefetchColumnNames() throws SQLException {

        // Our statement to the connection / driver
        final Statement statement = connection.createStatement();
        
        // Null check
        if (connDatabase  == null || connDatabase.equals("") || connTable == null || connTable.equals("")) {
            return null;
        }

        // Execute the query and get the result set
        final ResultSet result = statement.executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + connTable + "' AND TABLE_SCHEMA = '" + connDatabase + "';");
        final ArrayList<String> list = new ArrayList();

        // Add those column names.
        while (result.next()) {
            list.add(result.getString(1));
        }

        // Reset to beginning.
        result.first();

        //
        return list.toArray(new String[]{});
    }

    public String[] prefetchTableNames() throws SQLException {
        //
        final ArrayList<String> list;

        // Execute our result and grab our meta data
        try (Statement statement = connection.createStatement()) {

            //
            list = new ArrayList();

            // Change the query after we get the number of tables in the schema.
            ResultSet result = statement.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + connDatabase + "'");

            // This places us at the first table found.
            result.first();

            //
            do {

                //
                list.add(result.getString(1));
            } while (result.next());
        }

        // Return them.
        return list.toArray(new String[]{});
    }

    public String[] prefetchDatabaseNames() throws SQLException {

        final String[] output;

        //
        if (connection == null) {
            return null;
        }

        // Execute our query and get the result set
        try (Statement statement = createStatement()) {

            // Execute our query and get the result set
            ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM information_schema.SCHEMATA where schema_name not in ('mysql', 'information_schema', 'performance_schema');");

            // Set the result to the beginning.
            result.first();

            // Output now has a length equal to the number of tables in the schema.
            output = new String[result.getInt(1)];

            // Now grab all the database (schema) names from the connection that aren't useless like: mysql, performance_schema, information_schema.
            result = statement.executeQuery("SELECT SCHEMA_NAME FROM information_schema.SCHEMATA where SCHEMA_NAME not in ('mysql', 'performance_schema', 'information_schema');");
            result.first();

            // So let's make sure we have results from the count we made earlier.
            if (output.length > 0) {

                // Iterate over the rest of the result set.
                for (int i = 0; i < output.length; i++) {

                    //  Assign the name of the table to the index.
                    output[i] = result.getString(1);

                    // Move forward.
                    result.next();
                }
            }
        }

        // Return them.
        return output;
    }

    public int getPKeyIndex(DefaultTableModel model) {

        //
        final String primaryKeyName = getPKeyColumnName();

        //
        for (int i = 0; i < model.getColumnCount(); i++) {
            String s = String.valueOf(model.getColumnName(i));
            if (s.equalsIgnoreCase(primaryKeyName)) {
                return i;
            }
        }

        return -1;
    }

    public String getPKeyColumnName() {

        //
        if (connection != null) {

            //
            try {

                //
                final Statement statement = connection.createStatement();

                //
                final ResultSet result = statement.executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE CONSTRAINT_NAME = 'PRIMARY'"
                        + "AND TABLE_SCHEMA = '" + connDatabase + "'"
                        + "AND TABLE_NAME = '" + connTable + "';");

                //
                result.first();

                // Returns the Primary Key Name
                return result.getString(1);
            } catch (SQLException sqe) {
                
                //
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, sqe.getMessage());
            }
        }

        //
        return null;
    }
}

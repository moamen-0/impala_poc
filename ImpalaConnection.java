package sqlancer.impala;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Proof of concept for Apache Impala connection and schema extraction.
 * 
 * This class demonstrates the basic functionality needed to:
 * 1. Connect to an Apache Impala instance
 * 2. Create a test database
 * 3. Extract schema information
 * 
 * For GSoC 2025 project proposal: Adding Grammars to Test New Database Systems
 */
public class ImpalaConnection {

    private Connection connection;
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

    /**
     * Creates a new connection to Apache Impala.
     * 
     * @param host     Hostname of the Impala server
     * @param port     Port of the Impala server (default: 21050)
     * @param database Database name to use
     * @param user     Username for authentication
     * @param password Password for authentication
     */
    public ImpalaConnection(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    /**
     * Connects to the Impala instance.
     * 
     * @throws SQLException If connection fails
     */
    public void connect() throws SQLException {
        // Format: jdbc:impala://hostname:port/database
        String url = String.format("jdbc:impala://%s:%d/%s", host, port, database);
        
        // Connect with credentials if provided
        if (user != null && !user.isEmpty()) {
            connection = DriverManager.getConnection(url, user, password);
        } else {
            connection = DriverManager.getConnection(url);
        }
        
        System.out.println("Connected to Apache Impala instance at " + url);
    }

    /**
     * Creates a test database for SQLancer testing.
     * 
     * @param testDbName Name of the test database
     * @throws SQLException If database creation fails
     */
    public void createTestDatabase(String testDbName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP DATABASE IF EXISTS " + testDbName + " CASCADE");
            stmt.execute("CREATE DATABASE " + testDbName);
            stmt.execute("USE " + testDbName);
            System.out.println("Created test database: " + testDbName);
        }
    }

    /**
     * Lists tables in the current database.
     * 
     * @return List of table names
     * @throws SQLException If query fails
     */
    public List<String> getTables() throws SQLException {
        List<String> tables = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }
        
        return tables;
    }

    /**
     * Gets column information for a specific table.
     * 
     * @param tableName Table name to inspect
     * @return List of column names and their types
     * @throws SQLException If query fails
     */
    public List<Column> getTableColumns(String tableName) throws SQLException {
        List<Column> columns = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("DESCRIBE " + tableName);
            while (rs.next()) {
                String name = rs.getString(1);
                String type = rs.getString(2);
                columns.add(new Column(name, type));
            }
        }
        
        return columns;
    }

    /**
     * Represents a database column with name and type.
     */
    public static class Column {
        private final String name;
        private final String type;
        
        public Column(String name, String type) {
            this.name = name;
            this.type = type;
        }
        
        public String getName() {
            return name;
        }
        
        public String getType() {
            return type;
        }
        
        @Override
        public String toString() {
            return name + " (" + type + ")";
        }
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Simple test method demonstrating the functionality.
     */
    public static void main(String[] args) {
        ImpalaConnection impala = new ImpalaConnection("localhost", 21050, "default", "", "");
        try {
            // Connect to Impala
            impala.connect();
            
            // Create test database
            impala.createTestDatabase("sqlancer_test");
            
            // Create a sample table
            try (Statement stmt = impala.connection.createStatement()) {
                stmt.execute("CREATE TABLE test_table (id INT, name STRING, value DOUBLE)");
                System.out.println("Created test table");
                
                // Insert some data
                stmt.execute("INSERT INTO test_table VALUES (1, 'test1', 10.5), (2, 'test2', 20.3)");
                System.out.println("Inserted test data");
            }
            
            // List tables
            List<String> tables = impala.getTables();
            System.out.println("Tables in database: " + tables);
            
            // Get column information
            if (!tables.isEmpty()) {
                List<Column> columns = impala.getTableColumns(tables.get(0));
                System.out.println("Columns in " + tables.get(0) + ":");
                for (Column col : columns) {
                    System.out.println("  " + col);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            impala.close();
        }
    }
}

//package sqlancer.impala;

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
     * Mock implementation of ImpalaConnection for demonstration purposes.
     * This simulates the behavior of a real Impala connection.
     */
    public static class MockImpalaConnection extends ImpalaConnection {
        
        private boolean connected = false;
        private String currentDatabase = "default";
        
        /**
         * Creates a new mock Impala connection.
         */
        public MockImpalaConnection() {
            super("localhost", 21050, "default", "", "");
        }
        
        @Override
        public void connect() throws SQLException {
            connected = true;
            System.out.println("Connected to mock Apache Impala instance at jdbc:impala://localhost:21050/default");
        }
        
        @Override
        public void createTestDatabase(String testDbName) throws SQLException {
            if (!connected) {
                throw new SQLException("Not connected to Impala");
            }
            
            System.out.println("DROP DATABASE IF EXISTS " + testDbName + " CASCADE");
            System.out.println("CREATE DATABASE " + testDbName);
            System.out.println("USE " + testDbName);
            currentDatabase = testDbName;
            System.out.println("Created test database: " + testDbName);
        }
        
        @Override
        public List<String> getTables() throws SQLException {
            if (!connected) {
                throw new SQLException("Not connected to Impala");
            }
            
            // Simulate a query to list tables
            List<String> tables = new ArrayList<>();
            tables.add("test_table");
            return tables;
        }
        
        @Override
        public List<Column> getTableColumns(String tableName) throws SQLException {
            if (!connected) {
                throw new SQLException("Not connected to Impala");
            }
            
            // Simulate a query to get column information
            List<Column> columns = new ArrayList<>();
            columns.add(new Column("id", "INT"));
            columns.add(new Column("name", "STRING"));
            columns.add(new Column("value", "DOUBLE"));
            return columns;
        }
        
        /**
         * Creates a test table with sample data.
         */
        public void createTestTableWithData() throws SQLException {
            if (!connected) {
                throw new SQLException("Not connected to Impala");
            }
            
            System.out.println("CREATE TABLE test_table (id INT, name STRING, value DOUBLE)");
            System.out.println("Created test table");
            
            System.out.println("INSERT INTO test_table VALUES (1, 'test1', 10.5), (2, 'test2', 20.3)");
            System.out.println("Inserted test data");
        }
        
        @Override
        public void close() {
            if (connected) {
                connected = false;
                System.out.println("Mock connection closed");
            }
        }
        
        /**
         * Main method to demonstrate the mock functionality.
         */
        public static void main(String[] args) {
            System.out.println("Starting Impala connection test (mock implementation)...");
            
            // Create a mock Impala connection
            MockImpalaConnection impala = new MockImpalaConnection();
            
            try {
                // Connect to mock Impala
                impala.connect();
                
                // Create test database
                impala.createTestDatabase("sqlancer_test");
                
                // Create a sample table with data
                impala.createTestTableWithData();
                
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
                System.err.println("SQL Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Close connection
                impala.close();
            }
        }
    }
    
    /**
     * Simple test method demonstrating the functionality.
     */
    public static void main(String[] args) {
        System.out.println("Starting Impala connection test...");
        ImpalaConnection impala = null;
        try {
            // Try to load the Impala JDBC driver explicitly
            try {
                Class.forName("com.cloudera.impala.jdbc.Driver");
                System.out.println("Successfully loaded Impala JDBC driver");
            } catch (ClassNotFoundException e) {
                System.out.println("Could not load default Impala driver: " + e);
                
                // Try alternative driver class names
                try {
                    Class.forName("com.cloudera.impala.jdbc42.Driver");
                    System.out.println("Successfully loaded Impala JDBC42 driver");
                } catch (ClassNotFoundException e2) {
                    System.out.println("Could not load JDBC42 driver: " + e2);
                    
                    try {
                        Class.forName("com.cloudera.impala.jdbc41.Driver");
                        System.out.println("Successfully loaded Impala JDBC41 driver");
                    } catch (ClassNotFoundException e3) {
                        System.err.println("Failed to load any Impala JDBC driver");
                        System.err.println("Check your classpath and driver JAR file");
                        return; // Exit early, can't continue without driver
                    }
                }
            }
                    impala = new ImpalaConnection("localhost", 21050, "default", "", "");

       // ImpalaConnection impala = new ImpalaConnection("localhost", 21050, "default", "", "");
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
            System.err.println("SQL Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
        
    } catch (Exception e) {
        System.err.println("Unexpected error: " + e.getMessage());
        e.printStackTrace();
    } finally {
        if (impala != null) {
            impala.close();
        }
        }
    }
}
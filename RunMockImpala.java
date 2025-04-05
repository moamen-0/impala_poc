public class RunMockImpala {
    public static void main(String[] args) {
        System.out.println("Starting Impala connection test (mock implementation)...");
        
        // Create a mock Impala connection
        ImpalaConnection.MockImpalaConnection impala = new ImpalaConnection.MockImpalaConnection();
        
        try {
            // Connect to mock Impala
            impala.connect();
            
            // Create test database
            impala.createTestDatabase("sqlancer_test");
            
            // Create a sample table with data
            impala.createTestTableWithData();
            
            // List tables
            java.util.List<String> tables = impala.getTables();
            System.out.println("Tables in database: " + tables);
            
            // Get column information
            if (!tables.isEmpty()) {
                java.util.List<ImpalaConnection.Column> columns = impala.getTableColumns(tables.get(0));
                System.out.println("Columns in " + tables.get(0) + ":");
                for (ImpalaConnection.Column col : columns) {
                    System.out.println("  " + col);
                }
            }
            
        } catch (java.sql.SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close connection
            impala.close();
        }
    }
}
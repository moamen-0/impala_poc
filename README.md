# Apache Impala SQLancer Support - Proof of Concept

This directory contains a proof of concept implementation for supporting Apache Impala in SQLancer for the GSoC 2025 project proposal "Adding Grammars to Test New Database Systems."

## Overview

Apache Impala is an open-source, distributed SQL query engine for Apache Hadoop. This proof of concept demonstrates the basic capabilities needed to begin implementing full SQLancer support for Impala, focusing on connection management and schema extraction.

## Files Included

1. **ImpalaConnection.java** - A Java class demonstrating connection to Impala and basic schema operations
2. **RunMockImpala.java** - A runner class to demonstrate mock functionality without a real Impala instance

## Implementation Highlights

- **Connection Management**: Establishing JDBC connections to Impala
- **Schema Operations**: Creating databases, tables, and extracting schema information
- **Mock Implementation**: Working demonstration without requiring a full Impala setup
- **Error Handling**: Proper exception handling for database operations

## Setup Instructions

### Option 1: Using the Mock Implementation (Recommended)
javac ImpalaConnection.java RunMockImpala.java java RunMockImpala


### Option 2: Using a Real Impala Instance

1. Install Apache Impala
   - Option A: Use a Hadoop distribution with Impala (Cloudera CDH, CDP)
   - Option B: Use a Docker image: `docker pull smizy/impala:3.2.0-hadoop2.8.4`

2. Start Impala services
docker run -d --name impala-server -p 21050:21050 -p 25000:25000 -p 25010:25010 smizy/impala:3.2.0-hadoop2.8.4


3. Download the Impala JDBC driver from [Cloudera](https://www.cloudera.com/downloads/connectors/impala/jdbc.html)

4. Compile and run the test class
javac -cp /path/to/ImpalaJDBC42.jar ImpalaConnection.java java -cp .:/path/to/ImpalaJDBC42.jar ImpalaConnection


## Requirements

- Java 11 or higher
- For real implementation: Impala JDBC driver and running Impala instance

## Screenshots

- [Connection Establishment](screenshots/connection.png)
- [Database Creation](screenshots/database_creation.png)


## Next Steps for Full Implementation

A full implementation for SQLancer will require:

1. Creating proper SQLancer provider classes (`ImpalaProvider`, `ImpalaGlobalState`)
2. Implementing schema representation (`ImpalaSchema`, `ImpalaTable`, `ImpalaColumn`)
3. Developing complete grammar for Impala SQL syntax
4. Implementing expression generators for Impala-specific functions and types
5. Creating test oracles (NoREC, TLP) for automated bug finding
6. Adding Impala-specific options and configurations
7. Implementing comprehensive error handling

## References

- [Apache Impala Documentation](https://impala.apache.org/docs.html)
- [Impala SQL Reference](https://impala.apache.org/docs/build/html/topics/impala_langref.html)
- [SQLancer Contributing Guide](https://github.com/sqlancer/sqlancer/blob/master/CONTRIBUTING.md)
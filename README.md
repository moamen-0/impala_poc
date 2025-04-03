# Apache Impala SQLancer Support - Proof of Concept

This directory contains a proof of concept implementation for supporting Apache Impala in SQLancer for the GSoC 2025 project proposal "Adding Grammars to Test New Database Systems."

## Overview

Apache Impala is an open-source, distributed SQL query engine for Apache Hadoop. This proof of concept demonstrates the basic capabilities needed to begin implementing full SQLancer support for Impala using the SQL Generation Language (SGL) approach.

## Files Included

1. **ImpalaConnection.java** - A Java class demonstrating connection to Impala and basic schema operations
2. **impala_grammar.sgl** - A sample SGL grammar file for Apache Impala SQL syntax

## Setup Instructions

To run this proof of concept locally:

1. Install Apache Impala
   - Option 1: Use a Hadoop distribution with Impala (Cloudera CDH, CDP)
   - Option 2: Use a Docker image: `docker pull cloudera/quickstart:impala`

2. Start Impala services
   ```
   docker run -it --hostname=quickstart.cloudera --privileged=true -p 21050:21050 cloudera/quickstart:impala /usr/bin/docker-quickstart
   ```

3. Compile and run the test class
   ```
   javac -cp /path/to/impala-jdbc-driver.jar ImpalaConnection.java
   java -cp .:/path/to/impala-jdbc-driver.jar ImpalaConnection
   ```

## Requirements

- Java 11 or higher
- Impala JDBC driver
- Running Impala instance (local or remote)

## Next Steps for Full Implementation

This proof of concept is just the beginning. A full implementation for SQLancer will require:

1. Creating proper SQLancer provider classes
2. Implementing schema representation
3. Developing complete SGL grammar files
4. Implementing expression generators
5. Creating test oracles (NoREC, TLP)
6. Adding Impala-specific options and configurations
7. Implementing error handling

See the full project proposal and timeline documents for more details on the complete implementation plan.

## References

- [Apache Impala Documentation](https://impala.apache.org/docs.html)
- [Impala SQL Reference](https://impala.apache.org/docs/build/html/topics/impala_langref.html)
- [SQLancer Contributing Guide](https://github.com/sqlancer/sqlancer/blob/master/CONTRIBUTING.md)

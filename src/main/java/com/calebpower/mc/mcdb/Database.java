package com.calebpower.mc.mcdb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Database middleware to preload tables, set various metadata, and otherwise
 * establish connections to the underlying relational database.
 *
 * @author Caleb L. Power <cpower@axonibyte.com>
 */
public class Database {

  private HikariConfig hikariConfig = null;
  private HikariDataSource hikariDataSource = null;
  private String dbName = null;
  private int hashCode = 0;

  protected Database() { }

  /**
    * Overloaded constructor to set the database credentials.
    *
    * @param location the location of the database in the form
    *                 {@code location:port/name}
    * @param username the username to be used when accessing the database
    * @param password the password to be used when accessing the database
    * @param isSecure {@code true} iff the database connection should be secured
    * @param throws   SQLException if the database location and credentials are not
    *                 properly specified
    */
  public Database(String location, String username, String password, boolean isSecure) throws SQLException {
    this.hashCode = Objects.hash(location, username, password, isSecure);

    String[] locationArgs = location.split("/");
    if(locationArgs.length != 2)
      throw new SQLException(
          "Database location must include name of database (i.e.port/database)");
    
    this.dbName = locationArgs[1];
    
    this.hikariConfig = new HikariConfig();
    this.hikariConfig.setJdbcUrl(
        String.format(
            "jdbc:mysql://%1$s?autoReconnect=true&serverTimezone=UTC&useSSL=%2$b",
            location,
            isSecure));
    this.hikariConfig.setUsername(username);
    this.hikariConfig.setPassword(password);
    this.hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
    this.hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    this.hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    this.hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
    this.hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
    this.hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
    this.hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
    this.hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
    this.hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
    this.hikariConfig.addDataSourceProperty("maintainTimeState", "false");
    this.hikariConfig.addDataSourceProperty("connectionTimeout", "30000");
    this.hikariConfig.addDataSourceProperty("maxLifetime", "180000");
    this.hikariConfig.addDataSourceProperty("idleTimeout", "30000");
    this.hikariConfig.addDataSourceProperty("leakDetectionThreshold", "5000");
    // this.hikariConfig.addDataSourceProperty("logWriter", new PrintWriter(Logger.getInstance()));
  }

  /**
   * Starts the database driver.
   */
  public void init() {
    this.hikariDataSource = new HikariDataSource(hikariConfig);
  }

  /**
   * Retrieves the database connection.
   *
   * @return Connection the database connection
   * @throws SQLException to be thrown if the database failed to connect
   */
  public Connection getConnection() throws SQLException {
    return hikariDataSource.getConnection();
  }

  /**
   * Retrieves the name of the database.
   *
   * @return the database name
   */
  public String getName() {
    return dbName;
  }
  
  /**
   * Kills the database connection pool.
   */
  public void kill() {
    if(!hikariDataSource.isClosed()) hikariDataSource.close();
  }

  @Override public int hashCode() {
    return this.hashCode;
  }

  @Override public boolean equals(Object database) {
    return null != database && database instanceof Database && ((Database)database).hashCode == hashCode;
  }
  
}

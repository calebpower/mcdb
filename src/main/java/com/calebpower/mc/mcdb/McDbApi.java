package com.calebpower.mc.mcdb;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * An API used to retrieve database connections from the pool.
 *
 * @author Caleb L. Power <cpower@axonibyte.com>
 */
public abstract class McDbApi extends JavaPlugin {
  
  /**
   * Retrieves the database driver plugin, if it exists.
   *
   * @return the database driver plugin instance or
   *         {@code null} if it doesn't exist
   */
  public static McDbApi getInstance() {
    return (McDbApi)Bukkit.getServer().getPluginManager().getPlugin("McDbDriver");
  }
  
  /**
   * Adds a database to the driver.
   *
   * @param label the global label for the database
   * @param location the location of the database in the form {@code location:port/name}
   * @param username the username to be used when accessing the database
   * @param password the password to be used when accessing the database
   * @param isSecure {@code true} iff the database connection should be secured
   * @param throws SQLException if the database location and credentials are not
   *        properly specified
   * @return {@code true} iff the database was successfully added
   */
  public abstract boolean addDatabase(String label, String location,
      String username, String password, boolean isSecure) throws SQLException;
  
  /**
   * Removes a database from the driver.
   *
   * @param label the global label of the database
   * @return {@code true} iff the database was successfully removed
   */
  public abstract boolean removeDatabase(String label);

  /**
   * Retrieves the name of the database associated with the provided identifier,
   * perhaps for the purposes of populating database script templates.
   *
   * @param database the unique identification of the database in question
   * @return a {@link String} denoting the name of the database or
   *         {@code null} if no such database was found
   */
  public abstract String getDatabaseName(String database);
  
  /**
   * Retrieves a connection from the specified database.
   *
   * @param database the label associated with the database
   *        to be retrieved
   * @return a {@link Connection} to the specified database or
   *         {@code null} if no such database was established
   * @throws {@link SQLException} if a connection could not be made
   */
  public abstract Connection connect(String database) throws SQLException;
  
}

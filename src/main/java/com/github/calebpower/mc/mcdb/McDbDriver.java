package com.github.calebpower.mc.mcdb;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Spigot plugin designed to facilitate database connections.
 *
 * @author Caleb L. Power <cpower@axonibyte.com>
 */
public class McDbDriver extends McDbApi {

  private Map<String, Database> databases = new HashMap<>();
  private Map<Database, Set<String>> labels = new HashMap<>();

  @Override public void onEnable() {
    getLogger().log(Level.INFO, "Engineered by LordInateur");

    try {
      if(!getDataFolder().exists()) {
        getLogger().log(Level.INFO, "There appears to be no data folder. Creating data folder...");
        getDataFolder().mkdirs();
      }
      
      File cfgFile = new File(getDataFolder(), "config.json");

      if(!cfgFile.exists()) {
        getLogger().log(Level.WARNING, "A new config file was generated. Please update it and reload the plugin.");
        FileUtils.copyURLToFile(getClass().getResource("/config.json"), cfgFile);
        Bukkit.getPluginManager().disablePlugin(this);
      } else {
        StringBuilder sb = new StringBuilder();
        
        try(Scanner scanner = new Scanner(cfgFile)) {
          while(scanner.hasNext()) sb.append(scanner.nextLine().strip());
        }
        
        JSONObject cfgObj = new JSONObject(sb.toString());
        synchronized(databases) {
          for(Object obj : cfgObj.getJSONArray("databases")) {
            JSONObject dbObj = (JSONObject)obj;
            String label = dbObj.getString("label");
            
            if(!addDatabase(
                label,
                dbObj.getString("location"),
                dbObj.getString("username"),
                dbObj.getString("password"),
                dbObj.getBoolean("isSecure")))
              getLogger().log(
                  Level.SEVERE,
                  "Skipping duplicate \"{0}\" database entry.",
                  label);
          }
        }
      }
      
    } catch(IOException e) {
      getLogger().log(
          Level.SEVERE,
          "Could not load the contents of the data folder. {0}",
          null == e.getMessage() ? "No further info available." : e.getMessage());
      Bukkit.getPluginManager().disablePlugin(this);
    } catch(ClassCastException | JSONException e) {
      getLogger().log(
          Level.SEVERE,
          "Could not parse the configuration file: {0}",
          null == e.getMessage() ? "No further info available." : e.getMessage());
      Bukkit.getPluginManager().disablePlugin(this);
    } catch(SQLException e) {
      getLogger().log(
          Level.SEVERE,
          "Some database error occurred: {0}",
          null == e.getMessage() ? "No further info available." : e.getMessage());
      Bukkit.getPluginManager().disablePlugin(this);
    }
  }

  @Override public void onDisable() {
    synchronized(databases) {
      var dbEntries = this.databases.entrySet().iterator();
      while(dbEntries.hasNext()) {
        var database = dbEntries.next();
        getLogger().log(
            Level.INFO,
            "Disabling \"{0}\" database.",
            database.getKey());
        database.getValue().kill();
        dbEntries.remove();
      }
      this.databases = null;
    }
    getLogger().log(Level.INFO, "So long, and thanks for all the fish!");
  }

  @Override public boolean addDatabase(String label, String location,
      String username, String password, boolean isSecure) throws SQLException {
    synchronized(databases) {
      if(databases.containsKey(label)) return false;
      
      Database database = new Database(location, username, password, isSecure);
      
      if(labels.containsKey(database)) {
        getLogger().log(
            Level.INFO,
            "Database \"{0}\" was already known. Applying additional label.",
            label);
        database = databases.get(labels.get(database).iterator().next());
      } else {
        getLogger().log(
            Level.INFO,
            "Database \"{0}\" was not previously known. Instantiating now.",
            label);
        database.init();
        labels.put(database, new HashSet<>());
      }
      
      databases.put(label, database);
      labels.get(database).add(label);
      return true;
    }
  }

  @Override public boolean removeDatabase(String label) {
    synchronized(databases) {
      if(databases.containsKey(label)) return false;
      
      Database database = databases.get(label);
      
      if(labels.get(database).size() == 1) {
        getLogger().log(
            Level.INFO,
            "Database \"{0}\" is set to be terminated.",
            label);
        database.kill();
        labels.remove(database);
      } else {
        getLogger().log(
            Level.INFO,
            "Database \"{0}\" has an additional label, only its label will be disassociated.",
            label);
        labels.get(database).remove(label);
      }
      
      databases.remove(label);
      return true;
    }
  }
  
  @Override public Connection connect(String label) throws SQLException {
    synchronized(databases) {
      if(!databases.containsKey(label)) return null;
      return databases.get(label).getConnection();
    }
  }
  
}

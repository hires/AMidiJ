package org.andrewkilpatrick.amidij;

import java.util.HashMap;

public class SystemPortAliases {
    HashMap<String, String> sysToPortAlias;
    HashMap<String, String> portToSysAlias;

    /**
     * Creates a new system port alias for mapping port names to/from aliases.
     */
    public SystemPortAliases() {
        sysToPortAlias = new HashMap<>();
        portToSysAlias = new HashMap<>();
    }
    
    /**
     * Adds an alias mapping a sysName to an alias.
     * 
     * @param sysName the system port name
     * @param alias the alias
     */
    public void addAlias(String sysName, String alias) {
        sysToPortAlias.put(sysName, alias);
        portToSysAlias.put(alias, sysName);
    }
    
    /**
     * Removes an alias by sysName.
     * 
     * @param sysName the sysName of the alias to remove
     */
    public void removeAlias(String sysName) {
        if(sysToPortAlias.containsKey(sysName)) {
            String alias = sysToPortAlias.get(sysName);
            sysToPortAlias.remove(sysName);
            portToSysAlias.remove(alias);
        }
    }
    
    /**
     * Checks if the mapping contains a system name
     * 
     * @param sysName the sysName
     * @return true if there is an alias, false otherwise
     */
    public boolean containsSysName(String sysName) {
        return sysToPortAlias.containsKey(sysName);
    }
    
    /**
     * Gets an alias for a system name.
     * 
     * @param sysName the sysName to look up
     * @return the alias or null if not found
     */
    public String getAliasForSysName(String sysName) {
        return sysToPortAlias.get(sysName);
    }

    /**
     * Gets an alias for a system name, if the alias doesn't exist returns the system name.
     * 
     * @param sysName the system name
     * @return the alias if it exists, or the system name
     */
    public String getAliasForSysNameOrSysName(String sysName) {
        if(sysToPortAlias.containsKey(sysName)) {
            return sysToPortAlias.get(sysName);
        }
        return sysName;
    }
    
    /**
     * Checks if the mapping contains an alias.
     * 
     * @param alias the alias
     * @return true if the alias is found, false otherwise
     */
    public boolean containsAlias(String alias) {
        return portToSysAlias.containsKey(alias);
    }
    
    /**
     * Gets a system name for an alias.
     * 
     * @param alias the alias
     * @return the system name or null if not found
     */
    public String getSysNameForAlias(String alias) {
        return portToSysAlias.get(alias);
    }
    
    /**
     * Gets a system name for an alias.
     * 
     * @param alias the alias
     * @return the system name if it exists, or the alias
     */
    public String getSysNameForAliasOrAlias(String alias) {
        if(portToSysAlias.containsKey(alias)) {
            return portToSysAlias.get(alias);
        }
        return alias;
    }
}

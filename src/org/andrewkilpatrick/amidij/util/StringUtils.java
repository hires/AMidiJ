package org.andrewkilpatrick.amidij.util;

import org.andrewkilpatrick.amidij.AMidiJ;

public class StringUtils {
    
    /**
     * Makes a Jack input port name.
     * 
     * @param name the port name
     * @return a proper Jack port name
     */
    public static String makeInputName(String name) {
        return AMidiJ.clientName + "-in-" + name;
    }

    /**
     * Makes a Jack output port name.
     * 
     * @param name the port name
     * @return a proper Jack port name
     */
    public static String makeOutputName(String name) {
        return AMidiJ.clientName + "-out-" + name;
    }
    
    /**
     * Removes a Jack port prefix from the name.
     * 
     * @param jackPortName the full Jack port name
     * @return the port name without the Jack prefix
     */
    public static String removeJackPortPrefix(String jackPortName) {
        String inPrefix = makeInputName("");
        String outPrefix = makeOutputName("");
        if(jackPortName.startsWith(inPrefix)) {
            return jackPortName.substring(inPrefix.length());
        }
        if(jackPortName.startsWith(outPrefix)) {
            return jackPortName.substring(outPrefix.length());
        }
        return jackPortName;
    }
    
}

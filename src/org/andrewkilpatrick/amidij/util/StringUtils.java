/*
 * AMidiJ String Utils
 * 
 * Copyright 2020: Andrew Kilpatrick
 * Written by: Andrew Kilpatrick
 * 
 * This file is part of AMidiJ.
 *
 * AMidiJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AMidiJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AMidiJ.  If not, see <https://www.gnu.org/licenses/>.
 * 
 */
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

/*
 * Jack Port Name
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
package org.andrewkilpatrick.amidij.jack;

public class JackPortName {
    String clientName;
    String portName;
    
    public JackPortName(String clientName, String portName) {
        this.clientName = clientName;
        this.portName = portName;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public String getPortName() {
        return portName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof JackPortName)) {
            return false;
        }
        JackPortName port = (JackPortName)obj;
        if(!port.getClientName().equals(clientName)) {
            return false;
        }
        if(!port.getPortName().equals(portName)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return clientName.hashCode() + portName.hashCode();
    }
}

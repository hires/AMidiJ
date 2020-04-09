/*
 * Jack Patch Link
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

public class JackPatchLink {
    String ourPortName;  // port name without client name
    JackPortName otherPort;
    
    public JackPatchLink(String ourPortName, JackPortName otherPort) {
        this.ourPortName = ourPortName;
        this.otherPort = otherPort;
    }
    
    public String getOurPortName() {
        return ourPortName;
    }

    public JackPortName getOtherPort() {
        return otherPort;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof JackPatchLink)) {
            return false;
        }
        JackPatchLink link = (JackPatchLink)obj;
        if(!link.getOurPortName().equals(ourPortName)) {
            return false;
        }
        if(!link.getOtherPort().getClientName().equals(otherPort.getClientName())) {
            return false;
        }
        if(!link.getOtherPort().getPortName().equals(otherPort.getPortName())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return ourPortName.hashCode() + otherPort.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("our port: %s - other client: %s - port: %s",
                ourPortName, otherPort.getClientName(), otherPort.getPortName());
    }
}

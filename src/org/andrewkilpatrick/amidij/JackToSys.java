/*
 * Jack to System MIDI Map
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
package org.andrewkilpatrick.amidij;

import org.andrewkilpatrick.amidij.alsaMidi.SystemMidiInterface;
import org.jaudiolibs.jnajack.JackPort;

public class JackToSys {
    SystemMidiInterface sysMidi;  // system MIDI interface
    JackPort jackPort;  // to jack
    
    /**
     * Creates a JackToSys instance.
     * 
     * @param sysMidi the system MIDI interface
     * @param jackPort the jack port to read from
     */
    public JackToSys(SystemMidiInterface sysMidi, JackPort jackPort) {
        this.sysMidi = sysMidi;
        this.jackPort = jackPort;
    }
    
    /**
     * Gets the system MIDI interface.
     * 
     * @return the system MIDI interface
     */
    public SystemMidiInterface getSysMidi() {
        return sysMidi;
    }
    
    /**
     * Gets the JackPort.
     * 
     * @return the JackPort
     */
    public JackPort getJackPort() {
        return jackPort;
    }
}

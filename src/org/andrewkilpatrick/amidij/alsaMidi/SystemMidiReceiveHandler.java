/*
 * System MIDI Interface Receive Handler
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
package org.andrewkilpatrick.amidij.alsaMidi;

import javax.sound.midi.MidiMessage;

public interface SystemMidiReceiveHandler {
    
    /**
     * Handles a message received..
     * 
     * @param msg the message
     * @param source the MIDI handler that was the source
     */
	public void messageReceived(MidiMessage msg, SystemMidiInterface source);
	
}

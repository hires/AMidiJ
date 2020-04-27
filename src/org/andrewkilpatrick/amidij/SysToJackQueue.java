/*
 * System MIDI to Jack Queue
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

import java.util.concurrent.ConcurrentLinkedQueue;

import org.andrewkilpatrick.amidij.alsaMidi.SystemMidiInterface;
import org.jaudiolibs.jnajack.JackPort;

public class SysToJackQueue {
    SystemMidiInterface sysPort;  // from system
    JackPort jackPort;  // to jack
    ConcurrentLinkedQueue<TimedMessage> messageQueue;
    
    /**
     * Creates a SysToJack queue.
     * 
     * @param sysPort the system port
     * @param jackPort the jack port to send to
     */
    public SysToJackQueue(SystemMidiInterface sysPort, JackPort jackPort) {
        this.sysPort = sysPort;
        this.jackPort = jackPort;
        messageQueue = new ConcurrentLinkedQueue<>();
    }
    
    /**
     * Adds a message into the queue.
     * 
     * @param msg the message to add
     */
    public void addQueue(TimedMessage msg) {
        messageQueue.add(msg);
    }
    
    /**
     * Removes a message from the queue.
     * 
     * @return the message removed
     */
    public TimedMessage removeQueue() {
        return messageQueue.remove();
    }

    /**
     * Checks if there are messages available in the queue.
     * 
     * @return true if there are messages available, false otherwise
     */
    public boolean messageAvailable() {
        if(messageQueue.size() > 0) {
            return true;
        }
        return false;
    }
    
    /**
     * Gets the sys port.
     * 
     * @return the sys port
     */
    public SystemMidiInterface getSysPort() {
        return sysPort;
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

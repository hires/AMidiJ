package org.andrewkilpatrick.amidij;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.midi.MidiMessage;

import org.jaudiolibs.jnajack.JackPort;

public class SysToJackQueue {
    String sysPortName;  // system port name
    JackPort jackPort;  // to jack
    ConcurrentLinkedQueue<MidiMessage> messageQueue;
    
    /**
     * Creates a SysToJack queue.
     * 
     * @param sysPortName the system port name
     * @param jackPort the jack port to send to
     */
    public SysToJackQueue(String sysPortName, JackPort jackPort) {
        this.sysPortName = sysPortName;
        this.jackPort = jackPort;
        messageQueue = new ConcurrentLinkedQueue<>();
    }
    
    /**
     * Adds a message into the queue.
     * 
     * @param msg the message to add
     */
    public void addQueue(MidiMessage msg) {
        messageQueue.add(msg);
    }
    
    /**
     * Removes a message from the queue.
     * 
     * @return the message removed
     */
    public MidiMessage removeQueue() {
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
     * Gets the sys port name.
     * 
     * @return the sys port name
     */
    public String getSysPortName() {
        return sysPortName;
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

package org.andrewkilpatrick.amidij;

import javax.sound.midi.MidiMessage;

public class TimedMessage {
    MidiMessage msg;
    long timestamp;
    
    /**
     * Creates a TimedMessage.
     * 
     * @param msg the message
     * @param timestamp the timestamp
     */
    public TimedMessage(MidiMessage msg, long timestamp) {
        this.msg = msg;
        this.timestamp = timestamp;
    }
    
    /**
     * Gets the message.
     * 
     * @return the message
     */
    public MidiMessage getMessage() {
        return msg;
    }
    
    /**
     * Gets the timestamp.
     * 
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
}

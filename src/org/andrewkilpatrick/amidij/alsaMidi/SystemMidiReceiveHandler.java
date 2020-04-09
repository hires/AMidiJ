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

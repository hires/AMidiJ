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

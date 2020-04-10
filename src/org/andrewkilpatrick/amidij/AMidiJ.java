/*
 * AMidiJ - Main
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

import org.andrewkilpatrick.amidij.alsaMidi.SystemMidiInterface;
import org.andrewkilpatrick.amidij.alsaMidi.SystemMidiReceiveHandler;
import org.andrewkilpatrick.amidij.jack.JackClientAdapter;
import org.andrewkilpatrick.amidij.jack.JackClientAdapterException;
import org.andrewkilpatrick.amidij.jack.JackClientListener;
import org.andrewkilpatrick.amidij.jack.JackPatchLink;
import org.andrewkilpatrick.amidij.util.MidiMessageUtils;
import org.andrewkilpatrick.amidij.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.JackException;
import org.jaudiolibs.jnajack.JackMidi;
import org.jaudiolibs.jnajack.JackPort;

public class AMidiJ implements JackClientListener, SystemMidiReceiveHandler {
    Logger log;
    JackClientAdapter jackClient;
    public static String clientName = "amj";
    // known / registered system ports
    HashMap<String, JackPort> sysAvailableInputs;  // raw port, registered JackPort
    HashMap<String, JackPort> sysAvailableOutputs;  // raw port, registered JackPort
    HashMap<String, SystemMidiInterface> sysOpenInputs;  // raw port name, MIDI handler
    HashMap<String, SystemMidiInterface> sysOpenOutputs;  // raw port name, MIDI handler
    // routing
    HashMap<String, SysToJackQueue> sysToJackQueues;  // system port name, SysToJackQueue
    HashMap<String, JackToSys> jackToSysMap;  // jack port name, JackToSys instance
    
    /**
     * Creates and runs AMidiJ.
     * 
     * @param args command-line args
     */
    public AMidiJ(String args[]) {
        log = LogManager.getLogger(this.getClass());
        try {
            // start jack stuff
            jackClient = new JackClientAdapter("amidij");
            jackClient.registerJackClientListener(this);
            jackClient.registerJackClientListener(this);
            // start system stuff
            sysAvailableInputs = new HashMap<>();
            sysAvailableOutputs = new HashMap<>();
            sysOpenInputs = new HashMap<>();
            sysOpenOutputs = new HashMap<>();
            sysToJackQueues = new HashMap<>();
            jackToSysMap = new HashMap<>();
        } catch (JackClientAdapterException e) {
            log.error(e.toString());
            System.exit(1);
        }
        
        // poll for stuff
        while(true) {
            try {
                try {
                    scanSystemPorts();
                } catch (MidiUnavailableException e) {
                    log.error(e.toString());
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
        }
    }
    
    /*
     * Jack callbacks.
     */
    @Override
    public void availablePortsChanged() {
        log.debug("available ports changed");
    }

    @Override
    public void portConnected(JackPatchLink link) {
        String rawPortName = StringUtils.removeJackPortPrefix(link.getOurPortName());
        
        // connected input (from jack)
        if(jackClient.isPortNameMIDIInPort(link.getOurPortName())) {
            log.info("in from Jack port connected: " + link.getOurPortName() + " - raw name: " + rawPortName);
            try {
                SystemMidiInterface midi;
                // port is already open
                if(sysOpenOutputs.containsKey(rawPortName)) {
                    log.info("system port already opened: " + rawPortName);
                    midi = sysOpenInputs.get(rawPortName);
                }
                // first time using this port
                else {
                    midi = new SystemMidiInterface();
                    midi.openMIDIOutputPort(rawPortName);
                    sysOpenOutputs.put(rawPortName, midi);
                }
                jackToSysMap.put(rawPortName, new JackToSys(midi,
                    sysAvailableOutputs.get(rawPortName)));
            } catch (MidiUnavailableException e) {
                log.error(e.toString());
            }
        }
        // connected output (to jack)
        else if(jackClient.isPortNameMIDIOutPort(link.getOurPortName())) {
            log.info("out to Jack port connected: " + link.getOurPortName() + " - raw name: " + rawPortName);
            try {
                SystemMidiInterface midi;
                // port is already open
                if(sysOpenInputs.containsKey(rawPortName)) {
                    log.info("system port already opened: " + rawPortName);
                    midi = sysOpenInputs.get(rawPortName);
                }
                // first time using this port
                else {
                    midi = new SystemMidiInterface();
                    midi.openMIDIInputPort(rawPortName, this);
                    sysOpenInputs.put(rawPortName, midi);
                }
                sysToJackQueues.put(rawPortName, new SysToJackQueue(rawPortName,
                    sysAvailableInputs.get(rawPortName)));
            } catch (MidiUnavailableException e) {
                log.error(e.toString());
            }
        }
        else {
            log.error("port not found: " + link.getOurPortName());
        }
    }

    @Override
    public void portDisconnected(JackPatchLink link) {
        String rawPortName = StringUtils.removeJackPortPrefix(link.getOurPortName());
        log.info("Jack port disconnected: " + link.getOurPortName() + " - raw name: " + rawPortName);
        
        // disconnect input (from jack)
        if(jackClient.isPortNameMIDIInPort(link.getOurPortName())) {
            log.debug("in port (from jack)");
            if(!sysOpenOutputs.containsKey(rawPortName)) {
                log.error("system output port is not open: " + rawPortName);
                return;
            }
            // check to see if this is the only port connected
            String connectedPorts[] = jackClient.getConnectedPorts(link.getOurPortName());
            if(connectedPorts.length == 0) {
                log.info("no other ports are connected to this port - closing");
                SystemMidiInterface midi = sysOpenOutputs.get(rawPortName);
                midi.closeMIDIPorts();
                sysOpenOutputs.remove(rawPortName);
                jackToSysMap.remove(rawPortName);
            }
        }
        // disconnect output (to jack)
        else if(jackClient.isPortNameMIDIOutPort(link.getOurPortName())) {
            log.debug("out port (to jack)");
            if(!sysOpenInputs.containsKey(rawPortName)) {
                log.error("system input port is not open: " + rawPortName);
                return;
            }
            // check to see if this is the only port connected
            String connectedPorts[] = jackClient.getConnectedPorts(link.getOurPortName());
            if(connectedPorts.length == 0) {
                log.info("no other ports are connected to this port - closing");
                SystemMidiInterface midi = sysOpenInputs.get(rawPortName);
                midi.closeMIDIPorts();
                sysOpenInputs.remove(rawPortName);
                sysToJackQueues.remove(rawPortName);
            }
        }
        else {
            log.error("port not found: " + link.getOurPortName());
        }
    }

    @Override
    public boolean process(JackClient client, int nframes) {
        // process MIDI inputs (to Jack)
        Iterator<SysToJackQueue> iter = sysToJackQueues.values().iterator();
        while(iter.hasNext()) {
            SysToJackQueue queue = iter.next();
            JackPort port = queue.getJackPort();
            try {
                JackMidi.clearBuffer(port);
            } catch (JackException e) {
                log.error(e.toString());
            }
            while(queue.messageAvailable()) {
                MidiMessage msg = queue.removeQueue();
                int time = 0;
                try {
                    log.debug("to Jack MIDI port: " + port.getName() + " - msg length: " + msg.getLength());
                    JackMidi.eventWrite(port, time, msg.getMessage(), msg.getLength());
                } catch (JackException e) {
                    log.error(e.toString());
                }
            }
        }
        
        // process MIDI outputs (from Jack)
        Iterator<JackToSys> iter2 = jackToSysMap.values().iterator();
        while(iter2.hasNext()) {
            JackToSys j2s = iter2.next();
            JackPort port = j2s.getJackPort();
            SystemMidiInterface midi = j2s.getSysMidi();
            byte data[] = new byte[64];
            // port for new data
            try {
                JackMidi.Event event = new JackMidi.Event();
                for(int j = 0; j < JackMidi.getEventCount(port); j++) {
                    JackMidi.eventGet(event, port, j);
                    event.read(data);
//                    log.debug("from jack MIDI port: " + port.getName() + " - msg length: " + event.size());
                    // XXX SYSEX messages are probably not supported by this way of doing things
                    switch(event.size()) {
                        case 1:
                            midi.sendMessage(new ShortMessage(data[0] & 0xff));
                            break;
                        case 2:
                            midi.sendMessage(new ShortMessage(data[0] & 0xff,
                                data[1] & 0xff, 0));
                            break;
                        case 3:
                            midi.sendMessage(new ShortMessage(data[0] & 0xff,
                                    data[1] & 0xff, data[2] & 0xff));
                            break;
                        default:
                            log.error("unsupported message length: " + event.size());
                    }
                }
            } catch (JackException e) {
                log.error(e.toString());
            } catch (InvalidMidiDataException e) {
                log.error(e.toString());
            }
        }
        return true;
    }
    
    /**
     * Main!
     * 
     * @param args command-line args
     */
    public static void main(String[] args) {
        new AMidiJ(args);
    }
    
    /*
     * private methods
     */
    private void scanSystemPorts() throws MidiUnavailableException {
        // check for new system inputs
        LinkedList<String> inNames = SystemMidiInterface.getInputDeviceNames();
        for(String name : inNames) {
            // ignore this
            if(name.equals("Real Time Sequencer")) {
                continue;
            }
//            log.info("MIDI IN: " + name);
            // new port appeared
            if(!sysAvailableInputs.containsKey(name)) {
                log.info("new MIDI IN port appeared: " + name);
                try {
                    String portName = StringUtils.makeOutputName(name);
                    JackPort port = jackClient.registerMIDIOutPort(portName);
                    sysAvailableInputs.put(name, port);  // need to store the system name
                    log.info("system MIDI IN port registered as jack out: " + portName);
                } catch (JackException e) {
                    log.error(e.toString());
                }
            }
        }
        
        // check for system inputs that went away
        HashSet<String> inSet = new HashSet<>();
        for(String name : inNames) {
            inSet.add(name);
        }
        Iterator<String> iter = sysAvailableInputs.keySet().iterator();
        while(iter.hasNext()) {
            String name = iter.next();
            // port disappeared
            if(!inSet.contains(name)) {
                log.info("MIDI IN port disappeared: " + name);
                try {
                    String portName = StringUtils.makeOutputName(name);
                    jackClient.unregisterMIDIOutPort(portName);
                    iter.remove();
                    log.info("system MIDI IN port unregistered as jack out: " + portName);
                } catch (JackException e) {
                    log.error(e.toString());
                }
            }
        }
        
        // check for new outputs
        LinkedList<String> outNames = SystemMidiInterface.getOutputDeviceNames();
        for(String name : outNames) {
            // ignore this
            if(name.equals("Real Time Sequencer")) {
                continue;
            }
            if(name.equals("Gervill")) {
                continue;
            }
//            log.info("MIDI OUT: " + name);
            // new port appeared
            if(!sysAvailableOutputs.containsKey(name)) {
                log.info("new MIDI OUT port appeared: " + name);
                try {
                    String portName = StringUtils.makeInputName(name);
                    JackPort port = jackClient.registerMIDIInPort(portName);
                    sysAvailableOutputs.put(name, port);  // need to store the system name
                    log.info("system MIDI OUT port registered as jack in: " + portName);
                } catch (JackException e) {
                    log.error(e.toString());
                }
            }
        }

        // check for system inputs that went away
        HashSet<String> outSet = new HashSet<>();
        for(String name : outNames) {
            outSet.add(name);
        }
        iter = sysAvailableOutputs.keySet().iterator();
        while(iter.hasNext()) {
            String name = iter.next();
            // port disappeared
            if(!outSet.contains(name)) {
                log.info("MIDI OUT port disappeared: " + name);
                try {
                    String portName = StringUtils.makeInputName(name);
                    jackClient.unregisterMIDIInPort(portName);
                    iter.remove();
                    log.info("system MIDI OUT port unregistered as jack in: " + portName);
                } catch (JackException e) {
                    log.error(e.toString());
                }
            }
        }
    }

    @Override
    public void messageReceived(MidiMessage msg, SystemMidiInterface source) {
//        log.debug("got message from sys MIDI input: " + MidiMessageUtils.messageToString(msg));
        SysToJackQueue queue = sysToJackQueues.get(source.getInputDeviceNameOpened());
        if(queue != null) {
            queue.addQueue(msg);
        }
    }
}

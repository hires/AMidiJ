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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    long lastJackFrameCount = 0;
    // known / registered system ports
    HashMap<String, JackPort> sysAvailableInputs;  // alias port name, registered JackPort
    HashMap<String, JackPort> sysAvailableOutputs;  // alias port name, registered JackPort
    HashMap<String, SystemMidiInterface> sysOpenInputs;  // raw port name, MIDI handler
    HashMap<String, SystemMidiInterface> sysOpenOutputs;  // raw port name, MIDI handler
    // routing
    HashMap<String, SysToJackQueue> sysToJackQueues;  // system port name, SysToJackQueue
    HashMap<String, JackToSys> jackToSysMap;  // jack port name, JackToSys instance
    SystemPortAliases inPortAliases;
    SystemPortAliases outPortAliases;
    
    /**
     * Creates and runs AMidiJ.
     * 
     * @param args command-line args
     */
    public AMidiJ(String args[]) {
        log = LogManager.getLogger(this.getClass());
        inPortAliases = new SystemPortAliases();
        outPortAliases = new SystemPortAliases();
        for(int i = 0; i < args.length; i ++) {
            if(args[i].startsWith("--help")) {
                printUsage();
                System.exit(0);
            }
            else if(args[i].startsWith("--portaliases=")) {
                String parts[] = args[i].split("=");
                if(parts.length < 2) {
                    log.error("malformed argment: " + args[i]);
                    System.exit(1);
                }
                try {
                    loadSystemPortAliases(parts[1]);
                } catch (IOException e) {
                    log.error(e.toString());
                    System.exit(1);
                }
            }
        }
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
        String aliasName = StringUtils.removeJackPortPrefix(link.getOurPortName());
        
        // connected input (from jack)
        if(jackClient.isPortNameMIDIInPort(link.getOurPortName())) {
            log.info("in from Jack port connected: " + link.getOurPortName() + " - alias name: " + aliasName);
            try {
                SystemMidiInterface midi;
                String sysPortName = outPortAliases.getSysNameForAliasOrAlias(aliasName);
                // port is already open
                if(sysOpenOutputs.containsKey(sysPortName)) {
                    log.info("system port already opened: " + sysPortName);
                    midi = sysOpenOutputs.get(sysPortName);
                }
                // first time using this port
                else {
                    midi = new SystemMidiInterface();
                    midi.openMIDIOutputPort(sysPortName);
                    sysOpenOutputs.put(sysPortName, midi);
                }
                jackToSysMap.put(sysPortName, new JackToSys(midi,
                    sysAvailableOutputs.get(aliasName)));
            } catch (MidiUnavailableException e) {
                log.error(e.toString());
            }
        }
        // connected output (to jack)
        else if(jackClient.isPortNameMIDIOutPort(link.getOurPortName())) {
            log.info("out to Jack port connected: " + link.getOurPortName() + " - alias name: " + aliasName);
            try {
                SystemMidiInterface midi;
                String sysPortName = inPortAliases.getSysNameForAliasOrAlias(aliasName);
                // port is already open
                if(sysOpenInputs.containsKey(sysPortName)) {
                    log.info("system port already opened: " + sysPortName);
                    midi = sysOpenInputs.get(sysPortName);
                }
                // first time using this port
                else {
                    midi = new SystemMidiInterface();
                    midi.openMIDIInputPort(sysPortName, this);
                    sysOpenInputs.put(sysPortName, midi);
                }
                sysToJackQueues.put(sysPortName, new SysToJackQueue(
                    midi, sysAvailableInputs.get(aliasName)));
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
        String aliasName = StringUtils.removeJackPortPrefix(link.getOurPortName());
        log.info("Jack port disconnected: " + link.getOurPortName() + " - alias name: " + aliasName);
        
        // disconnect input (from jack)
        if(jackClient.isPortNameMIDIInPort(link.getOurPortName())) {
            log.debug("in port (from jack)");
            String sysPortName = outPortAliases.getSysNameForAliasOrAlias(aliasName);
            if(!sysOpenOutputs.containsKey(sysPortName)) {
                log.error("system output port is not open: " + sysPortName);
                return;
            }
            // check to see if this is the only port connected
            String connectedPorts[] = jackClient.getConnectedPorts(link.getOurPortName());
            if(connectedPorts.length == 0) {
                log.info("no other ports are connected to this port: " + sysPortName + " - closing");
                SystemMidiInterface midi = sysOpenOutputs.get(sysPortName);
                midi.closeMIDIPorts();
                sysOpenOutputs.remove(sysPortName);
                jackToSysMap.remove(sysPortName);
            }
        }
        // disconnect output (to jack)
        else if(jackClient.isPortNameMIDIOutPort(link.getOurPortName())) {
            log.debug("out port (to jack)");
            String sysPortName = inPortAliases.getSysNameForAliasOrAlias(aliasName);
            if(!sysOpenInputs.containsKey(sysPortName)) {
                log.error("system input port is not open: " + sysPortName);
                return;
            }
            // check to see if this is the only port connected
            String connectedPorts[] = jackClient.getConnectedPorts(link.getOurPortName());
            if(connectedPorts.length == 0) {
                log.info("no other ports are connected to this port: " + sysPortName + " - closing");
                SystemMidiInterface midi = sysOpenInputs.get(sysPortName);
                midi.closeMIDIPorts();
                sysOpenInputs.remove(sysPortName);
                sysToJackQueues.remove(sysPortName);
            }
        }
        else {
            log.error("port not found: " + link.getOurPortName());
        }
    }

    @Override
    public boolean process(JackClient client, int nframes) {
        lastJackFrameCount = jackClient.getLastFrameCount();  // last count of number of frames
        
        // process MIDI inputs (to Jack)
        Iterator<SysToJackQueue> iter = sysToJackQueues.values().iterator();
        while(iter.hasNext()) {
            SysToJackQueue queue = iter.next();
            SystemMidiInterface sysPort = queue.getSysPort();
            JackPort jackPort = queue.getJackPort();
            try {
                JackMidi.clearBuffer(jackPort);
            } catch (JackException e) {
                log.error(e.toString());
            }
            long sysPortTime = sysPort.getInputDevicePosition();
            while(queue.messageAvailable()) {
                TimedMessage tMsg = queue.removeQueue();
                MidiMessage msg = tMsg.getMessage();
                int offset = (int)(tMsg.getTimestamp() - lastJackFrameCount);  // offset in frames
                offset += nframes;  // push forward 1 buffer period
                // clamp to valid range
                if(offset < 0) {
                    offset = 0;
                }
                else if(offset >= nframes) {
                    offset = nframes - 1;
                }
                try {
                    JackMidi.eventWrite(jackPort, offset, msg.getMessage(), msg.getLength());
                } catch (JackException e) {
                    log.error(e.toString());
                    continue;
                }
            }
        }
        
        // process MIDI outputs (from Jack)
        Iterator<JackToSys> iter2 = jackToSysMap.values().iterator();
        while(iter2.hasNext()) {
            JackToSys j2s = iter2.next();
            JackPort port = j2s.getJackPort();
            SystemMidiInterface sysPort = j2s.getSysMidi();
            long sysPortTime = sysPort.getOutputDevicePosition();
            
            byte data[] = new byte[64];
            // poll for new data
            try {
                JackMidi.Event event = new JackMidi.Event();
                for(int j = 0; j < JackMidi.getEventCount(port); j++) {
                    JackMidi.eventGet(event, port, j);
                    event.read(data);
                    long jackOffset = jackClient.getCurrentFrameTime() - lastJackFrameCount;  // frames
                    long eventTime = event.time() - jackOffset + nframes;  // frames
                    long timestamp = sysPortTime + (long)((double)eventTime * jackClient.getFrameLengthSeconds());
                    if(timestamp < 0) {
                        timestamp = -1;
                    }
                    // XXX SYSEX messages are probably not supported by this way of doing things
                    switch(event.size()) {
                        case 1:
                            sysPort.sendMessage(new ShortMessage(data[0] & 0xff), timestamp);
                            break;
                        case 2:
                            sysPort.sendMessage(new ShortMessage(data[0] & 0xff,
                                data[1] & 0xff, 0), timestamp);
                            break;
                        case 3:
                            sysPort.sendMessage(new ShortMessage(data[0] & 0xff,
                                    data[1] & 0xff, data[2] & 0xff), timestamp);
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
            // new port appeared
            String aliasName = inPortAliases.getAliasForSysNameOrSysName(name);
            if(!sysAvailableInputs.containsKey(aliasName)) {
                try {
                    String portName = StringUtils.makeOutputName(aliasName);
                    JackPort port = jackClient.registerMIDIOutPort(portName, true);
                    sysAvailableInputs.put(aliasName, port);  // need to store the system name
                    log.info("system MIDI IN port: " + aliasName + " registered as jack out: " + portName);
                } catch (JackException e) {
                    log.error("error creating Jack OUT port: " + e.toString());
                }
            }
        }
        
        // check for system inputs that went away
        // use the list that was scanned from the system
        HashSet<String> inSet = new HashSet<>();
        for(String name : inNames) {
            inSet.add(inPortAliases.getAliasForSysNameOrSysName(name));  // convert to alias names
        }
        Iterator<String> iter = sysAvailableInputs.keySet().iterator();
        while(iter.hasNext()) {
            String aliasName = iter.next();
            // port disappeared
            if(!inSet.contains(aliasName)) {
                log.info("MIDI IN port disappeared: " + aliasName);
                try {
                    String portName = StringUtils.makeOutputName(aliasName);
                    jackClient.unregisterMIDIOutPort(portName);
                    iter.remove();
                    log.info("system MIDI IN port unregistered as jack out: " + portName);
                } catch (JackException e) {
                    log.error("error removing Jack OUT port: " + e.toString());
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
            // new port appeared
            String aliasName = outPortAliases.getAliasForSysNameOrSysName(name);
            if(!sysAvailableOutputs.containsKey(aliasName)) {
                try {
                    String portName = StringUtils.makeInputName(aliasName);
                    JackPort port = jackClient.registerMIDIInPort(portName, true);
                    sysAvailableOutputs.put(aliasName, port);  // need to store the system name
                    log.info("system MIDI OUT port: " + aliasName + " registered as jack in: " + portName);
                } catch (JackException e) {
                    log.error("error creating Jack IN port: " + e.toString());
                }
            }
        }

        // check for system inputs that went away
        HashSet<String> outSet = new HashSet<>();
        for(String name : outNames) {
            outSet.add(outPortAliases.getAliasForSysNameOrSysName(name));  // convert to alias names
        }
        iter = sysAvailableOutputs.keySet().iterator();
        while(iter.hasNext()) {
            String aliasName = iter.next();
            // port disappeared
            if(!outSet.contains(aliasName)) {
                log.info("MIDI OUT port disappeared: " + aliasName);
                try {
                    String portName = StringUtils.makeInputName(aliasName);
                    jackClient.unregisterMIDIInPort(portName);
                    iter.remove();
                    log.info("system MIDI OUT port unregistered as jack in: " + portName);
                } catch (JackException e) {
                    log.error("error removing Jack IN port: " + e.toString());
                }
            }
        }
    }

    /**
     * Loads a list of port aliases from a file to use as port names
     * 
     * @param filename the filename of port aliases
     * @throws IOException if there was a problem reading the file 
     */
    private void loadSystemPortAliases(String filename) throws IOException {
        log.info("loading system port aliases");
        BufferedReader in = new BufferedReader(new FileReader(filename));
        while(in.ready()) {
            String line = in.readLine().trim();
            // skip blank lines
            if(line.length() < 1) {
                continue;
            }
            // skip comments
            if(line.charAt(0) == '#') {
                continue;
            }
            String parts[] = line.split("=");
            if(parts.length != 3) {
                in.close();
                throw new IOException("malformed port alias: " + line);
            }
            // in aliases
            if(parts[0].equalsIgnoreCase("in")) {
                log.info("IN port alias: " + parts[1] + " = " + parts[2]);
                inPortAliases.addAlias(parts[1], parts[2]);
            }
            // out aliases
            else if(parts[0].equalsIgnoreCase("out")) {
                log.info("OUT port alias: " + parts[1] + " = " + parts[2]);
                outPortAliases.addAlias(parts[1], parts[2]);
            }
        }
        in.close();
    }
    
    /**
     * Prints program usage.
     */
    private void printUsage() {
        log.info("AMidiJ - usage:");
        log.info("  arguments:");
        log.info("    --help                  - print this message");
        log.info("    --portaliases=filename  - load a set of system port aliases");
        log.info("       format: {IN|OUT}=systemportname=alias");
    }
    
    @Override
    public void messageReceived(MidiMessage msg, long timestamp, SystemMidiInterface source) {
        // ignore active sensing
        if(msg.getStatus() == 0xfe) {
            return;
        }
//        log.debug("sys in - time: " + timestamp + " - " + MidiMessageUtils.messageToString(msg));
        long jackFrameTime = jackClient.getCurrentFrameTime();
//        log.debug("jackFrameTime: " + jackFrameTime);
        SysToJackQueue queue = sysToJackQueues.get(source.getInputDeviceNameOpened());
        if(queue != null) {
            queue.addQueue(new TimedMessage(msg, jackFrameTime));
        }
    }
}

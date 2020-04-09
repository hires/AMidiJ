/*
 * Jack MIDI Client Adapter
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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiolibs.jnajack.Jack;
import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.JackException;
import org.jaudiolibs.jnajack.JackOptions;
import org.jaudiolibs.jnajack.JackPort;
import org.jaudiolibs.jnajack.JackPortConnectCallback;
import org.jaudiolibs.jnajack.JackPortFlags;
import org.jaudiolibs.jnajack.JackPortRegistrationCallback;
import org.jaudiolibs.jnajack.JackPortType;
import org.jaudiolibs.jnajack.JackProcessCallback;
import org.jaudiolibs.jnajack.JackStatus;

public class JackClientAdapter implements JackPortConnectCallback, JackProcessCallback, JackPortRegistrationCallback {
    Logger log;
    Jack jack;
    JackClient jackClient;
    Object lock = new Object();
    HashSet<JackPortName> availableMidiInPorts;  // a list of MIDI in ports we might care about
    HashSet<JackPortName> availableMidiOutPorts;  // a list of MIDI out ports we might care about
    JackPatchStatus patchStatus;  // the current patch status for us
    JackClientListener jcl;
    HashMap<String, JackPort> registeredInPorts;  // port name, JackPort
    HashMap<String, JackPort> registeredOutPorts;  // port name, JackPort
    
    /**
     * Creates a JackMidiClient.
     * 
     * @param clientName the client name
     * @throws JackClientAdapterException if there is a problem setting up the client
     */
    public JackClientAdapter(String clientName) throws JackClientAdapterException {
        log = LogManager.getLogger(this.getClass());
        patchStatus = new JackPatchStatus();
        jcl = null;
        
        // set up Jack
        try {
            EnumSet<JackOptions> options = EnumSet.of(JackOptions.JackNoStartServer);
            EnumSet<JackStatus> status = EnumSet.noneOf(JackStatus.class);
            jack = Jack.getInstance();
            jackClient = jack.openClient(clientName, options, status);
            
            // create ports
            availableMidiInPorts = new HashSet<>();
            availableMidiOutPorts = new HashSet<>();
            scanPorts();
            
            // registered ports
            registeredInPorts = new HashMap<>();
            registeredOutPorts = new HashMap<>();
            
            // register callbacks
            jackClient.setProcessCallback(this);
            jackClient.setPortConnectCallback(this);
            jackClient.setPortRegistrationCallback(this);
            // start client
            jackClient.activate();
            log.debug("client name: " + jackClient.getName());
        } catch (JackException e) {
            throw new JackClientAdapterException(e.toString());
        }
        
        scanPorts();
        
        // wait to make sure the ports are scanned
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            log.error(e.toString());
        }
    }

    /**
     * Registers the listener for jack events.
     * 
     * @param listener the listener
     */
    public void registerJackClientListener(JackClientListener listener) {
        jcl = listener;
    }
    
    /**
     * Scans jack ports and makes a local cache.
     */
    private void scanPorts() {
        synchronized(lock) {
            try {
                availableMidiInPorts.clear();
                // MIDI input ports
                EnumSet<JackPortFlags> flags = EnumSet.of(JackPortFlags.JackPortIsOutput);
                String ports[] = jack.getPorts(jackClient, "", JackPortType.MIDI, flags);
                for (int i = 0; i < ports.length; i++) {
                    log.debug("jack MIDI input: " + ports[i]);
                    String parts[] = ports[i].split(":", 2);
                    if(parts.length < 2) {
                        continue;
                    }
                    // ignore our own ports
                    if(parts[0].equals(jackClient.getName())) {
                        continue;
                    }
                    availableMidiInPorts.add(new JackPortName(parts[0], parts[1]));
                }

                // MIDI output ports
                availableMidiOutPorts.clear();
                flags = EnumSet.of(JackPortFlags.JackPortIsInput);
                ports = jack.getPorts(jackClient, "", JackPortType.MIDI, flags);
                for (int i = 0; i < ports.length; i++) {
                    log.debug("jack MIDI output: " + ports[i]);
                    String parts[] = ports[i].split(":", 2);
                    if(parts.length < 2) {
                        continue;
                    }
                    // ignore our own ports
                    if(parts[0].equals(jackClient.getName())) {
                        continue;
                    }
                    availableMidiOutPorts.add(new JackPortName(parts[0], parts[1]));
                }

                // print some stats
                log.debug("available MIDI inputs: " + availableMidiInPorts.size());
                log.debug("available MIDI outputs: " + availableMidiOutPorts.size());

                // call listener
                if(jcl != null) {
                    jcl.availablePortsChanged();
                }
            } catch(JackException e) {
                log.error(e.toString());
            }
        }
    }
    
    /**
     * Registers a MIDI IN port. (from Jack)
     * 
     * @param name the port name to register
     * @returns the JackPort that was registered
     * @throws JackException if there is an error
     */
    public JackPort registerMIDIInPort(String name) throws JackException {
        EnumSet<JackPortFlags> flags = EnumSet.of(JackPortFlags.JackPortIsInput);
        JackPort port = jackClient.registerPort(name, JackPortType.MIDI, flags);
        registeredInPorts.put(name, port);
        return port;
    }

    /**
     * Unregisters a MIDI IN port. (from Jack)
     * 
     * @param name the port name to unregister
     * @throws JackException if the port is not registered
     */
    public void unregisterMIDIInPort(String name) throws JackException {
        JackPort port = registeredInPorts.get(name);
        if(port == null) {
            throw new JackException("port not registered");
        }
        jackClient.unregisterPort(port);
        registeredInPorts.remove(name);
    }

    /**
     * Checks if the port name is a MIDI IN port. (from Jack)
     * 
     * @param name the port name
     * @return true if the port name is an in port, false otherwise
     */
    public boolean isPortNameMIDIInPort(String name) {
        return registeredInPorts.containsKey(name);
    }
    
    /**
     * Registers a MIDI OUT port. (to Jack)
     * 
     * @param name the port name to register
     * @return the JackPort that was registered
     * @throws JackException if there is an error
     */
    public JackPort registerMIDIOutPort(String name) throws JackException {
        EnumSet<JackPortFlags> flags = EnumSet.of(JackPortFlags.JackPortIsOutput);
        JackPort port = jackClient.registerPort(name, JackPortType.MIDI, flags);
        registeredOutPorts.put(name, port);
        return port;
    }

    /**
     * Unregisters a MIDI OUT port. (to Jack);
     * 
     * @param name the port name to register
     * @throws JackException if the port name is not registered
     */
    public void unregisterMIDIOutPort(String name) throws JackException {
        JackPort port = registeredOutPorts.get(name);
        if(port == null) {
            throw new JackException("port not registered");
        }
        jackClient.unregisterPort(port);
        registeredOutPorts.remove(name);
    }

    /**
     * Checks if the port name is a MIDI OUT port. (to Jack)
     * 
     * @param name the port name
     * @return true if the port name is an out port, false otherwise
     */
    public boolean isPortNameMIDIOutPort(String name) {
        return registeredOutPorts.containsKey(name);
    }

    /**
     * Connect ports.
     * 
     * @param sourcePort the source port (must be an output)
     * @param destPort the destination port (must be an input)
     * @throws JackException if there is an error
     */
    public void connectPorts(JackPort sourcePort, JackPort destPort) throws JackException {
        jack.connect(jackClient, sourcePort.getName(), destPort.getName());
    }

    /**
     * Connect ports by name. This figures out the direction the ports should go.
     * 
     * @param ourPortName our port name
     * @param otherClientName the other client name
     * @param otherPortName the other port name
     * @throws JackException if there is an error
     */
    public void connectPortsNames(String ourPortName, String otherClientName, String otherPortName) throws JackException {
        boolean isOtherPortDest = false;
        JackPortName otherName = new JackPortName(otherClientName, otherPortName);
        // check the direction of the ports to make sure we map them in the right order
        Iterator<JackPortName> iter = availableMidiOutPorts.iterator();
        while(iter.hasNext()) {
            if(iter.next().equals(otherName)) {
                isOtherPortDest = true;
            }
        }
        // connect port
        if(isOtherPortDest) {
            jack.connect(jackClient, jackClient.getName() + ":" + ourPortName,
                    otherClientName + ":" + otherPortName);            
        }
        else {
            jack.connect(jackClient, otherClientName + ":" + otherPortName,            
                    jackClient.getName() + ":" + ourPortName);
        }
    }
    
    @Override
    public void portRegistered(JackClient client, String portFullName) {
        log.debug("port registered: " + portFullName);
        scanPorts();
    }

    @Override
    public void portUnregistered(JackClient client, String portFullName) {
        log.debug("port unregistered: " + portFullName);
        scanPorts();
    }
    
    @Override
    public void portsConnected(JackClient client, String portName1, String portName2) {
        log.debug("port connected: " + portName1 + " to " + portName2);
        String port1Parts[] = portName1.split(":", 2);
        String port2Parts[] = portName2.split(":", 2);
        if(port1Parts.length != 2) {
            return;
        }
        if(port2Parts.length != 2) {
            return;
        }
        String ourPortName;
        String clientName;
        String clientPortName;
        // is port1 our client?
        if(port1Parts[0].equals(jackClient.getName())) {
            ourPortName = port1Parts[1];
            clientName = port2Parts[0];
            clientPortName = port2Parts[1];
        }
        // is port2 our client?
        else if(port2Parts[0].equals(jackClient.getName())) {
            ourPortName = port2Parts[1];
            clientName = port1Parts[0];
            clientPortName = port1Parts[1];
        }
        // not our port
        else {
            return;
        }
        JackPatchLink newLink = new JackPatchLink(ourPortName,
                new JackPortName(clientName, clientPortName));
        try {
            // add the link
            patchStatus.addLink(newLink);
            patchStatus.printStatus();
            // call listener
            if(jcl != null) {
                jcl.portConnected(newLink);
            }
        } catch (JackClientAdapterException e) {
            log.error(e.toString());
        }
    }
    
    @Override
    public void portsDisconnected(JackClient client, String portName1, String portName2) {
        log.debug("port disconnected: " + portName1 + " from " + portName2);
        String port1Parts[] = portName1.split(":", 2);
        String port2Parts[] = portName2.split(":", 2);
        if(port1Parts.length != 2) {
            return;
        }
        if(port2Parts.length != 2) {
            return;
        }
        String ourPortName;
        String clientName;
        String clientPortName;
        // is port1 our client?
        if(port1Parts[0].equals(jackClient.getName())) {
            ourPortName = port1Parts[1];
            clientName = port2Parts[0];
            clientPortName = port2Parts[1];
        }
        // is port2 our client?
        else if(port2Parts[0].equals(jackClient.getName())) {
            ourPortName = port2Parts[1];
            clientName = port1Parts[0];
            clientPortName = port1Parts[1];
        }
        // not our port
        else {
            return;
        }
        JackPatchLink link = new JackPatchLink(ourPortName,
                new JackPortName(clientName, clientPortName));
        try {
            patchStatus.removeLink(link);
            patchStatus.printStatus();
            // call listener
            if(jcl != null) {
                jcl.portDisconnected(link);
            }
        } catch (JackClientAdapterException e) {
            log.error(e.toString());
        }
    }

    @Override
    public boolean process(JackClient client, int nframes) {
        if(jcl != null) {
            return jcl.process(client, nframes);
        }
        return true;
    }
}

/*
 * System MIDI Interface Wrapper
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

import java.util.LinkedList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class implements a MIDI handler.
 * 
 * @author andrew
 *
 */
public class SystemMidiInterface implements Receiver {
	Logger log;
	// input
	String inputDeviceName = "";
    MidiDevice inputDevice = null;
    Transmitter in = null;
    SystemMidiReceiveHandler mrh = null;
	// output
    String outputDeviceName = "";
	MidiDevice outputDevice = null;
	Receiver out = null;
	
	/**
	 * Creates a MIDIHandler with no ports open.
	 * 
	 * @throws MidiUnavailableException if there is an error getting MIDI port info
	 */
	public SystemMidiInterface() throws MidiUnavailableException {
		log = LogManager.getLogger(this.getClass());
		Thread t = new Thread() {
			public void run() {
				closeMIDIPorts();
			}
		};
		Runtime.getRuntime().addShutdownHook(t);
	}

	/**
	 * Gets a list of input device names.
	 * 
	 * @return a list of input device names
	 * @throws MidiUnavailableException if there is a problem getting port names
	 */
	public static LinkedList<String> getInputDeviceNames()
			throws MidiUnavailableException {
		LinkedList<String> inputNames = new LinkedList<String>();
		Info midiDevices[] = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < midiDevices.length; i++) {
			MidiDevice dev = MidiSystem.getMidiDevice(midiDevices[i]);
			// -1 = unlimited number of ports
			if (dev.getMaxTransmitters() != 0) {
				inputNames.addLast(midiDevices[i].getName());
			}
		}
		return inputNames;
	}
	/**
	 * Gets a list of output device names.
	 * 
	 * @return a list of output device names
	 * @throws MidiUnavailableException if there is a problem getting port names
	 */
	public static LinkedList<String> getOutputDeviceNames()
			throws MidiUnavailableException {
		LinkedList<String> outputNames = new LinkedList<String>();
		Info midiDevices[] = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < midiDevices.length; i++) {
			MidiDevice dev = MidiSystem.getMidiDevice(midiDevices[i]);
			// -1 = unlimited number of ports
			if (dev.getMaxReceivers() != 0) {
				outputNames.addLast(midiDevices[i].getName());
			}
		}
		return outputNames;
	}

	/**
	 * Gets a list of device names as a printable string.
	 * 
	 * @return a list of device names as a printable string.
	 * @throws MidiUnavailableException if there is an error getting device names
	 */
	public static String getDeviceNamePrintout() throws MidiUnavailableException {
		String msg;
		// print a list of valid midi devices
		LinkedList<String> inputNames = SystemMidiInterface.getInputDeviceNames();
		LinkedList<String> outputNames = SystemMidiInterface.getOutputDeviceNames();
		msg = "\nMIDI inputs:\n";
		for (int i = 0; i < inputNames.size(); i++) {
			msg += "dev: " + inputNames.get(i) + "\n";
		}
		msg += "\nMIDI outputs:\n";
		for (int i = 0; i < outputNames.size(); i++) {
			msg += "dev: " + outputNames.get(i) + "\n";
		}
		return msg;
	}

	/**
	 * Open MIDI input port by name.
	 * 
	 * @param inDevName the input device name to open
	 * @param receiveHandler the receive handler
	 * @throws MidiUnavailableException if there is a problem opening the port
	 */
	public void openMIDIInputPort(String inDevName,
	        SystemMidiReceiveHandler receiveHandler) throws MidiUnavailableException {
		// get device by searching names
		Info midiDevices[] = MidiSystem.getMidiDeviceInfo();
		int inputDevNum = -1;
		for(int i = 0; i < midiDevices.length; i++) {
			if(midiDevices[i].getName().toLowerCase().trim().startsWith(inDevName.toLowerCase().trim())
					&& MidiSystem.getMidiDevice(midiDevices[i])
							.getMaxTransmitters() != 0) {
				inputDevNum = i;
			}
		}
		if(inputDevNum < 0) {
			throw new MidiUnavailableException("MIDI input not found: " + inDevName);
		}
		
		if(inputDevNum >= 0) {
			log.info("opening MIDI in port: "
					+ midiDevices[inputDevNum].getName());
			inputDevice = MidiSystem.getMidiDevice(midiDevices[inputDevNum]);
			inputDevice.open();
			inputDeviceName = inDevName;
			in = inputDevice.getTransmitter();
			this.mrh = receiveHandler;
			in.setReceiver(this);
		}
	}

   /**
     * Open MIDI output port by name.
     * 
     * @param outDevName the output device name to open
     * @throws MidiUnavailableException if there is a problem opening the port
     */
    public void openMIDIOutputPort(String outDevName) throws MidiUnavailableException {
        // get device by searching names
        Info midiDevices[] = MidiSystem.getMidiDeviceInfo();
        int outputDevNum = -1;
        for(int i = 0; i < midiDevices.length; i++) {
            if(midiDevices[i].getName().toLowerCase().trim().startsWith(outDevName.toLowerCase().trim())
                    && MidiSystem.getMidiDevice(midiDevices[i])
                            .getMaxReceivers() != 0) {
                outputDevNum = i;
            }
        }
        if(outputDevNum < 0) {
            throw new MidiUnavailableException("MIDI output not found: " + outDevName);
        }

        if(outputDevNum >= 0) {
            log.info("opening MIDI out port: "
                    + midiDevices[outputDevNum].getName());
            outputDevice = MidiSystem.getMidiDevice(midiDevices[outputDevNum]);
            outputDevice.open();
            outputDeviceName = outDevName;
            out = outputDevice.getReceiver();
        }
    }
	
	/**
	 * Closes the MIDI ports.
	 */
	public void closeMIDIPorts() {
		log.info("closing MIDI port...");
		if(in != null) {
			in.close();
			in = null;
		}
		if(out != null) {
			out.close();
			out = null;
		}
	}
	
	/**
	 * Gets the input device name that is opened.
	 * 
	 * @return the input device name that is opened
	 */
	public String getInputDeviceNameOpened() {
		return inputDeviceName;
	}
	
	/**
	 * Gets the output device name that is opened.
	 * 
	 * @return the output device name that is opened
	 */
	public String getOutputDeviceNameOpened() {
		return outputDeviceName;
	}

	/**
	 * Gets the input device position in microseconds
	 * 
	 * @return the position in microseconds
	 */
	public long getInputDevicePosition() {
	    if(in == null) {
	        return -1;
	    }
	    return inputDevice.getMicrosecondPosition();
	}
	
	/**
	 * Gets the output device position in microseconds.
	 * 
	 * @return the position in microseconds
	 */
	public long getOutputDevicePosition() {
	    if(out == null) {
	        return -1;
	    }
	    return outputDevice.getMicrosecondPosition();
	}
	
	/**
	 * Sends a MIDI message to the output port.
	 * 
	 * @param msg the message to send
	 * @throws InvalidMidiDataException if there is an error sending the message
	 */
	public void sendMessage(MidiMessage msg) throws InvalidMidiDataException {
		if(out == null) {
			throw new InvalidMidiDataException("output port is not enabled");
		}
		out.send(msg, -1);
	}

   /**
     * Sends a MIDI message to the output port.
     * 
     * @param msg the message to send
     * @param timestamp the timestamp
     * @throws InvalidMidiDataException if there is an error sending the message
     */
    public void sendMessage(MidiMessage msg, long timestamp) throws InvalidMidiDataException {
        if(out == null) {
            throw new InvalidMidiDataException("output port is not enabled");
        }
        out.send(msg, timestamp);
    }

	/**
	 * Event handler for messages received by the MIDI port.
	 */
	@Override
	public void close() {
		log.info("MIDI output closing.");
		if (out != null) {
			out.close();
			out = null;
		}
		if (outputDevice != null) {
			outputDevice.close();
			outputDevice = null;
		}
	}

	/*
	 * This handles callbacks from received message.
	 */
	@Override
	public void send(MidiMessage message, long timestamp) {
	    // pitch bend fix
        if(message instanceof ShortMessage && (message.getStatus() & 0xf0) == 0xe0) {
            ShortMessage msg = (ShortMessage)message;
            int bend = msg.getData2() << 7 | msg.getData1();
            try {
                if(bend > 8191) {
                    bend -= 8192;
                }
                else {
                    bend += 8192;
                }
//                log.info("bend: " + bend);
                msg.setMessage(msg.getStatus(), bend & 0x7f, (bend >> 7) & 0x7f);
            } catch (InvalidMidiDataException e) {
                log.error(e.toString());
            }
            mrh.messageReceived(msg, timestamp, this);
            return;
        }
		mrh.messageReceived(message, timestamp, this);
	}
}

package org.andrewkilpatrick.amidij.util;

import java.time.Duration;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

public class MidiMessageUtils {
    // message type to use in NetworkMidiMessage header
    public enum MidiMessageType {
        SHORT_MESSAGE(0x80),  // this is just a fake type
        SYSEX_MESSAGE(0xf0),
        META_MESSAGE(0xff),
        UNKNOWN(-1);
        
        int type;
        
        private MidiMessageType(int type) {
            this.type = type;
        }
        
        public int getType() {
            return type;
        }
    };
    
    // meta message header types
    public enum MetaMessageType {
        SEQUENCE_NUMBER(0x00),
        TEXT(0x01),
        COPYRIGHT(0x02),
        SEQ_TRACK_NAME(0x03),
        INSTRUMENT_NAME(0x04),
        LYRIC(0x05),
        MARKER(0x06),
        CUE_POINT(0x07),
        MIDI_CHANNEL_PREFIX(0x20),
        MIDI_PORT_PREFIX(0x21),
        END_OF_TRACK(0x2f),
        SET_TEMPO(0x51),
        TIME_SIGNATURE(0x58),
        KEY_SIGNATURE(0x59),
        SEQ_SPECIFIC(0x7f),
        UNKNOWN(-1);

        int type;
        
        private MetaMessageType(int type) {
            this.type = type;
        }
        
        public int getType() {
            return type;
        }
    };
    
    // seq-specific message types
    public enum SeqSpecificType {
        PONG(0x4f),
        PING(0x50),
        UNKNOWN(-1);
        
        int type;
        
        private SeqSpecificType(int type) {
            this.type = type;
        }
        
        public int getType() {
            return type;
        }        
    };
    
    /// short message status bytes
    public enum ShortMessageType {
        NOTE_OFF(ShortMessage.NOTE_OFF),
        NOTE_ON(ShortMessage.NOTE_ON),
        POLY_PRESSURE(ShortMessage.POLY_PRESSURE),
        CONTROL_CHANGE(ShortMessage.CONTROL_CHANGE),
        PROGRAM_CHANGE(ShortMessage.PROGRAM_CHANGE),
        CHANNEL_PRESSURE(ShortMessage.CHANNEL_PRESSURE),
        PITCH_BEND(ShortMessage.PITCH_BEND),
        SONG_POSITION_POINTER(ShortMessage.SONG_POSITION_POINTER),
        SONG_SELECT(ShortMessage.SONG_SELECT),
        CLOCK_TICK(ShortMessage.TIMING_CLOCK),
        CLOCK_START(ShortMessage.START),
        CLOCK_CONTINUE(ShortMessage.CONTINUE),
        CLOCK_STOP(ShortMessage.STOP),
        ACTIVE_SENSING(ShortMessage.ACTIVE_SENSING),
        UNKNOWN(-1);
        
        int type;
        
        private ShortMessageType(int type) {
            this.type = type;
        }
        
        public int getType() {
            return type;
        }
    }
    
    /**
     * Gets the type of a MIDI message. This is based on the basic Java
     * MidiMessage subclass of ShortMessage, MetaMessage or SysexMessaeg.
     * 
     * @param message the message to check
     * @return the message type
     */
    public static MidiMessageType getMessageType(MidiMessage message) {
        if(message instanceof ShortMessage) {
            return MidiMessageType.SHORT_MESSAGE;
        }
        if(message instanceof MetaMessage) {
            return MidiMessageType.META_MESSAGE;
        }
        if(message instanceof SysexMessage) {
            return MidiMessageType.SYSEX_MESSAGE;
        }
        return MidiMessageType.UNKNOWN;
    }

    /**
     * Gets the type of a short message. This is based on the status byte.
     * 
     * @param message the message to check
     * @return the short message type
     */
    public static ShortMessageType getShortMessageType(MidiMessage message) {
        if(!(message instanceof ShortMessage)) {
            return ShortMessageType.UNKNOWN;
        }
        ShortMessage msg = (ShortMessage)message;
        switch(msg.getCommand()) {
        case ShortMessage.NOTE_OFF:
            return ShortMessageType.NOTE_OFF;
        case ShortMessage.NOTE_ON:
            return ShortMessageType.NOTE_ON;
        case ShortMessage.POLY_PRESSURE:
            return ShortMessageType.POLY_PRESSURE;
        case ShortMessage.CONTROL_CHANGE:
            return ShortMessageType.CONTROL_CHANGE;
        case ShortMessage.PROGRAM_CHANGE:
            return ShortMessageType.PROGRAM_CHANGE;
        case ShortMessage.CHANNEL_PRESSURE:
            return ShortMessageType.CHANNEL_PRESSURE;
        case ShortMessage.PITCH_BEND:
            return ShortMessageType.PITCH_BEND;
        default:
            // commands >= 0xf0
            switch(msg.getStatus()) {
            case ShortMessage.SONG_POSITION_POINTER:
                return ShortMessageType.SONG_POSITION_POINTER;
            case ShortMessage.SONG_SELECT:
                return ShortMessageType.SONG_SELECT;
            case ShortMessage.TIMING_CLOCK:
                return ShortMessageType.CLOCK_TICK;
            case ShortMessage.START:
                return ShortMessageType.CLOCK_START;
            case ShortMessage.CONTINUE:
                return ShortMessageType.CLOCK_CONTINUE;
            case ShortMessage.STOP:
                return ShortMessageType.CLOCK_STOP;
            case ShortMessage.ACTIVE_SENSING:
                return ShortMessageType.ACTIVE_SENSING;
            default:
                return ShortMessageType.UNKNOWN;
            }
        }
    }
    
    /**
     * Gets the type of a meta message. This is based on the meta type.
     * 
     * @param message the message to check
     * @return the meta message type
     */
    public static MetaMessageType getMetaMessageType(MidiMessage message) {
        if(!(message instanceof MetaMessage)) {
            return MetaMessageType.UNKNOWN;
        }
        switch(((MetaMessage)message).getType()) {
        case 0x00:
            return MetaMessageType.SEQUENCE_NUMBER;
        case 0x01:
            return MetaMessageType.TEXT;
        case 0x02:
            return MetaMessageType.COPYRIGHT;
        case 0x03:
            return MetaMessageType.SEQ_TRACK_NAME;
        case 0x04:
            return MetaMessageType.INSTRUMENT_NAME;
        case 0x05:
            return MetaMessageType.LYRIC;
        case 0x06:
            return MetaMessageType.MARKER;
        case 0x07:
            return MetaMessageType.CUE_POINT;
        case 0x20:
            return MetaMessageType.MIDI_CHANNEL_PREFIX;
        case 0x21:
            return MetaMessageType.MIDI_PORT_PREFIX;
        case 0x2f:
            return MetaMessageType.END_OF_TRACK;
        case 0x51:
            return MetaMessageType.SET_TEMPO;
        case 0x58:
            return MetaMessageType.TIME_SIGNATURE;
        case 0x59:
            return MetaMessageType.KEY_SIGNATURE;
        case 0x7f:
            return MetaMessageType.SEQ_SPECIFIC;
        default:
            return MetaMessageType.UNKNOWN;
        }
    }

    /**
     * Gets the type of a seq specific message. These are meta messages of type
     * SEQ_SPECIFIC. The first byte in the payload is the seq specific type.
     * 
     * @param message the message to check
     * @return the seq specific message type
     */
    public static SeqSpecificType getSeqSpecificType(MidiMessage message) {
        if(getMetaMessageType(message) != MetaMessageType.SEQ_SPECIFIC) {
            return SeqSpecificType.UNKNOWN;
        }
        MetaMessage mmsg = (MetaMessage)message;
        byte buf[] = mmsg.getData();
        if(buf.length < 1) {
            return SeqSpecificType.UNKNOWN;
        }
        switch(buf[0]) {
        case 0x4f:
            return SeqSpecificType.PONG;
        case 0x50:
            return SeqSpecificType.PING;
        default:
            return SeqSpecificType.UNKNOWN;
        }
    }
    
    /**
     * Fully compare two messages based on not only contents but deep comparison based
     * on the type of message and contents. For instance two note messages with the same
     * notes but different velocities are actually the same if they occupy the same time.
     * 
     * @param m1 the first message
     * @param m2 the second message
     * @return true if the messages are the same, false otherwise
     * @throws InvalidMidiDataException if there was a problem handling the messages
     */
    public static boolean compareFullyMessages(MidiMessage m1, MidiMessage m2) throws InvalidMidiDataException {
        if(getMessageType(m1) != getMessageType(m2)) {
            return false;
        }
        switch(getMessageType(m1)) {
        case META_MESSAGE:
            if(getMetaMessageType(m1) != getMetaMessageType(m2)) {
                return false;
            }
            switch(getMetaMessageType(m1)) {
            case COPYRIGHT:
            case CUE_POINT:
            case INSTRUMENT_NAME:
            case LYRIC:
            case MARKER:
            case SEQ_TRACK_NAME:
            case TEXT:
                if(!getMetaTextClassText((MetaMessage)m1).equals(getMetaTextClassText((MetaMessage)m2))) {
                    return false;
                }
                return true;
            case END_OF_TRACK:
                return true;
            case KEY_SIGNATURE:
                if(getMetaKeySignatureKey((MetaMessage)m1) != getMetaKeySignatureKey((MetaMessage)m2)) {
                    return false;
                }
                if(getMetaKeySignatureMinor((MetaMessage)m1) != getMetaKeySignatureMinor((MetaMessage)m2)) {
                    return false;
                }
                return true;
            case MIDI_CHANNEL_PREFIX:
                if(getMetaMidiChannelPrefixChannel((MetaMessage)m1) != getMetaMidiChannelPrefixChannel((MetaMessage)m2)) {
                    return false;
                }
                return true;
            case MIDI_PORT_PREFIX:
                if(getMetaMidiPortPrefixPort((MetaMessage)m1) != getMetaMidiPortPrefixPort((MetaMessage)m2)) {
                    return false;
                }
                return true;
            case SEQUENCE_NUMBER:
                if(getMetaSequenceNumberNum((MetaMessage)m1) != getMetaSequenceNumberNum((MetaMessage)m2)) {
                    return false;
                }
                return true;
            case SEQ_SPECIFIC:
                if(getSeqSpecificType(m1) != getSeqSpecificType(m2)) {
                    return false;
                }
                switch(getSeqSpecificType(m1)) {
                case PING:
                case PONG:
                    return true;
                case UNKNOWN:
                default:
                    return false;
                }
            case SET_TEMPO:
                if(getMetaSetTempoTempo((MetaMessage)m1) != getMetaSetTempoTempo((MetaMessage)m2)) {
                    return false;
                }
                return true;
            case TIME_SIGNATURE:
                if(getMetaTimeSignatureBeatsPerBar((MetaMessage)m1) != getMetaTimeSignatureBeatsPerBar((MetaMessage)m2)) {
                    return false;
                }
                if(getMetaTimeSignatureDenomNumber((MetaMessage)m1) != getMetaTimeSignatureDenomNumber((MetaMessage)m2)) {
                    return false;
                }
                if(getMetaTimeSignatureTicksPerClick((MetaMessage)m1) != getMetaTimeSignatureTicksPerClick((MetaMessage)m2)) {
                    return false;
                }
                if(getMetaTimeSignatureThirtySecondsPerQuarter((MetaMessage)m1) != getMetaTimeSignatureThirtySecondsPerQuarter((MetaMessage)m2)) {
                    return false;
                }
                return true;
            case UNKNOWN:
            default:
                return false;
            }
        case SHORT_MESSAGE:
            if(getShortMessageType(m1) != getShortMessageType(m2)) {
                return false;
            }
            ShortMessage smsg1 = (ShortMessage)m1;
            ShortMessage smsg2 = (ShortMessage)m2;
            switch(getShortMessageType(m1)) {
            case CHANNEL_PRESSURE:
                // same channel, any pressure
                if(smsg1.getChannel() != smsg2.getChannel()) {
                    return false;
                }
                return true;
            case CLOCK_CONTINUE:
            case CLOCK_START:
            case CLOCK_STOP:
            case CLOCK_TICK:
            case SONG_POSITION_POINTER:
            case SONG_SELECT:
            case ACTIVE_SENSING:
                return true;
            case CONTROL_CHANGE:
                // same channel and controller, any value
                if(smsg1.getChannel() != smsg2.getChannel()) {
                    return false;
                }
                if(smsg1.getData1() != smsg2.getData2()) {
                    return false;
                }
                return true;
            case NOTE_OFF:
            case NOTE_ON:
                // same channel and note, any velocity
                if(smsg1.getChannel() != smsg2.getChannel()) {
                    return false;
                }
                if(smsg1.getData1() != smsg2.getData2()) {
                    return false;
                }
                return true;
            case PITCH_BEND:
                // same channel, any bend
                if(smsg1.getChannel() != smsg2.getChannel()) {
                    return false;
                }
                return true;
            case POLY_PRESSURE:
                // same channel and note, any pressure
                if(smsg1.getChannel() != smsg2.getChannel()) {
                    return false;
                }
                if(smsg1.getData1() != smsg2.getData2()) {
                    return false;
                }
                return true;
            case PROGRAM_CHANGE:
                // same channel, any program
                if(smsg1.getChannel() != smsg2.getChannel()) {
                    return false;
                }
                return true;
            case UNKNOWN:
            default:
                return false;
            }
        case SYSEX_MESSAGE:
            SysexMessage sy1 = (SysexMessage)m1;
            return sy1.equals(m2);
        case UNKNOWN:
            return false;
        }
        return false;
    }
    
    /**
     * Converts a MidiMessage into a String that can be printed.
     * 
     * @param msg the message
     * @return a printable String or an error message if the supplied message is malformed
     */
    public static String messageToString(MidiMessage msg) {
        try {
            switch(getMessageType(msg)) {
            case META_MESSAGE:
                MetaMessage mmsg = (MetaMessage)msg;
                switch(getMetaMessageType(mmsg)) {
                case COPYRIGHT:
                    return "COPYRIGHT: " + getMetaTextClassText(mmsg);
                case CUE_POINT:
                    return "CUE POINT: " + getMetaTextClassText(mmsg);
                case END_OF_TRACK:
                    return "END OF TRACK";
                case INSTRUMENT_NAME:
                    return "INSTRUMENT NAME: " + getMetaTextClassText(mmsg);
                case KEY_SIGNATURE:
                    return "KEY SIGNATURE: " + 
                        formatKeyAsString(getMetaKeySignatureKey(mmsg),
                        getMetaKeySignatureMinor(mmsg));
                case LYRIC:
                    return "LYRIC: " + getMetaTextClassText(mmsg);
                case MARKER:
                    return "MARKER: " + getMetaTextClassText(mmsg);
                case MIDI_CHANNEL_PREFIX:
                    return "MIDI CHANNEL PREFIX: " + getMetaMidiChannelPrefixChannel(mmsg);
                case MIDI_PORT_PREFIX:
                    return "MIDI PORT PREFIX: " + getMetaMidiPortPrefixPort(mmsg);
                case SEQUENCE_NUMBER:
                    return "SEQUENCE NUMBER: " + getMetaSequenceNumberNum(mmsg);
                case SEQ_SPECIFIC:
                    byte buf[] = mmsg.getData();
                    return "SEQ SPECIFIC: " + formatByteBuffer(buf, buf.length); 
                case SEQ_TRACK_NAME:
                    return "SEQ TRACK NAME: " + getMetaTextClassText(mmsg);
                case SET_TEMPO:
                    return "SET TEMPO: " + String.format("%.1fBPM", getMetaSetTempoTempo(mmsg));
                case TEXT:
                    return "TEXT: " + getMetaTextClassText((MetaMessage)msg);
                case TIME_SIGNATURE:
                    return "TIME SIGNATURE: " + String.format("%d/%d - ticks/click: %d - 32nds/beat",
                        getMetaTimeSignatureBeatsPerBar(mmsg),
                        getMetaTimeSignatureDenomNumber(mmsg),
                        getMetaTimeSignatureTicksPerClick(mmsg),
                        getMetaTimeSignatureThirtySecondsPerQuarter(mmsg));
                case UNKNOWN:
                default:
                    return "UNKNOWN META MSG: " + formatByteBuffer(mmsg.getData(), mmsg.getLength());
                }
            case SHORT_MESSAGE:
                ShortMessage smsg = (ShortMessage)msg;
                switch(getShortMessageType(msg)) {
                case CHANNEL_PRESSURE:
                    return String.format("CHANNEL PRESSURE - chan: %d - pressure: %d",
                        smsg.getChannel(), getChannelPressurePressure(smsg));
                case CLOCK_CONTINUE:
                    return "CLOCK CONTINUE";
                case CLOCK_START:
                    return "CLOCK START";
                case CLOCK_STOP:
                    return "CLOCK STOP";
                case CLOCK_TICK:
                    return "CLOCK TICK";
                case CONTROL_CHANGE:
                    return String.format("CONTROL CHANGE - chan: %d - controller: %d - value: %d",
                        smsg.getChannel(), getControlChangeController(smsg), getControlChangeValue(smsg));
                case NOTE_OFF:
                    return String.format("NOTE OFF - chan: %d - note: %d - velocity: %d",
                        smsg.getChannel(), getNoteOffNote(smsg), getNoteOffVelocity(smsg));
                case NOTE_ON:
                    return String.format("NOTE ON - chan: %d - note: %d - velocity: %d",
                        smsg.getChannel(), getNoteOnNote(smsg), getNoteOnVelocity(smsg));
                case PITCH_BEND:
                    return String.format("PITCH BEND - chan: %d - bend: %d",
                        smsg.getChannel(), getPitchBendBend(smsg));
                case POLY_PRESSURE:
                    return String.format("POLY PRESSURE - chan: %d - note: %d - pressure: %d",
                        smsg.getChannel(), getPolyPressureNote(smsg), getPolyPressurePressure(smsg));
                case PROGRAM_CHANGE:
                    return String.format("PROGRAM CHANGE - chan: %d - program: %d",
                        smsg.getChannel(), getProgramChangeProgram(smsg));
                case SONG_POSITION_POINTER:
                    return "SONG POSITION POINTER: " + getSongPositionPointerPosition(smsg);
                case SONG_SELECT:
                    return "SONG SELECT: " + getSongSelectSong(smsg);
                case ACTIVE_SENSING:
                    return "ACTIVE SENSING";
                case UNKNOWN:
                default:                
                    return String.format("UNKNOWN SHORT MSG - stat: 0x%02x - data1: 0x%02x - data2: 0x%02x",
                        smsg.getStatus(), smsg.getData1(), smsg.getData2());
                }
            case SYSEX_MESSAGE:
                SysexMessage symsg = (SysexMessage)msg;
                return "SYSEX: " + formatByteBuffer(symsg.getData(), symsg.getLength());
            case UNKNOWN:
            default:
                return "UNKNOWN MIDI MSG: " + formatByteBuffer(msg.getMessage(), msg.getLength());
            }
        } catch(InvalidMidiDataException e) {
            return "ERROR parsing MIDI msg";
        }
    }
    
    /*
     * ShortMessage utils
     */ 
    /**
     * Strips the channel info from a ShortMessage.
     * 
     * @param msg the message to strip
     * @throws InvalidMidiDataException if there is a problem with the original message
     */
    public static void stripShortMessageChannel(ShortMessage msg) throws InvalidMidiDataException {
        msg.setMessage(msg.getCommand(), msg.getData1(), msg.getData2());
    }
    
    /**
     * Creates an ShortMessage with the channel changed. This makes a copy of the event.
     * 
     * @param msg the message
     * @param channel the new channel for the message
     * @return a copy of the message with the channel changed if needed
     * @throws InvalidMidiDataException if the message cannot be created
     */
    public static ShortMessage remapShortMessageChannel(ShortMessage msg, int channel) throws InvalidMidiDataException {
        if(msg.getStatus() < 0xf0) {
            return new ShortMessage(msg.getCommand() | (channel & 0x0f), msg.getData1(), msg.getData2());
        }
        return new ShortMessage(msg.getStatus(), msg.getData1(), msg.getData2());
    }
    
    /**
     * Converts a note on to off if the velocity is zero.
     * 
     * @param msg the message to convert in place
     * @throws InvalidMidiDataException if there is a problem with the original message
     */
    public static void noteOnToProperOff(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() == ShortMessage.NOTE_ON && msg.getData2() == 0) {
            msg.setMessage(ShortMessage.NOTE_OFF | msg.getChannel(),
                msg.getData1(), MidiProtocol.MIDI_NOTE_OFF_DEFAULT_VELOCITY);
        }
    }
    
    /**
     * Creates a note off message.
     * 
     * @param channel the channel
     * @param note the note
     * @param velocity the off velocity
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createNoteOffMessage(int channel, int note, int velocity) throws InvalidMidiDataException {
        if(channel < 0 || channel >= MidiProtocol.MIDI_NUM_CHANNELS) {
            throw new InvalidMidiDataException("channel invalid: " + channel);
        }
        if(note < 0 || note >= MidiProtocol.MIDI_NUM_NOTES) {
            throw new InvalidMidiDataException("note invalid: " + note);
        }
        if(velocity < 0 || velocity > MidiProtocol.MIDI_CONTROL_VALUE_MAX) {
            throw new InvalidMidiDataException("velocity invalid: " + velocity);
        }
        return new ShortMessage(ShortMessage.NOTE_OFF, channel, note, velocity);
    }

    /**
     * Gets the note from a note off message.
     * 
     * @param msg the message
     * @return the note of the message
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getNoteOffNote(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.NOTE_OFF) {
            throw new InvalidMidiDataException("not a note off message");
        }
        return msg.getData1();
    }
    
    /**
     * Gets the velocity from a note off message.
     * 
     * @param msg the message
     * @return the velocity of the message
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getNoteOffVelocity(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.NOTE_OFF) {
            throw new InvalidMidiDataException("not a note off message");
        }
        return msg.getData2();
    }
    
    /**
     * Creates a note on message.
     * 
     * @param channel the channel
     * @param note the note
     * @param velocity the velocity
     * @return a ShortMessage of the correct type - if the message has velocity == 0, a note off will be made
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createNoteOnMessage(int channel, int note, int velocity) throws InvalidMidiDataException {
        if(channel < 0 || channel >= MidiProtocol.MIDI_NUM_CHANNELS) {
            throw new InvalidMidiDataException("channel invalid: " + channel);
        }
        if(note < 0 || note >= MidiProtocol.MIDI_NUM_NOTES) {
            throw new InvalidMidiDataException("note invalid: " + note);
        }
        if(velocity < 0 || velocity > MidiProtocol.MIDI_CONTROL_VALUE_MAX) {
            throw new InvalidMidiDataException("velocity invalid: " + velocity);
        }
        // make note off instead
        if(velocity == 0) {
            return createNoteOffMessage(channel, note, 0);
        }
        return new ShortMessage(ShortMessage.NOTE_ON, channel, note, velocity);
    }

    /**
     * Gets the note from a note on message.
     * 
     * @param msg the message
     * @return the note of the message
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getNoteOnNote(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.NOTE_ON) {
            throw new InvalidMidiDataException("not a note on message");
        }
        return msg.getData1();
    }
    
    /**
     * Gets the velocity from a note on message.
     * 
     * @param msg the message
     * @return the velocity of the message
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getNoteOnVelocity(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.NOTE_ON) {
            throw new InvalidMidiDataException("not a note on message");
        }
        return msg.getData2();
    }

    /**
     * Creates a poly pressure message.
     * 
     * @param channel the channel
     * @param note the note
     * @param pressure the pressure
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createPolyPressureMessage(int channel, int note, int pressure) throws InvalidMidiDataException {
        if(channel < 0 || channel >= MidiProtocol.MIDI_NUM_CHANNELS) {
            throw new InvalidMidiDataException("channel invalid: " + channel);
        }
        if(note < 0 || note >= MidiProtocol.MIDI_NUM_NOTES) {
            throw new InvalidMidiDataException("note invalid: " + note);
        }
        if(pressure < 0 || pressure > MidiProtocol.MIDI_CONTROL_VALUE_MAX) {
            throw new InvalidMidiDataException("pressure invalid: " + pressure);
        }
        return new ShortMessage(ShortMessage.POLY_PRESSURE, channel, note, pressure);
    }

    /**
     * Gets the poly pressure note.
     * 
     * @param msg the message
     * @return the note
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getPolyPressureNote(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.POLY_PRESSURE) {
            throw new InvalidMidiDataException("not a poly pressure message");
        }
        return msg.getData1();
    }
    
    /**
     * Gets the poly pressure pressure.
     * 
     * @param msg the message
     * @return the pressure
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getPolyPressurePressure(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.POLY_PRESSURE) {
            throw new InvalidMidiDataException("not a poly pressure message");
        }
        return msg.getData2();
    }
    
    /**
     * Creates a control change message.
     * 
     * @param channel the channel
     * @param controller the controller
     * @param value the value
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createControlChangeMessage(int channel, int controller, int value) throws InvalidMidiDataException {
        if(channel < 0 || channel >= MidiProtocol.MIDI_NUM_CHANNELS) {
            throw new InvalidMidiDataException("channel invalid: " + channel);
        }
        if(controller < 0 || controller >= MidiProtocol.MIDI_NUM_CONTROLLERS) {
            throw new InvalidMidiDataException("controller invalid: " + controller);
        }
        if(value < 0 || value > MidiProtocol.MIDI_CONTROL_VALUE_MAX) {
            throw new InvalidMidiDataException("value invalid: " + value);
        }
        return new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, controller, value);
    }
    
    /**
     * Gets the control change controller.
     * 
     * @param msg the message
     * @return the controller
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getControlChangeController(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.CONTROL_CHANGE) {
            throw new InvalidMidiDataException("not a control change message");
        }
        return msg.getData1();
    }
    
    /**
     * Gets the control change value.
     * 
     * @param msg the message
     * @return the value
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getControlChangeValue(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.CONTROL_CHANGE) {
            throw new InvalidMidiDataException("not a control change message");
        }
        return msg.getData2();
    }

    /**
     * Creates a program change message.
     * 
     * @param channel the channel
     * @param program the program
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createProgramChangeMessage(int channel, int program) throws InvalidMidiDataException {
        if(channel < 0 || channel >= MidiProtocol.MIDI_NUM_CHANNELS) {
            throw new InvalidMidiDataException("channel invalid: " + channel);
        }
        if(program < 0 || program >= MidiProtocol.MIDI_NUM_PROGRAMS) {
            throw new InvalidMidiDataException("program invalid: " + program);
        }
        return new ShortMessage(ShortMessage.PROGRAM_CHANGE, channel, program, 0);
    }

    /**
     * Gets the program change program.
     * 
     * @param msg the message
     * @return the program
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getProgramChangeProgram(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.PROGRAM_CHANGE) {
            throw new InvalidMidiDataException("not a program change message");
        }
        return msg.getData1();
    }
    
    /**
     * Creates a channel pressure message.
     * 
     * @param channel the channel
     * @param pressure the pressure
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createChannelPressureMessage(int channel, int pressure) throws InvalidMidiDataException {
        if(channel < 0 || channel >= MidiProtocol.MIDI_NUM_CHANNELS) {
            throw new InvalidMidiDataException("channel invalid: " + channel);
        }
        if(pressure < 0 || pressure > MidiProtocol.MIDI_CONTROL_VALUE_MAX) {
            throw new InvalidMidiDataException("pressure invalid: " + pressure);
        }
        return new ShortMessage(ShortMessage.CHANNEL_PRESSURE, channel, pressure, 0);
    }

    /**
     * Gets the channel pressure pressure.
     * 
     * @param msg the message
     * @return the pressure
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getChannelPressurePressure(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.CHANNEL_PRESSURE) {
            throw new InvalidMidiDataException("not a channel pressure message");
        }
        return msg.getData1();
    }
    
    /**
     * Creates a pitch bend message.
     * 
     * @param channel the channel
     * @param bend the bend amount
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createPitchBendMessage(int channel, int bend) throws InvalidMidiDataException {
        if(channel < 0 || channel >= MidiProtocol.MIDI_NUM_CHANNELS) {
            throw new InvalidMidiDataException("channel invalid: " + channel);
        }
        if(bend < MidiProtocol.MIDI_PITCH_BEND_MIN || bend > MidiProtocol.MIDI_PITCH_BEND_MAX) {
            throw new InvalidMidiDataException("bend invalid: " + bend);
        }
        return new ShortMessage(ShortMessage.PITCH_BEND, channel,
            (bend + 8192) & 0x7f, ((bend + 8192) >> 7) & 0x7f);
    }

    /**
     * Gets the pitch bend bend.
     * 
     * @param msg the message
     * @return the bend
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getPitchBendBend(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.PITCH_BEND) {
            throw new InvalidMidiDataException("not a pitch bend message");
        }
        return (msg.getData1() | (msg.getData2() << 7)) - 8192;
    }
    
    /**
     * Creates a song position pointer message.
     * 
     * @param position the position
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createSongPositionPointerMessage(int position) throws InvalidMidiDataException {
        if(position < 0 || position > MidiProtocol.MIDI_SONG_POSITION_MAX) {
            throw new InvalidMidiDataException("position invalid: " + position);
        }
        return new ShortMessage(ShortMessage.SONG_POSITION_POINTER,
            position & 0x7f, (position >> 7) & 0x7f);
    }

    /**
     * Gets the song position pointer position.
     * 
     * @param msg the message
     * @return the position
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getSongPositionPointerPosition(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.SONG_POSITION_POINTER) {
            throw new InvalidMidiDataException("not a song position pointer message");
        }
        return (msg.getData1() | (msg.getData2() << 7));
    }
    
    /**
     * Creates a song select message.
     * 
     * @param song the song
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createSongSelectMessage(int song) throws InvalidMidiDataException {
        if(song < 0 || song >= MidiProtocol.MIDI_NUM_SONGS) {
            throw new InvalidMidiDataException("song invalid: " + song);
        }
        return new ShortMessage(ShortMessage.SONG_SELECT, song, 0);
    }

    /**
     * Gets the song select song.
     * 
     * @param msg the message
     * @return the song
     * @throws InvalidMidiDataException if the message is of an incorrect type
     */
    public static int getSongSelectSong(ShortMessage msg) throws InvalidMidiDataException {
        if(msg.getCommand() != ShortMessage.SONG_SELECT) {
            throw new InvalidMidiDataException("not a song select message");
        }
        return msg.getData1();
    }
    
    /**
     * Creates a clock tick message.
     * 
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createClockTickMessage() throws InvalidMidiDataException {
        return new ShortMessage(ShortMessage.TIMING_CLOCK);
    }
    
    /**
     * Creates a clock start message.
     * 
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createClockStartMessage() throws InvalidMidiDataException {
        return new ShortMessage(ShortMessage.START);
    }
    
    /**
     * Creates a clock continue message.
     * 
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createClockContinueMessage() throws InvalidMidiDataException {
        return new ShortMessage(ShortMessage.CONTINUE);
    }
    
    /**
     * Creates a clock stop message.
     * 
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createClockStopMessage() throws InvalidMidiDataException {
        return new ShortMessage(ShortMessage.STOP);
    }
    
    /**
     * Creates an active sensing message.
     * 
     * @return a ShortMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static ShortMessage createActiveSensingMessage() throws InvalidMidiDataException {
        return new ShortMessage(ShortMessage.ACTIVE_SENSING);
    }
    
    /*
     * MetaMessage utils
     */
    /**
     * Creates a seq-specific ping message.
     * 
     * @return the MetaMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static MetaMessage createPingMessage() throws InvalidMidiDataException {
        byte msgBuf[] = new byte[1];
        msgBuf[0] = (byte)(SeqSpecificType.PING.getType() & 0xff);
        try {
            return new MetaMessage(MetaMessageType.SEQ_SPECIFIC.getType(), msgBuf, msgBuf.length);
        } catch (InvalidMidiDataException e) {
            throw new InvalidMidiDataException(e.toString());
        }
    }
    
    /**
     * Creates a seq-specific pong message.
     * 
     * @return the MetaMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static MetaMessage createPongMessage() throws InvalidMidiDataException {
        byte msgBuf[] = new byte[1];
        msgBuf[0] = (byte)(SeqSpecificType.PONG.getType() & 0xff);
        try {
            return new MetaMessage(MetaMessageType.SEQ_SPECIFIC.getType(), msgBuf, msgBuf.length);
        } catch (InvalidMidiDataException e) {
            throw new InvalidMidiDataException(e.toString());
        }
    }
    
    /**
     * Creates a meta text class of message.
     * 
     * @param type the type of the message
     * @param text the text
     * @return the MetaMessage of the correct type
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static MetaMessage createMetaTextClassMessage(MetaMessageType type, String text) throws InvalidMidiDataException {
        switch(type) {
        case COPYRIGHT:
        case CUE_POINT:
        case INSTRUMENT_NAME:
        case LYRIC:
        case MARKER:
        case SEQ_TRACK_NAME:
        case TEXT:
            byte textBuf[] = new byte[text.length()];
            for(int i = 0; i < text.length(); i ++) {
                textBuf[i] = (byte)text.charAt(i);
            }
            return new MetaMessage(type.getType(), textBuf, textBuf.length);
        default:
            throw new InvalidMidiDataException("not a text type: " + type.getType());        
        }        
    }

    /**
     * Gets the text from a text class meta message.
     * 
     * @param msg the message
     * @return the text
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static String getMetaTextClassText(MetaMessage msg) throws InvalidMidiDataException {
        switch(getMetaMessageType(msg)) {
        case COPYRIGHT:
        case CUE_POINT:
        case INSTRUMENT_NAME:
        case LYRIC:
        case MARKER:
        case SEQ_TRACK_NAME:
        case TEXT:
            if(msg.getLength() < 1) {
                throw new InvalidMidiDataException("no data in message");
            }
            byte buf[] = msg.getData();
            return new String(buf, 0, buf.length);
        default:
            throw new InvalidMidiDataException("not a text class meta message");
        }
    }
    
    /**
     * Creates a meta end of track message.
     * 
     * @return a meta end of track message
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static MetaMessage createMetaEndOfTrackMessage() throws InvalidMidiDataException {
        return new MetaMessage(MetaMessageType.END_OF_TRACK.getType(), null, 0);
    }
    
    /**
     * Creates a meta key signature message.
     * 
     * @param key the key in sharps (+) or flats (-)
     * @param minor true if the key is minor, false otherwise
     * @return a meta key signature message
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static MetaMessage createMetaKeySignatureMessage(int key, boolean minor) throws InvalidMidiDataException {
        if(key < -7 || key > 7) {
            throw new InvalidMidiDataException("key invalid: " + key);
        }
        byte buf[] = new byte[2];
        buf[0] = (byte)key;
        if(minor) {
            buf[1] = 1;
        }
        else {
            buf[1] = 0;
        }
        return new MetaMessage(MetaMessageType.KEY_SIGNATURE.getType(), buf, buf.length); 
    }
    
    /**
     * Gets the key from a meta key signature message.
     * 
     * @param msg the message
     * @return the key in sharps (+) or flats (-)
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static int getMetaKeySignatureKey(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.KEY_SIGNATURE) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 2) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        return (int)buf[0];
    }

    /**
     * Gets the minor flag from a meta key signature message.
     * 
     * @param msg the message
     * @return true if the key is minor, false otherwise
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static boolean getMetaKeySignatureMinor(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.KEY_SIGNATURE) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 2) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        if(buf[1] == 1) {
            return true;
        }
        return false;
    }
    
    /**
     * Creates a meta MIDI channel prefix message.
     * 
     * @param channel the channel
     * @return a meta MIDI channel prefix message
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static MetaMessage createMetaMidiChannelPrefixMessage(int channel) throws InvalidMidiDataException {
        if(channel < 0 || channel > MidiProtocol.MIDI_NUM_CHANNELS - 1) {
            throw new InvalidMidiDataException("channel invalid: " + channel);
        }
        byte buf[] = new byte[1];
        buf[0] = (byte)channel;
        return new MetaMessage(MetaMessageType.MIDI_CHANNEL_PREFIX.getType(), buf, buf.length);
    }
    
    /**
     * Gets the channel for a meta MIDI channel prefix message.
     * 
     * @param msg the message
     * @return the channel
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static int getMetaMidiChannelPrefixChannel(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.MIDI_CHANNEL_PREFIX) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 1) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        return (int)(buf[0] & 0xff);
    }
    
    /**
     * Creates a meta MIDI port prefix message.
     * 
     * @param port the port
     * @return a meta MIDI port prefix message
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static MetaMessage createMetaMidiPortPrefixMessage(int port) throws InvalidMidiDataException {
        if(port < 0 || port > 0xff) {
            throw new InvalidMidiDataException("port invalid: " + port);
        }
        byte buf[] = new byte[1];
        buf[0] = (byte)port;
        return new MetaMessage(MetaMessageType.MIDI_PORT_PREFIX.getType(), buf, buf.length);
    }
    
    /**
     * Gets the port for a meta MIDI port prefix message.
     * 
     * @param msg the message
     * @return the port
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static int getMetaMidiPortPrefixPort(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.MIDI_PORT_PREFIX) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 1) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        return (int)(buf[0] & 0xff);
    }
    
    /**
     * Creates a meta sequence number message.
     * 
     * @param num the sequence number
     * @return a meta sequence number message
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static MetaMessage createMetaSequenceNumberMessage(int num) throws InvalidMidiDataException {
        if(num < 0 || num > 255) {
            throw new InvalidMidiDataException("num invalid: " + num);
        }
        byte buf[] = new byte[1];
        buf[0] = (byte)num;
        return new MetaMessage(MetaMessageType.SEQUENCE_NUMBER.getType(), buf, buf.length);
    }
    
    /**
     * Gets the sequence number for a meta sequence number message.
     * 
     * @param msg the message
     * @return the sequence number
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static int getMetaSequenceNumberNum(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.SEQUENCE_NUMBER) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 1) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        return (int)(buf[0] & 0xff);
    }
    
    /**
     * Creates a meta set tempo message.
     * 
     * @param tempoBpm the tempo in beats per minute
     * @return a meta set tempo message
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static MetaMessage createMetaSetTempoMessage(double tempoBpm) throws InvalidMidiDataException {
        int tempo = (int)((60.0f / tempoBpm) * 1000000.0f);
        if(tempo < 0 || tempo >= (128 * 128 * 128)) {
            throw new InvalidMidiDataException("tempo invalid: " + tempo);
        }
        byte buf[] = new byte[3];;
        buf[0] = (byte)((tempo >> 16) & 0xff);
        buf[1] = (byte)((tempo >> 8) & 0xff);
        buf[2] = (byte)(tempo & 0xff);
        return new MetaMessage(MetaMessageType.SET_TEMPO.getType(), buf, buf.length);
    }
    
    /**
     * Gets the tempo from a meta set tempo event
     * 
     * @param msg the message
     * @return the tempo in BPM
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static double getMetaSetTempoTempo(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.SET_TEMPO) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 3) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        return (double)60000000 / (double)((int)((buf[0] & 0xff) << 16) | 
            ((buf[1] & 0xff) << 8) | (buf[2] & 0xff));
    }
    
    /**
     * Creates a meta time signature message.
     * 
     * @param beatsPerBar the number of beats per bar (i.e. 4, 6, 2, etc.)
     * @param denomNumber the denominator number (i.e. 2, 4, 8, etc.)
     * @param ticksPerClick the number of metronome ticks per click - in 24PPQ format (usually 1)
     * @param thirtySecondsPerQuarter the number of 32nd notes per quarter (usually 8)
     * @return a meta time signature message
     * @throws InvalidMidiDataException if there is an error creating the message
     */
    public static MetaMessage createMetaTimeSignatureMessage(int beatsPerBar, int denomNumber, int ticksPerClick, 
            int thirtySecondsPerQuarter) throws InvalidMidiDataException {
        if(beatsPerBar < MidiProtocol.MIDI_BEATS_PER_BAR_MIN ||
                beatsPerBar > MidiProtocol.MIDI_BEATS_PER_BAR_MAX) {
            throw new InvalidMidiDataException("beatsPerBar invalid: " + beatsPerBar);
        }
        int denomPower;
        switch(denomNumber) {
        case 1:
            denomPower = 0;
            break;
        case 2:
            denomPower = 1;
            break;
        case 4:
            denomPower = 2;
            break;
        case 8:
            denomPower = 3;
            break;
        case 16:
            denomPower = 4;
            break;
        case 32:
            denomPower = 5;
            break;
        default:
            throw new InvalidMidiDataException("denomNumber invalid: " + denomNumber);
        }
        if(ticksPerClick < MidiProtocol.MIDI_TICKS_PER_CLICK_MIN ||
                ticksPerClick > MidiProtocol.MIDI_TICKS_PER_CLICK_MAX) {
            throw new InvalidMidiDataException("ticksPerClick invalid: " + ticksPerClick);
        }
        // check the 32nds per quarter note
        boolean thirtySecondsOkay = false;
        for(int i = MidiProtocol.MIDI_THIRTYSECONDS_PER_QUARTER_MIN;
                i <= MidiProtocol.MIDI_THIRTYSECONDS_PER_QUARTER_MAX; i *= 2) {
            if(thirtySecondsPerQuarter == i) {
                thirtySecondsOkay = true;
                break;
            }
        }
        if(!thirtySecondsOkay) {
            throw new InvalidMidiDataException("thirtySecondsPerBeat invalid: " + thirtySecondsPerQuarter);
        }
        byte buf[] = new byte[4];
        buf[0] = (byte)beatsPerBar;
        buf[1] = (byte)denomPower;
        buf[2] = (byte)ticksPerClick;
        buf[3] = (byte)thirtySecondsPerQuarter;
        return new MetaMessage(MetaMessageType.TIME_SIGNATURE.getType(), buf, buf.length);
    }
    
    /**
     * Gets the beats per bar for a meta time signature message.
     * 
     * @param msg the message
     * @return the number of beats in the bar
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static int getMetaTimeSignatureBeatsPerBar(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.TIME_SIGNATURE) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 4) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        return (int)(buf[0] & 0xff);
    }
    
    /**
     * Gets the denominator number for a meta time signature message.
     * 
     * @param msg the message
     * @return the denominator number (i.e. 2, 4, 8, etc.)
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static int getMetaTimeSignatureDenomNumber(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.TIME_SIGNATURE) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 4) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        return (int)Math.pow(2, buf[1] & 0xff);
    }
    
    /**
     * Gets the denominator power for a meta time signature message.
     * 
     * @param msg the message
     * @return the denominator power (i.e. 0, 1, 2, etc.)
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static int getMetaTimeSignatureDenomPower(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.TIME_SIGNATURE) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 4) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        return (int)buf[1] & 0xff;
    }
    
    /**
     * Gets the number of ticks per click for a meta time signature message. (24PPQ format)
     * 
     * @param msg the message
     * @return the number of ticks per click (24PPQ format)
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static int getMetaTimeSignatureTicksPerClick(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.TIME_SIGNATURE) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 4) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        return (int)(buf[2] & 0xff);
    }
    
    /**
     * Gets the number of 32nds per quarter for a meta time signature message.
     * 
     * @param msg the message
     * @return the number of 32nds per quarter (usually 8)
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static int getMetaTimeSignatureThirtySecondsPerQuarter(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.TIME_SIGNATURE) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 4) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        return (int)(buf[2] & 0xff);
    }

    /**
     * Gets the number of ticks per beat. (24PPQ format)
     * 
     * @param msg the message
     * @return the number of ticks per beat (24PPQ format)
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static int getMetaTimeSignatureTickPerBeat(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.TIME_SIGNATURE) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 4) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        return (MidiProtocol.MIDI_STANDARD_PPQ * 4) >> (buf[1] & 0xff);
    }
    
    /**
     * Gets the number of ticks per bar. (24PPQ format)
     * 
     * @param msg the message
     * @return the number of ticks per bar (24PPQ format)
     * @throws InvalidMidiDataException if the message is of an invalid type or malformed
     */
    public static int getMetaTimeSignatureTickPerBar(MetaMessage msg) throws InvalidMidiDataException {
        if(getMetaMessageType(msg) != MetaMessageType.TIME_SIGNATURE) {
            throw new InvalidMidiDataException("incorrect message type: " + getMetaMessageType(msg));
        }
        byte buf[] = msg.getData();
        if(buf.length != 4) {
            throw new InvalidMidiDataException("message malformed - len is: " + buf.length);
        }
        return ((MidiProtocol.MIDI_STANDARD_PPQ * 4) >> (buf[1] & 0xff)) * (buf[0] & 0xff);
    }
    
    /**
     * Formats a control value as a string.
     * 
     * @param value the value
     * @return the formatted string
     */
    public static String formatControlValue(int value) {
        if(value == MidiProtocol.MIDI_CONTROLLER_UNDEF) {
            return "---";
        }
        return String.format("%3d", value);
    }
    
    /**
     * Formats a bank number as a string.
     * 
     * @param bank the bank number
     * @return the formatted string
     */
    public static String formatBankNum(int bank) {
        if(bank == MidiProtocol.MIDI_BANK_PROG_UNDEF) {
            return "----";
        }
        return String.format("%02x%02x", ((bank >> 7) & 0x7f), (bank & 0x7f));
    }

    /**
     * Formats an input port number as a string.
     * 
     * @param port the port number
     * @return the formatted string
     */
    public static String formatInputPortNum(int port) {
        if(port == MidiProtocol.MIDI_PORT_UNDEF_GLOBAL) {
            return "Global";
        }
        return String.format("%2d", port);
    }

    /**
     * Formats an output port number as a string.
     * 
     * @param port the port number
     * @return the formatted string
     */
    public static String formatOutputPortNum(int port) {
        if(port == MidiProtocol.MIDI_PORT_UNDEF_GLOBAL) {
            return "----";
        }
        return String.format("%2d", port);
    }
    
    /**
     * Formats a MIDI channel as a string.
     * 
     * @param channel the MIDI channel
     * @return the formatted string
     */
    public static String formatMidiChannel(int channel) {
        if(channel < 0 || channel >= MidiProtocol.MIDI_NUM_CHANNELS) {
            return "--";
        }
        return String.format("%2d", (channel + 1));
    }
    
    /**
     * Format a boolean as an on/off string.
     * 
     * @param state the state
     * @return the formatted string
     */
    public static String formatOnOff(boolean state) {
        if(state) {
            return "On";
        }
        return "Off";
    }
    
    /**
     * Shortens a String by putting "..." in the middle.
     * 
     * @param text the text to shorten
     * @param maxLen the max length of the resulting String - it must be longer than 8
     * @return the String, possibly shortened
     */
    public static String shortenString(String text, int maxLen) {
        if(maxLen < 8) {
            return text;
        }
        if(text.length() <= maxLen) {
            return text;
        }
        int len2 = (maxLen / 2) - 1;
        return text.substring(0, len2 - 1) + "..." + text.substring(text.length() - len2, text.length());
    }
    
    /**
     * Formats a number of microseconds as time in format: hh:mm:ss.ttt
     * 
     * @param us the time to format in microseconds
     * @return the time formatted as a String
     */
    public static String formatMicrosecondClockTime(long us) {
        Duration dur = Duration.ofMillis(us / 1000);
        return String.format("%02d:%02d:%02d.%03d",
            dur.toHoursPart(),
            dur.toMinutesPart(),
            dur.toSecondsPart(),
            dur.toMillisPart());
    }

    /**
     * Formats a byte buffer as a string that can be printed.
     * 
     * @param buf the buffer
     * @param length the length
     * @return the buffer formatted as a String
     */
    public static String formatByteBuffer(byte buf[], int length) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i ++) {
            sb.append(String.format("%02x ", buf[i] & 0xff));
            if(((i + 1) % 16 == 0)) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    /**
     * Formats a key as a string.
     * 
     * @param key the key (+ for sharps, - for flats)
     * @param minor true if the key is minor, false otherwise
     * @return the formatted string
     */
    public static String formatKeyAsString(int key, boolean minor) {
        if(minor) {
            switch(key) {
            case -7:
                return "Ab-";
            case -6:
                return "Eb-";
            case -5:
                return "Bb-";
            case -4:
                return "F-";
            case -3:
                return "C-";
            case -2:
                return "G-";
            case -1:
                return "D-";
            case 1:
                return "E-";
            case 2:
                return "B-";
            case 3:
                return "F#-";
            case 4:
                return "C#-";
            case 5:
                return "G#-";
            case 6:
                return "D#-";
            case 7:
                return "A#-";
            case 0:
            default:
                return "A-";
            }
        }
        else {
            switch(key) {
            case -7:
                return "Cb";
            case -6:
                return "Gb";
            case -5:
                return "Db";
            case -4:
                return "Ab";
            case -3:
                return "Eb";
            case -2:
                return "Bb";
            case -1:
                return "F";
            case 1:
                return "G";
            case 2:
                return "D";
            case 3:
                return "A";
            case 4:
                return "E";
            case 5:
                return "B";
            case 6:
                return "F#";
            case 7:
                return "C#";
            case 0:
            default:
                return "C";
            }
        }
    }
}
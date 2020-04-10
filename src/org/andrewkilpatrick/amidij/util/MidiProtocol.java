package org.andrewkilpatrick.amidij.util;

public class MidiProtocol {
    // special values
    public static final int MIDI_PORT_UNDEF_GLOBAL = -1;
    public static final int MIDI_BANK_PROG_UNDEF = -1;
    public static final int MIDI_CONTROLLER_UNDEF = -1;
    public static final int MIDI_CHANNEL_OMNI = -1;
    public static final int MIDI_CONTROL_VALUE_MAX = 127;
    public static final int MIDI_NOTE_OFF_DEFAULT_VELOCITY = 0x40;

    // controller assignments
    public static final int MIDI_CONTROLLER_BANK_MSB = 0;
    public static final int MIDI_CONTROLLER_MOD_WHEEL = 1;
    public static final int MIDI_CONTROLLER_VOLUME = 7;
    public static final int MIDI_CONTROLLER_PAN = 10;
    public static final int MIDI_CONTROLLER_BANK_LSB = 32;
    public static final int MIDI_CONTROLLER_DAMPER_PEDAL = 64;
    public static final int MIDI_CONTROLLER_ALL_SOUNDS_OFF = 120;
    public static final int MIDI_CONTROLLER_RESET_ALL_CONTROLLERS = 121;
    public static final int MIDI_CONTROLLER_LOCAL_CONTROL = 122;
    public static final int MIDI_CONTROLLER_ALL_NOTES_OFF = 123;
    public static final int MIDI_CONTROLLER_OMNI_OFF = 124;
    public static final int MIDI_CONTROLLER_OMNI_ON = 125;
    public static final int MIDI_CONTROLLER_MONO_ON = 126;
    public static final int MIDI_CONTROLLER_POLY_ON = 127;
    
    // sizes
    public static final int MIDI_NUM_CHANNELS = 16;
    public static final int MIDI_NUM_NOTES = 128;
    public static final int MIDI_NUM_BANKS = 16384;
    public static final int MIDI_NUM_PROGRAMS = 128;
    public static final int MIDI_NUM_CONTROLLERS = 128;
    public static final int MIDI_NUM_SONGS = 128;
    public static final int MIDI_STANDARD_PPQ = 24;  // MIDI clock wire rate
    public static final int MIDI_PITCH_BEND_MIN = -8192;
    public static final int MIDI_PITCH_BEND_MAX = 8191;
    
    // clock
    public static final double MIDI_CLOCK_TEMPO_MIN = 30.0;  // BPM
    public static final double MIDI_CLOCK_TEMPO_MAX = 300.0;  // BPM
    public static final double MIDI_CLOCK_TEMPO_DEFAULT = 120.0;  // based on MIDI file format
    public static final int MIDI_SONG_POSITION_MAX = 16383;
    
    // meter and key
    public static final int MIDI_KEY_MIN = -7;
    public static final int MIDI_KEY_MAX = 7;
    public static final int MIDI_KEY_DEFAULT = 0;
    public static final int MIDI_BEATS_PER_BAR_MIN = 1;
    public static final int MIDI_BEATS_PER_BAR_MAX = 32;
    public static final int MIDI_BEATS_PER_BAR_DEFAULT = 4;
    public static final int MIDI_BEAT_DENOM_POWER_MIN = 0;  // ?/1
    public static final int MIDI_BEAT_DENOM_POWER_MAX = 5;  // ?/32
    public static final int MIDI_BEAT_DENOM_NUMBER_DEFAULT = 4;
    public static final int MIDI_TICKS_PER_CLICK_MIN = 6;  // sixteenth
    public static final int MIDI_TICKS_PER_CLICK_MAX = 96;  // whole
    public static final int MIDI_TICKS_PER_CLICK_DEFAULT = 24;  // quarter
    public static final int MIDI_THIRTYSECONDS_PER_QUARTER_MIN = 1;  // 1/8x speed
    public static final int MIDI_THIRTYSECONDS_PER_QUARTER_MAX = 32;  // 4x speed
    public static final int MIDI_THIRTYSECONDS_PER_QUARTER_DEFAULT = 8;  // normal speed
}

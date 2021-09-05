# AMidiJ

**Note: I am no longer maintaining this project. In general it seems better
to use apps on Linux which can directly access MIDI devices via ALSA instead
of relying on Jack.**

AMidiJ can bridge ALSA (or other OS-native) MIDI ports with Jack to provide
an easy interface for using native MIDI hardware from Jack applications such
as REAPER, Carla and other Jack audio/MIDI applications.

It is designed as a replacement for a2jmidid which did not work well for me.
The main problem with a2jmidid is that it opens all the MIDI ports on the
system even when they are not being used. If you have programs that access
ALSA MIDI ports directly, as well as Jack applications then this doesn't work
well because the ALSA programs won't be able to open the ports when a2jmidid
is running.

Instead, AMidiJ scans all the hardware ports and presents them to Jack all
the time, but no ports are opened until a Jack connection is made. In a program
like QjackCtl or Carla you can just drag a cable between the AMidiJ port and
another application. AMidiJ will detect this and will open the MIDI port and
start routing data between the port and Jack. Similarly when a connection is
broken it will close the port so it can be used by another application.

Also, AMidiJ continuously scans the available hardware MIDI ports and can
detect when devices are added or removed. If you plug in a MIDI device while
AMidiJ is running it will instantly show up in the Jack server.

Limitations and Disclaimers:

* Only hardware MIDI ports are used. This is a limitation (at least on Linux)
of the Java native MIDI interface API.

* This program has not been tested on Windows or macOS.

* This program is written in Java. Although I am quite good at C, I like
doing fun projects in Java. If that's not what you're looking for, oh well.

License:

* This program is licensed under GPL 3.


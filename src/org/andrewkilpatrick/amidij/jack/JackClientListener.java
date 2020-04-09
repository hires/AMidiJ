package org.andrewkilpatrick.amidij.jack;

import org.jaudiolibs.jnajack.JackClient;

public interface JackClientListener {

    public void availablePortsChanged();
    
    public void portConnected(JackPatchLink link);
    
    public void portDisconnected(JackPatchLink link);
    
    public boolean process(JackClient client, int nframes);
}

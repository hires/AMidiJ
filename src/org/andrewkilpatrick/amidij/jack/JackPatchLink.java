package org.andrewkilpatrick.amidij.jack;

public class JackPatchLink {
    String ourPortName;  // port name without client name
    JackPortName otherPort;
    
    public JackPatchLink(String ourPortName, JackPortName otherPort) {
        this.ourPortName = ourPortName;
        this.otherPort = otherPort;
    }
    
    public String getOurPortName() {
        return ourPortName;
    }

    public JackPortName getOtherPort() {
        return otherPort;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof JackPatchLink)) {
            return false;
        }
        JackPatchLink link = (JackPatchLink)obj;
        if(!link.getOurPortName().equals(ourPortName)) {
            return false;
        }
        if(!link.getOtherPort().getClientName().equals(otherPort.getClientName())) {
            return false;
        }
        if(!link.getOtherPort().getPortName().equals(otherPort.getPortName())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return ourPortName.hashCode() + otherPort.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("our port: %s - other client: %s - port: %s",
                ourPortName, otherPort.getClientName(), otherPort.getPortName());
    }
}

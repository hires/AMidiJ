package org.andrewkilpatrick.amidij.jack;

public class JackPortName {
    String clientName;
    String portName;
    
    public JackPortName(String clientName, String portName) {
        this.clientName = clientName;
        this.portName = portName;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public String getPortName() {
        return portName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof JackPortName)) {
            return false;
        }
        JackPortName port = (JackPortName)obj;
        if(!port.getClientName().equals(clientName)) {
            return false;
        }
        if(!port.getPortName().equals(portName)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return clientName.hashCode() + portName.hashCode();
    }
}

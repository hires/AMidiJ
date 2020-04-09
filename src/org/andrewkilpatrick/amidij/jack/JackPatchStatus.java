package org.andrewkilpatrick.amidij.jack;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JackPatchStatus {
    Logger log;
    HashSet<JackPatchLink> linkSet;
    LinkedList<JackPatchLink> linkList;
    
    public JackPatchStatus() {
        log = LogManager.getLogger(this.getClass());
        linkSet = new HashSet<>();
        linkList = new LinkedList<>();
    }
    
    public void addLink(JackPatchLink link) throws JackClientAdapterException {
        if(linkSet.contains(link)) {
            throw new JackClientAdapterException("link already exists: " + link.toString());
        }
        linkSet.add(link);
        linkList.addLast(link);
    }
    
    public void removeLink(JackPatchLink link) throws JackClientAdapterException {
        if(!linkSet.contains(link)) {
            throw new JackClientAdapterException("link does not exist: " + link.toString());
        }
        linkSet.remove(link);
        Iterator<JackPatchLink> iter = linkList.iterator();
        while(iter.hasNext()) {
            JackPatchLink checkLink = iter.next();
            if(checkLink.equals(link)) {
                iter.remove();
            }
        }
    }
    
    public int getNumLinks() {
        return linkList.size();
    }
    
    public JackPatchLink getLink(int index) {
        return linkList.get(index);
    }
    
    public LinkedList<JackPatchLink> getLinkList() {
        return linkList;
    }
    
    public void printStatus() {
        log.debug("patch entries: " + linkList.size());
        for(int i = 0; i < linkList.size(); i ++) {
            log.debug("  " + linkList.get(i).toString());
        }
    }
}

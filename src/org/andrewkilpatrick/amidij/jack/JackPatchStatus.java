/*
 * Jack Patch Status
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

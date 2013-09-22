package org.hexbot.updater.search;

import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/21/13
 * Copyright under GPL liscense by author.
 */
public class EntryPattern {
    InsnEntry[] entries;

    public EntryPattern(InsnEntry... entries) {
        this.entries = entries;
    }

    public boolean find(ClassNode classNode) {
        for (MethodNode mn : classNode.methods) {
            if (find(mn))
                return true;
        }
        return false;
    }

    public boolean find(ClassNode classNode, String desc) {
        for (MethodNode mn : classNode.methods) {
            if (mn.desc.equals(desc) && find(mn))
                return true;
        }
        return false;
    }

    public boolean find(MethodNode methodNode) {
        Iterator iterator = methodNode.instructions.iterator();
        int index = 0, radius = 0;
        while (iterator.hasNext()) {
            if (index >= entries.length)
                break;
            if (radius >= 12) {
                index = 0;
                radius = 0;
            }
            AbstractInsnNode ain = (AbstractInsnNode) iterator.next();
            radius++;
            if (entries[index].equals(ain)) {
                entries[index++].setInstance(ain);
                radius = 0;
            }
        }
        return index == entries.length;
    }

    public InsnEntry get(int index) {
        return entries[index];
    }
}

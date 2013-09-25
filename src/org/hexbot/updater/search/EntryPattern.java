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
        Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        int index = 0, radius = 0;
        while (iterator.hasNext()) {
            if (index >= entries.length)
                break;
            if (radius >= 12) {
                index = 0;
                radius = 0;
            }
            AbstractInsnNode ain = iterator.next();
            radius++;
            if (entries[index].equals(ain)) {
                entries[index++].setInstance(ain);
                radius = 0;
            }
        }
        return index == entries.length;
	}

	public boolean findAt(AbstractInsnNode sub) {
		int index = 0, radius = 0;
		while (sub.getNext() != null) {
			if (index >= entries.length)
				break;
			if (radius >= 12) {
				index = 0;
				radius = 0;
			}
			radius++;
			if (entries[index].equals(sub)) {
				entries[index++].setInstance(sub);
				radius = 0;
			}
			sub = sub.getNext();
		}
		return index == entries.length;
	}

    public InsnEntry get(int index) {
        return entries[index];
    }

	public <T> T get(int index, Class<T> clazz) {
		return (T) get(index).getInstance();
	}

	public InsnEntry[] getEntries() {
		return entries;
	}

}

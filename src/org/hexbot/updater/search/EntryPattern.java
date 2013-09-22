package org.hexbot.updater.search;

import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.*;

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

    public AbstractInsnNode[] find(ClassNode classNode) {
        AbstractInsnNode[] ain = null;
        for (MethodNode mn : classNode.methods) {
            ain = find(mn);
            if (ain == null)
                continue;
        }
        return ain;
    }

    public AbstractInsnNode[] find(ClassNode classNode, String desc) {
        AbstractInsnNode[] ain = null;
        for (MethodNode mn : classNode.methods) {
            if (!mn.desc.equals(desc))
                continue;
            ain = find(mn);
            if (ain == null)
                continue;
        }
        return ain;
    }

    public AbstractInsnNode[] find(MethodNode methodNode) {
        LinkedList<AbstractInsnNode> nodes = new LinkedList<>();
        InsnEntry first = entries[0];
        searcher:
        for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
            if (first.equals(ain)) {
                first.setInstance(ain);
                // nodes.add(ain);
                for (int i = 1; i < entries.length; i++) {
                    InsnEntry ie = entries[i];
                    AbstractInsnNode found = ASMUtil.getNext(ain, ie.opcode);
                    if (found == null) continue searcher;
                    //if (!ie.equals(found)) continue;

                    //Correct for field instructions
                    if (found != null && found instanceof FieldInsnNode && ie.desc != null) {
                        FieldInsnNode fin = (FieldInsnNode) found;
                        while (!fin.desc.equals(ie.desc)) {
                            fin = (FieldInsnNode) ASMUtil.getNext(fin, ie.opcode);
                            if (fin == null) continue searcher;
                        }
                        found = fin;
                    } else if (found != null && found instanceof MethodInsnNode && ie.desc != null) {
                        MethodInsnNode min = (MethodInsnNode) found;
                        while (!(min.name).equals(ie.desc)) {
                            min = (MethodInsnNode) ASMUtil.getNext(min, ie.opcode);
                            if (min == null) continue searcher;
                        }
                        found = min;
                    } else if (found != null && found instanceof LdcInsnNode && ie.desc != null) {
                        LdcInsnNode lin = (LdcInsnNode) found;
                        while (!(lin.cst).equals(ie.desc)) {
                            lin = (LdcInsnNode) ASMUtil.getNext(lin, ie.opcode);
                            if (lin == null) continue searcher;
                        }
                        found = lin;
                    } else if (found != null && found instanceof IntInsnNode && ie.desc != null) {
                        IntInsnNode iin = (IntInsnNode) found;
                        while (!(iin.operand + "").equals(ie.desc)) {
                            iin = (IntInsnNode) ASMUtil.getNext(iin, ie.opcode);
                            if (iin == null) continue searcher;
                        }
                        found = iin;
                    }
                    if (found == null) continue searcher;
                    ie.setInstance(found);
                    nodes.add(found);
                }
//                if (nodes.size() != entries.length) {
//                    nodes.clear();
//                } else {
//                    return nodes.toArray(new AbstractInsnNode[nodes.size()]);
//                }
            }
        }
        for (InsnEntry ie : entries) {
            if (ie.getInstance() == null)
                return null;
        }
        //return null;
        return nodes.toArray(new AbstractInsnNode[nodes.size()]);
    }

    public InsnEntry get(int index) {
        return entries[index];
    }
}

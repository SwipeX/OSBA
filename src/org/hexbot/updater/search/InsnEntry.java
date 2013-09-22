package org.hexbot.updater.search;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/21/13
 * Copyright under GPL liscense by author.
 */
public class InsnEntry {
    int opcode;
    String desc;
    AbstractInsnNode instance;

    public InsnEntry(int opcode, String desc) {
        this.opcode = opcode;
        this.desc = desc;
    }

    public InsnEntry(int opcode) {
        this(opcode, null);
    }

    public AbstractInsnNode getInstance() {
        return instance;
    }

    public void setInstance(AbstractInsnNode a) {
        instance = a;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof AbstractInsnNode)) return false;
        AbstractInsnNode ain = (AbstractInsnNode) o;
        if (ain.getOpcode() != opcode) return false;
        if (desc == null) return true;
        if (ain instanceof FieldInsnNode) {
            return desc.equals(((FieldInsnNode) ain).desc);
        } else if (ain instanceof MethodInsnNode) {
            MethodInsnNode min = (MethodInsnNode) ain;
            return (min.name).equalsIgnoreCase(desc);
        } else if (ain instanceof IntInsnNode) {
            return Integer.toString(((IntInsnNode) ain).operand).equals(desc);
        } else if (ain instanceof LdcInsnNode) {
            Object cst = ((LdcInsnNode) ain).cst;
            return cst != null && cst.toString().equals(desc);
        }
        return false;
    }
}

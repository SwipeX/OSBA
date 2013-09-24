package org.hexbot.updater.search;

import org.objectweb.asm.tree.*;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/21/13
 * Copyright under GPL liscense by author.
 */
public class InsnEntry {

    int opcode;
    String desc, owner;
    AbstractInsnNode instance;

    public InsnEntry(int opcode, String desc, String owner) {
        this.opcode = opcode;
        this.desc = desc;
	    this.owner = owner;
    }

	public InsnEntry(int opcode, String desc) {
		this(opcode, desc, null);
	}

    public InsnEntry(int opcode) {
        this(opcode, null, null);
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
            if (desc.equals(((FieldInsnNode) ain).desc)) {
	            return owner == null || ((FieldInsnNode) ain).owner.equals(owner);
            }
        } else if (ain instanceof MethodInsnNode) {
            MethodInsnNode min = (MethodInsnNode) ain;
            if ((min.name).equalsIgnoreCase(desc)) {
	            return owner == null || min.owner.equals(owner);
            }
        } else if (ain instanceof IntInsnNode) {
	        int op = ((IntInsnNode) ain).operand;
	        if (desc.startsWith("<")) {
		        return op < Integer.parseInt(desc.substring(1));
	        } else if (desc.startsWith(">")) {
		        return op > Integer.parseInt(desc.substring(1));
	        } else if (desc.startsWith("<=")) {
	            return op <= Integer.parseInt(desc.substring(2));
	        } else if (desc.startsWith(">=")) {
				return op >= Integer.parseInt(desc.substring(2));
	        } else {
		        return op == Integer.parseInt(desc);
	        }
        } else if (ain instanceof LdcInsnNode) {
            Object cst = ((LdcInsnNode) ain).cst;
            return cst != null && cst.toString().equals(desc);
        }
        return false;
    }
}

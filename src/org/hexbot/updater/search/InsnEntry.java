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
	Class<?> type;
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

	public InsnEntry(Class<?> type, String desc, String owner) {
		this.type = type;
		this.desc = desc;
		this.owner = owner;
	}

	public InsnEntry(Class<?> type, String desc) {
		this(type, desc, null);
	}

	public InsnEntry(Class<?> type) {
		this(type, null, null);
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
		if (type == null) {
			if (ain.getOpcode() != opcode) return false;
		} else {
			if (!ain.getClass().equals(type)) return false;
		}
		if (desc == null) return true;
		if (ain instanceof FieldInsnNode) {
			/**
			 * Check if has tags format, if not just compares to FieldInsnNode#desc
			 *
			 * Example: new InsnEntry(Opcodes.GETFIELD, "name:gg;owner:client;desc:I;");
			 */
			FieldInsnNode f = (FieldInsnNode) ain;
			String[] tags = {"name:", "owner:", "desc:"};
			boolean hasTag = false;
			for (String tag : tags) {
				if (desc.contains(tag)) {
					hasTag = true;
					String value = desc.split(tag)[1].split(";")[0];
					switch (tag) {
						case "name:":
							if (!f.name.equals(value)) {
								return false;
							}
							break;
						case "owner:":
							if (!f.owner.equals(value)) {
								return false;
							}
							break;
						case "desc:":
							if (!f.desc.equals(value) && !f.desc.equals(value+";")) {
								return false;
							}
							break;
					}
				}
			}
			return hasTag || desc.equals(f.desc);
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

	public boolean contained(MethodNode m) {
		for (AbstractInsnNode node : m.instructions.toArray()) {
			if (this.equals(node))
				return true;
		}
		return false;
	}

}

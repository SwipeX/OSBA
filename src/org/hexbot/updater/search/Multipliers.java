package org.hexbot.updater.search;


import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Caleb
 */
public class Multipliers {

	private static Multipliers global;
	private List<Multiplier> fields;

	private Multipliers(List<Multiplier> fields) {
		this.fields = fields;
	}

	public static Multipliers getGlobal() {
		return global;
	}

	public static Multipliers setGlobal(Multipliers global) {
		return Multipliers.global = global;
	}

	public static Multipliers instance(Collection<ClassNode> nodes) {
		List<Multiplier> fields = new ArrayList<>();
		for (ClassNode node : nodes)
			for (FieldNode field : node.fields) {
				if (field.desc.equals("I")) {
					fields.add(new Multiplier(node, field));
				}
			}
		List<MethodNode> methods = new LinkedList<>();
		for (ClassNode node : nodes) {
			for (MethodNode method : node.methods) {
				methods.add(method);
			}
		}
		for (MethodNode method : methods) {
			search(fields, method);
		}
		return new Multipliers(fields);
	}

	private static void search(List<Multiplier> fields, MethodNode method) {
		AbstractInsnNode[] instructions = method.instructions.toArray();
		iterator:
		for (int index = 1; index < instructions.length - 1; index++) {
			AbstractInsnNode[] surround = {instructions[index - 1], instructions[index], instructions[index + 1]};
			LdcInsnNode ldc = null;
			InsnNode multiply = null;
			FieldInsnNode fieldNode = null;
			for (AbstractInsnNode insn : surround) {
				switch (insn.getType()) {
					case AbstractInsnNode.LDC_INSN:
						ldc = (LdcInsnNode) insn;
						break;
					case AbstractInsnNode.FIELD_INSN:
						fieldNode = (FieldInsnNode) insn;
						break;
					case AbstractInsnNode.INSN:
						if (insn.getOpcode() == Opcodes.IMUL) {
							multiply = (InsnNode) insn;
						}
						break;
				}
			}
			Object[] nodes = {ldc, multiply, fieldNode};
			for (Object node : nodes)
				if (node == null)
					continue iterator;
			if (ldc.cst == null || !(ldc.cst instanceof Number))
				continue;
			for (Multiplier field : fields) {
				if (field.isMatch(fieldNode)) {
					Integer multiplier = ((Number) ldc.cst).intValue();
					if (field.multipliers.containsKey(multiplier)) {
						field.multipliers.get(multiplier).incrementAndGet();
					} else {
						field.multipliers.put(multiplier, new AtomicInteger(1));
					}
				}
			}
		}
	}

	public List<Multiplier> getFields() {
		return fields;
	}

	public Multiplier getField(FieldInsnNode insn) {
		for (Multiplier field : fields) {
			if (field.isMatch(insn))
				return field;
		}
		return null;
	}

	public Multiplier getField(ClassNode owner, FieldNode f) {
		for (Multiplier field : fields) {
			if (field.isMatch(owner, f))
				return field;
		}
		return null;
	}

	public Multiplier getField(String owner, String fName) {
		for (Multiplier field : fields) {
			if (field.isMatch(owner, fName))
				return field;
		}
		return null;
	}

	public static int getSurrounding(AbstractInsnNode insn) {
		AbstractInsnNode prev = insn.getPrevious();
		AbstractInsnNode next = insn.getNext();
		LdcInsnNode l;
		if (prev != null && prev.getType() == AbstractInsnNode.LDC_INSN) {
			l = (LdcInsnNode) prev;
			if (l.cst != null && l.cst instanceof Number)
				return ((Number) l.cst).intValue();
		}
		if (next != null && next.getType() == AbstractInsnNode.LDC_INSN) {
			l = (LdcInsnNode) next;
			if (l.cst != null && l.cst instanceof Number)
				return ((Number) l.cst).intValue();
		}
		return -1;
	}

	public static int getMostUsed(FieldInsnNode f) {
		return getGlobal().getField(f).getMostUsed();
	}

	public static int getMostUsed(ClassNode owner, FieldNode f) {
		return getGlobal().getField(owner, f).getMostUsed();
	}

}

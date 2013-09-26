package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.search.Multipliers;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

public class Widget extends Container {

	public Widget(Updater updater) {
		super(updater);
	}

	private enum OpcodeHook {
		TEXT_COLOR(1101, new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, "I")), 0, 0),
		TEXTURE_ID(1105, new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, "I")), 0, 0),
		TEXT(1112, new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "Ljava/lang/String;")), 0, 0),
		BORDER_THICKNESS(1116, new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, "I")), 0, 0),
		INDEX(1702, new EntryPattern(new InsnEntry(Opcodes.GETSTATIC, "[I"), new InsnEntry(Opcodes.GETFIELD, "I")), 0, 1),
		SCROLL_X(1100, new EntryPattern(new InsnEntry(Opcodes.GETSTATIC, "[I"), new InsnEntry(Opcodes.GETFIELD, "I")), 0, 1),
		SCROLL_Y(1601, new EntryPattern(new InsnEntry(Opcodes.GETSTATIC, "[I"), new InsnEntry(Opcodes.GETFIELD, "I")), 0, 1);

		private final int operand;
		private final EntryPattern pattern;
		private final int skip;
		private final int fieldIndex;

		boolean identified;

		OpcodeHook(int operand, EntryPattern pattern, int skip, int fieldIndex) {
			this.operand = operand;
			this.pattern = pattern;
			this.skip = skip;
			this.fieldIndex = fieldIndex;
		}

		FieldInsnNode getFieldInsn() {
			return pattern.get(fieldIndex, FieldInsnNode.class);
		}

	}

	@Override
	public int getTotalHookCount() {
		return 17;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals(CLASS_MATCHES.get("Node"))) {
				if (cn.fieldCount("[Ljava/lang/Object;") > 10) {
					CLASS_MATCHES.put("Widget", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		addHook("getMasterX", "masterX", cn.name, cn.name, "I", -1);
		addHook("getMasterY", "masterY", cn.name, cn.name, "I", -1);
		EntryPattern setters = new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, "Z"), new InsnEntry(Opcodes.PUTFIELD, "I"),
				new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"),
				new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"),
				new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"),
				new InsnEntry(Opcodes.PUTFIELD, "I"));
		FieldNode parent = cn.getField(null, "L" + cn.name + ";", true);
		FieldNode children = cn.getField(null, "[L" + cn.name + ";", true);
		if (parent != null)
			addHook("getParent", parent.name, cn.name, cn.name, getDescriptor(), -1);
		if (children != null)
			addHook("getChildren", children.name, cn.name, cn.name, getDescriptor(1), -1);
		if (setters.find(cn)) {
			FieldInsnNode type = setters.get(2, FieldInsnNode.class);
			FieldInsnNode x = setters.get(4, FieldInsnNode.class);
			FieldInsnNode y = setters.get(6, FieldInsnNode.class);
			FieldInsnNode width = setters.get(8, FieldInsnNode.class);
			FieldInsnNode height = setters.get(9, FieldInsnNode.class);
			FieldInsnNode parentId = setters.get(11, FieldInsnNode.class);
			addHook("getType", type, cn.name, Multipliers.getMostUsed(type));
			addHook("getX", x, cn.name, Multipliers.getMostUsed(x));
			addHook("getY", y, cn.name, Multipliers.getMostUsed(y));
			addHook("getWidth", width, cn.name, Multipliers.getMostUsed(width));
			addHook("getHeight", height, cn.name, Multipliers.getMostUsed(height));
			addHook("getParentId", parentId, cn.name, Multipliers.getMostUsed(parentId));
		}
		for (OpcodeHook hook : OpcodeHook.values()) {
			hook.identified = identifyOpcodeHook(hook.operand, hook.pattern, hook.skip);
		}
		if (OpcodeHook.TEXT_COLOR.identified)
			addHook("getTextColor", OpcodeHook.TEXT_COLOR.getFieldInsn(), cn.name, -1);
		if (OpcodeHook.TEXTURE_ID.identified)
			addHook("getTextureId", OpcodeHook.TEXTURE_ID.getFieldInsn(), cn.name, -1);
		if (OpcodeHook.TEXT.identified)
			addHook("getText", OpcodeHook.TEXT.getFieldInsn(), cn.name, -1);
		if (OpcodeHook.BORDER_THICKNESS.identified)
			addHook("getBorderThickness", OpcodeHook.BORDER_THICKNESS.getFieldInsn(), cn.name, -1);
		if (OpcodeHook.INDEX.identified)
			addHook("getIndex", OpcodeHook.INDEX.getFieldInsn(), cn.name, -1);
		if (OpcodeHook.SCROLL_X.identified)
			addHook("getScrollX", OpcodeHook.SCROLL_X.getFieldInsn(), cn.name, -1);
		if (OpcodeHook.SCROLL_Y.identified)
			addHook("getScrollY", OpcodeHook.SCROLL_Y.getFieldInsn(), cn.name, -1);

	}

	private boolean identifyOpcodeHook(int operand, EntryPattern p, int skips) {
		for (ClassNode c : getUpdater().classnodes.values()) {
			for (MethodNode m : c.methods) {
				boolean b = identifyOpcodeHook(operand, p, skips, m);
				if (b)
					return true;
			}
		}
		return false;
	}

	private boolean identifyOpcodeHook(int operand, EntryPattern pattern, int skips, MethodNode m) {
		AbstractInsnNode current = m.instructions.getFirst();
		int skip = 0;
		while (current != null) {
			if (current.getType() == AbstractInsnNode.INT_INSN) {
				IntInsnNode intInsn = (IntInsnNode) current;
				if (intInsn.operand == operand) {
					AbstractInsnNode sub = current.getNext();
					while (sub != null) {
						if (pattern.findAt(sub)) {
							if (skip >= skips)
								return true;
							skip++;
						}
						sub = sub.getNext();
					}
				}
			}
			current = current.getNext();
		}
		return false;
	}

}

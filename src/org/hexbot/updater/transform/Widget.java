package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.search.Multipliers;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class Widget extends Container {

	public Widget(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 8;
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
		EntryPattern setters = new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, "Z"), new InsnEntry(Opcodes.PUTFIELD, "I"),
				new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"),
				new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"),
				new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"),
				new InsnEntry(Opcodes.PUTFIELD, "I"));
		FieldNode parent = cn.getField(null, "L" + cn.name + ";", true);
		FieldNode children = cn.getField(null, "[L" + cn.name + ";", true);
		if (parent != null)
			addHook("getParent", parent.name, cn.name, cn.name, parent.desc, -1);
		if (children != null)
			addHook("getChildren", children.name, cn.name, cn.name, children.desc, -1);
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
	}

}

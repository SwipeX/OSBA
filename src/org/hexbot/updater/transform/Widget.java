package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

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
			addHook("getType", setters.get(2, FieldInsnNode.class), cn.name, -1);
			addHook("getX", setters.get(4, FieldInsnNode.class), cn.name, -1);
			addHook("getY", setters.get(6, FieldInsnNode.class), cn.name, -1);
			addHook("getWidth", setters.get(8, FieldInsnNode.class), cn.name, -1);
			addHook("getHeight", setters.get(9, FieldInsnNode.class), cn.name, -1);
			addHook("getParentId", setters.get(11, FieldInsnNode.class), cn.name, -1);
		}
	}

}

package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class Node extends Container {

	public Node(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 3;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.fields.size() == 3) {
				if (cn.fieldCount("J") == 1 && cn.fieldCount("L" + cn.name + ";") == 2) {
					CLASS_MATCHES.put("Node", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		FieldNode uid = cn.getField(null, "J");
		addHook("getUID", uid.name, cn.name, cn.name, uid.desc, -1);
		addNodeHooks(this, cn);
	}

	protected static void addNodeHooks(Container container, ClassNode cn) {
		String desc = "L" + cn.name + ";";
		EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.ACONST_NULL),
				new InsnEntry(Opcodes.PUTFIELD, desc), new InsnEntry(Opcodes.PUTFIELD, desc));
		if (pattern.find(cn)) {
			FieldInsnNode next = (FieldInsnNode) pattern.get(1).getInstance();
			FieldInsnNode prev = (FieldInsnNode) pattern.get(2).getInstance();
			container.addHook("getNext", next.name, next.owner, cn.name, next.desc, -1);
			container.addHook("getPrevious", prev.name, prev.owner, cn.name, prev.desc, -1);
		}
	}
}

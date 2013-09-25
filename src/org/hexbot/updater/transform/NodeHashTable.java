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

public class NodeHashTable extends Container {

	public NodeHashTable(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 3;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		String desc = "L" + CLASS_MATCHES.get("Node") + ";";
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals("java/lang/Object")) {
				if (cn.fieldCount(desc) == 2 && cn.fieldCount("[" + desc) == 1) {
					CLASS_MATCHES.put("NodeHashTable", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		String node = "L" + CLASS_MATCHES.get("Node") + ";";
		FieldNode buckets = cn.getField(null, "[" + node);
		addHook("getBuckets", buckets.name, cn.name, cn.name, buckets.desc, -1);
		EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "[" + node, cn.name),
				new InsnEntry(Opcodes.GETFIELD, "I", cn.name), new InsnEntry(Opcodes.PUTFIELD, node, cn.name));
		if (pattern.find(cn)) {
			FieldInsnNode size = (FieldInsnNode) pattern.get(1).getInstance();
			FieldInsnNode head = (FieldInsnNode) pattern.get(2).getInstance();
			addHook("getSize", size.name, size.owner, cn.name, size.desc, -1);
			addHook("getHead", head.name, head.owner, cn.name, head.desc, -1);
		}
	}
}

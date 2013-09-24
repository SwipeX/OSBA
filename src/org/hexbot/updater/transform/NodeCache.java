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

public class NodeCache extends Container {

	public NodeCache(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 5;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.getFieldTypeCount() == 4 && cn.fieldCount("L" + CLASS_MATCHES.get("CacheableNode") + ";") == 1 &&
					cn.fieldCount("L" + CLASS_MATCHES.get("CacheableNodeDeque") + ";") == 1 &&
					cn.fieldCount("L" + CLASS_MATCHES.get("NodeHashTable") + ";") == 1) {
				CLASS_MATCHES.put("NodeCache", cn.name);
				return cn;
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		FieldNode node = cn.getField(null, "L" + CLASS_MATCHES.get("CacheableNode") + ";");
		FieldNode hashtable = cn.getField(null, "L" + CLASS_MATCHES.get("NodeHashTable") + ";");
		FieldNode deque = cn.getField(null, "L" + CLASS_MATCHES.get("CacheableNodeDeque") + ";");
		addHook("getNode", node.name, cn.name, cn.name, node.desc, -1);
		addHook("getTable", hashtable.name, cn.name, cn.name, hashtable.desc, -1);
		addHook("getDeque", deque.name, cn.name, cn.name, deque.desc, -1);
		EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.INVOKESPECIAL, "<init>"),
				new InsnEntry(Opcodes.GETFIELD, "I", cn.name), new InsnEntry(Opcodes.PUTFIELD, "I", cn.name));
		if (pattern.find(cn)) {
			FieldInsnNode size = (FieldInsnNode) pattern.get(1).getInstance();
			FieldInsnNode remaining = (FieldInsnNode) pattern.get(2).getInstance();
			addHook("getSize", size.name, size.owner, cn.name, size.desc, -1);
			addHook("getRemaining", remaining.name, remaining.owner, cn.name, remaining.desc, -1);
		}
	}
}

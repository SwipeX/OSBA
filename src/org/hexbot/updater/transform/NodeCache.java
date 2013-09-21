package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class NodeCache extends Container {

	public NodeCache(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 0;
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
	}
}

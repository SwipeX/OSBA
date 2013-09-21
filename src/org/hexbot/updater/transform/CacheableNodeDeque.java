package org.hexbot.updater.transform;


import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class CacheableNodeDeque extends Container {

	public CacheableNodeDeque(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 0;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals("java/lang/Object")) {
				if (cn.fields.size() == 1 && cn.fieldCount("L" + CLASS_MATCHES.get("CacheableNode") + ";") == 1) {
					CLASS_MATCHES.put("CacheableNodeDeque", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
	}
}

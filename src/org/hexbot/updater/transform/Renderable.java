package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class Renderable extends Container {

	public Renderable(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 0;
	}


	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if ((cn.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
				if (cn.superName.equals(CLASS_MATCHES.get("CacheableNode"))) {
					CLASS_MATCHES.put("Renderable", cn.name);
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

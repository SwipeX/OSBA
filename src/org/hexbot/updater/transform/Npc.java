package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class Npc extends Container {

	public Npc(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 0;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.fieldCount("L" + CLASS_MATCHES.get("NpcDefinition") + ";") == 1) {
				CLASS_MATCHES.put("Npc", cn.name);
				return cn;
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
	}
}

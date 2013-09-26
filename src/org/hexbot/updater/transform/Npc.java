package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class Npc extends Container {

	public Npc(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 1;
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
		FieldNode def = cn.getField(null, "L" + CLASS_MATCHES.get("NpcDefinition") + ";");
		addHook("getDefinition", def.name, cn.name, cn.name, getUpdater().getContainer(NpcDefinition.class).getDescriptor(), -1);
	}
}

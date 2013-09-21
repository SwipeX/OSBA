package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class Character extends Container {

	public Character(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 0;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals(CLASS_MATCHES.get("Renderable"))) {
				if (cn.fieldCount("[I") == 5 && cn.fieldCount("Z") == 1 && cn.fieldCount("Ljava/lang/String;") == 1) {
					CLASS_MATCHES.put("Character", cn.name);
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

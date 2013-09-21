package org.hexbot.updater.transform;


import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class CollisionMap extends Container {

	public CollisionMap(Updater updater) {
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
				if (cn.getFieldTypeCount() == 2 && cn.fieldCount("[[I") == 1 && cn.fieldCount("I") == 4) {
					CLASS_MATCHES.put("CollisionMap", cn.name);
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

package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class ObjectDefinition extends Container {

	public ObjectDefinition(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 0;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.getFieldTypeCount() == 6 && cn.fieldCount("[S") == 4 && cn.fieldCount("[I") == 4) {
				CLASS_MATCHES.put("ObjectDefinition", cn.name);
				return cn;
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
	}
}

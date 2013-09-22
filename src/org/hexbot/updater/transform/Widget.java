package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class Widget extends Container {

	public Widget(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 0;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals(CLASS_MATCHES.get("Node"))) {
				if (cn.fieldCount("[Ljava/lang/Object;") > 10) {
					CLASS_MATCHES.put("Widget", cn.name);
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

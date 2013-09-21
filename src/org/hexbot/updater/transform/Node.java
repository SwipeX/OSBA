package org.hexbot.updater.transform;
import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class Node extends Container {

	public Node(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 0;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.fields.size() == 3) {
				if (cn.fieldCount("J") == 1 && cn.fieldCount("L" + cn.name + ";") == 2) {
					CLASS_MATCHES.put("Node", cn.name);
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

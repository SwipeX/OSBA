package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class NodeHashTable extends Container {

	public NodeHashTable(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 0;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		String desc = "L" + CLASS_MATCHES.get("Node") + ";";
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals("java/lang/Object")) {
				if (cn.fieldCount(desc) == 2 && cn.fieldCount("[" + desc) == 1) {
					CLASS_MATCHES.put("NodeHashTable", cn.name);
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

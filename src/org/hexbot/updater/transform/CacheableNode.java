package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class CacheableNode extends Container {

	public CacheableNode(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 2;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals(CLASS_MATCHES.get("Node"))) {
				if (cn.fieldCount("L" + cn.name + ";") == 2) {
					CLASS_MATCHES.put("CacheableNode", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		/**
		 * Remove method is identical to Node
		 */
		Node.addNodeHook(this, "L" + GETTER + ";", cn);
	}

}

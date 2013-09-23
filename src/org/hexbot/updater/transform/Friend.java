package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class Friend extends Container {

	public Friend(Updater updater) {
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
				if (cn.getAbnormalFieldCount() == 0 && cn.fieldCount("Ljava/lang/String;") == 2 &&
						cn.fieldCount("Z") == 2 && cn.fieldCount("I") == 2) {
					CLASS_MATCHES.put("Friend", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		/**
		 * We Have No Friends
		 */
	}

}

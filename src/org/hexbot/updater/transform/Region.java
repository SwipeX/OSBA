package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class Region extends Container {

	public Region(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 0;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals("java/lang/Object") && cn.getAbnormalFieldCount() == 2 && 
					cn.fieldCount("[[[L" + CLASS_MATCHES.get("TileData") + ";") == 1 &&
					cn.fieldCount("[L" + CLASS_MATCHES.get("InteractableObject") + ";") == 1) {
				CLASS_MATCHES.put("Region", cn.name);
				return cn;
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
	}
}

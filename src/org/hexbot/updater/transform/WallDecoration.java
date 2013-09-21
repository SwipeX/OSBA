package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class WallDecoration extends Container {

	public WallDecoration(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 0;
	}
	
	@Override
	public String getInterfaceString() {
		return GETTER_PREFIX + "GameObject";
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals("java/lang/Object")) {
				if (cn.getFieldTypeCount() == 2 && cn.fieldCount("I") == 9 &&
						cn.fieldCount("L" + CLASS_MATCHES.get("Renderable") + ";") == 2) {
					CLASS_MATCHES.put("WallDecoration", cn.name);
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

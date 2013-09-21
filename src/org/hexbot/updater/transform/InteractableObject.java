package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class InteractableObject extends Container {

	public InteractableObject(Updater updater) {
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
				if (cn.fieldCount("L" + CLASS_MATCHES.get("Renderable") + ";") == 1 && cn.getAbnormalFieldCount() == 1 &&
						cn.getFieldTypeCount() == 2 && cn.fieldCount("I") != 5) {
					CLASS_MATCHES.put("InteractableObject", cn.name);
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

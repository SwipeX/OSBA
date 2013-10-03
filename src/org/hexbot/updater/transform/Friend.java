package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.util.Map;

public class Friend extends Container {

	public Friend(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 3;
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
		ClassNode client = updater.classnodes.get("client");
		EntryPattern ep = new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, "Ljava/lang/String;", cn.name),
				new InsnEntry(Opcodes.PUTFIELD, "Ljava/lang/String;", cn.name),
				new InsnEntry(Opcodes.PUTFIELD, "I", cn.name));
		if (ep.find(client)) {
			FieldInsnNode name = (FieldInsnNode) ep.get(0).getInstance();
			FieldInsnNode prev = (FieldInsnNode) ep.get(1).getInstance();
			FieldInsnNode world = (FieldInsnNode) ep.get(2).getInstance();
			addHook("getName", name.name,  cn.name, cn.name, name.desc, -1);
			addHook("getPreviousName", prev.name,  cn.name, cn.name, prev.desc, -1);
			addHook("getWorld", world.name,  cn.name, cn.name, world.desc, -1);
		}
	}

}

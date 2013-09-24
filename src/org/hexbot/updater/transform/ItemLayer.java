package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.util.Map;

public class ItemLayer extends Container {

	public ItemLayer(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 5;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals("java/lang/Object")) {
				if (cn.getFieldTypeCount() == 2 && cn.fieldCount("L" + CLASS_MATCHES.get("Renderable") + ";") == 3) {
					CLASS_MATCHES.put("ItemLayer", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		ClassNode region = updater.classnodes.get(CLASS_MATCHES.get("Region"));
		if (region != null) {
			String render = "L" + CLASS_MATCHES.get("Renderable") + ";";
			EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, render, cn.name),
					new InsnEntry(Opcodes.PUTFIELD, "I", cn.name), new InsnEntry(Opcodes.PUTFIELD, "I", cn.name),
					new InsnEntry(Opcodes.PUTFIELD, "I", cn.name), new InsnEntry(Opcodes.PUTFIELD, render, cn.name),
					new InsnEntry(Opcodes.PUTFIELD, render, cn.name));
			if (pattern.find(region)) {
				FieldInsnNode item1 = (FieldInsnNode) pattern.get(0).getInstance();
				FieldInsnNode x = (FieldInsnNode) pattern.get(1).getInstance();
				FieldInsnNode y = (FieldInsnNode) pattern.get(3).getInstance();
				FieldInsnNode item2 = (FieldInsnNode) pattern.get(4).getInstance();
				FieldInsnNode item3 = (FieldInsnNode) pattern.get(5).getInstance();
				addHook("getItem1", item1.name, item1.owner, cn.name, item1.desc, -1);
				addHook("getX", x.name, x.owner, cn.name, x.desc, -1);
				addHook("getY", y.name, y.owner, cn.name, y.desc, -1);
				addHook("getItem2", item2.name, item2.owner, cn.name, item2.desc, -1);
				addHook("getItem3", item3.name, item3.owner, cn.name, item3.desc, -1);
			}
		}
	}
}

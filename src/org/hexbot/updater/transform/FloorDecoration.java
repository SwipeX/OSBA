package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.search.Multipliers;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class FloorDecoration extends Container {

	public FloorDecoration(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 4;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals("java/lang/Object")) {
				if (cn.getFieldTypeCount() == 2 && cn.fieldCount("L" + CLASS_MATCHES.get("Renderable") + ";") == 1 && cn.fieldCount("I") == 5) {
					CLASS_MATCHES.put("FloorDecoration", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
        ClassNode region = updater.classnodes.get(CLASS_MATCHES.get("Region"));
        EntryPattern ep = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "L" + cn.name + ";"), new InsnEntry(Opcodes.GETFIELD, "I"));
        if (ep.find(region, "(III)I")) {
            FieldInsnNode id = (FieldInsnNode) ep.get(1).getInstance();
            addHook("getId", id.name, id.owner, id.owner, id.desc, Multipliers.getBest(id));
        }
		String render = "L" + CLASS_MATCHES.get("Renderable") + ";";
		FieldNode renderable = cn.getField(null, render);
		addHook("getModel", renderable.name, cn.name, cn.name, getUpdater().getContainer(Model.class).getDescriptor(), -1);
		EntryPattern ep1 = new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, render, cn.name),
				new InsnEntry(Opcodes.PUTFIELD, "I", cn.name), new InsnEntry(Opcodes.PUTFIELD, "I", cn.name));
		if (ep1.find(region)) {
			FieldInsnNode x = (FieldInsnNode) ep1.get(1).getInstance();
			FieldInsnNode y = (FieldInsnNode) ep1.get(2).getInstance();
			addHook("getWorldX", x.name, x.owner, cn.name, x.desc,Multipliers.getBest(x));
			addHook("getWorldY", y.name, y.owner, cn.name, y.desc, Multipliers.getBest(y));
		}
	}
}

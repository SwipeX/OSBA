package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.util.Map;

public class NpcDefinition extends Container {

	public NpcDefinition(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 4;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals(CLASS_MATCHES.get("CacheableNode"))) {
				if (cn.fieldCount("Z") == 4) {
					CLASS_MATCHES.put("NpcDefinition", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		EntryPattern ep1 = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "I"), new InsnEntry(Opcodes.I2L),
				new InsnEntry(Opcodes.INVOKEVIRTUAL), new InsnEntry(Opcodes.CHECKCAST));
		if (ep1.find(cn)) {
			FieldInsnNode id = ep1.get(0, FieldInsnNode.class);
			addHook("getId", id.name, id.owner, id.owner, id.desc, -1);
		}

		EntryPattern ep = new EntryPattern(new InsnEntry(Opcodes.BIPUSH, "95"), new InsnEntry(Opcodes.PUTFIELD, "I"));
		if (ep.find(cn)) {
			FieldInsnNode level = ep.get(1, FieldInsnNode.class);
			addHook("getLevel", level.name, level.owner, level.owner, level.desc, -1);
		}

		EntryPattern ep2 = new EntryPattern(new InsnEntry(Opcodes.LDC, "null"), new InsnEntry(Opcodes.PUTFIELD, "Ljava/lang/String;"));
		if (ep2.find(cn)) {
			FieldInsnNode name = ep2.get(1, FieldInsnNode.class);
			addHook("getName", name.name, name.owner, name.owner, name.desc, -1);
		}

		EntryPattern ep3 = new EntryPattern(new InsnEntry(Opcodes.ANEWARRAY), new InsnEntry(Opcodes.PUTFIELD, "[Ljava/lang/String;"));
		if (ep3.find(cn)) {
			FieldInsnNode actions = ep3.get(1, FieldInsnNode.class);
			addHook("getActions", actions.name, actions.owner, actions.owner, actions.desc, -1);
		}
	}
}

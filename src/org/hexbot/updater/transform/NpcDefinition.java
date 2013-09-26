package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.search.Multipliers;
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
		EntryPattern idPattern = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "I"), new InsnEntry(Opcodes.I2L),
				new InsnEntry(Opcodes.INVOKEVIRTUAL), new InsnEntry(Opcodes.CHECKCAST));
		if (idPattern.find(cn)) {
			FieldInsnNode id = idPattern.get(0, FieldInsnNode.class);
			addHook("getId", id.name, id.owner, id.owner, id.desc, Multipliers.getBest(id));
		}

		EntryPattern levelPattern = new EntryPattern(new InsnEntry(Opcodes.BIPUSH, "95"), new InsnEntry(Opcodes.PUTFIELD, "I"));
		if (levelPattern.find(cn)) {
			FieldInsnNode level = levelPattern.get(1, FieldInsnNode.class);
			addHook("getLevel", level.name, level.owner, level.owner, level.desc, Multipliers.getBest(level));
		}

		EntryPattern namePattern = new EntryPattern(new InsnEntry(Opcodes.LDC, "null"), new InsnEntry(Opcodes.PUTFIELD, "Ljava/lang/String;"));
		if (namePattern.find(cn)) {
			FieldInsnNode name = namePattern.get(1, FieldInsnNode.class);
			addHook("getName", name.name, name.owner, name.owner, name.desc, -1);
		}

		EntryPattern actionsPattern = new EntryPattern(new InsnEntry(Opcodes.ANEWARRAY), new InsnEntry(Opcodes.PUTFIELD, "[Ljava/lang/String;"));
		if (actionsPattern.find(cn)) {
			FieldInsnNode actions = actionsPattern.get(1, FieldInsnNode.class);
			addHook("getActions", actions.name, actions.owner, actions.owner, actions.desc, -1);
		}
	}
}

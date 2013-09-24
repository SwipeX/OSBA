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

public class PlayerDefinition extends Container {

	public PlayerDefinition(Updater updater) {
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
				if (cn.getFieldTypeCount() == 4 && cn.fieldCount("J") == 2) {
					CLASS_MATCHES.put("PlayerDefinition", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		EntryPattern ep = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "[I"), new InsnEntry(Opcodes.GETSTATIC, "[I"));
		if (ep.find(cn)) {
			FieldInsnNode ids = ep.get(0, FieldInsnNode.class);
			addHook("getAppearanceIds", ids.name, ids.owner, ids.owner, ids.desc, -1);
			FieldInsnNode idx = ep.get(1, FieldInsnNode.class);
			addHook("getAppearanceIndices", idx.name, idx.owner, idx.owner, idx.desc, -1);
		}
		FieldNode id = cn.getField(null, "I");
		addHook("getNpcId", id.name, cn.name, cn.name, id.desc, Multipliers.getMostUsed(cn, id));
		FieldNode female = cn.getField(null, "Z");
		addHook("isFemale", female.name, cn.name, cn.name, female.desc, -1);
	}
}

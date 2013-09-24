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

public class CollisionMap extends Container {

	public CollisionMap(Updater updater) {
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
				if (cn.getFieldTypeCount() == 2 && cn.fieldCount("[[I") == 1 && cn.fieldCount("I") == 4) {
					CLASS_MATCHES.put("CollisionMap", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		FieldNode flags = cn.getPublicField(null, "[[I");
		addHook("getFlags", flags.name, cn.name, cn.name, flags.desc, -1);
		EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "I", cn.name),
				new InsnEntry(Opcodes.GETFIELD, "I", cn.name), new InsnEntry(Opcodes.GETFIELD, "[[I", cn.name));
		if (pattern.find(cn)) {
			FieldInsnNode x = (FieldInsnNode) pattern.get(0).getInstance();
			FieldInsnNode y = (FieldInsnNode) pattern.get(1).getInstance();
			addHook("getOffsetX", x.name, x.owner, cn.name, x.desc, Multipliers.getSurrounding(x));
			addHook("getOffsetY", y.name, y.owner, cn.name, y.desc, Multipliers.getSurrounding(y));
		}
	}
}

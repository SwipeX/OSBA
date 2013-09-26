package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.search.Multipliers;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class Renderable extends Container {

	public Renderable(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 1;
	}


	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if ((cn.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
				if (cn.superName.equals(CLASS_MATCHES.get("CacheableNode"))) {
					CLASS_MATCHES.put("Renderable", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		addHook("getModel", "model", cn.name, cn.name, "Ljava/lang/Object;", -1);
		/**
		 * Renderable() {
		 * 		modelHeight = 0;
		 * }
		 */
		for (MethodNode m : cn.methods) {
			if (m.name.equals("<init>")) {
				EntryPattern height = new EntryPattern(new InsnEntry(Opcodes.LDC), new InsnEntry(Opcodes.PUTFIELD, "I"));
				if (height.find(m)) {
					FieldInsnNode modelHeight = height.get(1, FieldInsnNode.class);
					addHook("getModelHeight", modelHeight.name, modelHeight.owner, modelHeight.owner, "I", Multipliers.getMostUsed(modelHeight));
				}
			}
		}
	}

}

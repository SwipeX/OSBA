package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

public class NodeDeque extends Container {

	public NodeDeque(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 2;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals("java/lang/Object")) {
				if (cn.fields.size() == 2 && cn.fieldCount("L" + CLASS_MATCHES.get("Node") + ";") == 2) {
					CLASS_MATCHES.put("NodeDeque", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		for (MethodNode mn : cn.methods) {
			for (AbstractInsnNode ain : mn.instructions.toArray()) {
				if (ain.getOpcode() != Opcodes.PUTFIELD) continue;
				FieldInsnNode fin = (FieldInsnNode) ain;
				if (fin.owner.equals(cn.name)) {
					addHook("getCurrent", fin.name, fin.owner, cn.name, fin.desc, -1);
					for (FieldNode fn : cn.fields) {
						if (fn.name.equals(fin.name) || (fn.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) continue;
						addHook("getHead", fn.name, cn.name, cn.name, fn.desc, -1);
						break;
					}
					return;
				}
			}
		}
	}
}

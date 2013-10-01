package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class ObjectDefinition extends Container {

	public ObjectDefinition(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 4;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.getFieldTypeCount() == 6 && cn.fieldCount("[S") == 4 && cn.fieldCount("[I") == 4) {
				CLASS_MATCHES.put("ObjectDefinition", cn.name);
				return cn;
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
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

		searcher: for (ClassNode c : updater.classnodes.values()) {
			for (MethodNode mn : c.methods) {
				if ((mn.access & Opcodes.ACC_STATIC) != Opcodes.ACC_STATIC) continue;
				if (mn.desc.equals("(I)L" + cn.name + ";")) {
					addHook("getObjectDefinition", mn.name, c.name, c.name, mn.desc, -1);
					break searcher;
				}
			}
		}
	}
}

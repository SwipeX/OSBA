package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.search.Multipliers;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

public class Player extends Container {

	public Player(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 4;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals(CLASS_MATCHES.get("Character"))) {
				if (cn.fieldCount("Ljava/lang/String;") == 1 && cn.fieldCount("Z") == 1 && cn.getAbnormalFieldCount() == 2) {
					CLASS_MATCHES.put("Player", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		FieldNode name = cn.getField(null, "Ljava/lang/String;");
		addHook("getName", name.name, cn.name, cn.name, name.desc, -1);
		FieldNode def = cn.getField(null, "L" + CLASS_MATCHES.get("PlayerDefinition") + ";");
		addHook("getDefinition", def.name, cn.name, cn.name, def.desc, -1);
		EntryPattern ep = new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"),
				new InsnEntry(Opcodes.GETFIELD, "L" + CLASS_MATCHES.get("PlayerDefinition") + ";"));
		if (ep.find(cn)) {
			FieldInsnNode lvl = (FieldInsnNode) ep.get(0).getInstance();
			addHook("getLevel", lvl.name, lvl.owner, lvl.owner, lvl.desc, Multipliers.getMostUsed(lvl));
		}
		ClassNode client = updater.classnodes.get("client");
		EntryPattern ep1 = new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, "I", cn.name),
				new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.PUTFIELD, "I", cn.name),
				new InsnEntry(Opcodes.PUTFIELD, "L" + CLASS_MATCHES.get("Model") + ";"));
		if (ep1.find(client)) {
			FieldInsnNode skull = (FieldInsnNode) ep1.get(2).getInstance();
			addHook("getSkullIcon", skull.name, skull.owner, cn.name, skull.desc, Multipliers.getMostUsed(skull));
		}
	}
}

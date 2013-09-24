package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.ASMUtil;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.search.Multipliers;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

public class Projectile extends Container {

	public Projectile(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 10;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals(CLASS_MATCHES.get("Renderable"))) {
				if (cn.getFieldTypeCount() == 4 && cn.fieldCount("Z") == 1) {
					CLASS_MATCHES.put("Projectile", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		FieldNode moving = cn.getField(null, "Z");
		addHook("isMoving", moving.name, cn.name, cn.name, moving.desc, -1);
		for (FieldNode fn : cn.fields) {
			if ((fn.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
				continue;
			}
			String desc = fn.desc;
			if (desc.startsWith("L") && desc.endsWith(";") && !desc.contains("java")) {
				addHook("getDefinition", fn.name, cn.name, cn.name, fn.desc, -1);
				break;
			}
		}
		MethodNode mn = cn.getMethod("(IIIII)V");
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain.getOpcode() == Opcodes.GETFIELD) {
				FieldInsnNode fin = (FieldInsnNode) ain;
				if (fin.owner.equals(cn.name) && fin.desc.equals("I")) {
					FieldInsnNode x = (FieldInsnNode) ASMUtil.getNext(fin, Opcodes.GETFIELD);
					FieldInsnNode y = (FieldInsnNode) ASMUtil.getNext(x, Opcodes.GETFIELD);
					addHook("getX", x.name, x.owner, cn.name, x.desc, Multipliers.getMostUsed(x));
					addHook("getY", y.name, y.owner, cn.name, y.desc, Multipliers.getMostUsed(y));
					break;
				}
			}
		}
		EntryPattern speeds = new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, "D", cn.name),
				new InsnEntry(Opcodes.PUTFIELD, "D", cn.name), new InsnEntry(Opcodes.DMUL),
				new InsnEntry(Opcodes.INVOKESTATIC, "sqrt"));
		if (speeds.find(cn)) {
			FieldInsnNode y = (FieldInsnNode) speeds.get(0).getInstance();
			FieldInsnNode x = (FieldInsnNode) speeds.get(1).getInstance();
			addHook("getSpeedY", y.name, y.owner, cn.name, y.desc, -1);
			addHook("getSpeedX", x.name, x.owner, cn.name, x.desc, -1);
			FieldInsnNode scalar = (FieldInsnNode) ASMUtil.getNext(x, Opcodes.PUTFIELD);
			addHook("getScalar", scalar.name, scalar.owner, cn.name, scalar.desc, -1);
		}
		EntryPattern zep = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "D", cn.name),
				new InsnEntry(LdcInsnNode.class, "0.5"));
		if (zep.find(cn)) {
			FieldInsnNode z = (FieldInsnNode) zep.get(0).getInstance();
			addHook("getSpeedZ", z.name, z.owner, cn.name, z.desc, -1);
			FieldInsnNode height = (FieldInsnNode) ASMUtil.getNext(z, Opcodes.PUTFIELD);
			addHook("getHeight", height.name, height.owner, cn.name, height.desc, -1);
		}
		for (MethodNode m : cn.methods) {
			AbstractInsnNode second = m.instructions.get(1);
			if (second != null && second instanceof FieldInsnNode) {
				FieldInsnNode fin = (FieldInsnNode) second;
				if (fin.owner.equals(cn.name) && fin.desc.equals("I")) {
					addHook("getId", fin.name, fin.owner, cn.name, fin.desc, Multipliers.getMostUsed(fin));
					break;
				}
			}
		}
	}
}

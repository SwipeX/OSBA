package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.ASMUtil;
import org.hexbot.updater.search.Identifying;
import org.hexbot.updater.search.Multipliers;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Collection;
import java.util.Map;

public class Widget extends Container {

	public Widget(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 21;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.superName.equals(CLASS_MATCHES.get("Node"))) {
				if (cn.fieldCount("[Ljava/lang/Object;") > 10) {
					CLASS_MATCHES.put("Widget", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		addHook("getMasterX", "masterX", cn.name, cn.name, "I", -1);
		addHook("getMasterY", "masterY", cn.name, cn.name, "I", -1);
		for (FieldNode fn : cn.fields) {
			if (fn.desc.equals("[L" + cn.name + ";")) {
				addHook("getComponents", fn.name, cn.name, cn.name, getDescriptor(1), -1);
			}
			if (fn.desc.equals("L" + cn.name + ";")) {
				addHook("getParent", fn.name, cn.name, cn.name, getDescriptor(), -1);
			}
		}
		Collection<ClassNode> cns = getUpdater().classnodes.values();
		addHook("getX", Identifying.getFieldFromPush(cns, 1500, "I"), true);
		addHook("getY", Identifying.getFieldFromPush(cns, 1501, "I"), true);
		addHook("getWidth", Identifying.getFieldFromPush(cns, 1502, "I"), true);
		addHook("getHeight", Identifying.getFieldFromPush(cns, 1503, "I"), true);
		addHook("getParentId", Identifying.getFieldFromPush(cns, 1505, "I"), true);
		addHook("getComponentIndex", Identifying.getFieldFromPush(cns, 1702, "I"), true);
		addHook("getActions", Identifying.getFieldFromPush(cns, 1801, "[Ljava/lang/String;"), false);
		addHook("getText", Identifying.getFieldFromPush(cns, 1112, "Ljava/lang/String;"), false);
		addHook("getComponentName", Identifying.getFieldFromPush(cns, 1802, "Ljava/lang/String;"), false);
		addHook("getScrollX", Identifying.getFieldFromPush(cns, 1100, "I"), true);
		addHook("getScrollY", Identifying.getFieldFromPush(cns, 1601, "I"), true);
		addHook("getTextureId", Identifying.getFieldFromPush(cns, 1105, "I", true), true);
		addHook("getTextColor", Identifying.getFieldFromPush(cns, 1101, "I", true), true);
		addHook("getBorderThickness", Identifying.getFieldFromPush(cns, 1116, "I", true), true);
		locateInv(cn);
		locateTrade(cn);
		identifyId(cn);
	}

	private void addHook(String name, FieldInsnNode f, boolean multiply) {
		addHook(name, f, f.owner, multiply ? Multipliers.getBest(f) : -1);
	}

	public void locateTrade(ClassNode cn) {
		for (MethodNode mn : cn.methods) {
			if (mn.name.equals("<init>")) {
				AbstractInsnNode[] ains = mn.instructions.toArray();
				for (int i = 0; i < ains.length; i++) {
					if (ains[i] instanceof FieldInsnNode) {
						FieldInsnNode ain = (FieldInsnNode) ains[i];
						if (ain.getOpcode() == Opcodes.GETSTATIC) {
							if (ain.getNext() instanceof FieldInsnNode) {
								FieldInsnNode ain1 = (FieldInsnNode) ain.getNext();
								if (ain1.getOpcode() == Opcodes.PUTFIELD) {
									FieldInsnNode id = (FieldInsnNode) ASMUtil.getNext(ain1, FieldInsnNode.class);
									FieldInsnNode stack = (FieldInsnNode) ASMUtil.getNext(id, FieldInsnNode.class);
									addHook("getTradeId", id.name, id.owner, cn.name, "I", Multipliers.getBest(id));
									addHook("getTradeStack", stack.name, stack.owner, cn.name, "I", Multipliers.getBest(stack));
								}
							}
						}
					}
				}
			}
		}
	}

	private void identifyId(ClassNode classnode) {
		for (MethodNode mn : classnode.methods) {
			if (!mn.name.equals("<init>"))
				continue;
			AbstractInsnNode[] ains = mn.instructions.toArray();
			for (int i = 0; i < ains.length; i++) {
				if (ains[i] instanceof FieldInsnNode) {
					FieldInsnNode id = (FieldInsnNode) ains[i];
					if (id.desc.equals("I")) {
						addHook("getId", id.name, id.owner, classnode.name, "I", Multipliers.getBest(id));
						return;
					}
				}
			}
		}
	}

	public void locateInv(ClassNode cn) {
		String first = null;
		for (MethodNode mn : cn.methods) {
			if (mn.desc.equals("(III)V")) { //can be a short also
				for (AbstractInsnNode ain : mn.instructions.toArray()) {
					if (ain instanceof FieldInsnNode) {
						if (first == null) {
							FieldInsnNode fin = (FieldInsnNode) ain;
							addHook("getSlotIds", fin.name, fin.owner, "[I", cn.name, -1);
							first = fin.name;
						} else {
							FieldInsnNode fin = (FieldInsnNode) ain;
							if (!fin.name.equals(first)) {
								addHook("getStackSizes", fin.name, fin.owner, "[I", cn.name, -1);
								return;
							}
						}
					}
				}
			}
		}
	}

}

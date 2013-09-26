package org.hexbot.updater.search;

import org.objectweb.asm.tree.*;

import java.util.Collection;

/**
 * @author Caleb Bradford
 */
public class Identifying {

	public static FieldInsnNode getNodeFromPush(MethodNode mn, int push) {
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain instanceof IntInsnNode) {
				IntInsnNode iin = (IntInsnNode) ain;
				if (iin.operand == push) {
					FieldInsnNode fin = (FieldInsnNode) ASMUtil.getNext(iin, FieldInsnNode.class);
					if (fin != null) {
						return fin;
					}
				}
			}
		}
		return null;
	}

	public static FieldInsnNode getNodeFromPush(ClassNode cn, int push) {
		for (MethodNode mn : cn.methods) {
			FieldInsnNode node = getNodeFromPush(mn, push);
			if (node != null)
				return node;
		}
		return null;
	}
	
	public static FieldInsnNode getFieldFromPush(Collection<ClassNode> classes, int value, String desc) {
		for (final ClassNode cn : classes) {
			for (final MethodNode mn : cn.methods) {
				int k = 0;
				for (AbstractInsnNode ain : mn.instructions.toArray()) {
					if (ain instanceof IntInsnNode) {
						if (((IntInsnNode) ain).operand == value) {
							if (mn.instructions.size() < k + 20)
								continue;
							for (int i = k; i < k + 20; i++) {
								if (mn.instructions.toArray()[i] instanceof FieldInsnNode && mn.instructions.toArray()[i].getOpcode() == 180) {
									FieldInsnNode fin = (FieldInsnNode) mn.instructions.toArray()[i];
									if (fin != null && fin.desc.equals(desc))
										return fin;
								}
							}
							for (int i = k - 20; i < k + 20; i++) {
								if (mn.instructions.toArray()[i] instanceof FieldInsnNode && mn.instructions.toArray()[i].getOpcode() == 180) {
									FieldInsnNode fin = (FieldInsnNode) mn.instructions.toArray()[i];
									if (fin != null && fin.desc.equals(desc))
										return fin;
								}
							}
						}
					}
					k++;
				}
			}
		}
		return null;
	}

	public static FieldInsnNode getFieldFromPush(Collection<ClassNode> classes, int value, String desc, int opcode) {
		for (final ClassNode cn : classes) {
			for (final MethodNode mn : cn.methods) {
				int k = 0;
				for (AbstractInsnNode ain : mn.instructions.toArray()) {
					if (ain instanceof IntInsnNode) {
						if (((IntInsnNode) ain).operand == value) {
							if (mn.instructions.size() < k + 20)
								continue;
							for (int i = k; i < k + 20; i++) {
								if (mn.instructions.toArray()[i] instanceof FieldInsnNode && mn.instructions.toArray()[i].getOpcode() == opcode) {
									FieldInsnNode fin = (FieldInsnNode) mn.instructions.toArray()[i];
									if (fin != null && fin.desc.equals(desc))
										return fin;
								}
							}
							for (int i = k + 20; i > k + 20; i--) {
								if (mn.instructions.toArray()[i] instanceof FieldInsnNode && mn.instructions.toArray()[i].getOpcode() == opcode) {
									FieldInsnNode fin = (FieldInsnNode) mn.instructions.toArray()[i];
									if (fin != null && fin.desc.equals(desc))
										return fin;
								}
							}
						}
					}
					k++;
				}
			}
		}
		return null;
	}

	public static FieldInsnNode getFieldFromPush(Collection<ClassNode> classes, int value, String desc, boolean getstatic) {
		int a = 181;
		for (final ClassNode cn : classes) {
			for (final MethodNode mn : cn.methods) {
				int k = 0;
				for (AbstractInsnNode ain : mn.instructions.toArray()) {
					if (ain instanceof IntInsnNode) {
						if (((IntInsnNode) ain).operand == value) {
							if (mn.instructions.size() < k + 20)
								continue;
							for (int i = k; i < k + 20; i++) {
								if (mn.instructions.toArray()[i] instanceof FieldInsnNode && mn.instructions.toArray()[i].getOpcode() == a) {
									FieldInsnNode fin = (FieldInsnNode) mn.instructions.toArray()[i];
									if (fin != null && fin.desc.equals(desc))
										return fin;
								}
							}
							for (int i = k + 20; i > k + 20; i--) {
								if (mn.instructions.toArray()[i] instanceof FieldInsnNode && mn.instructions.toArray()[i].getOpcode() == a) {
									FieldInsnNode fin = (FieldInsnNode) mn.instructions.toArray()[i];
									if (fin != null && fin.desc.equals(desc))
										return fin;
								}
							}
						}
					}
					k++;
				}
			}
		}
		return null;
	}
	
}

package org.hexbot.updater.search;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

/**
 * @author Caleb Bradford
 */
public class Multipliers {

	public static int getBest(String owner, String name) {
		return Cache.get(owner, name).intValue();
	}

	public static int getBest(FieldInsnNode f) {
		return getBest(f.owner, f.name);
	}

	public static int getBest(ClassNode cn, FieldNode f) {
		return getBest(cn.name, f.name);
	}

}

package org.hexbot.updater.transform;

import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public abstract class Transform {

	public abstract ClassNode validate(Map<String, ClassNode> classnodes);
	public abstract void transform(ClassNode cn);
}

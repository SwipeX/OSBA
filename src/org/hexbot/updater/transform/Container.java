package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;

public abstract class Container extends Transform {

	public final String GETTER_PREFIX = "org/hexbot/core/impl/";
	public final String GETTER = GETTER_PREFIX + getClass().getSimpleName();

	public static final Map<String, String> CLASS_MATCHES = new HashMap<>();

	protected Updater updater;

	protected int successful = 0;

	public ClassNode cn = null;
	
	public String getInterfaceString() {
		return GETTER;
	}
	
	public String getName(ClassNode cn) {
		return cn != null ? cn.name : null;
	}
	
	public FieldNode getField(ClassNode cn, String name, String desc, boolean ignoreStatic) {
		if (cn != null) {
			return cn.getField(name, desc, ignoreStatic);
		}
		return null;
	}
	
	public FieldNode getField(ClassNode cn, String name, String desc) {
		return getField(cn, name, desc, true);
	}
	
	public FieldNode getPublicField(ClassNode cn, String name, String desc) {
		if (cn != null) {
			return cn.getPublicField(name, desc);
		}
		return null;
	}
	
	public MethodNode getMethod(ClassNode cn, String desc) {
		if (cn != null) {
			return cn.getMethod(desc);
		}
		return null;
	}

	public String getterDesc(String iface) {
		return "L" + GETTER_PREFIX + iface + ";";
	}

	public Container(Updater updater) {
		this.updater = updater;
	}

	public abstract int getTotalHookCount();

	public final int getHookSuccessCount() {
		return successful;
	}
}

package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.Updater;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class Client extends Container {

	@Override
	public int getTotalHookCount() {
		return 0;
	}

	@Override
	public String getInterfaceString() {
		return GETTER_PREFIX.substring(0, GETTER_PREFIX.length() - 1) + "Client";
	}
	
	public Client(Updater updater) {
		super(updater);
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		return classnodes.get("client");
	}

	@Override
	public void transform(ClassNode cn) {

	}
}

package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.tree.ClassNode;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Caleb Bradford
 */
public class Mouse extends Container {

	private static final String[] IMPL = {"java/awt/event/MouseListener", "java/awt/event/MouseMotionListener", "java/awt/event/FocusListener"};

	public Mouse(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 0;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.interfaces.containsAll(Arrays.asList(IMPL))) {
				CLASS_MATCHES.put("Mouse", cn.name);
				return cn;
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
	}

}

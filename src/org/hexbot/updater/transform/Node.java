package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.util.Map;

public class Node extends Container {

	public Node(Updater updater) {
		super(updater);
	}

	@Override
	public int getTotalHookCount() {
		return 2;
	}

	@Override
	public ClassNode validate(Map<String, ClassNode> classnodes) {
		for (ClassNode cn : classnodes.values()) {
			if (cn.fields.size() == 3) {
				if (cn.fieldCount("J") == 1 && cn.fieldCount("L" + cn.name + ";") == 2) {
					CLASS_MATCHES.put("Node", cn.name);
					return cn;
				}
			}
		}
		return null;
	}

	@Override
	public void transform(ClassNode cn) {
		addNodeHook(this, "L" + GETTER + ";", cn);
	}

	/**
	 * For convenience when hooking CacheableNode
	 */
	protected static void addNodeHook(Container container, String desc, ClassNode cn) {
		/**
		 * 	next & previous are found in the remove() method
		 *
		 * 	<Code>
		 *
		 *	if(this.next != null) {
		 *		this.next.previous = this.previous;
		 *		this.previous.next = this.next;
		 *		this.previous = null;
		 *		this.next = null;
		 *    }
		 *
		 */
		FieldInsnNode fin;
		EntryPattern next = new EntryPattern(new InsnEntry(Opcodes.GETFIELD), new InsnEntry(Opcodes.IFNONNULL));
		//TODO fix patterns
		//idk wtf is happening here, it returns false even though the pattern is found.
		//if i remove the if boxing it works fine
		//if (next.find(cn)) {
		next.find(cn);
		fin = next.get(0, FieldInsnNode.class);
		container.addHook("getNext", fin.name, fin.owner, fin.owner, desc, -1);
		//}
		EntryPattern previous = new EntryPattern(new InsnEntry(Opcodes.GETFIELD), new InsnEntry(Opcodes.PUTFIELD));
		if (previous.find(cn)) {
			fin = previous.get(1, FieldInsnNode.class);
			container.addHook("getPrevious", fin.name, fin.owner, fin.owner, desc, -1);
		}
	}

}

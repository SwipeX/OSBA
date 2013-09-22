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
		/**
		 * next & previous are found in the remove() method
		 *
		 * 	<Code>
		 *
		 *	if(this.next != null) {
		 *		this.next.previous = this.previous;
		 *		this.previous.next = this.next;
		 *		this.previous = null;
		 *		this.next = null;
		 *	}
		 *
		 */
		FieldInsnNode fin;
		EntryPattern next = new EntryPattern(
				new InsnEntry(Opcodes.GETFIELD),
				new InsnEntry(Opcodes.IFNONNULL)
		);
		next.find(cn);
		fin = (FieldInsnNode) next.get(0).getInstance();
		String desc = "L" + GETTER + ";";
		addHook("getNext", fin.name, fin.owner, fin.owner, desc, -1);
		EntryPattern previous = new EntryPattern(
				new InsnEntry(Opcodes.GETFIELD),
				new InsnEntry(Opcodes.PUTFIELD)
		);
		previous.find(cn);
		fin = (FieldInsnNode) previous.get(1).getInstance();
		addHook("getPrevious", fin.name, fin.owner, fin.owner, desc, -1);
	}

}

package org.hexbot.updater.search;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Caleb Bradford
 */
public class Multiplier {

	FieldNode field;
	Map<Integer, AtomicInteger> multipliers;
	ClassNode owner;

	protected Multiplier(ClassNode owner, FieldNode field) {
		this.owner = owner;
		this.field = field;
		this.multipliers = new HashMap<>();
	}

	public boolean isMatch(FieldInsnNode insn) {
		return insn.owner.equals(owner.name) && insn.desc.equals(field.desc) && field.name.equals(insn.name);
	}

	public FieldNode getField() {
		return field;
	}

	public Map<Integer, AtomicInteger> getMultipliers() {
		return multipliers;
	}

	public ClassNode getOwner() {
		return owner;
	}

	public int getMostUsed() {
		Map.Entry<Integer, AtomicInteger> entry = null;
		for (Map.Entry<Integer, AtomicInteger> multiplier : multipliers.entrySet()) {
			if (entry == null || entry.getValue().get() < multiplier.getValue().get()) {
				entry = multiplier;
			}
		}
		return entry == null ? 1 : entry.getKey();
	}

	public boolean isMatch(ClassNode owner, FieldNode f) {
		return getOwner().name.equals(owner.name) && field.name.equals(f.name);
	}

	public boolean isMatch(String owner, String fName) {
		return getOwner().name.equals(owner) && field.name.equals(fName);
	}

	public void debug() {
		for (Map.Entry<Integer, AtomicInteger> multiplier : multipliers.entrySet()) {
			System.out.println(multiplier.getKey() + " is used " + multiplier.getValue().get() + " times");
		}
	}

}

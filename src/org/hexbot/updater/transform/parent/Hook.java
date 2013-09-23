package org.hexbot.updater.transform.parent;

/**
 * @author Tim
 */
public class Hook {

	public String name, clazz, field, desc, toInject;
	public int multiplier;

	public Hook(String name, String field, String clazz, String toInject, String desc, int multiplier) {
		this.name = name;
		this.clazz = clazz;
		this.field = field;
		this.desc = desc;
		this.toInject = toInject;
		this.multiplier = multiplier;
	}
}

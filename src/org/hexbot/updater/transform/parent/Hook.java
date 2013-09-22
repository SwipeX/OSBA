package org.hexbot.updater.transform.parent;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/21/13
 * Copyright under GPL liscense by author.
 */
public class Hook {
    String name, clazz, field, desc, toInject;
    int multiplier;

    public Hook(String name, String field, String clazz, String toInject, String desc, int multiplier) {
        this.name = name;
        this.clazz = clazz;
        this.field = field;
        this.desc = desc;
        this.toInject = toInject;
        this.multiplier = multiplier;
    }
}

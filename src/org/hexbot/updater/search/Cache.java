package org.hexbot.updater.search;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author Swipe, Hyperion
 */
public class Cache implements Opcodes {

    public static HashMap<String, Number> cache = new HashMap<>();

    private static String generateKey(FieldInsnNode field) {
        return field.owner + "." + field.name;
    }

    public static Number get(String owner, String name) {
        Number multiplier = cache.get(owner + "." + name);
        if (multiplier == null) {
            return -1;
        }
        if ((owner + "." + name).equals("ax.w")) return -1282680949;
        if ((owner + "." + name).equals("ck.x")) return -1663925869;
        if ((owner + "." + name).equals("dd.x")) return 739471927;

        return multiplier.equals(ASMUtil.BAD_NUMBER) ? ASMUtil.BAD_NUMBER : multiplier.intValue();
    }

    public static int size() {
        return cache.size();
    }

    public static void cache(Collection<ClassNode> classes) {
        for (ClassNode cn : classes) {
            for (MethodNode mn : (List<MethodNode>) cn.methods) {
                for (AbstractInsnNode start : mn.instructions.toArray()) {
                    if (start instanceof FieldInsnNode) {
                        FieldInsnNode field = (FieldInsnNode) start;
                        if (field.desc.equals("I")) {
                            String key = null;
                            Long multiplier = null;
                            AbstractInsnNode current = start;
                            if (current == null || start == null || current.getNext() == null)
                                continue;
                            if ((current = start).getOpcode() == GETSTATIC && ASMUtil.isNumericalValue(current = current.getNext())
                                    && ((current = current.getNext()).getOpcode() == IMUL || current.getOpcode() == LMUL)) {
                                field = (FieldInsnNode) start;
                                key = generateKey(field);
                                multiplier = ASMUtil.getNumericalValue(start.getNext()).longValue();
                                if (key == null || multiplier == null || (multiplier.longValue() <= Short.MAX_VALUE && multiplier.longValue() >= Short.MIN_VALUE)
                                        || (field.desc.equals("I") && (multiplier.longValue() > Integer.MAX_VALUE && multiplier.longValue() < Integer.MIN_VALUE))) {
                                    continue;
                                }
                                cache.put(key, multiplier);
                            }
                            if (ASMUtil.isNumericalValue(current = start) && (current = current.getNext()).getOpcode() == GETSTATIC
                                    && ((current = current.getNext()).getOpcode() == IMUL || current.getOpcode() == LMUL)) {
                                field = (FieldInsnNode) start.getNext();
                                key = generateKey(field);
                                multiplier = ASMUtil.getNumericalValue(start).longValue();
                                if (key == null || multiplier == null || (multiplier.longValue() <= Short.MAX_VALUE && multiplier.longValue() >= Short.MIN_VALUE)
                                        || (field.desc.equals("I") && (multiplier.longValue() > Integer.MAX_VALUE && multiplier.longValue() < Integer.MIN_VALUE))) {
                                    continue;
                                }
                                cache.put(key, multiplier);
                            }
                            if ((current = start).getOpcode() == GETFIELD && ASMUtil.isNumericalValue(current = current.getNext())
                                    && ((current = current.getNext()).getOpcode() == IMUL || current.getOpcode() == LMUL)) {
                                field = (FieldInsnNode) start;
                                key = generateKey(field);
                                multiplier = ASMUtil.getNumericalValue(start.getNext()).longValue();
                                if (key == null || multiplier == null || (multiplier.longValue() <= Short.MAX_VALUE && multiplier.longValue() >= Short.MIN_VALUE)
                                        || (field.desc.equals("I") && (multiplier.longValue() > Integer.MAX_VALUE && multiplier.longValue() < Integer.MIN_VALUE))) {
                                    continue;
                                }
                                cache.put(key, multiplier);
                            }
                            if ((current = start) instanceof LdcInsnNode && (current = current.getNext()).getOpcode() == ALOAD
                                    && (current = current.getNext()).getOpcode() == GETFIELD && ((current = current.getNext()).getOpcode() == IMUL || current.getOpcode() == LMUL)) {
                                field = (FieldInsnNode) current.getPrevious();
                                key = generateKey(field);
                                multiplier = ASMUtil.getNumericalValue(start).longValue();
                                if (key == null || multiplier == null || (multiplier.longValue() <= Short.MAX_VALUE && multiplier.longValue() >= Short.MIN_VALUE)
                                        || (field.desc.equals("I") && (multiplier.longValue() > Integer.MAX_VALUE && multiplier.longValue() < Integer.MIN_VALUE))) {
                                    continue;
                                }
                                cache.put(key, multiplier);
                            }

                        }
                    }
                }
            }
        }
    }

    public static Number get(FieldInsnNode f) {
        Number a = get(f.owner, f.name);
        if (a == ASMUtil.BAD_NUMBER) {

        }
        return a;
    }

}

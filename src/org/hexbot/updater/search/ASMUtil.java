package org.hexbot.updater.search;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ASMUtil {

    public static final Number BAD_NUMBER = 1000000;
    private static Map<String, Integer> OPCODE_MAP = new HashMap<>();

    static {
        boolean advance = false;
        for (Field f : Opcodes.class.getFields()) {
            if (!advance) {
                if (!f.getName().equals("NOP")) {
                    continue;
                }
                advance = true;
                try {
                    OPCODE_MAP.put(f.getName(), (Integer) f.get(null));
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    OPCODE_MAP.put(f.getName(), (Integer) f.get(null));
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean access(int check, int access) {
        return (check & access) == access;
    }

    public static boolean access(FieldNode fn, int access) {
        return access(fn.access, access);
    }

    public static boolean access(ClassNode cn, int access) {
        return access(cn.access, access);
    }

    public static boolean isDescStandard(String desc) {
        String[] keywords = {"I", "Z", "F", "J", "S", "B", "D"};
        return desc.contains("java") && desc.contains("/") || Arrays.binarySearch(keywords, desc) >= 0 || desc.contains("[") && Arrays.binarySearch(keywords, stripDesc(desc)) >= 0;
    }

    public static boolean isFieldStandard(FieldNode field) {
        return isDescStandard(field.desc);
    }

    public static String stripDesc(String desc) {
        if (desc.startsWith("L")) {
            desc = desc.substring(1);
        }
        return desc.replaceAll("\\[L", "").replaceAll("\\[", "").replaceAll(";", "");
    }

    public static int getOpcode(String opstring) {
        try {
            return OPCODE_MAP.get(opstring.toUpperCase());
        } catch (Exception e) {
            return -1;
        }
    }

	public static String getOpcode (int opcode) {
		for (Map.Entry<String, Integer> i : OPCODE_MAP.entrySet())
			if (i.getValue() == opcode)
				return i.getKey();
		return "null (" + opcode + ")";
	}


    public static int getReturnFor(String desc) {
        if (desc.startsWith("[")) {
            return Opcodes.ARETURN;
        } else if (desc.equals("D")) {
            return Opcodes.DRETURN;
        } else if (desc.equals("F")) {
            return Opcodes.FRETURN;
        } else if (desc.equals("J")) {
            return Opcodes.LRETURN;
        }
        String[] i = {"I", "Z"};
        for (String s : i) {
            if (s.equals(desc)) {
                return Opcodes.IRETURN;
            }
        }
        return Opcodes.ARETURN;
    }

    public static FieldNode getField(ClassNode cn, FieldInsnNode fin) {
        for (FieldNode fn : (List<FieldNode>) cn.fields) {
            if (fn.name.equals(fin.name) && fn.desc.equals(fin.desc)) {
                return fn;
            }
        }
        return null;
    }

    public static FieldNode getField(ClassNode cn, String field, String desc) {
        for (FieldNode fn : (List<FieldNode>) cn.fields) {
            if (fn.name.equals(field) && fn.desc.equals(desc)) {
                return fn;
            }
        }
        return null;
    }

    public static MethodNode getMethod(ClassNode cn, String method) {
        for (MethodNode mn : (List<MethodNode>) cn.methods) {
            if (!mn.name.equals(method)) {
                continue;
            }
            return mn;
        }
        return null;
    }


    public static boolean matches(int opcode, int... opcodes) {
        for (int i : opcodes) {
            if (opcode == i) {
                return true;
            }
        }
        return false;
    }

    public static Number getNumericalValue(final AbstractInsnNode instruction) {
        if (instruction == null)
            return -1;
        if (instruction instanceof LdcInsnNode) {
            final Object value = ((LdcInsnNode) instruction).cst;
            if (value instanceof Number) {
                return (Number) value;
            }
        } else if (instruction instanceof IntInsnNode) {
            return ((IntInsnNode) instruction).operand;
        } else if (instruction.getOpcode() - Opcodes.ICONST_M1 >= 0 && instruction.getOpcode() - Opcodes.ICONST_M1 <= 6) {
            return instruction.getOpcode() - Opcodes.ICONST_0;
        } else if (instruction.getOpcode() == Opcodes.LCONST_0 || instruction.getOpcode() == Opcodes.LCONST_1) {
            return instruction.getOpcode() - Opcodes.LCONST_0;
        } else if (instruction.getOpcode() - Opcodes.FCONST_0 >= 0 && instruction.getOpcode() - Opcodes.FCONST_0 <= 2) {
            return instruction.getOpcode() - Opcodes.FCONST_0;
        } else if (instruction.getOpcode() == Opcodes.DCONST_0 || instruction.getOpcode() == Opcodes.DCONST_1) {
            return instruction.getOpcode() - Opcodes.DCONST_0;
        }
        return BAD_NUMBER;
    }

    public static boolean isNumericalValue(final AbstractInsnNode instruction) {
        return getNumericalValue(instruction) != BAD_NUMBER;
    }

    public static AbstractInsnNode generateNumericalInstruction(final Number number) {
        if (number instanceof Integer) {
            final int value = (Integer) number;
            if (value >= -128 && value <= 127) {
                return new IntInsnNode(Opcodes.BIPUSH, value);
            } else if (value >= -32768 && value <= 32767) {
                return new IntInsnNode(Opcodes.SIPUSH, value);
            }
        }
        return new LdcInsnNode(number);
    }


    public static FieldNode getField(final ClassNode[] classes, final String owner, final String name, final String desc) {
        for (final ClassNode node : classes) {
            if (node.name.equals(owner)) {
                for (final FieldNode field : (List<FieldNode>) node.fields) {
                    if (field.name.equals(name) && field.desc.equals(desc)) {
                        return field;
                    }
                }
            }
        }
        return null;
    }

    public static int getHashcode(final ClassNode classnode) {
        final ClassWriter writer = new ClassWriter(0);
        classnode.accept(writer);
        return Arrays.hashCode(writer.toByteArray());
    }

    public static boolean isFieldInherited(final ClassNode base, final String fieldName, final String fieldDescriptor) {
        if (base == null) {
            return false;
        }
        for (final FieldNode field : (List<FieldNode>) base.fields) {
            if (field.name.equals(fieldName) && field.desc.equals(fieldDescriptor)) {
                return false;
            }
        }
        return true;
    }

    public static AbstractInsnNode getNext(AbstractInsnNode base, final int opcode) {
        while ((base = base.getNext()) != null && base.getOpcode() != opcode) ;
        return base;
    }

    public static AbstractInsnNode getNext(AbstractInsnNode base, final Class<? extends AbstractInsnNode> nodeType) {
        while ((base = base.getNext()) != null && !nodeType.isAssignableFrom(base.getClass())) ;
        return base;
    }

    public static AbstractInsnNode getNextNumericalInsn(AbstractInsnNode base) {
        while ((base = base.getNext()) != null && isNumericalValue(base)) ;
        return base;
    }

    public static AbstractInsnNode getNextNumericalInsn(AbstractInsnNode base, Number value) {
        while ((base = base.getNext()) != null && !getNumericalValue(base).equals(value)) ;
        return base;
    }

    public static AbstractInsnNode getPrevious(AbstractInsnNode base, final int opcode) {
        while ((base = base.getPrevious()) != null && base.getOpcode() != opcode) ;
        return base;
    }

    public static AbstractInsnNode getPrevious(AbstractInsnNode base, final Class<? extends AbstractInsnNode> nodeType) {
        while ((base = base.getPrevious()) != null && !nodeType.isAssignableFrom(base.getClass())) ;
        return base;
    }

    public static AbstractInsnNode getPrevious(AbstractInsnNode base, final Class<? extends AbstractInsnNode> nodeType, final int opcode) {
        while ((base = base.getPrevious()) != null && !nodeType.isAssignableFrom(base.getClass()) && base.getOpcode() != opcode)
            ;
        return base;
    }

    public static AbstractInsnNode getNext(AbstractInsnNode base, final Class<? extends AbstractInsnNode> nodeType, final int opcode) {
        while ((base = base.getNext()) != null && !nodeType.isAssignableFrom(base.getClass()) && base.getOpcode() != opcode)
            ;
        return base;
    }

    public static AbstractInsnNode getPreviousNumericalInsn(AbstractInsnNode base) {
        while ((base = base.getPrevious()) != null && !isNumericalValue(base)) ;
        return base;
    }

    public static AbstractInsnNode getPreviousNumericalInsn(AbstractInsnNode base, Number value) {
        while ((base = base.getPrevious()) != null && !getNumericalValue(base).equals(value)) ;
        return base;
    }

    public static AbstractInsnNode getRealPrevious(AbstractInsnNode ain) {
        while ((ain = ain.getPrevious()) != null) {
            if (ain.getOpcode() >= 0)
                break;
        }
        return ain;
    }
}

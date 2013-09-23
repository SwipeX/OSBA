package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.ASMUtil;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class Model extends Container {

    public Model(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 6;
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        for (ClassNode cn : classnodes.values()) {
            if (cn.superName.equals(CLASS_MATCHES.get("Renderable"))) {
                if (cn.fieldCount("[B") == 3) {
                    CLASS_MATCHES.put("Model", cn.name);
                    return cn;
                }
            }
        }
        return null;
    }

    @Override
    public void transform(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            if (mn.desc.equals("(I)V") && ASMUtil.access(mn.access, Opcodes.ACC_FINAL)) {
                if (mn.instructions.toArray().length <= 0)
                    continue;
                FieldInsnNode xvert = (FieldInsnNode) ASMUtil.getNext(mn.instructions.toArray()[0], FieldInsnNode.class);
                while (!xvert.desc.equals("[I") || xvert.getOpcode() != Opcodes.GETFIELD) {
                    xvert = (FieldInsnNode) ASMUtil.getNext(xvert, FieldInsnNode.class);
                }
                FieldInsnNode yvert = xvert;
                while (!yvert.desc.equals("[I") || yvert.name.equals(xvert.name)) {
                    yvert = (FieldInsnNode) ASMUtil.getNext(yvert, FieldInsnNode.class);
                }
                FieldInsnNode zvert = yvert;
                while (!zvert.desc.equals("[I") || zvert.name.equals(yvert.name)) {
                    zvert = (FieldInsnNode) ASMUtil.getNext(zvert, FieldInsnNode.class);
                }
                addHook("getXTriangles", xvert.name, xvert.owner, cn.name, "[I", -1);
                addHook("getYTriangles", yvert.name, yvert.owner, cn.name, "[I", -1);
                addHook("getZTriangles", zvert.name, zvert.owner, cn.name, "[I", -1);
                break;
            }
        }
        for (MethodNode mn : cn.methods) {
            if (mn.desc.equals("(III)V") && mn.instructions.size() > 50 && mn.instructions.size() < 70) {
                FieldInsnNode xvert = (FieldInsnNode) ASMUtil.getNext(mn.instructions.toArray()[0], FieldInsnNode.class);
                while (!xvert.desc.equals("[I")) {
                    xvert = (FieldInsnNode) ASMUtil.getNext(xvert, FieldInsnNode.class);
                }
                FieldInsnNode yvert = xvert;
                while (!yvert.desc.equals("[I") || yvert.name.equals(xvert.name)) {
                    yvert = (FieldInsnNode) ASMUtil.getNext(yvert, FieldInsnNode.class);
                }
                FieldInsnNode zvert = yvert;
                while (!zvert.desc.equals("[I") || zvert.name.equals(yvert.name)) {
                    zvert = (FieldInsnNode) ASMUtil.getNext(zvert, FieldInsnNode.class);
                }
                addHook("getXVerticies", xvert.name, xvert.owner, cn.name, "[I", -1);
                addHook("getYVerticies", yvert.name, yvert.owner, cn.name, "[I", -1);
                addHook("getZVerticies", zvert.name, zvert.owner, cn.name, "[I", -1);
                break;
            }
        }
    }
}

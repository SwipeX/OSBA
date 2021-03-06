package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.search.Multipliers;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.IntInsnNode;

import java.util.Map;

public class Boundary extends Container {

    public Boundary(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 4;
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        for (ClassNode cn : classnodes.values()) {
            if (cn.superName.equals("java/lang/Object")) {
                if (cn.getFieldTypeCount() == 2 && cn.fieldCount("L" + CLASS_MATCHES.get("Renderable") + ";") == 2 && cn.fieldCount("I") == 7) {
                    CLASS_MATCHES.put("Boundary", cn.name);
                    return cn;
                }
            }
        }
        return null;
    }

    @Override
    public void transform(ClassNode cn) {
        ClassNode region = updater.classnodes.get(CLASS_MATCHES.get("Region"));
        String render = "L" + CLASS_MATCHES.get("Renderable") + ";";
        EntryPattern ep1 = new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, render, cn.name),
                new InsnEntry(Opcodes.PUTFIELD, "I", cn.name), new InsnEntry(Opcodes.PUTFIELD, "I", cn.name));
        if (ep1.find(region)) {
            FieldInsnNode x = (FieldInsnNode) ep1.get(1).getInstance();
            FieldInsnNode y = (FieldInsnNode) ep1.get(2).getInstance();
            addHook("getWorldX", x.name, cn.name, cn.name, x.desc, -1);
            addHook("getWorldY", y.name, cn.name, cn.name, y.desc, -1);
        }
        EntryPattern hits = new EntryPattern(
                new InsnEntry(Opcodes.PUTFIELD, "I"),
                new InsnEntry(Opcodes.PUTFIELD, "I"));

        if (hits.find(cn, "()V")) {
            addHook("getId", hits.get(0, FieldInsnNode.class), cn.name, Multipliers.getBest(hits.get(0, FieldInsnNode.class)));
        }
        EntryPattern model = new EntryPattern(
                new InsnEntry(Opcodes.GETFIELD, "desc:" + render + "owner:" + cn.name + ";"),
                new InsnEntry(Opcodes.PUTFIELD, render));


        if (model.find(updater.classnodes.get("client"))) {
            FieldInsnNode fin = model.get(1, FieldInsnNode.class);
            addHook("getModel", fin.name, fin.owner, cn.name, getUpdater().getContainer(Model.class).getDescriptor(), -1);
        }

    }
}

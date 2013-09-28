package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.search.Multipliers;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.util.Map;

public class ItemLayer extends Container {

    public ItemLayer(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 6;
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        for (ClassNode cn : classnodes.values()) {
            if (cn.superName.equals("java/lang/Object")) {
                if (cn.getFieldTypeCount() == 2 && cn.fieldCount("L" + CLASS_MATCHES.get("Renderable") + ";") == 3) {
                    CLASS_MATCHES.put("ItemLayer", cn.name);
                    return cn;
                }
            }
        }
        return null;
    }

    @Override
    public void transform(ClassNode cn) {
        ClassNode region = updater.classnodes.get(CLASS_MATCHES.get("Region"));
        if (region != null) {
            String render = "L" + CLASS_MATCHES.get("Renderable") + ";";
            EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, render, cn.name),
                    new InsnEntry(Opcodes.PUTFIELD, "I", cn.name), new InsnEntry(Opcodes.PUTFIELD, "I", cn.name),
                    new InsnEntry(Opcodes.PUTFIELD, "I", cn.name), new InsnEntry(Opcodes.PUTFIELD, "I", cn.name),
                    new InsnEntry(Opcodes.PUTFIELD, render, cn.name), new InsnEntry(Opcodes.PUTFIELD, render, cn.name),
                    new InsnEntry(Opcodes.GETFIELD));
            //new InsnEntry(Opcodes.ISTORE), new InsnEntry(Opcodes.PUTFIELD, "I", cn.name));
            if (pattern.find(region)) {
                FieldInsnNode bottom = (FieldInsnNode) pattern.get(0).getInstance();
                FieldInsnNode x = (FieldInsnNode) pattern.get(1).getInstance();
                FieldInsnNode y = (FieldInsnNode) pattern.get(3).getInstance();
                FieldInsnNode mid = (FieldInsnNode) pattern.get(5).getInstance();
                FieldInsnNode top = (FieldInsnNode) pattern.get(6).getInstance();
                FieldInsnNode height = (FieldInsnNode) pattern.get(4).getInstance();
                String renderDesc = getUpdater().getContainer(Renderable.class).getDescriptor();
                addHook("getBottomItem", bottom.name, bottom.owner, cn.name, renderDesc, -1);
                addHook("getX", x.name, x.owner, cn.name, x.desc, Multipliers.getBest(x));
                addHook("getY", y.name, y.owner, cn.name, y.desc, Multipliers.getBest(y));
                addHook("getMiddleItem", mid.name, mid.owner, cn.name, renderDesc, -1);
                addHook("getTopItem", top.name, top.owner, cn.name, renderDesc, -1);
                addHook("getHeight", height.name, height.owner, cn.name, height.desc, Multipliers.getBest(height));
            }
        }
    }
}

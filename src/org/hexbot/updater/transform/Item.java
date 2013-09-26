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

public class Item extends Container {

    public Item(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 2;
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        for (ClassNode cn : classnodes.values()) {
            if (cn.superName.equals(CLASS_MATCHES.get("Renderable"))) {
                if (cn.getFieldTypeCount() == 1 && cn.fieldCount("I") == 2) {
                    CLASS_MATCHES.put("Item", cn.name);
                    return cn;
                }
            }
        }
        return null;
    }

    @Override
    public void transform(ClassNode cn) {
        EntryPattern ep1 = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "I"), new InsnEntry(Opcodes.INVOKESTATIC),
                new InsnEntry(Opcodes.GETFIELD, "I"), new InsnEntry(Opcodes.ARETURN));
        if (ep1.find(cn)) {
            FieldInsnNode id = ep1.get(0, FieldInsnNode.class);
            addHook("getId", id.name, id.owner, id.owner, id.desc, Multipliers.getBest(id));
            FieldInsnNode stack = ep1.get(2, FieldInsnNode.class);
            addHook("getStackSize", stack.name, stack.owner, stack.owner, stack.desc, Multipliers.getBest(stack));
        }
    }
}

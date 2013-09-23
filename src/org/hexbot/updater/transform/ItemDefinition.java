package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.util.Map;

public class ItemDefinition extends Container {

    public ItemDefinition(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 3;
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        for (ClassNode cn : classnodes.values()) {
            if (cn.superName.equals(CLASS_MATCHES.get("CacheableNode"))) {
                if (cn.getFieldTypeCount() == 6 && cn.fieldCount("[Ljava/lang/String;") == 2) {
                    CLASS_MATCHES.put("ItemDefinition", cn.name);
                    return cn;
                }
            }
        }
        return null;
    }

    @Override
    public void transform(ClassNode cn) {
        EntryPattern ep1 = new EntryPattern(new InsnEntry(Opcodes.GETSTATIC, "Ljava/lang/String;"), new InsnEntry(Opcodes.PUTFIELD, "Ljava/lang/String;")
                ,new InsnEntry(Opcodes.PUTFIELD, "[Ljava/lang/String;"),new InsnEntry(Opcodes.PUTFIELD, "[Ljava/lang/String;"));
        if (ep1.find(cn)) {
            FieldInsnNode name = ep1.get(1, FieldInsnNode.class);
            addHook("getName", name.name, name.owner, name.owner, name.desc, -1);
            FieldInsnNode actions = ep1.get(2, FieldInsnNode.class);
            addHook("getActions", actions.name, actions.owner, actions.owner, actions.desc, -1);
            FieldInsnNode groundActions = ep1.get(3, FieldInsnNode.class);
            addHook("getGroundActions", groundActions.name, groundActions.owner, groundActions.owner, groundActions.desc, -1);
        }
    }
}

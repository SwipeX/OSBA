package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

public class ItemDefinition extends Container {

    public ItemDefinition(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 7;
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
        EntryPattern ep = new EntryPattern(new InsnEntry(Opcodes.GETSTATIC, "Ljava/lang/String;"), new InsnEntry(Opcodes.PUTFIELD, "Ljava/lang/String;")
                , new InsnEntry(Opcodes.PUTFIELD, "[Ljava/lang/String;"), new InsnEntry(Opcodes.PUTFIELD, "[Ljava/lang/String;"));
        if (ep.find(cn)) {
            FieldInsnNode name = ep.get(1, FieldInsnNode.class);
            addHook("getName", name.name, name.owner, name.owner, name.desc, -1);
            FieldInsnNode actions = ep.get(2, FieldInsnNode.class);
            addHook("getActions", actions.name, actions.owner, actions.owner, actions.desc, -1);
            FieldInsnNode groundActions = ep.get(3, FieldInsnNode.class);
            addHook("getGroundActions", groundActions.name, groundActions.owner, groundActions.owner, groundActions.desc, -1);
        }
        FieldNode members = cn.getField(null, "Z");
        addHook("isMembers", members.name, cn.name, cn.name, members.desc, -1);
        EntryPattern ep1 = new EntryPattern(new InsnEntry(Opcodes.ACONST_NULL),
                new InsnEntry(Opcodes.GETFIELD, "[I", cn.name));
        if (ep1.find(cn)) {
            FieldInsnNode ids = (FieldInsnNode) ep1.get(1).getInstance();
            addHook("getIds", ids.name, ids.owner, cn.name, ids.desc, -1);
        }
        EntryPattern ep2 = new EntryPattern(new InsnEntry(Opcodes.ILOAD), new InsnEntry(Opcodes.ALOAD),
                new InsnEntry(Opcodes.GETFIELD, "[I", cn.name), new InsnEntry(Opcodes.GETFIELD, "[I", cn.name));
        if (ep2.find(cn)) {
            FieldInsnNode stacks = (FieldInsnNode) ep2.get(2).getInstance();
            addHook("getStacks", stacks.name, stacks.owner, cn.name, stacks.desc, -1);
        }
        for (MethodNode mn : updater.classnodes.get(CLASS_MATCHES.get("ItemDefinition")).methods) {
            if ((mn.access & Opcodes.ACC_STATIC) != Opcodes.ACC_STATIC) continue;
            if (mn.desc.equals("(I)L" + cn.name + ";")) {
                addHook("getItemDefinition*", mn.name, CLASS_MATCHES.get("ItemDefinition"), CLASS_MATCHES.get("ItemDefinition"), "method", -1);
                break;
            }
        }
    }
}

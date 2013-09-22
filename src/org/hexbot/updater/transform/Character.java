package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.util.Map;

public class Character extends Container {

    public Character(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 1;
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        for (ClassNode cn : classnodes.values()) {
            if (cn.superName.equals(CLASS_MATCHES.get("Renderable"))) {
                if (cn.fieldCount("[I") == 5 && cn.fieldCount("Z") == 1 && cn.fieldCount("Ljava/lang/String;") == 1) {
                    CLASS_MATCHES.put("Character", cn.name);
                    return cn;
                }
            }
        }
        return null;
    }

    @Override
    public void transform(ClassNode cn) {
        EntryPattern ep = new EntryPattern(new InsnEntry[]{new InsnEntry(Opcodes.IADD), new InsnEntry(Opcodes.PUTFIELD, "I"),
                new InsnEntry(Opcodes.IADD), new InsnEntry(Opcodes.PUTFIELD, "I")});
        ep.find(cn);
        FieldInsnNode x = (FieldInsnNode) ep.get(1).getInstance();
        FieldInsnNode y = (FieldInsnNode) ep.get(3).getInstance();
        System.out.println("az." + x.name);
        System.out.println("az." + y.name);
    }
}

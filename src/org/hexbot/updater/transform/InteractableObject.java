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

import java.util.Map;

public class InteractableObject extends Container {

    public InteractableObject(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 5;
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        for (ClassNode cn : classnodes.values()) {
            if (cn.superName.equals("java/lang/Object")) {
                if (cn.fieldCount("L" + CLASS_MATCHES.get("Renderable") + ";") == 1 && cn.getAbnormalFieldCount() == 1 &&
                        cn.getFieldTypeCount() == 2 && cn.fieldCount("I") != 5) {
                    CLASS_MATCHES.put("InteractableObject", cn.name);
                    return cn;
                }
            }
        }
        return null;
    }

    @Override
    public void transform(ClassNode cn) {
        ClassNode region = updater.classnodes.get(CLASS_MATCHES.get("Region"));
        EntryPattern ep1 = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "[L" + cn.name + ";"), new InsnEntry(Opcodes.AALOAD), new InsnEntry(Opcodes.GETFIELD, "I"));
        if (ep1.find(region)) {
	        FieldInsnNode id = (FieldInsnNode) ep1.get(2).getInstance();
            addHook("getId", id.name, id.owner, id.owner, id.desc, Multipliers.getSurrounding(id));
        }
        EntryPattern ep2 = new EntryPattern(new InsnEntry(Opcodes.INVOKESPECIAL), new InsnEntry(Opcodes.PUTFIELD, "I"),
                new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"),
                new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.PUTFIELD, "I"));
        if (ep2.find(region)) {
	        FieldInsnNode worldx = (FieldInsnNode) ep2.get(4).getInstance();
	        addHook("getWorldX", worldx.name, worldx.owner, worldx.owner, worldx.desc, Multipliers.getMostUsed(worldx));
	        FieldInsnNode height = (FieldInsnNode) ep2.get(6).getInstance();
	        addHook("getHeight", height.name, height.owner, height.owner, height.desc, Multipliers.getMostUsed(height));
	        FieldInsnNode worldy = (FieldInsnNode) ep2.get(5).getInstance();
            addHook("getWorldY", worldy.name, worldy.owner, worldy.owner, worldy.desc, Multipliers.getMostUsed(worldy));
        }
	    FieldNode renderable = cn.getField(null, "L" + CLASS_MATCHES.get("Renderable") + ";");
	    addHook("getModel", renderable.name, cn.name, cn.name, getUpdater().getContainer(Renderable.class).getDescriptor(), -1);
    }
}

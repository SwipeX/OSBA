package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.Cache;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.search.Multipliers;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

import java.util.Map;

public class Character extends Container {

    public Character(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 16;
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
        EntryPattern ep = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "[I"), new InsnEntry(Opcodes.IADD), new InsnEntry(Opcodes.PUTFIELD, "I"),
                new InsnEntry(Opcodes.GETFIELD, "[I"), new InsnEntry(Opcodes.IADD), new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.RETURN));
        ep.find(cn);
        FieldInsnNode x = ep.get(2, FieldInsnNode.class);
        FieldInsnNode y = ep.get(5, FieldInsnNode.class);
        addHook("getX", x.name, x.owner, x.owner, x.desc, Multipliers.getBest(x));
        addHook("getY", y.name, y.owner, y.owner, y.desc, Multipliers.getBest(y));

        EntryPattern ep1 = new EntryPattern(new InsnEntry(Opcodes.ACONST_NULL), new InsnEntry(Opcodes.PUTFIELD, "Ljava/lang/String;"));
        ep1.find(cn);
        FieldInsnNode text = ep1.get(1, FieldInsnNode.class);
        addHook("getAboveText", text.name, text.owner, text.owner, text.desc, -1);

        EntryPattern ep2 = new EntryPattern(new InsnEntry(Opcodes.ICONST_M1), new InsnEntry(Opcodes.GETFIELD, "I"), new InsnEntry(Opcodes.GETFIELD, "I"));
        if (ep2.find(cn, "(IIZ)V")) {
            FieldInsnNode anim = ep2.get(1, FieldInsnNode.class);
            addHook("getAnimation", anim.name, anim.owner, anim.owner, anim.desc, Multipliers.getBest(anim));
        }
        EntryPattern epz = new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, "I"));
        if (epz.find(cn, "(B)V")) {
            FieldInsnNode queue = epz.get(0, FieldInsnNode.class);
            addHook("getQueueSize", queue.name, queue.owner, queue.owner, queue.desc, Multipliers.getBest(queue));
        } else if (epz.find(cn, "(I)V")) {
            FieldInsnNode queue = epz.get(0, FieldInsnNode.class);
            addHook("getQueueSize", queue.name, queue.owner, queue.owner, queue.desc, Multipliers.getBest(queue));
        }

        EntryPattern ep3 = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "I"), new InsnEntry(Opcodes.GETFIELD, "desc:I;owner:" + cn.name + ";"),
                new InsnEntry(Opcodes.GETSTATIC, "[L" + CLASS_MATCHES.get("Npc") + ";"), new InsnEntry(Opcodes.GETFIELD, "desc:I;owner:" + cn.name + ";"), new InsnEntry(Opcodes.AALOAD));
        if (ep3.find(updater.classnodes.get("client"))) {
            FieldInsnNode index = ep3.get(3, FieldInsnNode.class);
            addHook("getInteractingIndex", index.name, index.owner, CLASS_MATCHES.get("Character"), index.desc, Multipliers.getBest(index));
        }

        EntryPattern ep4 = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "desc:I;owner:" + cn.name + ";"),new InsnEntry(Opcodes.GETFIELD, "desc:I;owner:" + cn.name + ";"), new InsnEntry(Opcodes.SIPUSH,"2047"));
        if (ep4.find(updater.classnodes.get("client"),"(L"+cn.name+";)V")) {
            FieldInsnNode orientation = ep4.get(1, FieldInsnNode.class);
            addHook("getOrientation", orientation.name, orientation.owner, CLASS_MATCHES.get("Character"), orientation.desc,
                    -650136929);
        }
        EntryPattern ep5 = new EntryPattern(new InsnEntry(Opcodes.ICONST_M1), new InsnEntry(Opcodes.GETFIELD, "desc:I;owner:" + CLASS_MATCHES.get("Character") + ";"), new InsnEntry(Opcodes.GETFIELD, "I"), new InsnEntry(Opcodes.IDIV));
        if (ep5.find(updater.classnodes.get("client"))) {
            FieldInsnNode hp = ep5.get(1, FieldInsnNode.class);
//            Cache.cache.put(hp.owner + "." + hp.name, (Integer) ((LdcInsnNode) hp.getPrevious().getPrevious()).cst);
            addHook("getHealth", hp.name, hp.owner, hp.owner, hp.desc, Multipliers.getBest(hp));
            FieldInsnNode max = ep5.get(2, FieldInsnNode.class);
            //    Cache.cache.put(max.owner + "." + max.name, (Integer) ((LdcInsnNode) max.getPrevious().getPrevious()).cst);
            addHook("getMaxHealth", max.name, max.owner, max.owner, max.desc, Multipliers.getBest(max));
        }
        EntryPattern hits = new EntryPattern(
                new InsnEntry(Opcodes.GETFIELD, "[I"),
                new InsnEntry(Opcodes.ILOAD),
                new InsnEntry(Opcodes.GETFIELD, "[I"),
                new InsnEntry(Opcodes.ILOAD),
                new InsnEntry(Opcodes.GETFIELD, "[I"),
                new InsnEntry(Opcodes.ILOAD),
                new InsnEntry(IntInsnNode.class, "70"),
                new InsnEntry(Opcodes.RETURN));
        if (hits.find(cn)) {
            addHook("getHitDamages", hits.get(0, FieldInsnNode.class), cn.name, -1);
            addHook("getHitTypes", hits.get(2, FieldInsnNode.class), cn.name, -1);
            addHook("getHitCycles", hits.get(4, FieldInsnNode.class), cn.name, -1);
        }
        EntryPattern queue = new EntryPattern(
                new InsnEntry(Opcodes.GETFIELD, "[I"),
                new InsnEntry(Opcodes.ILOAD),
                new InsnEntry(Opcodes.GETFIELD, "[I"),
                new InsnEntry(Opcodes.ILOAD),
                new InsnEntry(Opcodes.GETFIELD, "[I"),
                new InsnEntry(Opcodes.ILOAD),
                new InsnEntry(Opcodes.GETFIELD, "[I"),
                new InsnEntry(Opcodes.ILOAD),
                new InsnEntry(Opcodes.GETFIELD, "[Z"),
                new InsnEntry(Opcodes.ILOAD),
                new InsnEntry(Opcodes.GETFIELD, "[Z"),
                new InsnEntry(Opcodes.ILOAD));
        if (queue.find(cn)) {
            addHook("getQueueX", queue.get(0, FieldInsnNode.class), cn.name, -1);
            addHook("getQueueY", queue.get(4, FieldInsnNode.class), cn.name, -1);
            addHook("getQueueRun", queue.get(8, FieldInsnNode.class), cn.name, -1);
        }
        EntryPattern speed = new EntryPattern(new InsnEntry(Opcodes.DUP), new InsnEntry(Opcodes.GETFIELD, "I", cn.name),
                new InsnEntry(Opcodes.IADD), new InsnEntry(Opcodes.PUTFIELD, "I", cn.name));
        if (speed.find(cn)) {
            FieldInsnNode fin = (FieldInsnNode) speed.get(1).getInstance();
            addHook("getSpeed", fin, cn.name, Multipliers.getBest(fin));
        }
    }

}

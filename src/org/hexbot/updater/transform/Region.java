package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.ASMUtil;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class Region extends Container {

    public Region(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 2;
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        for (ClassNode cn : classnodes.values()) {
            if (cn.superName.equals("java/lang/Object") && cn.getAbnormalFieldCount() == 2 &&
                    cn.fieldCount("[[[L" + CLASS_MATCHES.get("TileData") + ";") == 1 &&
                    cn.fieldCount("[L" + CLASS_MATCHES.get("InteractableObject") + ";") == 1) {
                CLASS_MATCHES.put("Region", cn.name);
                return cn;
            }
        }
        return null;
    }

    @Override
    public void transform(ClassNode classNode) {
        for (FieldNode fn : classNode.fields) {
            if (fn.desc.equals("[[[L" + CLASS_MATCHES.get("TileData") + ";")) {
                addHook("getGroundData", fn.name, classNode.name, classNode.name, fn.desc, -1);
            }
            if (fn.desc.equals("[L" + CLASS_MATCHES.get("InteractableObject") + ";") && ASMUtil.access(fn, Opcodes.ACC_STATIC)) {
                addHook("getSceneObjectCache", fn.name, classNode.name, classNode.name, fn.desc, -1);
            }
        }
    }
}

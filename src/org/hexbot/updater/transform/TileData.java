package org.hexbot.updater.transform;


import org.hexbot.updater.Updater;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class TileData extends Container {

    public TileData(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 3;
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        for (ClassNode cn : classnodes.values()) {
            if (cn.superName.equals(CLASS_MATCHES.get("Node"))) {
                if (cn.fieldCount("Z") == 3) {
                    CLASS_MATCHES.put("TileData", cn.name);
                    return cn;
                }
            }
        }
        return null;
    }

    @Override
    public void transform(ClassNode classNode) {
        for (FieldNode fn : classNode.fields) {
            if (fn.desc.equals("[L" + CLASS_MATCHES.get("InteractableObject") + ";")) {
                addHook("getSceneObjects", fn.name, classNode.name, classNode.name, fn.desc, -1);
            }
            if (fn.desc.equals("L" + CLASS_MATCHES.get("FloorDecoration") + ";")) {
                addHook("getFloorDecoration",fn.name, classNode.name, classNode.name, fn.desc, -1);
            }
            if (fn.desc.equals("L" + CLASS_MATCHES.get("WallDecoration") + ";")) {
                addHook("getWallObject",fn.name, classNode.name, classNode.name, fn.desc, -1);
            }
        }
    }
}

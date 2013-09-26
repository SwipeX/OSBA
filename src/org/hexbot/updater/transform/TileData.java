package org.hexbot.updater.transform;


import org.hexbot.updater.Updater;
import org.hexbot.updater.search.Multipliers;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

public class TileData extends Container {

    public TileData(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 8;
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
	    FieldNode objects = cn.getField(null, "[L" + CLASS_MATCHES.get("InteractableObject") + ";");
	    FieldNode floors = cn.getField(null, "L" + CLASS_MATCHES.get("FloorDecoration") + ";");
	    FieldNode walls = cn.getField(null, "L" + CLASS_MATCHES.get("WallDecoration") + ";");
	    FieldNode boundary = cn.getField(null, "L" + CLASS_MATCHES.get("Boundary") + ";");
	    FieldNode layer = cn.getField(null, "L" + CLASS_MATCHES.get("ItemLayer") + ";");
	    addHook("getSceneObjects", objects.name, cn.name, cn.name, getUpdater().getContainer(InteractableObject.class).getDescriptor(1), -1);
	    addHook("getFloorDecoration", floors.name, cn.name, cn.name, getUpdater().getContainer(FloorDecoration.class).getDescriptor(), -1);
	    addHook("getWallObject", walls.name, cn.name, cn.name, getUpdater().getContainer(WallDecoration.class).getDescriptor(), -1);
	    addHook("getBoundary", boundary.name, cn.name, cn.name, getUpdater().getContainer(Boundary.class).getDescriptor(), -1);
	    addHook("getItemLayer", layer.name, cn.name, cn.name, getUpdater().getContainer(ItemLayer.class).getDescriptor(), -1);
	    MethodNode init = cn.getMethod("<init>", null);
	    for (AbstractInsnNode ain : init.instructions.toArray()) {
		    if (ain.getOpcode() == Opcodes.ILOAD) {
			    VarInsnNode iin = (VarInsnNode) ain;
			    while ((ain = ain.getNext()) != null) {
				    if (ain.getOpcode() == Opcodes.PUTFIELD) {
					    break;
				    }
			    }
			    FieldInsnNode fin = (FieldInsnNode) ain;
			    if (fin == null) break;
			    if (iin.var == 1) {
				    addHook("getPlane", fin.name, fin.owner, cn.name, fin.desc, Multipliers.getMostUsed(fin));
			    } else if (iin.var == 2) {
				    addHook("getX", fin.name, fin.owner, cn.name, fin.desc, Multipliers.getMostUsed(fin));
			    } else if (iin.var == 3) {
				    addHook("getY", fin.name, fin.owner, cn.name, fin.desc, Multipliers.getMostUsed(fin));
				    break;
			    }
		    }
	    }
    }
}

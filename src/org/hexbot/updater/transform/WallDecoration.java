package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class WallDecoration extends Container {

    public WallDecoration(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 3;
    }

    @Override
    public String getInterfaceString() {
        return GETTER_PREFIX + "GameObject";
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        for (ClassNode cn : classnodes.values()) {
            if (cn.superName.equals("java/lang/Object")) {
                if (cn.getFieldTypeCount() == 2 && cn.fieldCount("I") == 9 &&
                        cn.fieldCount("L" + CLASS_MATCHES.get("Renderable") + ";") == 2) {
                    CLASS_MATCHES.put("WallDecoration", cn.name);
                    return cn;
                }
            }
        }
        return null;
    }

    @Override
    public void transform(ClassNode cn) {
	    FieldNode id = cn.getPublicField(null, "I");
	    addHook("getId", id.name, cn.name, cn.name, id.desc, -1);
        ClassNode region = updater.classnodes.get(CLASS_MATCHES.get("Region"));
	    EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.SIPUSH, "256"),
			    new InsnEntry(Opcodes.GETFIELD, "I", cn.name), new InsnEntry(Opcodes.GETFIELD, "I", cn.name));
	    if (pattern.find(region)) {
		    FieldInsnNode x = (FieldInsnNode) pattern.get(1).getInstance();
		    FieldInsnNode y = (FieldInsnNode) pattern.get(2).getInstance();
		    addHook("getX", x.name, x.owner, cn.name, x.desc, -1);
		    addHook("getY", y.name, y.owner, cn.name, y.desc, -1);
	    }
    }
}

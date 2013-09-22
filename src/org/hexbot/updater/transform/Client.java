package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

import java.util.Map;

public class Client extends Container {

    public Client(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 1;
    }

    @Override
    public String getInterfaceString() {
        return GETTER_PREFIX.substring(0, GETTER_PREFIX.length() - 1) + "Client";
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        return classnodes.get("client");
    }

    @Override
    public void transform(ClassNode cn) {

    }
}

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

public class Client extends Container {

    public Client(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 9;
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
        for (ClassNode cl : updater.classnodes.values()) {
            for (FieldNode fieldNode : cl.fields) {
                if (fieldNode.desc.equals("L" + CLASS_MATCHES.get("Mouse") + ";")) {
                    addHook("getMouse", fieldNode.name, cl.name, "client", fieldNode.desc, -1);
                }
                if (fieldNode.desc.equals("L" + CLASS_MATCHES.get("Player") + ";")) {
                    addHook("getLocalPlayer", fieldNode.name, cl.name, "client", fieldNode.desc, -1);
                }
                if (fieldNode.desc.equals("[L" + CLASS_MATCHES.get("Player") + ";")) {
                    addHook("getPlayers", fieldNode.name, cl.name, "client", fieldNode.desc, -1);
                }
                if (fieldNode.desc.equals("[L" + CLASS_MATCHES.get("Npc") + ";")) {
                    addHook("getNpcs", fieldNode.name, cl.name, "client", fieldNode.desc, -1);
                }
                if (fieldNode.desc.equals("[[L" + CLASS_MATCHES.get("Widget") + ";")) {
                    addHook("getWidgets", fieldNode.name, cl.name, "client", fieldNode.desc, -1);
                }
                if (fieldNode.desc.equals("L" + CLASS_MATCHES.get("Region") + ";")) {
                    addHook("getRegion", fieldNode.name, cl.name, "client", fieldNode.desc, -1);
                }
            }
        }
        logoutHooks();
        playerIndex();
        skillArrays();
        destinationHooks();
        menuHooks();
    }

    private void menuHooks() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.PUTSTATIC, "Z"), new InsnEntry(Opcodes.GETSTATIC, "I"),
                new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "I"),
                new InsnEntry(Opcodes.INVOKESTATIC), new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "[I"), new InsnEntry(Opcodes.GETSTATIC, "I"));
        if (pattern.find(cn)) {
            FieldInsnNode open = pattern.get(0, FieldInsnNode.class);
            addHook("isMenuOpen", open.name, open.owner, "client", "Z", -1);
            FieldInsnNode x = pattern.get(1, FieldInsnNode.class);
            addHook("getMenuX", x.name, x.owner, "client", "I", -1);
            FieldInsnNode y = pattern.get(2, FieldInsnNode.class);
            addHook("getMenuY", y.name, y.owner, "client", "I", -1);
            FieldInsnNode width = pattern.get(3, FieldInsnNode.class);
            addHook("getMenuWidth", width.name, width.owner, "client", "I", -1);
            FieldInsnNode height = pattern.get(4, FieldInsnNode.class);
            addHook("getMenuHeight", height.name, height.owner, "client", "I", -1);
            FieldInsnNode count = pattern.get(8, FieldInsnNode.class);
            addHook("getMenuCount", count.name, count.owner, "client", "I", -1);
        }

    }

    private void destinationHooks() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.GETSTATIC, "L" + CLASS_MATCHES.get("Player") + ";"), new InsnEntry(Opcodes.GETFIELD, "I"),
                new InsnEntry(Opcodes.BIPUSH, "7"), new InsnEntry(Opcodes.GETSTATIC), new InsnEntry(Opcodes.GETSTATIC));
        if (pattern.find(cn, "(Z)V")) {
            FieldInsnNode x = pattern.get(3, FieldInsnNode.class);
            addHook("getDestinationX", x.name, x.owner, "client", "I", -1);
            FieldInsnNode y = pattern.get(4, FieldInsnNode.class);
            addHook("getDestinationY", y.name, y.owner, "client", "I", -1);
        }
    }

    private void skillArrays() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.BIPUSH, "25"), new InsnEntry(Opcodes.NEWARRAY),
                new InsnEntry(Opcodes.PUTSTATIC, "[I"), new InsnEntry(Opcodes.BIPUSH, "25"), new InsnEntry(Opcodes.NEWARRAY),
                new InsnEntry(Opcodes.PUTSTATIC, "[I"), new InsnEntry(Opcodes.BIPUSH, "25"), new InsnEntry(Opcodes.NEWARRAY),
                new InsnEntry(Opcodes.PUTSTATIC, "[I"));
        if (pattern.find(cn)) {
            FieldInsnNode currentLevels = pattern.get(2, FieldInsnNode.class);
            addHook("getCurrentLevels", currentLevels.name, currentLevels.owner, "client", "I", -1);
            FieldInsnNode realLevels = pattern.get(5, FieldInsnNode.class);
            addHook("getRealLevels", realLevels.name, realLevels.owner, "client", "I", -1);
            FieldInsnNode exp = pattern.get(8, FieldInsnNode.class);
            addHook("getExpArray", exp.name, exp.owner, "client", "I", -1);
        }
    }

    private void playerIndex() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "L" + CLASS_MATCHES.get("Player") + ";"),
                new InsnEntry(Opcodes.ACONST_NULL));
        if (pattern.find(cn)) {
            FieldInsnNode index = pattern.get(0, FieldInsnNode.class);
            addHook("getPlayerIndex", index.name, index.owner, "client", "I", -1);
        }
    }

    private void logoutHooks() {
        // void logout()
        for (ClassNode cn : getUpdater().classnodes.values()) {
            EntryPattern pattern = new EntryPattern(
                    new InsnEntry(Opcodes.ICONST_0), new InsnEntry(Opcodes.PUTSTATIC, "I"), //login index
                    new InsnEntry(Opcodes.LDC, ""), new InsnEntry(Opcodes.PUTSTATIC, "Ljava/lang/String;"), // username
                    new InsnEntry(Opcodes.LDC, ""), new InsnEntry(Opcodes.PUTSTATIC, "Ljava/lang/String;"), // password
                    new InsnEntry(Opcodes.ICONST_0), new InsnEntry(Opcodes.PUTSTATIC, "Z")); // logged in
            if (pattern.find(cn)) {
                FieldInsnNode loginIndex = pattern.get(1, FieldInsnNode.class);
                FieldInsnNode username = pattern.get(3, FieldInsnNode.class);
                FieldInsnNode password = pattern.get(5, FieldInsnNode.class);
                FieldInsnNode loggedIn = pattern.get(7, FieldInsnNode.class);
                addHook("getLoginIndex", loginIndex.name, loginIndex.owner, "client", "I", -1);
                addHook("getUsername", username.name, username.owner, "client", "Ljava/lang/String;", -1);
                addHook("getPassword", password.name, password.owner, "client", "Ljava/lang/String;", -1);
                addHook("isLoggedIn", loggedIn.name, loggedIn.owner, "client", "Z", -1);
                break;
            }
        }
    }

}

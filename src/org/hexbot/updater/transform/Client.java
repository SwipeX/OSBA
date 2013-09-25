package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.*;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.GETSTATIC;

public class Client extends Container {

    public Client(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 39;
    }

    @Override
    public String getInterfaceString() {
        return GETTER_PREFIX + "Client";
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
                if (fieldNode.desc.equals("L" + CLASS_MATCHES.get("Keyboard") + ";")) {
                    addHook("getKeyboard", fieldNode.name, cl.name, "client", fieldNode.desc, -1);
                }
                if (fieldNode.desc.equals("L" + CLASS_MATCHES.get("Player") + ";")) {
                    addHook("getLocalPlayer", fieldNode.name, cl.name, "client", fieldNode.desc, -1);
                }
                if (fieldNode.desc.equals("[L" + CLASS_MATCHES.get("Player") + ";")) {
                    addHook("getPlayers", fieldNode.name, cl.name, "client", fieldNode.desc, -1);
                }
                if (fieldNode.desc.equals("[[[L" + CLASS_MATCHES.get("NodeDeque") + ";")) {
                    addHook("getGroundItems", fieldNode.name, cl.name, "client", fieldNode.desc, -1);
                }
                if (fieldNode.desc.equals("[L" + CLASS_MATCHES.get("Npc") + ";")) {
                    addHook("getNpcs", fieldNode.name, cl.name, "client", fieldNode.desc, -1);
                }
                if (fieldNode.desc.equals("[L" + CLASS_MATCHES.get("CollisionMap") + ";")) {
                    addHook("getCollisionMaps", fieldNode.name, cl.name, "client", fieldNode.desc, -1);
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
        menuText();
        runEnergy();
        identifyTileData();
        getPlane();
        getBase();
        getCamera();
        identifyLogin();
        settings();
    }

    private void runEnergy() {
        for (ClassNode cn : getUpdater().classnodes.values()) {
            EntryPattern pattern = new EntryPattern(
                    new InsnEntry(Opcodes.SIPUSH, "3321"), new InsnEntry(Opcodes.GETSTATIC, "[I"), new InsnEntry(Opcodes.GETSTATIC, "I"));
            if (pattern.find(cn)) {
                FieldInsnNode energy = pattern.get(2, FieldInsnNode.class);
                addHook("getEnergy", energy.name, energy.owner, "client", "I", Multipliers.getSurrounding(energy));
                return;
            }
        }
    }

    private void identifyLogin() {
        ClassNode cn = getUpdater().classnodes.get("client");
        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain instanceof LdcInsnNode) {
                    LdcInsnNode lin = (LdcInsnNode) ain;
                    if (lin.cst.equals("js5connect")) {
                        FieldInsnNode fin = (FieldInsnNode) ASMUtil.getPrevious(lin, FieldInsnNode.class);
                        addHook("getGameState", fin.name, fin.owner, "client", "I", Multipliers.getSurrounding(fin));
                        return;
                    }
                }
            }
        }
    }

    private void menuHooks() {
        EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.PUTSTATIC, "Z"), new InsnEntry(Opcodes.GETSTATIC, "I"),
                new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "I"),
                new InsnEntry(Opcodes.INVOKESTATIC), new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "[I"), new InsnEntry(Opcodes.GETSTATIC, "I"));
        if (pattern.find(cn)) {
            FieldInsnNode open = pattern.get(0, FieldInsnNode.class);
            addHook("isMenuOpen", open.name, open.owner, "client", "Z", -1);
            FieldInsnNode x = pattern.get(1, FieldInsnNode.class);
            addHook("getMenuX", x.name, x.owner, "client", "I", Multipliers.getSurrounding(x));
            FieldInsnNode y = pattern.get(2, FieldInsnNode.class);
            addHook("getMenuY", y.name, y.owner, "client", "I", Multipliers.getSurrounding(y));
            FieldInsnNode width = pattern.get(3, FieldInsnNode.class);
            addHook("getMenuWidth", width.name, width.owner, "client", "I", Multipliers.getSurrounding(width));
            FieldInsnNode height = pattern.get(4, FieldInsnNode.class);
            addHook("getMenuHeight", height.name, height.owner, "client", "I", Multipliers.getSurrounding(height));
            FieldInsnNode count = pattern.get(8, FieldInsnNode.class);
            addHook("getMenuCount", count.name, count.owner, "client", "I", Multipliers.getSurrounding(count));
        }

    }

    private void menuText() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.GETSTATIC, "I"),
                new InsnEntry(Opcodes.GETSTATIC, "[Ljava/lang/String;"), new InsnEntry(Opcodes.GETSTATIC, "[Ljava/lang/String;"),
                new InsnEntry(Opcodes.GETSTATIC, "[Ljava/lang/String;"), new InsnEntry(Opcodes.GETSTATIC, "[Ljava/lang/String;"), new InsnEntry(Opcodes.GETSTATIC,"[I"));
        if (pattern.find(cn)) {
            FieldInsnNode menuOptions = pattern.get(1, FieldInsnNode.class);
            addHook("getMenuOptions", menuOptions.name, menuOptions.owner, "client", "[Ljava/lang/String;", -1);
            FieldInsnNode menuActions = pattern.get(3, FieldInsnNode.class);
            addHook("getMenuActions", menuActions.name, menuActions.owner, "client", "[Ljava/lang/String;", -1);
        }
    }

    private void destinationHooks() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.GETSTATIC, "L" + CLASS_MATCHES.get("Player") + ";"), new InsnEntry(Opcodes.GETFIELD, "I"),
                new InsnEntry(Opcodes.BIPUSH, "7"), new InsnEntry(Opcodes.GETSTATIC), new InsnEntry(Opcodes.GETSTATIC));
        if (pattern.find(cn, "(Z)V")) {
            FieldInsnNode x = pattern.get(3, FieldInsnNode.class);
            addHook("getDestinationX", x.name, x.owner, "client", "I", Multipliers.getSurrounding(x));
            FieldInsnNode y = pattern.get(4, FieldInsnNode.class);
            addHook("getDestinationY", y.name, y.owner, "client", "I", Multipliers.getSurrounding(y));
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
            addHook("getCurrentLevels", currentLevels.name, currentLevels.owner, "client", "[I", -1);
            FieldInsnNode realLevels = pattern.get(5, FieldInsnNode.class);
            addHook("getRealLevels", realLevels.name, realLevels.owner, "client", "[I", -1);
            FieldInsnNode exp = pattern.get(8, FieldInsnNode.class);
            addHook("getExpArray", exp.name, exp.owner, "client", "[I", -1);
        }
    }

    private void playerIndex() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "L" + CLASS_MATCHES.get("Player") + ";"),
                new InsnEntry(Opcodes.ACONST_NULL));
        if (pattern.find(cn)) {
            FieldInsnNode index = pattern.get(0, FieldInsnNode.class);
            addHook("getPlayerIndex", index.name, index.owner, "client", "I", Multipliers.getSurrounding(index));
        }
    }
    private void settings() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.GETFIELD), new InsnEntry(Opcodes.GETSTATIC, "[I"),
                new InsnEntry(Opcodes.GETSTATIC, "[I"));
        if (pattern.find(cn)) {
            FieldInsnNode index = pattern.get(2, FieldInsnNode.class);
            addHook("getSettings", index.name, index.owner, "client", "[I", -1);
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
                addHook("getLoginIndex", loginIndex.name, loginIndex.owner, "client", "I", Multipliers.getGlobal().getField(loginIndex).getMostUsed());
                addHook("getUsername", username.name, username.owner, "client", "Ljava/lang/String;", -1);
                addHook("getPassword", password.name, password.owner, "client", "Ljava/lang/String;", -1);
                addHook("isLoggedIn", loggedIn.name, loggedIn.owner, "client", "Z", -1);
                break;
            }
        }
    }

    public void identifyTileData() {
        tileBytes:
        for (final ClassNode cn : updater.classnodes.values()) {
            for (final MethodNode mn : cn.methods) {
                if ((mn.access & ACC_STATIC) == ACC_STATIC && mn.desc.endsWith(")V")) {
                    for (final AbstractInsnNode start : mn.instructions.toArray()) {
                        AbstractInsnNode current = start;
                        if (start.getOpcode() == GETSTATIC
                                && (current = current.getNext()).getOpcode() == ILOAD
                                && (current = current.getNext()).getOpcode() == AALOAD
                                && (current = current.getNext()).getOpcode() == ILOAD
                                && (current = current.getNext()).getOpcode() == AALOAD
                                && (current = current.getNext()).getOpcode() == ILOAD
                                && (current = current.getNext()).getOpcode() == BALOAD
                                && ASMUtil.getNumericalValue(current = current.getNext()).equals(16)
                                && current.getNext().getOpcode() == IAND) {
                            FieldInsnNode fin = (FieldInsnNode) start;
                            addHook("getTileBytes", fin.name, fin.owner, "client", "[[[B", -1);
                            break tileBytes;
                        }
                    }
                }
            }
        }
        for (final ClassNode cn : updater.classnodes.values()) {
            for (final MethodNode mn : cn.methods) {
                if ((mn.access & ACC_STATIC) == ACC_STATIC && mn.desc.endsWith(")I")) {
                    for (final AbstractInsnNode start : mn.instructions.toArray()) {
                        AbstractInsnNode current = start;
                        if (ASMUtil.getNumericalValue(current).equals(7)
                                && (current = current.getNext()).getOpcode() == ISHR
                                && (current = ASMUtil.getPrevious(current, GETSTATIC)) != null) {
                            FieldInsnNode fin = (FieldInsnNode) current;
                            if (fin.desc.equals("[[[I")) {
                                addHook("getTileHeights", fin.name, fin.owner, "client", "[[[I", -1);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public void getPlane() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.SIPUSH, "13056"), new InsnEntry(Opcodes.SIPUSH, "13056"),
                new InsnEntry(Opcodes.GETSTATIC, "I"));
        if (pattern.find(cn)) {
            FieldInsnNode index = pattern.get(2, FieldInsnNode.class);
            addHook("getPlane", index.name, index.owner, "client", "I", Multipliers.getSurrounding(index));
        }
    }

    public void getCamera() {
        EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.BIPUSH, "64"), new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.INVOKESTATIC),
                new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "I"));
        if (pattern.find(cn)) {
            FieldInsnNode x = pattern.get(4, FieldInsnNode.class);
            addHook("getCameraX", x.name, x.owner, "client", "I", Multipliers.getSurrounding(x));
            FieldInsnNode z = pattern.get(5, FieldInsnNode.class);
            addHook("getCameraZ", z.name, z.owner, "client", "I", Multipliers.getSurrounding(z));
            FieldInsnNode y = pattern.get(6, FieldInsnNode.class);
            addHook("getCameraY", y.name, y.owner, "client", "I", Multipliers.getSurrounding(y));
        }
        EntryPattern pattern1 = new EntryPattern(new InsnEntry(Opcodes.INVOKESTATIC), new InsnEntry(Opcodes.SIPUSH, "2047"),
                new InsnEntry(Opcodes.PUTSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "I"));
        if (pattern1.find(cn)) {
            FieldInsnNode pitch = pattern1.get(2, FieldInsnNode.class);
            addHook("getPitch", pitch.name, pitch.owner, "client", "I", Multipliers.getSurrounding(pitch));
            FieldInsnNode yaw = pattern1.get(3, FieldInsnNode.class);
            addHook("getYaw", yaw.name, yaw.owner, "client", "I", Multipliers.getSurrounding(yaw));
        }
    }

    public void getBase() {
        EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.GETSTATIC, "[[B"), new InsnEntry(Opcodes.GETSTATIC, "[[B"), new InsnEntry(Opcodes.GETSTATIC, "[I"),
                new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "[I"), new InsnEntry(Opcodes.GETSTATIC, "I"));
        if (pattern.find(cn)) {
            FieldInsnNode x = pattern.get(3, FieldInsnNode.class);
            addHook("getBaseX", x.name, x.owner, "client", "I", Multipliers.getSurrounding(x));
            FieldInsnNode y = pattern.get(5, FieldInsnNode.class);
            addHook("getBaseY", y.name, y.owner, "client", "I", Multipliers.getSurrounding(y));
        }
    }
}

package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.ASMUtil;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.search.Multipliers;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class Client extends Container {

    public Client(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 47;
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
                    addHook("getMouse", fieldNode.name, cl.name, "client", getUpdater().getContainer(Mouse.class).getDescriptor(), -1);
                }
                if (fieldNode.desc.equals("L" + CLASS_MATCHES.get("Keyboard") + ";")) {
                    addHook("getKeyboard", fieldNode.name, cl.name, "client", getUpdater().getContainer(Keyboard.class).getDescriptor(), -1);
                }
                if (fieldNode.desc.equals("L" + CLASS_MATCHES.get("Player") + ";")) {
                    addHook("getLocalPlayer", fieldNode.name, cl.name, "client", getUpdater().getContainer(Player.class).getDescriptor(), -1);
                }
                if (fieldNode.desc.equals("[L" + CLASS_MATCHES.get("Player") + ";")) {
                    addHook("getPlayers", fieldNode.name, cl.name, "client", getUpdater().getContainer(Player.class).getDescriptor(1), -1);
                }
                if (fieldNode.desc.equals("[[[L" + CLASS_MATCHES.get("NodeDeque") + ";")) {
                    addHook("getGroundItems", fieldNode.name, cl.name, "client", getUpdater().getContainer(NodeDeque.class).getDescriptor(3), -1);
                }
                if (fieldNode.desc.equals("[L" + CLASS_MATCHES.get("Npc") + ";")) {
                    addHook("getNpcs", fieldNode.name, cl.name, "client", getUpdater().getContainer(Npc.class).getDescriptor(1), -1);
                }
                if (fieldNode.desc.equals("[L" + CLASS_MATCHES.get("CollisionMap") + ";")) {
                    addHook("getCollisionMaps", fieldNode.name, cl.name, "client", getUpdater().getContainer(CollisionMap.class).getDescriptor(1), -1);
                }
                if (fieldNode.desc.equals("[[L" + CLASS_MATCHES.get("Widget") + ";")) {
                    addHook("getWidgets", fieldNode.name, cl.name, "client", getUpdater().getContainer(Widget.class).getDescriptor(2), -1);
                }
                if (fieldNode.desc.equals("L" + CLASS_MATCHES.get("Region") + ";")) {
                    addHook("getRegion", fieldNode.name, cl.name, "client", getUpdater().getContainer(Region.class).getDescriptor(), -1);
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
        identifyMinimap();
        identifyLogin();
        settings();
        selectionState();
		cursorType();
		cursorOn();
    }

    private void identifyMinimap() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.GETSTATIC, "I"),
                new InsnEntry(Opcodes.GETSTATIC, "I"),
                new InsnEntry(Opcodes.SIPUSH, "2047"),
                new InsnEntry(Opcodes.GETSTATIC, "[I"),
                new InsnEntry(Opcodes.GETSTATIC, "[I"),
                new InsnEntry(Opcodes.SIPUSH, "256"),
                new InsnEntry(Opcodes.GETSTATIC, "I")
        );
        if (pattern.find(cn)) {
            FieldInsnNode compass = pattern.get(0, FieldInsnNode.class);
            addHook("getCompassAngle", compass.name, compass.owner, "client", "I", Multipliers.getBest(compass));
            FieldInsnNode scale = pattern.get(1, FieldInsnNode.class);
            addHook("getMinimapScale", scale.name, scale.owner, "client", "I", Multipliers.getBest(scale));
            FieldInsnNode offest = pattern.get(6, FieldInsnNode.class);
            addHook("getMinimapOffset", offest.name, offest.owner, "client", "I", Multipliers.getBest(offest));
        }
    }

    private void runEnergy() {
        for (ClassNode cn : getUpdater().classnodes.values()) {
            EntryPattern pattern = new EntryPattern(
                    new InsnEntry(Opcodes.SIPUSH, "3321"), new InsnEntry(Opcodes.GETSTATIC, "[I"), new InsnEntry(Opcodes.GETSTATIC, "I"));
            if (pattern.find(cn)) {
                FieldInsnNode energy = pattern.get(2, FieldInsnNode.class);
                addHook("getEnergy", energy.name, energy.owner, "client", "I", Multipliers.getBest(energy));
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
                        addHook("getGameState", fin.name, fin.owner, "client", "I", Multipliers.getBest(fin));
                        return;
                    }
                }
            }
        }
    }

    private void menuHooks() {
        /**
         * look at void client.jk()
         *
         * seems to be something like showMenu(), sets the bounds and sets menuOpen to true.
         * The "potential" method contains 765 and 503, for bound restrictions etc
         *
         * still need count
         */
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.PUTSTATIC, "Z"),
                new InsnEntry(Opcodes.PUTSTATIC, "I"),
                new InsnEntry(Opcodes.PUTSTATIC, "I"),
                new InsnEntry(Opcodes.PUTSTATIC, "I"),
                new InsnEntry(Opcodes.PUTSTATIC, "I")
        );
        for (MethodNode m : cn.methods) {
            if (new InsnEntry(IntInsnNode.class, "765").contained(m) && new InsnEntry(IntInsnNode.class, "503").contained(m))
                if (pattern.find(m)) {
                    FieldInsnNode open = pattern.get(0, FieldInsnNode.class);
                    addHook("isMenuOpen", open.name, open.owner, "client", "Z", -1);
                    FieldInsnNode x = pattern.get(1, FieldInsnNode.class);
                    addHook("getMenuX", x.name, x.owner, "client", "I", Multipliers.getBest(x));
                    FieldInsnNode y = pattern.get(2, FieldInsnNode.class);
                    addHook("getMenuY", y.name, y.owner, "client", "I", Multipliers.getBest(y));
                    FieldInsnNode width = pattern.get(3, FieldInsnNode.class);
                    addHook("getMenuWidth", width.name, width.owner, "client", "I", Multipliers.getBest(width));
                    FieldInsnNode height = pattern.get(4, FieldInsnNode.class);
                    addHook("getMenuHeight", height.name, height.owner, "client", "I", Multipliers.getBest(height));
                    break;
                }
        }
        getMenuCount();
    }

    private void menuText() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.GETSTATIC, "[Ljava/lang/String;"), new InsnEntry(Opcodes.GETSTATIC, "[Ljava/lang/String;"),
                new InsnEntry(Opcodes.GETSTATIC, "[Ljava/lang/String;"), new InsnEntry(Opcodes.GETSTATIC, "[Ljava/lang/String;"));
        if (pattern.find(cn, "(I)Ljava/lang/String;")) {
            FieldInsnNode menuOptions = pattern.get(0, FieldInsnNode.class);
            addHook("getMenuOptions", menuOptions.name, menuOptions.owner, "client", "[Ljava/lang/String;", -1);
            FieldInsnNode menuActions = pattern.get(3, FieldInsnNode.class);
            addHook("getMenuActions", menuActions.name, menuActions.owner, "client", "[Ljava/lang/String;", -1);
        }
    }

    private void destinationHooks() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.GETSTATIC, "L" + CLASS_MATCHES.get("Player") + ";"), new InsnEntry(Opcodes.GETFIELD, "I"),
                new InsnEntry(Opcodes.BIPUSH, "7"), new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.PUTSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "I"));
        if (pattern.find(cn, "(Z)V")) {
            FieldInsnNode x = pattern.get(3, FieldInsnNode.class);
            addHook("getDestinationY", x.name, x.owner, "client", "I", Multipliers.getBest(x));
            FieldInsnNode y = pattern.get(4, FieldInsnNode.class);
            addHook("getDestinationX", y.name, y.owner, "client", "I", Multipliers.getBest(y));
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
            addHook("getPlayerIndex", index.name, index.owner, "client", "I", Multipliers.getBest(index));
        }
    }

    private void settings() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.GETFIELD), new InsnEntry(Opcodes.GETSTATIC, "[I"),
                new InsnEntry(Opcodes.GETSTATIC, "[I"));
        for (MethodNode mn : cn.methods) {
            if (pattern.find(mn)) {
                FieldInsnNode test = pattern.get(1, FieldInsnNode.class);
                FieldInsnNode index = pattern.get(2, FieldInsnNode.class);
                if (test.owner.equals(index.owner)) {
                    addHook("getSettings", index.name, index.owner, "client", "[I", -1);
                    return;
                }
            }
        }
    }

    private void logoutHooks() {
        // void logout()
        for (ClassNode cn : getUpdater().classnodes.values()) {
            EntryPattern pattern = new EntryPattern(
                    new InsnEntry(Opcodes.ICONST_0), new InsnEntry(Opcodes.PUTSTATIC, "I"), //login index
                    new InsnEntry(Opcodes.LDC, ""), new InsnEntry(Opcodes.PUTSTATIC, "Ljava/lang/String;"), // username
                    new InsnEntry(Opcodes.LDC, ""), new InsnEntry(Opcodes.PUTSTATIC, "Ljava/lang/String;")); // password
            if (pattern.find(cn)) {
                FieldInsnNode loginIndex = pattern.get(1, FieldInsnNode.class);
                FieldInsnNode username = pattern.get(3, FieldInsnNode.class);
                FieldInsnNode password = pattern.get(5, FieldInsnNode.class);
                addHook("getLoginIndex", loginIndex.name, loginIndex.owner, "client", "I", Multipliers.getBest(loginIndex));
                addHook("getUsername", username.name, username.owner, "client", "Ljava/lang/String;", -1);
                addHook("getPassword", password.name, password.owner, "client", "Ljava/lang/String;", -1);
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

    public void getMenuCount() {
        EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.GETSTATIC, "[Ljava/lang/String;"),
                new InsnEntry(Opcodes.GETSTATIC, "[I"),new InsnEntry(Opcodes.PUTSTATIC, "I")
        );
        if (pattern.find(cn)) {
            FieldInsnNode index = pattern.get(2, FieldInsnNode.class);
            addHook("getMenuCount", index.name, index.owner, "client", "I", Multipliers.getBest(index));
        }
    }

    public void getPlane() {
        EntryPattern pattern = new EntryPattern(
                new InsnEntry(Opcodes.SIPUSH, "13056"), new InsnEntry(Opcodes.SIPUSH, "13056"),
                new InsnEntry(Opcodes.GETSTATIC, "I"));
        if (pattern.find(cn)) {
            FieldInsnNode index = pattern.get(2, FieldInsnNode.class);
            addHook("getPlane", index.name, index.owner, "client", "I", Multipliers.getBest(index));
        }
    }

    public void getCamera() {
        EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.INVOKESTATIC),
                new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "I"),
                new InsnEntry(Opcodes.GETSTATIC, "[I"), new InsnEntry(Opcodes.GETSTATIC, "I"));
        cam:
        for (final ClassNode cn : updater.classnodes.values()) {
            if (cn.name.equals("client"))
                continue;
            for (final MethodNode mn : cn.methods) {
                if (pattern.find(mn)) {
                    FieldInsnNode x = pattern.get(2, FieldInsnNode.class);
                    addHook("getCameraX", x.name, x.owner, "client", "I", Multipliers.getBest(x));
                    FieldInsnNode y = pattern.get(3, FieldInsnNode.class);
                    addHook("getCameraZ", y.name, y.owner, "client", "I", Multipliers.getBest(y));
                    FieldInsnNode z = pattern.get(4, FieldInsnNode.class);
                    addHook("getCameraY", z.name, z.owner, "client", "I", Multipliers.getBest(z));
                    break cam;
                }
            }
        }
        /**
         * Have to include the atan2 before the last one so the updater wont miss it. (first 2 insn)
         */
        EntryPattern pattern1 = new EntryPattern(new InsnEntry(Opcodes.INVOKESTATIC), new InsnEntry(Opcodes.SIPUSH, "2047"),
                new InsnEntry(Opcodes.INVOKESTATIC), new InsnEntry(Opcodes.SIPUSH, "2047"),
                new InsnEntry(Opcodes.PUTSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "I"));
        if (pattern1.find(cn)) {
            FieldInsnNode yaw = pattern1.get(4, FieldInsnNode.class);
            addHook("getYaw", yaw.name, yaw.owner, "client", "I", Multipliers.getBest(yaw));
            FieldInsnNode pitch = pattern1.get(5, FieldInsnNode.class);
            addHook("getPitch", pitch.name, pitch.owner, "client", "I", Multipliers.getBest(pitch));
        }
    }

    public void getBase() {
        EntryPattern pattern = new EntryPattern(new InsnEntry(Opcodes.GETSTATIC, "[[B"), new InsnEntry(Opcodes.GETSTATIC, "[[B"), new InsnEntry(Opcodes.GETSTATIC, "[I"),
                new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "[I"), new InsnEntry(Opcodes.GETSTATIC, "I"));
        if (pattern.find(cn)) {
            FieldInsnNode x = pattern.get(3, FieldInsnNode.class);
            addHook("getBaseX", x.name, x.owner, "client", "I", Multipliers.getBest(x));
            FieldInsnNode y = pattern.get(5, FieldInsnNode.class);
            addHook("getBaseY", y.name, y.owner, "client", "I", Multipliers.getBest(y));
        }
    }

    public void selectionState() {
        FieldInsnNode selectedName = null;
        EntryPattern name = new EntryPattern(new InsnEntry(Opcodes.LDC, "null"), new InsnEntry(Opcodes.PUTSTATIC, "Ljava/lang/String;"));
        if (name.find(cn)) {
            selectedName = name.get(1, FieldInsnNode.class);
            addHook("getSelectedName", selectedName, cn.name, -1);
        }
        if (selectedName != null) {
            for (MethodNode m : cn.methods) {
                if (!Modifier.isStatic(m.access))
                    continue;
                String selectedNameDesc = String.format("name:%s;owner:%s;", selectedName.name, selectedName.owner);
                EntryPattern state = new EntryPattern(
                        new InsnEntry(Opcodes.GETSTATIC, "desc:I;owner:client;"),
                        new InsnEntry(Opcodes.GETSTATIC, selectedNameDesc)
                );
                if (state.find(m)) {
                    FieldInsnNode selectionState = state.get(0, FieldInsnNode.class);
                    addHook("getSelectionState", selectionState, cn.name, Multipliers.getBest(selectionState));
                    break;
                }
            }
        }
    }

	public void cursorType() {
		EntryPattern ep = new EntryPattern(new InsnEntry(Opcodes.IADD), new InsnEntry(Opcodes.IMUL),
				new InsnEntry(Opcodes.SIPUSH, "400"),
				new InsnEntry(Opcodes.PUTSTATIC, "I"));
		if (ep.find(cn)) {
			FieldInsnNode fin = (FieldInsnNode) ep.get(3).getInstance();
			addHook("getCursorState", fin.name, fin.owner, cn.name, fin.desc, Multipliers.getBest(fin));
		}
	}

	public void cursorOn() {
		EntryPattern ep = new EntryPattern(new InsnEntry(Opcodes.ICONST_M1), new InsnEntry(Opcodes.ISTORE),
				new InsnEntry(Opcodes.GETSTATIC, "I"), new InsnEntry(Opcodes.GETSTATIC, "[I"));
		if (ep.find(cn)) {
			FieldInsnNode count = (FieldInsnNode) ep.get(2).getInstance();
			FieldInsnNode uids = (FieldInsnNode) ep.get(3).getInstance();
			addHook("getOnCursorCount", count.name, count.owner, cn.name, count.desc, Multipliers.getBest(count));
			addHook("getOnCursorUids", uids.name, uids.owner, cn.name, uids.desc, -1);
		}
	}
}

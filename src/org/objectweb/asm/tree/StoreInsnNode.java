package org.objectweb.asm.tree;

/**
 * Created with IntelliJ IDEA.
 * User: Jeroen
 * Date: 27-2-13
 * Time: 22:21
 */
public class StoreInsnNode extends VarInsnNode {

    public AbstractInsnNode node;

    public StoreInsnNode(int opcode, int var, AbstractInsnNode node) {
        super(opcode, var);
        this.node = node;
    }
}

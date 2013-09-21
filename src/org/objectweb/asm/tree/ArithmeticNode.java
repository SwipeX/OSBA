package org.objectweb.asm.tree;

import org.objectweb.asm.MethodVisitor;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jeroen
 * Date: 27-2-13
 * Time: 22:41
 */
public class ArithmeticNode extends AbstractInsnNode {

    public AbstractInsnNode left;
    public AbstractInsnNode right;

    public ArithmeticNode(int opcode, AbstractInsnNode left, AbstractInsnNode right) {
        super(opcode);
        this.left = left;
        this.right = right;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public void accept(MethodVisitor cv) {

    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return null;
    }
}

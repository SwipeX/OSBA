package org.objectweb.asm.tree;

import org.objectweb.asm.MethodVisitor;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jeroen
 * Date: 27-2-13
 * Time: 23:07
 */
public class AReturnNode extends AbstractInsnNode {

    public AbstractInsnNode result;

    public AReturnNode(int opcode, AbstractInsnNode result) {
        super(opcode);
        this.result = result;
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

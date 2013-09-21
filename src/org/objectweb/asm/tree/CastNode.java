package org.objectweb.asm.tree;

import org.objectweb.asm.MethodVisitor;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jeroen
 * Date: 27-2-13
 * Time: 22:48
 */
public class CastNode extends AbstractInsnNode {

    public AbstractInsnNode target;

    public CastNode(int opcode, AbstractInsnNode target) {
        super(opcode);
        this.target = target;
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

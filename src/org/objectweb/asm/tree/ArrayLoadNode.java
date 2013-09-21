package org.objectweb.asm.tree;

import org.objectweb.asm.MethodVisitor;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jeroen
 * Date: 27-2-13
 * Time: 22:17
 */
public class ArrayLoadNode extends AbstractInsnNode {
    public AbstractInsnNode array;

    public AbstractInsnNode index;

    public ArrayLoadNode(int opcode, AbstractInsnNode array, AbstractInsnNode index) {
        super(opcode);
        this.array = array;
        this.index = index;
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

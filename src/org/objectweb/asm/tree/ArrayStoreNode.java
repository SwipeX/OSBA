package org.objectweb.asm.tree;

import org.objectweb.asm.MethodVisitor;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jeroen
 * Date: 27-2-13
 * Time: 22:25
 */
public class ArrayStoreNode extends AbstractInsnNode {

    public AbstractInsnNode value;
    public AbstractInsnNode index;
    public AbstractInsnNode array;

    public ArrayStoreNode(int opcode, AbstractInsnNode value, AbstractInsnNode index, AbstractInsnNode array) {
        super(opcode);
        this.value = value;
        this.index = index;
        this.array = array;
    }

    @Override
    public int getType() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void accept(MethodVisitor cv) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

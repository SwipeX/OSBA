package com.bool.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: Jeroen
 * Date: 27-2-13
 * Time: 18:08
 */
public class BasicBlock {

    private Label label;
    private InsnList instructions = new InsnList();

    private List<BasicBlock> preds;
    private Stack<AbstractInsnNode> stack;
    private BasicBlock next;
    private BasicBlock target;
    public boolean visited = false;
    public MethodNode owner;

    public BasicBlock(Label label) {
        this.label = label;
        this.instructions.add(new LabelNode(label));
        preds = new ArrayList<BasicBlock>();
    }

    public void addInsn(AbstractInsnNode ain) {
        instructions.add(ain);
    }

    public BasicBlock getNext() {
        return next;
    }

    public BasicBlock getTarget() {
        return target;
    }


    public void setNext(BasicBlock next) {
        this.next = next;
        this.next.preds.add(this);
    }

    public void setTarget(BasicBlock target) {
        this.target = target;
        this.target.preds.add(this);
        if(instructions.getLast() instanceof JumpInsnNode){
            ((JumpInsnNode) instructions.getLast()).update(target);
        }
    }

    public void removePred(BasicBlock pred) {
        if(pred.next == this) {
            pred.next = null;
        } else if(pred.target == this){
            pred.target = null;
        }
        preds.remove(pred);
    }

    public List<BasicBlock> getPreds() {
        return preds;
    }

    public void removeAllPreds() {
        while (!preds.isEmpty()) {
            removePred(preds.remove(0));
        }
    }

    public Label getLabel() {
        return label;
    }


    public InsnList getInstructions() {
        return instructions;
    }

    public void initStack(Stack<AbstractInsnNode> stack) {
        this.stack = new Stack<AbstractInsnNode>();
        for (AbstractInsnNode ain : stack) {
            this.stack.push(ain);
        }
    }

    public Stack<AbstractInsnNode> getStack() {
        if (stack == null) {
            stack = new Stack<AbstractInsnNode>();
        }
        return stack;
    }
    
    public int getIndex() {
    	return owner.basicBlocks.indexOf(this);
    }

    @Override
    public String toString() {
        String s = "Block" + label.index + "  " + owner.basicBlocks.indexOf(this) + "\r\n";
        for (AbstractInsnNode ins : getInstructions().toArray()) {
            s += " " + ins + "\r\n";
        }
        return s;
    }

    public String getName() {
        return "Block:" + label.index;
    }

    public boolean isEmpty() {
        return preds.isEmpty() && instructions.size() <= 1;
    }

    public void accept(MethodVisitor mv) {
        if (!visited) {
            visited = true;
            instructions.accept(mv);
        }
    }
}

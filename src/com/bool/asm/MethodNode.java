/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.bool.asm;

import java.util.*;

import com.bool.Application;
import com.bool.graph.FlowGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.DepthFirstIterator;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

/**
 * A node that represents a method.
 *
 * @author Eric Bruneton
 */
public class MethodNode extends MethodVisitor {

    public ClassNode owner;
    /**
     * The method's access flags (see {@link Opcodes}). This field also
     * indicates if the method is synthetic and/or deprecated.
     */
    public int access;

    /**
     * The method's name.
     */
    public String name;

    /**
     * The method's descriptor (see {@link Type}).
     */
    public String desc;

    /**
     * The method's signature. May be <tt>null</tt>.
     */
    public String signature;

    /**
     * The internal names of the method's exception classes (see
     * {@link Type#getInternalName() getInternalName}). This list is a list of
     * {@link String} objects.
     */
    public List<String> exceptions;

    /**
     * The runtime visible annotations of this method. This list is a list of
     * {@link AnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.objectweb.asm.tree.AnnotationNode
     * @label visible
     */
    public List<AnnotationNode> visibleAnnotations;

    /**
     * The runtime invisible annotations of this method. This list is a list of
     * {@link AnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.objectweb.asm.tree.AnnotationNode
     * @label invisible
     */
    public List<AnnotationNode> invisibleAnnotations;

    /**
     * The non standard attributes of this method. This list is a list of
     * {@link Attribute} objects. May be <tt>null</tt>.
     *
     * @associates org.objectweb.asm.Attribute
     */
    public List<Attribute> attrs;

    /**
     * The default value of this annotation interface method. This field must be
     * a {@link Byte}, {@link Boolean}, {@link Character}, {@link Short},
     * {@link Integer}, {@link Long}, {@link Float}, {@link Double},
     * {@link String} or {@link Type}, or an two elements String array (for
     * enumeration values), a {@link AnnotationNode}, or a {@link java.util.List} of
     * values of one of the preceding types. May be <tt>null</tt>.
     */
    public Object annotationDefault;

    /**
     * The runtime visible parameter annotations of this method. These lists are
     * lists of {@link AnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.objectweb.asm.tree.AnnotationNode
     * @label invisible parameters
     */
    public List<AnnotationNode>[] visibleParameterAnnotations;

    /**
     * The runtime invisible parameter annotations of this method. These lists
     * are lists of {@link AnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.objectweb.asm.tree.AnnotationNode
     * @label visible parameters
     */
    public List<AnnotationNode>[] invisibleParameterAnnotations;

    /**
     * The instructions of this method. This list is a list of
     * {@link AbstractInsnNode} objects.
     *
     * @associates org.objectweb.asm.tree.AbstractInsnNode
     * @label instructions
     */
    //public InsnList instructions;
    public List<BasicBlock> basicBlocks;
    private BasicBlock current;

    /**
     * The try catch blocks of this method. This list is a list of
     * {@link TryCatchBlockNode} objects.
     *
     * @associates org.objectweb.asm.tree.TryCatchBlockNode
     */
    public List<TryCatchBlockNode> tryCatchBlocks;

    /**
     * The maximum stack size of this method.
     */
    public int maxStack;

    /**
     * The maximum number of local variables of this method.
     */
    public int maxLocals;

    /**
     * The local variables of this method. This list is a list of
     * {@link LocalVariableNode} objects. May be <tt>null</tt>
     *
     * @associates org.objectweb.asm.tree.LocalVariableNode
     */
    public List<LocalVariableNode> localVariables;

    /**
     * If the accept method has been called on this object.
     */
    private boolean visited;

    public Label[] labels;

    public Handle handle;

    /**
     * Constructs an uninitialized {@link MethodNode}. <i>Subclasses must not
     * use this constructor</i>. Instead, they must use the
     * {@link #MethodNode(int)} version.
     */
    public MethodNode() {
        this(Opcodes.ASM4);
    }

    /**
     * Constructs an uninitialized {@link MethodNode}.
     *
     * @param api the ASM API version implemented by this visitor. Must be one
     *            of {@link Opcodes#ASM4}.
     */
    public MethodNode(final int api) {
        super(api);
        this.basicBlocks = new ArrayList<BasicBlock>();
        current = new BasicBlock(new Label(0));
        current.initStack(new Stack<AbstractInsnNode>());
        basicBlocks.add(current);
        handle = new Handle(0, owner.name, name, desc);
    }

    /**
     * Constructs a new {@link MethodNode}. <i>Subclasses must not use this
     * constructor</i>. Instead, they must use the
     *
     * @param access     the method's access flags (see {@link Opcodes}). This
     *                   parameter also indicates if the method is synthetic and/or
     *                   deprecated.
     * @param name       the method's name.
     * @param desc       the method's descriptor (see {@link Type}).
     * @param signature  the method's signature. May be <tt>null</tt>.
     * @param exceptions the internal names of the method's exception classes (see
     *                   {@link Type#getInternalName() getInternalName}). May be
     *                   <tt>null</tt>.
     */
    public MethodNode(ClassNode owner, final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        this(Opcodes.ASM4, owner, access, name, desc, signature, exceptions);
    }

    /**
     * Constructs a new {@link MethodNode}.
     *
     * @param api        the ASM API version implemented by this visitor. Must be one
     *                   of {@link Opcodes#ASM4}.
     * @param access     the method's access flags (see {@link Opcodes}). This
     *                   parameter also indicates if the method is synthetic and/or
     *                   deprecated.
     * @param name       the method's name.
     * @param desc       the method's descriptor (see {@link Type}).
     * @param signature  the method's signature. May be <tt>null</tt>.
     * @param exceptions the internal names of the method's exception classes (see
     *                   {@link Type#getInternalName() getInternalName}). May be
     *                   <tt>null</tt>.
     */
    public MethodNode(final int api, final ClassNode owner, final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        super(api);
        this.owner = owner;
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = new ArrayList<String>(exceptions == null ? 0 : exceptions.length);
        boolean isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
        if (!isAbstract) {
            this.localVariables = new ArrayList<LocalVariableNode>(5);
        }
        this.tryCatchBlocks = new ArrayList<TryCatchBlockNode>();
        if (exceptions != null) {
            this.exceptions.addAll(Arrays.asList(exceptions));
        }
        this.basicBlocks = new ArrayList<BasicBlock>();
        current = new BasicBlock(new Label(0));
        current.initStack(new Stack<AbstractInsnNode>());
        basicBlocks.add(current);
        handle = new Handle(0, owner.name, name, desc);
    }

    // ------------------------------------------------------------------------
    // Implementation of the MethodVisitor abstract class
    // ------------------------------------------------------------------------

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return new AnnotationNode(new ArrayList<Object>(0) {
            @Override
            public boolean add(final Object o) {
                annotationDefault = o;
                return super.add(o);
            }
        });
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        AnnotationNode an = new AnnotationNode(desc);
        if (visible) {
            if (visibleAnnotations == null) {
                visibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            visibleAnnotations.add(an);
        } else {
            if (invisibleAnnotations == null) {
                invisibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            invisibleAnnotations.add(an);
        }
        return an;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {
        AnnotationNode an = new AnnotationNode(desc);
        if (visible) {
            if (visibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length;
                visibleParameterAnnotations = (List<AnnotationNode>[]) new List<?>[params];
            }
            if (visibleParameterAnnotations[parameter] == null) {
                visibleParameterAnnotations[parameter] = new ArrayList<AnnotationNode>(1);
            }
            visibleParameterAnnotations[parameter].add(an);
        } else {
            if (invisibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length;
                invisibleParameterAnnotations = (List<AnnotationNode>[]) new List<?>[params];
            }
            if (invisibleParameterAnnotations[parameter] == null) {
                invisibleParameterAnnotations[parameter] = new ArrayList<AnnotationNode>(1);
            }
            invisibleParameterAnnotations[parameter].add(an);
        }
        return an;
    }

    @Override
    public void visitAttribute(final Attribute attr) {
        if (attrs == null) {
            attrs = new ArrayList<Attribute>(1);
        }
        attrs.add(attr);
    }

    @Override
    public void visitCode() {
    }

    @Override
    public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack) {
        //instructions.add(new FrameNode(type, nLocal, local == null ? null : getLabelNodes(local), nStack, stack == null ? null : getLabelNodes(stack)));
    }

    @Override
    public void visitInsn(final int opcode) {
        getLastBlock().addInsn(new InsnNode(opcode));
        switch (opcode) {
            case Opcodes.RETURN:
            case Opcodes.IRETURN:
            case Opcodes.ARETURN:
            case Opcodes.FRETURN:
            case Opcodes.DRETURN:
            case Opcodes.LRETURN:
            case Opcodes.ATHROW:
                current = getBlock(new Label(), false);
                break;
            default:
                break;
        }
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        getLastBlock().addInsn(new IntInsnNode(opcode, operand));
    }

    @Override
    public void visitVarInsn(final int opcode, final int var) {
        getLastBlock().addInsn(new VarInsnNode(opcode, var));
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        getLastBlock().addInsn(new TypeInsnNode(opcode, type));
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        getLastBlock().addInsn(new FieldInsnNode(opcode, owner, name, desc));
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        getLastBlock().addInsn(new MethodInsnNode(opcode, owner, name, desc));
        Application.getInstance().callGraph.addMethodCall(handle, new Handle(0, owner, name, desc));
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        getLastBlock().addInsn(new InvokeDynamicInsnNode(name, desc, bsm, bsmArgs));
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        System.out.println("!!");
        BasicBlock block = getBlock(label);
        current.setTarget(block);
        if (opcode != Opcodes.GOTO) {
            getLastBlock().addInsn(new JumpInsnNode(opcode, block));
        }
        Stack<AbstractInsnNode> curStack = current.getStack();
        current = getBlock(new Label(-1), !(opcode == Opcodes.GOTO));
        current.initStack(curStack);

    }

    public void visitJumpInsn(int opcode, Label label, Label next) {
        BasicBlock block = getBlock(label);
        current.setTarget(block);
        getLastBlock().addInsn(new JumpInsnNode(opcode, block));
        Stack<AbstractInsnNode> curStack = current.getStack();
        current = getBlock(next, !(opcode == Opcodes.GOTO));
        current.initStack(curStack);
    }

    @Override
    public void visitLabel(final Label label) {
        //getLastBlock().addInsn(getLabelNode(label));
        Stack<AbstractInsnNode> curStack = current == null ? new Stack<AbstractInsnNode>() : current.getStack();
        current = getBlock(label);
        current.initStack(curStack);
    }

    @Override
    public void visitLdcInsn(final Object cst) {
        getLastBlock().addInsn(new LdcInsnNode(cst));
    }

    @Override
    public void visitIincInsn(final int var, final int increment) {
        getLastBlock().addInsn(new IincInsnNode(var, increment));
    }

    @Override
    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels) {
        getLastBlock().addInsn(new TableSwitchInsnNode(min, max, getBlock(dflt), getBlocks(labels)));
    }

    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        getLastBlock().addInsn(new LookupSwitchInsnNode(getBlock(dflt), keys, getBlocks(labels)));
    }

    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        getLastBlock().addInsn(new MultiANewArrayInsnNode(desc, dims));
    }

    @Override
    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
        //tryCatchBlocks.add(new TryCatchBlockNode(getBlock(start), getBlock(end), getBlock(handler, false), type));
    }

    @Override
    public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
        //localVariables.add(new LocalVariableNode(name, desc, signature, getLabelNode(start), getLabelNode(end), index));
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
        //getLastBlock().addInsn(new LineNumberNode(line, getLabelNode(start)));
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
    }

    @Override
    public void visitEnd() {
        ListIterator<BasicBlock> block = basicBlocks.listIterator();
        while (block.hasNext()) {
            BasicBlock basicBlock = block.next();
            basicBlock.owner = this;
            if (basicBlock.isEmpty()) {
                block.remove();
            }
        }
        Collections.sort(basicBlocks, new Comparator<BasicBlock>() {
            @Override
            public int compare(BasicBlock o1, BasicBlock o2) {
                return o1.getLabel().index - o2.getLabel().index;
            }
        });
    }

    // ------------------------------------------------------------------------
    // Accept method
    // ------------------------------------------------------------------------

    /**
     * Checks that this method node is compatible with the given ASM API
     * version. This methods checks that this node, and all its nodes
     * recursively, do not contain elements that were introduced in more recent
     * versions of the ASM API than the given version.
     *
     * @param api an ASM API version. Must be one of {@link Opcodes#ASM4}.
     */
    public void check(final int api) {
        // nothing to do
    }

    /**
     * Makes the given class visitor visit this method.
     *
     * @param cv a class visitor.
     */
    public void accept(final ClassVisitor cv) {
        String[] exceptions = new String[this.exceptions.size()];
        this.exceptions.toArray(exceptions);
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null) {
            accept(mv);
        }
    }

    /**
     * Makes the given method visitor visit this method.
     *
     * @param mv a method visitor.
     */
    public void accept(final MethodVisitor mv) {
        // visits the method attributes
        int i, j, n;
        if (annotationDefault != null) {
            AnnotationVisitor av = mv.visitAnnotationDefault();
            AnnotationNode.accept(av, null, annotationDefault);
            if (av != null) {
                av.visitEnd();
            }
        }
        n = visibleAnnotations == null ? 0 : visibleAnnotations.size();
        for (i = 0; i < n; ++i) {
            AnnotationNode an = visibleAnnotations.get(i);
            an.accept(mv.visitAnnotation(an.desc, true));
        }
        n = invisibleAnnotations == null ? 0 : invisibleAnnotations.size();
        for (i = 0; i < n; ++i) {
            AnnotationNode an = invisibleAnnotations.get(i);
            an.accept(mv.visitAnnotation(an.desc, false));
        }
        n = visibleParameterAnnotations == null ? 0 : visibleParameterAnnotations.length;
        for (i = 0; i < n; ++i) {
            List<?> l = visibleParameterAnnotations[i];
            if (l == null) {
                continue;
            }
            for (j = 0; j < l.size(); ++j) {
                AnnotationNode an = (AnnotationNode) l.get(j);
                an.accept(mv.visitParameterAnnotation(i, an.desc, true));
            }
        }
        n = invisibleParameterAnnotations == null ? 0 : invisibleParameterAnnotations.length;
        for (i = 0; i < n; ++i) {
            List<?> l = invisibleParameterAnnotations[i];
            if (l == null) {
                continue;
            }
            for (j = 0; j < l.size(); ++j) {
                AnnotationNode an = (AnnotationNode) l.get(j);
                an.accept(mv.visitParameterAnnotation(i, an.desc, false));
            }
        }
        if (visited) {
            //instructions.resetLabels();
        }
        n = attrs == null ? 0 : attrs.size();
        for (i = 0; i < n; ++i) {
            mv.visitAttribute(attrs.get(i));
        }
        // visits the method's code
        if (basicBlocks.size() > 0) {
            mv.visitCode();
            // visits try catch blocks
            n = tryCatchBlocks == null ? 0 : tryCatchBlocks.size();
            for (i = 0; i < n; ++i) {
                tryCatchBlocks.get(i).accept(mv);
            }


            DepthFirstIterator<BasicBlock, DefaultEdge> bfi = new DepthFirstIterator(new FlowGraph(this).getGraph(), basicBlocks.get(0));
            while (bfi.hasNext()) {
                bfi.next().accept(mv);
            }
            //instructions.accept(mv);
            // visits local variables
            n = localVariables == null ? 0 : localVariables.size();
            for (i = 0; i < n; ++i) {
                localVariables.get(i).accept(mv);
            }
            // visits maxs
            mv.visitMaxs(maxStack, maxLocals);
            visited = true;
        }
        mv.visitEnd();
    }

    public BasicBlock getLastBlock() {
        return current;
    }

    private BasicBlock[] getBlocks(Label[] labels) {
        BasicBlock[] blocks = new BasicBlock[labels.length];
        for (int i = 0; i < labels.length; i++) {
            blocks[i] = getBlock(labels[i]);
        }
        return blocks;
    }

    public BasicBlock getBlock(Label label) {
        return getBlock(label, true);
    }

    public BasicBlock getBlock(Label label, boolean addsuccesor) {
        if (!(label.info instanceof BasicBlock)) {
            label.info = new BasicBlock(label);
            if (addsuccesor) {
                current.setNext((BasicBlock) label.info);
            }
            basicBlocks.add((BasicBlock) label.info);
        }
        return (BasicBlock) label.info;
    }
}

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
package org.objectweb.asm.tree;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.MethodVisitor;

/**
 * A node that represents a bytecode instruction. <i>An instruction can appear
 * at most once in at most one {@link org.objectweb.asm.tree.InsnList} at a time</i>.
 * 
 * @author Eric Bruneton
 */
public abstract class AbstractInsnNode {

    /**
     * The names of the Java Virtual Machine opcodes.
     */
    public static final String[] OPCODES;

    /**
     * The names of the for <code>operand</code> parameter values of the
     * {@link org.objectweb.asm.MethodVisitor#visitIntInsn} method when
     * <code>opcode</code> is <code>NEWARRAY</code>.
     */
    public static final String[] TYPES;

    /**
     * The names of the <code>tag</code> field values for
     * {@link org.objectweb.asm.Handle}.
     */
    public static final String[] HANDLE_TAG;

    static {
        String s = "NOP,ACONST_NULL,ICONST_M1,ICONST_0,ICONST_1,ICONST_2,"
                + "ICONST_3,ICONST_4,ICONST_5,LCONST_0,LCONST_1,FCONST_0,"
                + "FCONST_1,FCONST_2,DCONST_0,DCONST_1,BIPUSH,SIPUSH,LDC,,,"
                + "ILOAD,LLOAD,FLOAD,DLOAD,ALOAD,,,,,,,,,,,,,,,,,,,,,IALOAD,"
                + "LALOAD,FALOAD,DALOAD,AALOAD,BALOAD,CALOAD,SALOAD,ISTORE,"
                + "LSTORE,FSTORE,DSTORE,ASTORE,,,,,,,,,,,,,,,,,,,,,IASTORE,"
                + "LASTORE,FASTORE,DASTORE,AASTORE,BASTORE,CASTORE,SASTORE,POP,"
                + "POP2,DUP,DUP_X1,DUP_X2,DUP2,DUP2_X1,DUP2_X2,SWAP,IADD,LADD,"
                + "FADD,DADD,ISUB,LSUB,FSUB,DSUB,IMUL,LMUL,FMUL,DMUL,IDIV,LDIV,"
                + "FDIV,DDIV,IREM,LREM,FREM,DREM,INEG,LNEG,FNEG,DNEG,ISHL,LSHL,"
                + "ISHR,LSHR,IUSHR,LUSHR,IAND,LAND,IOR,LOR,IXOR,LXOR,IINC,I2L,"
                + "I2F,I2D,L2I,L2F,L2D,F2I,F2L,F2D,D2I,D2L,D2F,I2B,I2C,I2S,LCMP,"
                + "FCMPL,FCMPG,DCMPL,DCMPG,IFEQ,IFNE,IFLT,IFGE,IFGT,IFLE,"
                + "IF_ICMPEQ,IF_ICMPNE,IF_ICMPLT,IF_ICMPGE,IF_ICMPGT,IF_ICMPLE,"
                + "IF_ACMPEQ,IF_ACMPNE,GOTO,JSR,RET,TABLESWITCH,LOOKUPSWITCH,"
                + "IRETURN,LRETURN,FRETURN,DRETURN,ARETURN,RETURN,GETSTATIC,"
                + "PUTSTATIC,GETFIELD,PUTFIELD,INVOKEVIRTUAL,INVOKESPECIAL,"
                + "INVOKESTATIC,INVOKEINTERFACE,INVOKEDYNAMIC,NEW,NEWARRAY,"
                + "ANEWARRAY,ARRAYLENGTH,ATHROW,CHECKCAST,INSTANCEOF,"
                + "MONITORENTER,MONITOREXIT,,MULTIANEWARRAY,IFNULL,IFNONNULL,";
        OPCODES = new String[200];
        int i = 0;
        int j = 0;
        int l;
        while ((l = s.indexOf(',', j)) > 0) {
            OPCODES[i++] = j + 1 == l ? null : s.substring(j, l);
            j = l + 1;
        }

        s = "T_BOOLEAN,T_CHAR,T_FLOAT,T_DOUBLE,T_BYTE,T_SHORT,T_INT,T_LONG,";
        TYPES = new String[12];
        j = 0;
        i = 4;
        while ((l = s.indexOf(',', j)) > 0) {
            TYPES[i++] = s.substring(j, l);
            j = l + 1;
        }

        s = "H_GETFIELD,H_GETSTATIC,H_PUTFIELD,H_PUTSTATIC,"
                + "H_INVOKEVIRTUAL,H_INVOKESTATIC,H_INVOKESPECIAL,"
                + "H_NEWINVOKESPECIAL,H_INVOKEINTERFACE,";
        HANDLE_TAG = new String[10];
        j = 0;
        i = 1;
        while ((l = s.indexOf(',', j)) > 0) {
            HANDLE_TAG[i++] = s.substring(j, l);
            j = l + 1;
        }
    }


    /**
     * The type of {@link org.objectweb.asm.tree.InsnNode} instructions.
     */
    public static final int INSN = 0;

    /**
     * The type of {@link org.objectweb.asm.tree.IntInsnNode} instructions.
     */
    public static final int INT_INSN = 1;

    /**
     * The type of {@link org.objectweb.asm.tree.VarInsnNode} instructions.
     */
    public static final int VAR_INSN = 2;

    /**
     * The type of {@link org.objectweb.asm.tree.TypeInsnNode} instructions.
     */
    public static final int TYPE_INSN = 3;

    /**
     * The type of {@link org.objectweb.asm.tree.FieldInsnNode} instructions.
     */
    public static final int FIELD_INSN = 4;

    /**
     * The type of {@link org.objectweb.asm.tree.MethodInsnNode} instructions.
     */
    public static final int METHOD_INSN = 5;

    /**
     * The type of {@link InvokeDynamicInsnNode} instructions.
     */
    public static final int INVOKE_DYNAMIC_INSN = 6;

    /**
     * The type of {@link org.objectweb.asm.tree.JumpInsnNode} instructions.
     */
    public static final int JUMP_INSN = 7;

    /**
     * The type of {@link org.objectweb.asm.tree.LabelNode} "instructions".
     */
    public static final int LABEL = 8;

    /**
     * The type of {@link org.objectweb.asm.tree.LdcInsnNode} instructions.
     */
    public static final int LDC_INSN = 9;

    /**
     * The type of {@link org.objectweb.asm.tree.IincInsnNode} instructions.
     */
    public static final int IINC_INSN = 10;

    /**
     * The type of {@link TableSwitchInsnNode} instructions.
     */
    public static final int TABLESWITCH_INSN = 11;

    /**
     * The type of {@link LookupSwitchInsnNode} instructions.
     */
    public static final int LOOKUPSWITCH_INSN = 12;

    /**
     * The type of {@link MultiANewArrayInsnNode} instructions.
     */
    public static final int MULTIANEWARRAY_INSN = 13;

    /**
     * The type of {@link org.objectweb.asm.tree.FrameNode} "instructions".
     */
    public static final int FRAME = 14;

    /**
     * The type of {@link org.objectweb.asm.tree.LineNumberNode} "instructions".
     */
    public static final int LINE = 15;

    /**
     * The opcode of this instruction.
     */
    protected int opcode;

    /**
     * Previous instruction in the list to which this instruction belongs.
     */
    AbstractInsnNode prev;

    /**
     * Next instruction in the list to which this instruction belongs.
     */
    AbstractInsnNode next;

    /**
     * Index of this instruction in the list to which it belongs. The value of
     * this field is correct only when {@link org.objectweb.asm.tree.InsnList#cache} is not null. A
     * value of -1 indicates that this instruction does not belong to any
     * {@link org.objectweb.asm.tree.InsnList}.
     */
    int index;

    /**
     * Constructs a new {@link AbstractInsnNode}.
     * 
     * @param opcode
     *            the opcode of the instruction to be constructed.
     */
    protected AbstractInsnNode(final int opcode) {
        this.opcode = opcode;
        this.index = -1;
    }

    /**
     * Returns the opcode of this instruction.
     * 
     * @return the opcode of this instruction.
     */
    public int getOpcode() {
        return opcode;
    }

    /**
     * Returns the type of this instruction.
     * 
     * @return the type of this instruction, i.e. one the constants defined in
     *         this class.
     */
    public abstract int getType();

    /**
     * Returns the previous instruction in the list to which this instruction
     * belongs, if any.
     * 
     * @return the previous instruction in the list to which this instruction
     *         belongs, if any. May be <tt>null</tt>.
     */
    public AbstractInsnNode getPrevious() {
        return prev;
    }

    /**
     * Returns the next instruction in the list to which this instruction
     * belongs, if any.
     * 
     * @return the next instruction in the list to which this instruction
     *         belongs, if any. May be <tt>null</tt>.
     */
    public AbstractInsnNode getNext() {
        return next;
    }

    /**
     * Makes the given code visitor visit this instruction.
     * 
     * @param cv
     *            a code visitor.
     */
    public abstract void accept(final MethodVisitor cv);

    /**
     * Returns a copy of this instruction.
     * 
     * @param labels
     *            a map from LabelNodes to cloned LabelNodes.
     * @return a copy of this instruction. The returned instruction does not
     *         belong to any {@link org.objectweb.asm.tree.InsnList}.
     */
    public abstract AbstractInsnNode clone(
            final Map<LabelNode, LabelNode> labels);

    /**
     * Returns the clone of the given label.
     * 
     * @param label
     *            a label.
     * @param map
     *            a map from LabelNodes to cloned LabelNodes.
     * @return the clone of the given label.
     */
    static LabelNode clone(final LabelNode label,
            final Map<LabelNode, LabelNode> map) {
        return map.get(label);
    }

    /**
     * Returns the clones of the given labels.
     * 
     * @param labels
     *            a list of labels.
     * @param map
     *            a map from LabelNodes to cloned LabelNodes.
     * @return the clones of the given labels.
     */
    static LabelNode[] clone(final List<LabelNode> labels,
            final Map<LabelNode, LabelNode> map) {
        LabelNode[] clones = new LabelNode[labels.size()];
        for (int i = 0; i < clones.length; ++i) {
            clones[i] = map.get(labels.get(i));
        }
        return clones;
    }

    @Override
    public String toString() {
        if(opcode == -1) return super.toString();
        return OPCODES[opcode];
    }
}

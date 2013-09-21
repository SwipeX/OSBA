package com.bool.graph;

import com.bool.asm.BasicBlock;
import com.bool.asm.MethodNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

/**
 * Created with IntelliJ IDEA.
 * User: Jeroen
 * Date: 27-2-13
 * Time: 15:54
 */
public class FlowGraph {

    private MethodNode mn;

    public FlowGraph(MethodNode mn) {
        this.mn = mn;
    }


    public DirectedGraph<BasicBlock, DefaultEdge> getGraph() {
        DirectedGraph<BasicBlock, DefaultEdge> g = new SimpleDirectedGraph<BasicBlock, DefaultEdge>(DefaultEdge.class);
        for (BasicBlock ain : mn.basicBlocks) {
            //System.out.println(ain);
            if (!g.containsVertex(ain)) {
                g.addVertex(ain);
            }
            if(ain.getTarget() != null && ain.getTarget() != ain){
                if (!g.containsVertex(ain.getTarget())) {
                    g.addVertex(ain.getTarget());
                }
                g.addEdge(ain, ain.getTarget());
            }
            if (ain.getNext() != null) {

                if (!g.containsVertex(ain.getNext())) {
                    g.addVertex(ain.getNext());
                }
                g.addEdge(ain, ain.getNext());
            }
        }
        /*AbstractInsnNode ain = mn.instructions.getFirst();
        while (ain != null) {
            AbstractInsnNode next = ain.getNext();
            if (next != null) {
                if(ain.getOpcode() != Opcodes.GOTO)
                    g.addEdge(ain, next);
                if(ain instanceof JumpInsnNode){
                    g.addEdge(ain, ((JumpInsnNode) ain).label);
                }
            } else if(ain.getOpcode() == Opcodes.GOTO){
                g.addEdge(ain, ((JumpInsnNode)ain).label);
            }
            ain = ain.getNext();
        }    */
        return g;
    }

}

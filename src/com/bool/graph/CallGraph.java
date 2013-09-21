package com.bool.graph;

import com.bool.asm.MethodNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.objectweb.asm.Handle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jeroen
 * Date: 27-2-13
 * Time: 18:28
 */
public class CallGraph {

    //public Map<MethodNode,List<Handle>> map = new HashMap<MethodNode, List<Handle>>();
    public DirectedGraph<Handle, DefaultEdge> graph = new DirectedMultigraph<Handle, DefaultEdge>(DefaultEdge.class);

    public CallGraph() {

    }

    public void addMethodCall(Handle from, Handle target) {
        addVertex(from);
        addVertex(target);
        graph.addEdge(from, target);
    }

    private void addVertex(Handle vertex) {
        if (!graph.containsVertex(vertex)) {
            graph.addVertex(vertex);
        }
    }
}

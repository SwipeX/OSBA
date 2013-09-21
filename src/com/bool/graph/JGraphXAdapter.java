package com.bool.graph;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.bool.asm.BasicBlock;
import com.bool.asm.ClassNode;
import com.bool.asm.MethodNode;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.objectweb.asm.ClassReader;

public class JGraphXAdapter<V, E> extends mxGraph {

    private Graph<V, E> graphT;

    private HashMap<V, mxCell> vertexToCellMap = new HashMap<V, mxCell>();

    private HashMap<E, mxCell> edgeToCellMap = new HashMap<E, mxCell>();

    private HashMap<mxCell, V> cellToVertexMap = new HashMap<mxCell, V>();

    private HashMap<mxCell, E> cellToEdgeMap = new HashMap<mxCell, E>();

    /*
     * CONSTRUCTOR
     */

    public JGraphXAdapter(final Graph<V, E> graphT) {
        super();
        this.graphT = graphT;
        insertJGraphT(graphT);
    }

    /*
     * METHODS
     */

    public void addJGraphTVertex(V vertex) {

        getModel().beginUpdate();

        try {
            mxCell cell = new mxCell(vertex);
            cell.setVertex(true);
            cell.setId(null);
            addCell(cell, defaultParent);
            vertexToCellMap.put(vertex, cell);
            cellToVertexMap.put(cell, vertex);
        } finally {
            getModel().endUpdate();
        }
    }

    public void addJGraphTEdge(E edge) {

        getModel().beginUpdate();

        try {
            V source = graphT.getEdgeSource(edge);
            V target = graphT.getEdgeTarget(edge);
            mxCell cell = new mxCell(edge);
            cell.setEdge(true);
            cell.setId(null);
            cell.setGeometry(new mxGeometry());
            cell.getGeometry().setRelative(true);
            addEdge(cell, defaultParent, vertexToCellMap.get(source), vertexToCellMap.get(target), null);
            edgeToCellMap.put(edge, cell);
            cellToEdgeMap.put(cell, edge);
        } finally {
            getModel().endUpdate();
        }
    }

    public HashMap<V, mxCell> getVertexToCellMap() {
        return vertexToCellMap;
    }

    public HashMap<E, mxCell> getEdgeToCellMap() {
        return edgeToCellMap;
    }

    public HashMap<mxCell, E> getCellToEdgeMap() {
        return cellToEdgeMap;
    }

    public HashMap<mxCell, V> getCellToVertexMap() {
        return cellToVertexMap;
    }

    /*
     * GRAPH LISTENER
     */

    /*
     * PRIVATE METHODS
     */

    private void insertJGraphT(Graph<V, E> graphT) {
        getModel().beginUpdate();
        try {
            for (V vertex : graphT.vertexSet()) {
                addJGraphTVertex(vertex);
            }
            for (E edge : graphT.edgeSet()) {
                addJGraphTEdge(edge);
            }
        } finally {
            getModel().endUpdate();
        }

    }

    /*
     * MAIN METHOD
     */

    public static void main(String[] args) throws Throwable {
        // create a JGraphT graph
        ClassReader cr = new ClassReader(new FileInputStream("fz.class"));
        ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        for (MethodNode mn : cn.methods) {
            if (!mn.name.equals("k")) {
                continue;
            }
            JGraphXAdapter<BasicBlock, DefaultEdge> graph = new JGraphXAdapter<BasicBlock, DefaultEdge>(new FlowGraph(mn).getGraph());
            graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_NOLABEL, "1");
            JFrame frame = new JFrame();
            mxGraphComponent graphComponent = new mxGraphComponent(graph);
            frame.getContentPane().add(graphComponent);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 320);
            frame.setVisible(true);
            graph.setAutoSizeCells(true);
            for (mxCell cell : graph.getVertexToCellMap().values()) {
                graph.getModel().setGeometry(cell, new mxGeometry(0, 0, 20, 20));
                graph.updateCellSize(cell);
            }
            graph.getModel().beginUpdate();
            mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
            layout.execute(graph.getDefaultParent());
            graph.getModel().endUpdate();
            saveToPng(graphComponent);
            Dimension d = graphComponent.getGraphControl().getSize();
            BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            graphComponent.getGraphControl().paint(g);
            final File outputfile = new File("graph.png");
            ImageIO.write(image, "png", outputfile);
        }
    }

    public static void saveToPng(mxGraphComponent graphComponent) {
        try {
            Color bg = Color.white;
            BufferedImage image = mxCellRenderer.createBufferedImage(graphComponent.getGraph(), null, 1, bg, graphComponent.isAntiAlias(), null, graphComponent.getCanvas());
            ImageIO.write(image, "png", new File("graph_.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
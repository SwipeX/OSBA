package com.bool;

import com.bool.asm.BasicBlock;
import com.bool.asm.ClassNode;
import com.bool.asm.MethodNode;
import com.bool.graph.CallGraph;
import com.bool.graph.FlowGraph;
import com.bool.graph.JGraphXAdapter;
import com.bool.util.ClassPath;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import org.jgrapht.graph.DefaultEdge;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: Jeroen
 * Date: 27-2-13
 * Time: 21:12
 */
public class Application {

    public ClassPath classPath = new ClassPath();
    public CallGraph callGraph = new CallGraph();

    private static Application instance;

    public static Application getInstance() {
        if(instance == null)
            instance = new Application();
        return instance;
    }

    public void run() throws IOException {
        try {
            classPath.addJar("./gamepack.jar");
        } catch (IOException e) {
        }

        int count = 0;
        for (ClassNode cn : classPath.getClasses().values()){
            ListIterator<MethodNode> it = cn.methods.listIterator();
            while (it.hasNext()) {
                MethodNode mn = it.next();
                if(cn.name.equals("client") && mn.name.equals("gld")){
                	showFrame(mn);
                }
                if(!mn.name.startsWith("<") && !callGraph.graph.containsVertex(mn.handle)){
                    it.remove();
                    count++;
                }
            }
        }
        System.out.println("Removed " + count + " unused methods!");
        classPath.safeJar("./deob.jar");
    }

    public void showFrame(MethodNode mn){
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
    }

    public static void main(String[] args){
        try {
            Application.getInstance().run();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

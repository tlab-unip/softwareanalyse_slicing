package de.uni_passau.fim.se2.sa.slicing;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

// import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

// import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
// import com.mxgraph.util.mxCellRenderer;

import de.uni_passau.fim.se2.sa.slicing.cfg.*;
import de.uni_passau.fim.se2.sa.slicing.coverage.*;
import de.uni_passau.fim.se2.sa.slicing.graph.*;
import de.uni_passau.fim.se2.sa.slicing.instrumentation.*;

import org.objectweb.asm.*;

public class SlicerTest {

    private String className = "de.uni_passau.fim.se2.sa.examples.Calculator";
    private String methodName = "evaluate";
    private String methodDesc = "(Ljava/lang/String;)I";
    private ClassNode classNode;
    private MethodNode methodNode;
    private Map<String, LocalVariableTable> localVariableTables;

    @BeforeEach
    public void InitializeNodes() throws Exception {
        final int api = Opcodes.ASM9;
        classNode = new ClassNode(api);
        final var cr = new ClassReader(className);
        cr.accept(classNode, 0);
        final var visitor = new CFGLocalVariableTableVisitor(api);
        cr.accept(visitor, 0);
        localVariableTables = visitor.getLocalVariableTables();
        methodNode = classNode.methods.stream().filter(m -> methodName.equals(m.name) && methodDesc.equals(m.desc))
                .findAny()
                .orElse(null);
    }

    // @SuppressWarnings("unchecked")
    // public void VisualizeGraph(Object graph, String fileName) throws Exception {
    //     var g = (org.jgrapht.Graph<Node, DefaultEdge>) graph;
    //     var adapter = new JGraphXAdapter<>(g);
    //     var layout = new mxHierarchicalLayout(adapter);
    //     layout.execute(adapter.getDefaultParent());
    //     var image = mxCellRenderer.createBufferedImage(adapter, null, 2, Color.WHITE,
    //             true, null);
    //     var file = new File(fileName);
    //     ImageIO.write(image, "PNG", file);

    //     assertEquals(file, true);
    // }

    @Test
    public void GraphTest() throws Exception {
        var pdt = new PostDominatorTree(classNode, methodNode);
        var cdg = new ControlDependenceGraph(classNode, methodNode);
        var ddg = new DataDependenceGraph(classNode, methodNode);
        var pdg = new ProgramDependenceGraph(classNode, methodNode);

        var field = new ProgramGraph().getClass().getDeclaredField("graph");
        field.setAccessible(true);
        var pdtGraph = field.get(pdt.computeResult());
        var cdgGraph = field.get(cdg.computeResult());
        var ddgGraph = field.get(ddg.computeResult());
        var pdgGraph = field.get(pdg.computeResult());
        var slice = pdg.backwardSlice(pdg.getCFG().getExit().get());
    }

    @Test
    public void CoverageTest() {
        CoverageTracker.trackLineVisit(0);
        var visitedLines = CoverageTracker.getVisitedLines();
        assertEquals(visitedLines, Set.of(0));
    }

    @Test
    public void InstrumentationTest() {
        var transformer = new LineCoverageTransformer(className);
        transformer.transform(null, className, getClass(), null, null);
    }
}

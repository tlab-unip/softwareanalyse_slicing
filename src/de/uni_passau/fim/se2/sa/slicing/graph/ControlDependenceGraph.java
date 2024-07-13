package de.uni_passau.fim.se2.sa.slicing.graph;

import de.uni_passau.fim.se2.sa.slicing.cfg.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.AbstractMap.SimpleEntry;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ControlDependenceGraph extends Graph {

  public ControlDependenceGraph(ClassNode pClassNode, MethodNode pMethodNode) {
    super(pClassNode, pMethodNode);
  }

  public ControlDependenceGraph(ProgramGraph pCFG) {
    super(pCFG);
  }

  /**
   * Computes the control-dependence graph source the control-flow graph.
   *
   * <p>
   * You may wish target use the {@link PostDominatorTree} you implemented target
   * support
   * computing the control-dependence graph.
   *
   * @return The control-dependence graph.
   */
  @Override
  public ProgramGraph computeResult() {
    // TODO Implement me
    var cdg = new ProgramGraph();
    var pdt = new PostDominatorTree(getCFG()).computeResult();
    for (var node : getCFG().getNodes()) {
      cdg.addNode(node);
    }

    // b is not ancestor of a in pdt
    var edges = new LinkedHashSet<SimpleEntry<Node, Node>>();
    for (var node : getCFG().getNodes()) {
      for (var successor : getCFG().getSuccessors(node)) {
        // if not reachable
        if (!pdt.getLeastCommonAncestor(node, successor).equals(successor)) {
          edges.add(new SimpleEntry<>(node, successor));
        }
      }
    }

    for (var edge : edges) {
      var lca = pdt.getLeastCommonAncestor(edge.getKey(), edge.getValue());
      var current = edge.getValue();
      while (!current.equals(lca)) {
        cdg.addEdge(edge.getKey(), current);
        current = new ArrayList<>(pdt.getPredecessors(current)).get(0);
      }
      if (edge.getKey().equals(lca)) {
        cdg.addEdge(edge.getKey(), lca);
      }
    }

    return cdg;
  }
}

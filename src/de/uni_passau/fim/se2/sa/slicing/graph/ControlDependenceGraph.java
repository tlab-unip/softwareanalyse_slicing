package de.uni_passau.fim.se2.sa.slicing.graph;

import de.uni_passau.fim.se2.sa.slicing.cfg.*;

import java.util.List;

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
    var graph = new ProgramGraph();
    var pdt = new PostDominatorTree(getCFG()).computeResult();
    for (var node : getCFG().getNodes()) {
      cdg.addNode(node);
      graph.addNode(node);
    }

    for (var node : getCFG().getNodes()) {
      for (var successor : getCFG().getSuccessors(node)) {
        if (pdt.getLeastCommonAncestor(node, successor) != successor) {
          graph.addEdge(node, successor);
        }
      }
    }

    for (var node : graph.getNodes()) {
      for (var successor : graph.getSuccessors(node)) {
        var lca = pdt.getLeastCommonAncestor(node, successor);
        var current = successor;
        while (current != lca) {
          cdg.addEdge(node, current);
          current = pdt.getPredecessors(current).iterator().next();
        }
        if (node == lca) {
          cdg.addEdge(node, lca);
        }
      }
    }

    var entry = getCFG().getEntry().get();
    for (var node : cdg.getNodes()) {
      if (!node.equals(entry)
          && (cfg.getPredecessors(node).isEmpty()
              || cfg.getPredecessors(node).equals(List.of(node)))) {
        cdg.addEdge(entry, node);
      }
    }

    return graph;
  }
}

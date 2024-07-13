package de.uni_passau.fim.se2.sa.slicing.graph;

import de.uni_passau.fim.se2.sa.slicing.cfg.Node;
import de.uni_passau.fim.se2.sa.slicing.cfg.ProgramGraph;

import java.util.HashSet;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/** Provides an analysis that calculates the program-dependence graph. */
public class ProgramDependenceGraph extends Graph implements Sliceable<Node> {

  private ProgramGraph pdg;
  private final ProgramGraph cdg;
  private final ProgramGraph ddg;

  public ProgramDependenceGraph(ClassNode pClassNode, MethodNode pMethodNode) {
    super(pClassNode, pMethodNode);
    pdg = null;

    if (cfg != null) {
      cdg = new ControlDependenceGraph(pClassNode, pMethodNode).computeResult();
      ddg = new DataDependenceGraph(pClassNode, pMethodNode).computeResult();
    } else {
      cdg = null;
      ddg = null;
    }
  }

  public ProgramDependenceGraph(ProgramGraph pProgramGraph) {
    super(null);
    pdg = pProgramGraph;
    cdg = null;
    ddg = null;
  }

  /**
   * Computes the program-dependence graph from a control-flow graph.
   *
   * <p>
   * You may wish to use the {@link ControlDependenceGraph} and
   * {@link DataDependenceGraph} you
   * have already implemented to support computing the program-dependence graph.
   *
   * @return A program-dependence graph.
   */
  @Override
  public ProgramGraph computeResult() {
    // TODO Implement me
    if (pdg != null) {
      return pdg;
    }

    pdg = new ProgramGraph();
    for (var node : cfg.getNodes()) {
      pdg.addNode(node);
    }
    // add all edges in cdg and ddg
    for (var node : cfg.getNodes()) {
      for (var successor : cdg.getSuccessors(node)) {
        pdg.addEdge(node, successor);
      }
      for (var successor : ddg.getSuccessors(node)) {
        pdg.addEdge(node, successor);
      }
    }
    return pdg;
  }

  /** {@inheritDoc} */
  @Override
  public Set<Node> backwardSlice(Node pCriterion) {
    // TODO Implement me
    if (pdg == null) {
      computeResult();
    }
    var set = new HashSet<Node>(pdg.getPredecessors(pCriterion));
    boolean changed;
    do {
      changed = false;
      for (var node : set) {
        var predecessors = pdg.getPredecessors(node);
        changed = set.addAll(predecessors);
      }
    } while (changed);
    return set;
  }
}

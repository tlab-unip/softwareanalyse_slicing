package de.uni_passau.fim.se2.sa.slicing.graph;

import de.uni_passau.fim.se2.sa.slicing.cfg.Node;
import de.uni_passau.fim.se2.sa.slicing.cfg.ProgramGraph;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/** Provides an analysis computing a post-dominator tree for a CFG. */
public class PostDominatorTree extends Graph {

  PostDominatorTree(ClassNode pClassNode, MethodNode pMethodNode) {
    super(pClassNode, pMethodNode);
  }

  PostDominatorTree(ProgramGraph pCFG) {
    super(pCFG);
  }

  /**
   * Computes the post-dominator tree of the method.
   *
   * <p>
   * The implementation uses the {@link #cfg} graph as the starting point.
   *
   * @return The post-dominator tree of the control-flow graph
   */
  @Override
  public ProgramGraph computeResult() {
    // TODO Implement me

    var graph = new ProgramGraph();
    // records post-dominators of each node
    var map = new LinkedHashMap<Node, Set<Node>>();
    for (var node : getCFG().getNodes()) {
      graph.addNode(node);
      if (getCFG().getSuccessors(node).size() != 0) {
        map.computeIfAbsent(node, k -> new LinkedHashSet<>(getCFG().getNodes()));
      } else {
        // for the exit node
        map.computeIfAbsent(node, k -> Set.of(k));
      }
    }

    // direct solution for finding post-dominators
    var wrapper = new Object() {
      boolean changed;
    };
    do {
      wrapper.changed = false;
      for (var node : getCFG().getNodes()) {
        var successors = getCFG().getSuccessors(node);
        successors.forEach(n -> {
          if (map.get(node).retainAll(map.get(n))) {
            wrapper.changed = true;
          }
        });
      }
    } while (wrapper.changed);

    // turn the map into a graph
    for (var entry : map.entrySet()) {
      for (var node : entry.getValue()) {
        graph.addEdge(entry.getKey(), node);
      }
    }
    return graph;
  }
}

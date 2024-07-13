package de.uni_passau.fim.se2.sa.slicing.graph;

import de.uni_passau.fim.se2.sa.slicing.cfg.Node;
import de.uni_passau.fim.se2.sa.slicing.cfg.ProgramGraph;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
    var exit = getCFG().getExit().get();
    // records post-dominators of each node
    var dominators = new LinkedHashMap<Node, Set<Node>>();
    dominators.put(exit, Set.of(exit));
    for (var node : getCFG().getNodes()) {
      graph.addNode(node);
      dominators.computeIfAbsent(node, k -> new LinkedHashSet<>(getCFG().getNodes()));
    }

    // direct solution for finding post-dominators
    boolean changed = true;
    while (changed) {
      changed = false;
      for (var node : getCFG().getNodes()) {
        if (node.equals(exit)) {
          continue;
        }
        var curDom = dominators.get(node);
        var newDom = new LinkedHashSet<Node>(getCFG().getNodes());
        var successors = getCFG().getSuccessors(node);
        successors.forEach(n -> newDom.retainAll(dominators.get(n)));
        newDom.add(node);
        if (curDom.size() != newDom.size() || !curDom.containsAll(newDom)) {
          dominators.put(node, newDom);
          changed = true;
        }
      }
    }

    // strict dominators
    for (var entry : dominators.entrySet()) {
      entry.getValue().remove(entry.getKey());
    }
    var queue = new LinkedList<Node>(List.of(exit));
    while (!queue.isEmpty()) {
      var current = queue.poll();
      for (var node : getCFG().getNodes()) {
        if (dominators.get(node).remove(current)) {
          if (dominators.get(node).isEmpty()) {
            graph.addEdge(current, node);
            queue.add(node);
          }
        }
      }
    }

    return graph;
  }
}

package de.uni_passau.fim.se2.sa.slicing.graph;

import br.usp.each.saeg.asm.defuse.Variable;
import de.uni_passau.fim.se2.sa.slicing.cfg.Node;
import de.uni_passau.fim.se2.sa.slicing.cfg.ProgramGraph;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class DataDependenceGraph extends Graph {

  public DataDependenceGraph(ClassNode pClassNode, MethodNode pMethodNode) {
    super(pClassNode, pMethodNode);
  }

  /**
   * Computes the data-dependence graph from the control-flow graph.
   *
   * <p>
   * This requires the computation of the reaching-definition algorithm. We
   * recommend using the
   * provided {@link DataFlowAnalysis} implementation.
   *
   * <p>
   * Remember that the CFG stores for each node the instruction at that node. With
   * that, calling
   * {@link DataFlowAnalysis#definedBy(String, MethodNode, AbstractInsnNode)}
   * provides a collection
   * of {@link Variable}s that are defined by this particular instruction; calling
   * {@link
   * DataFlowAnalysis#usedBy(String, MethodNode, AbstractInsnNode)} provides a
   * collection of {@link
   * Variable}s that are used by this particular instruction, respectively. From
   * this information
   * you can compute for each node n in the CFG the GEN[n] and KILL[n] sets.
   * Afterwards, it is
   * possible to compute the IN[n] and OUT[n] sets using the reaching-definitions
   * algorithm.
   *
   * <p>
   * Finally, you can compute all def-use pairs and construct the data-dependence
   * graph from
   * these pairs.
   *
   * @return The data-dependence graph for a control-flow graph
   */
  @Override
  public ProgramGraph computeResult() {
    // TODO Implement me
    try {
      var ddg = new ProgramGraph();
      var reachInMap = new LinkedHashMap<Node, Set<SimpleEntry<Variable, Node>>>();
      var reachOutMap = new LinkedHashMap<Node, Set<SimpleEntry<Variable, Node>>>();
      var genMap = new LinkedHashMap<Node, Set<SimpleEntry<Variable, Node>>>();

      for (var node : getCFG().getNodes()) {
        ddg.addNode(node);
        reachInMap.computeIfAbsent(node, k -> new LinkedHashSet<>());
        reachOutMap.computeIfAbsent(node, k -> new LinkedHashSet<>());
        genMap.computeIfAbsent(node, k -> new LinkedHashSet<>());

        if (node.getInstruction() != null) {
          var defs = DataFlowAnalysis.definedBy(classNode.name, methodNode, node.getInstruction());
          genMap.get(node).addAll(defs.stream().map(v -> new SimpleEntry<>(v, node)).toList());
        }
      }

      // reaching definitions
      boolean changed = true;
      while (changed) {
        changed = false;
        for (var node : getCFG().getNodes()) {
          var newIn = new LinkedHashSet<SimpleEntry<Variable, Node>>();
          var predecessors = getCFG().getPredecessors(node);
          predecessors.forEach(pred -> newIn.addAll(reachOutMap.get(pred)));
          if (!newIn.equals(reachInMap.get(node))) {
            reachInMap.put(node, newIn);
            changed = true;
          }

          var newOut = new LinkedHashSet<>(reachInMap.get(node));
          // remove overwritten vars
          newOut.removeIf(e -> {
            for (var defined : genMap.get(node)) {
              if (defined.getKey() == e.getKey()) {
                return true;
              }
            }
            return false;
          });
          newOut.addAll(genMap.get(node));
          if (!newOut.equals(reachOutMap.get(node))) {
            reachOutMap.put(node, newOut);
            changed = true;
          }
        }
      }

      // construct the graph
      for (var node : getCFG().getNodes()) {
        if (node.getInstruction() != null) {
          var uses = DataFlowAnalysis.usedBy(classNode.name, methodNode, node.getInstruction());
          for (var inFact : reachInMap.get(node)) {
            if (uses.contains(inFact.getKey())) {
              ddg.addEdge(inFact.getValue(), node);
            }
          }
        }
      }
      return ddg;
    } catch (Exception e) {
      throw new UnsupportedOperationException("Implement me", e);
    }
  }
}

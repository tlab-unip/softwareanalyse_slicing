package de.uni_passau.fim.se2.sa.slicing.graph;

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.Variable;
import java.util.Collection;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

/** Provides a simple data-flow analysis. */
class DataFlowAnalysis {

  private DataFlowAnalysis() {}

  /**
   * Provides the collection of {@link Variable}s that are used by the given instruction.
   *
   * @param pOwningClass The class that owns the method
   * @param pMethodNode The method that contains the instruction
   * @param pInstruction The instruction
   * @return The collection of {@link Variable}s that are used by the given instruction
   * @throws AnalyzerException In case an error occurs during the analysis
   */
  static Collection<Variable> usedBy(
      String pOwningClass, MethodNode pMethodNode, AbstractInsnNode pInstruction)
      throws AnalyzerException {
    DefUseFrame[] frames = getDefUseFrames(pOwningClass, pMethodNode);
    int index = getInstructionIndex(pMethodNode, pInstruction);

    return frames[index].getUses();
  }

  /**
   * Provides the collection of {@link Variable}s that are defined by the given instruction.
   *
   * @param pOwningClass The class that owns the method
   * @param pMethodNode The method that contains the instruction
   * @param pInstruction The instruction
   * @return The collection of {@link Variable}s that are defined by the given instruction
   * @throws AnalyzerException In case an error occurs during the analysis
   */
  static Collection<Variable> definedBy(
      String pOwningClass, MethodNode pMethodNode, AbstractInsnNode pInstruction)
      throws AnalyzerException {
    DefUseFrame[] frames = getDefUseFrames(pOwningClass, pMethodNode);
    int index = getInstructionIndex(pMethodNode, pInstruction);

    return frames[index].getDefinitions();
  }

  private static int getInstructionIndex(MethodNode pMethodNode, AbstractInsnNode pInstruction) {
    return pMethodNode.instructions.indexOf(pInstruction);
  }

  private static DefUseFrame[] getDefUseFrames(String pOwningClass, MethodNode pMethodNode)
      throws AnalyzerException {
    DefUseAnalyzer analyzer = new DefUseAnalyzer();
    analyzer.analyze(pOwningClass, pMethodNode);

    return analyzer.getDefUseFrames();
  }
}

package de.uni_passau.fim.se2.sa.slicing;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sa.slicing.cfg.*;
import de.uni_passau.fim.se2.sa.slicing.coverage.*;
import de.uni_passau.fim.se2.sa.slicing.graph.*;
import de.uni_passau.fim.se2.sa.slicing.instrumentation.*;

public class SlicerTest {

    @Test
    public void GraphTest() {
        var pdg = new ProgramDependenceGraph(null);
        pdg.computeResult();
        pdg.backwardSlice(null);
    }

    @Test
    public void CoverageTest() {
        CoverageTracker.trackLineVisit(0);
        CoverageTracker.getVisitedLines();
    }

    @Test
    public void InstrumentationTest() {
        var transformer = new LineCoverageTransformer(null);
        transformer.transform(null, null, getClass(), null, null);
    }

    @Test
    public void MainTest() {
        
    }
}

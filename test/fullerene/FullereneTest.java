package fullerene;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;
import org.openscience.cdk.graph.Matching;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import group.Partition;
import group.molecule.AtomEquitablePartitionRefiner;
import group.molecule.MoleculeRefinable;
import model.Graph;

public class FullereneTest {
    
    public static final String FULLERENE_DIR = "/Users/maclean/data/FullereneLib";
    
    @Test
    public void cubeneTest() {
        IAtomContainer cub2ene = makeCub2Ene();
        refineOld(cub2ene, org.openscience.cdk.group.Partition.unit(8));
        refineNew(cub2ene, Partition.unit(8));
    }
    
    @Test
    public void testNo1C2() throws IOException {
        IAtomContainer ac = read("C80", "No.1-D5d.cc1");
        assign(ac);
        refineOld(ac, colorZeroOld(ac.getAtomCount()));
        refineNew(ac, colorZero(ac.getAtomCount()));
    }
    
    private IAtomContainer read(String subdir, String name) throws IOException {
        File file = new File(FULLERENE_DIR, new File(subdir, name).toString());
        System.out.println(file.toString());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        int index = 0;
        IAtomContainer ac = new AtomContainer();
        Map<Integer, List<Integer>> bondMap = new HashMap<Integer, List<Integer>>();
        do {
            line = reader.readLine();
            if (line != null && line.length() > 5) {
                ac.addAtom(new Atom("C"));
                index++;
                String[] parts = line.split("\\s+");
                List<Integer> others = new ArrayList<Integer>();
                for (int i = 7; i < parts.length; i++) {
                    int other = Integer.parseInt(parts[i]); 
                    if (other > index) {
                        others.add(other);
                    }
                }
                if (others.size() > 0) {
                    bondMap.put(index, others);
                }
            }
        } while (line != null);
        for (int atomIndex : bondMap.keySet()) {
            for (int otherIndex : bondMap.get(atomIndex)) {
                ac.addBond(atomIndex - 1, otherIndex - 1, IBond.Order.SINGLE);
            }
        }
        reader.close();
        return ac;
    }
    
    public static Graph make26Fullerene() {
        return new Graph(
                "C0C1C2C3C4C5C6C7C8C9C10C11C12C13C14C15C16C17C18C19C20C21C22C23C24C25 0:1(1),0:4(1),0:5(1)," +
                "1:2(1),1:7(1),2:3(1),2:9(1),3:4(1),3:12(1),4:14(1),5:6(1),5:15(1),6:7(1),6:17(1)," +
                "7:8(1),8:9(1),8:19(1),9:10(1),10:11(1),10:20(1),11:12(1),11:22(1),12:13(1),13:14(1),13:23(1)," +
                "14:15(1),15:16(1),16:17(1),16:24(1),17:18(1),18:19(1),18:25(1),19:20(1),20:21(1),21:22(1)," +
                "21:25(1),22:23(1),23:24(1),24:25(1)"
        );
    }
    
    private static IAtomContainer makeCub2Ene() {
        return io.AtomContainerPrinter.fromString(
                "C0C1C2C3C4C5C6C7 0:1(1),0:2(2),0:4(1),1:3(1),1:5(1),2:3(1),"
                + "2:6(1),3:7(1),4:5(1),4:6(1),5:7(2),6:7(1)", 
                DefaultChemObjectBuilder.getInstance());
    }

    
    private IAtomContainer make() {
        Graph g = make26Fullerene();
        return io.AtomContainerPrinter.fromString(
                g.toString(), DefaultChemObjectBuilder.getInstance());
    }
    
    private void assign(IAtomContainer ac) {
        io.AtomContainerPrinter.print(ac);
        Matching matching = Matching.withCapacity(ac.getAtomCount());
        final EdgeToBondMap bonds = EdgeToBondMap.withSpaceFor(ac);
        final int[][] graph = GraphUtil.toAdjList(ac, bonds);
        BitSet all = all(ac.getAtomCount());
        matching.perfect(graph, all);
        System.out.println(matching);
        for (int v = 0; v < ac.getAtomCount(); v++) {
            if (matching.matched(v)) {
                int u = matching.other(v);
//                if (v < u) {
                    ac.getBond(ac.getAtom(v), ac.getAtom(u)).setOrder(IBond.Order.DOUBLE);
//                }
            }
        }
    }
    
    private void refineNew(IAtomContainer ac, Partition coarse) {
        AtomEquitablePartitionRefiner refiner = 
                new AtomEquitablePartitionRefiner(new MoleculeRefinable(ac));
        Partition fine = refiner.refine(coarse);
        System.out.println(fine);
    }
    
    private void refineOld(IAtomContainer ac, org.openscience.cdk.group.Partition coarse) {
        System.exit(0); // TODO - commented below out, as deprecated in 1.5.15
//        org.openscience.cdk.group.AtomDiscretePartitionRefiner parent
//            = new org.openscience.cdk.group.AtomDiscretePartitionRefiner();
//        parent.getAutomorphismGroup(ac);
//        org.openscience.cdk.group.AtomEquitablePartitionRefiner refiner =
//                new org.openscience.cdk.group.AtomEquitablePartitionRefiner(parent);
//        System.out.println(refiner.refine(coarse));
    }
    
    private org.openscience.cdk.group.Partition colorZeroOld(int n) {
        org.openscience.cdk.group.Partition p = new org.openscience.cdk.group.Partition();
        p.addCell(0);
        int[] cell = new int[n - 1];
        for (int i = 1; i < n; i++) {
            cell[i - 1] = i;
        }
        p.addCell(cell);
        return p;
    }
    
    private Partition colorZero(int n) {
        Partition p = new Partition();
        p.addCell(0);
        int[] cell = new int[n - 1];
        for (int i = 1; i < n; i++) {
            cell[i - 1] = i;
        }
        p.addCell(cell);
        return p;
    }
    
    @Test
    public void fullerene26() {
        IAtomContainer ac = make();
        assign(ac);
        io.AtomContainerPrinter.print(ac);
        refineNew(ac, colorZero(ac.getAtomCount()));
        refineOld(ac, colorZeroOld(ac.getAtomCount()));
    }
    
    private BitSet all(int n) {
        BitSet all = new BitSet(n);
        all.flip(0, n);
        return all;
    }

}

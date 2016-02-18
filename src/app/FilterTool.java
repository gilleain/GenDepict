package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.smsd.Isomorphism;
import org.openscience.cdk.smsd.interfaces.Algorithm;

import io.AtomContainerPrinter;
import io.IteratingACPReader;

public class FilterTool {
    
    public static void look(String toLookFor, File toLookIn) throws IOException, CDKException {
        System.out.println("looking for " + toLookFor + " in " + toLookIn);
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance(); 
        IAtomContainer query = AtomContainerPrinter.fromString(toLookFor, builder);
        IteratingACPReader reader = new IteratingACPReader(new FileInputStream(toLookIn), builder);
        Isomorphism isomorphism = new Isomorphism(Algorithm.TurboSubStructure, true);
        while (reader.hasNext()) {
            IAtomContainer target = reader.next();
            isomorphism.init(query, target, true, true);
            if (isomorphism.isSubgraph()) {
                System.out.println(AtomContainerPrinter.toString(target));
            }
        }
        reader.close();
    }

    public static void main(String[] args) throws IOException, CDKException {
        String toFind = args[0];
        String toLook = args[1];
        look(toFind, new File(toLook));
    }
}

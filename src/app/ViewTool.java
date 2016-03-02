package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.atomtype.IAtomTypeMatcher;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import io.IteratingACPReader;

public class ViewTool {
    
    public static void draw(String inputFilename, String outputFilename) throws IOException {
        List<IAtomContainer> containers = new ArrayList<IAtomContainer>();
        IteratingACPReader reader = new IteratingACPReader(
                new FileInputStream(inputFilename), DefaultChemObjectBuilder.getInstance());
        
        
        while (reader.hasNext()) {
            IAtomContainer container = reader.next();
            try {
                IAtomTypeMatcher matcher = getMatcher();
                for (IAtom atom : container.atoms()) {
                    IAtomType type = matcher.findMatchingAtomType(container, atom);
                    AtomTypeManipulator.configure(atom, type);
                }
                CDKHydrogenAdder.getInstance(
                        SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(container);
                System.out.println(SmilesGenerator.unique().create(container));
            } catch (CDKException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            containers.add(container);
        }
        reader.close();
        drawOut(containers, new File(outputFilename));
    }
    
    private static IAtomTypeMatcher getMatcher() {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        return CDKAtomTypeMatcher.getInstance(builder);
    }
    
    private static void drawOut(List<IAtomContainer> containers, File outputFile) {
        DepictionGenerator depictionGenerator = 
                new DepictionGenerator().withTerminalCarbons();
        try {
            depictionGenerator.depict(containers).writeTo("PNG", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CDKException e) {
            e.printStackTrace();
        }
    }
     
    public static void main(String[] args) throws IOException {
        String inputFilename = args[0];
        String outputFilename = args[1];
        draw(inputFilename, outputFilename);
    }

}

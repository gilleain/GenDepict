package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import io.AtomContainerPrinter;
import io.IteratingACPReader;



public class DuplicateChecker {

    public static IChemObjectBuilder getBuilder() {
        return SilentChemObjectBuilder.getInstance();
    }
    
    private static Map<String, List<IAtomContainer>> getFromReader(IteratingACPReader reader) {
        Map<String, List<IAtomContainer>> duplicateMap = 
                new HashMap<String, List<IAtomContainer>>();

        // read in from the file, and add to the map
        while (reader.hasNext()) {
            IAtomContainer atomContainer = reader.next();
            String signature = new MoleculeSignature(atomContainer).toCanonicalString();
            List<IAtomContainer> duplicates; 
            if (duplicateMap.containsKey(signature)) {
                duplicates = duplicateMap.get(signature);
            } else {
                duplicates = new ArrayList<IAtomContainer>();
                duplicateMap.put(signature, duplicates);
            }
            duplicates.add(atomContainer);
        }
        return duplicateMap;
    }
    
    private static void printDups(Map<String, List<IAtomContainer>> duplicateMap) {
        int count = 0;
        for (String signature : duplicateMap.keySet()) {
            List<IAtomContainer> bin = duplicateMap.get(signature);
            if (bin.size() > 1) {
                System.out.println("Bin : " + count + " Size " + bin.size());
                for (IAtomContainer atomContainer : bin) {
                    System.out.println(AtomContainerPrinter.toString(atomContainer));
                }
                System.out.println("-----------------");
                count++;
            }
        }
    }
    
    private static void drawDups(Map<String, List<IAtomContainer>> duplicateMap, File outputDir) {
        int count = 0;
        for (String signature : duplicateMap.keySet()) {
            List<IAtomContainer> bin = duplicateMap.get(signature);
            if (bin.size() > 1) {
                File outputFile = new File(outputDir, "bin_" + count + ".png");
                drawBin(bin, outputFile);
                count++;
            }
        }
    }
    
    private static void drawBin(List<IAtomContainer> bin, File outputFile) {
        DepictionGenerator depictionGenerator = 
                new DepictionGenerator().withAtomNumbers();
        try {
            depictionGenerator.depict(bin).writeTo("PNG", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CDKException e) {
            e.printStackTrace();
        }
    }
    

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length < 1) return;   // TODO
        
        String filename = args[0];
        String flag = "-p";
        if (args.length > 1) flag = args[1];
        String outputDir = ".";
        if (args.length > 2) outputDir = args[2];
        
        InputStream in = new FileInputStream(filename);
        IteratingACPReader reader = new IteratingACPReader(in, getBuilder());
        
        // map signature strings to lists of atom containers
        Map<String, List<IAtomContainer>> duplicateMap = getFromReader(reader);
        
        // now print out the duplicates
        if (flag.equals("-p")) {
            printDups(duplicateMap);
        } else {
            drawDups(duplicateMap, new File(outputDir));
        }
        
        reader.close();
    }

}

package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import io.IteratingACPReader;

public class ViewTool {
    
    public static void draw(String inputFilename, String outputFilename) throws IOException {
        List<IAtomContainer> containers = new ArrayList<IAtomContainer>();
        IteratingACPReader reader = new IteratingACPReader(
                new FileInputStream(inputFilename), DefaultChemObjectBuilder.getInstance());
        
        while (reader.hasNext()) {
            IAtomContainer container = reader.next();
            containers.add(container);
        }
        reader.close();
        drawOut(containers, new File(outputFilename));
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

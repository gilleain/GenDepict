package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.smsd.Isomorphism;
import org.openscience.cdk.smsd.interfaces.Algorithm;

import io.AtomContainerPrinter;
import io.IteratingACPReader;

public class FilterTool {
    
    private interface Matcher {
        public boolean matches(IAtomContainer target);
    }
    
    private class SMSDMatcher implements Matcher {
        
        Isomorphism isomorphism;
        
        IAtomContainer query;
        
        public SMSDMatcher(IAtomContainer query) {
            this.query = query;
        }

        @Override
        public boolean matches(IAtomContainer target) {
            isomorphism = new Isomorphism(Algorithm.MCSPlus, true);
            try {
                isomorphism.init(query, target, true, true);
            } catch (CDKException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return isomorphism.isSubgraph();
        }
    }
    
    private class SignatureMatcher implements Matcher {
        String queryString;
        
        public SignatureMatcher(IAtomContainer query) {
            this.queryString = new MoleculeSignature(query).toCanonicalString();
        }

        @Override
        public boolean matches(IAtomContainer target) {
            MoleculeSignature targetSig = new MoleculeSignature(target);
            return queryString.equals(targetSig.toCanonicalString());
        }
        
        
    }
    
    public void look(String flag, String toLookFor, File toLookIn) throws IOException {
        System.out.println("looking for " + toLookFor + " in " + toLookIn);
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance(); 
        IAtomContainer query = AtomContainerPrinter.fromString(toLookFor, builder);
        
        Matcher matcher;
        if (flag.equals("-i")) {
            matcher = new SignatureMatcher(query);
        } else if (flag.equals("-s")) {
            matcher = new SMSDMatcher(query);
        } else {
            System.err.println("Flag must be -i (isomorphism) or -s (subgraph)");
            return;
        }
        
        IteratingACPReader reader = new IteratingACPReader(new FileInputStream(toLookIn), builder);
        while (reader.hasNext()) {
            IAtomContainer target = reader.next();
            if (matcher.matches(target)) {
                System.out.println(AtomContainerPrinter.toString(target));
            }
        }
        reader.close();
    }

    public static void main(String[] args) throws IOException, CDKException {
        String flag = args[0];
        String toFind = args[1];
        String toLook = args[2];
        new FilterTool().look(flag, toFind, new File(toLook));
    }
}

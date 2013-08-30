package uk.ac.ebi.ricordo.rdfconverter.reactometordf;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 28/01/13
 *         Time: 15:48
 */
public class ReactometoRDFGeneratorImpl implements ReactometoRDFGenerator {
    Logger logger = Logger.getLogger(ReactometoRDFGeneratorImpl.class);
    private File inputFile;
    private String inputFolder;
    private ReactomeModifier reactomeModifier;

    public ReactometoRDFGeneratorImpl(ReactomeModifier reactomeModifier){
        this.reactomeModifier = reactomeModifier;
    }

    public void aModeltoRDF() {
        logger.info("Converting to RDF: " + inputFile);
        reactomeModifier.parseReactomeModel(inputFile);
    }

    public void allModelstoRDF() {
        File folder = new File(inputFolder);
        if(folder.isDirectory()){
            ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
            for(File file:files){
                logger.info("Converting to RDF: " + file);
                reactomeModifier.parseReactomeModel(file);

            }
        }else{
            logger.info("Path not found: " + inputFolder);
        }


    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public void setInputFolder(String inputFolder) {
        this.inputFolder = inputFolder;
    }
}

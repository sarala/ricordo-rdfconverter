package uk.ac.ebi.ricordo.rdfconverter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.ricordo.rdfconverter.sbmltordf.SBMLtoRDFGenerator;
import uk.ac.ebi.ricordo.rdfconverter.sbmltordf.SBMLtoRDFGeneratorImpl;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 14/04/2014
 *         Time: 09:36
 */
public class SBMLtoRDF {

    public static void main (String []args){
        if(args.length == 0){
            printHelp();
        }
        else{
            try {
                String inputFolderPath = null;
                if (args.length > 1) {
                    inputFolderPath = args[1];
                }
                runSBMLtoRDF(Integer.parseInt(args[0]), inputFolderPath);
            } catch (NumberFormatException e) {
                System.err.println("Argument '" + args[0] + "' must be an integer.");
                printHelp();
                System.exit(1);
            }
        }
    }

    public static void runSBMLtoRDF(int option, String path) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:sbml-config.xml");
        SBMLtoRDFGenerator sbmlToRdfGenerator = (SBMLtoRDFGeneratorImpl)ctx.getBean("sbmlToRdfGeneratorImpl");

        switch (option){
            case 1:
                sbmlToRdfGenerator.bioModelsReleaseSetUp("(BIOMD|MODEL)\\d{10}"+".xml", false);
                break;
            case 2:
                sbmlToRdfGenerator.bioModelsReleaseSetUp("(BIOMD|MODEL)\\d{10}"+".xml", true);
                break;
            case 3:
                if (null != path && !path.trim().isEmpty()) {
                    sbmlToRdfGenerator.allBioModelsFromFolderToRDF(path);
                } else {
                    sbmlToRdfGenerator.allBioModelsFromFolderToRDF();
                }
                break;
        }
    }

    public static void printHelp(){
        System.out.println("Please enter a valid option number. Folder paths need to set up in sbml-config.properties");
        System.out.println("[1] - BioModels release set up");
        System.out.println("[2] - BioModels release set up as a batch");
        System.out.println("[3] - Generate RDF from a folder. If a path is provided, " +
                "it becomes the default input path");
    }
}

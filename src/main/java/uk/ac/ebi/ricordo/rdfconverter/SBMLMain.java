package uk.ac.ebi.ricordo.rdfconverter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.ricordo.rdfconverter.sbmltordf.SBMLtoRDFGenerator;
import uk.ac.ebi.ricordo.rdfconverter.sbmltordf.SBMLtoRDFGeneratorImpl;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 23/01/13
 *         Time: 13:43
 */
public class SBMLMain {

    public static void main (String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:sbml-config.xml");
        SBMLtoRDFGenerator sbmlToRdfGenerator = (SBMLtoRDFGeneratorImpl)ctx.getBean("sbmlToRdfGeneratorImpl");
//        sbmlToRdfGenerator.allNonCuratedModelsFromBioModelsDBToRDF();
//        sbmlToRdfGenerator.allModelsFromBioModelsDBToRDF();
//          sbmlToRdfGenerator.aModelFromBioModelsDBToRDF("BIOMD0000000063");
//        sbmlToRdfGenerator.allBioModelsFromFolderToRDF("/nfs/research2/lenovere/WWW/biomodels/models-main/release/release_20121211/sbml_files/curated/");
//        sbmlToRdfGenerator.aBioModelFromFileToRDF("C:\\Users\\sarala.EBI\\Documents\\GitHub\\ricordo-rdfconverter\\resources\\sbmlxml\\MODEL1107050000.xml");
//        sbmlToRdfGenerator.bioModelsReleaseSetUp("C:\\Users\\sarala.EBI\\Documents\\GitHub\\ricordo-rdfconverter\\resources\\sbmlfoldertest\\", "(BIOMD|MODEL)\\d{10}"+".xml");
        sbmlToRdfGenerator.pathToModelSetUp("/nfs/production/biomodels/biomodels/r24/metabolic/aaa/", "(BIOMD|MODEL|BMID)\\d{12}"+"_url.xml");


    }
}

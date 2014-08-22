package uk.ac.ebi.ricordo.rdfconverter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.ricordo.rdfconverter.provenance.ProvenanceGenerator;
import uk.ac.ebi.ricordo.rdfconverter.provenance.ProvenanceGeneratorImpl;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 23/01/13
 *         Time: 13:44
 */
public class ProvenanceMain {

    public static void main (String [] args){
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:provenance-config.xml");
        ProvenanceGenerator provenanceGenerator = (ProvenanceGeneratorImpl)ctx.getBean("provenanceGenerator");
        provenanceGenerator.generateProvenace();
    }
}

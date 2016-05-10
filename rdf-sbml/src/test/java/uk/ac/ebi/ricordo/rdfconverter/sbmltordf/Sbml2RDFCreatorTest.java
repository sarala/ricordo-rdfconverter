package uk.ac.ebi.ricordo.rdfconverter.sbmltordf;


import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for SbmlToRdfCreator.
 *
 * @author Mihai Glonț
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:sbml-config.xml")
public class Sbml2RDFCreatorTest {
    @Autowired
    private SBMLtoRDFCreator sbmlToRdf;

    @Test
    @Ignore // OOME from exponential space needed to copy listener lists every time a new child is added to an ast node.
    public void MODEL1112110002JSBML_OOME() {
        final File model = new File("src/test/resources/MODEL1112110002.xml");
        assertTrue("the model file does not exist", model.exists() && model.canRead());
        SBMLReader r = new SBMLReader();
        try {
            SBMLDocument doc = r.readSBML(model);

            assertNotNull("the SBML document should not be null", doc);
        } catch (XMLStreamException | IOException | OutOfMemoryError e) {
            fail("could not read SBML model because of " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void blacklistMODEL1112110002() {
        final File model = new File("src/test/resources/MODEL1112110002.xml");
        assertTrue("the model file does not exist", model.exists() && model.canRead());
        sbmlToRdf.generateSBMLtoRDFFromFile("MODEL1112110002", model);
    }
}

/*
 * Copyright 2012 EMBL-EBI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.ricordo.rdfconverter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.ricordo.rdfconverter.sbmltordf.SBMLtoRDFGeneratorImpl;
import uk.ac.ebi.ricordo.rdfconverter.tordf.ProvenanceGenerator;
import uk.ac.ebi.ricordo.rdfconverter.tordf.ProvenanceGeneratorImpl;
import uk.ac.ebi.ricordo.rdfconverter.tordf.RDFGenerator;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 19/07/12
 *         Time: 16:48
 */
public class Main {

    public static void main (String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring-config.xml");
        RDFGenerator sbmlToRdfGenerator = (SBMLtoRDFGeneratorImpl)ctx.getBean("sbmlToRdfGeneratorImpl");
//        sbmlToRdfGenerator.allModelsFromBioModelsDBToRDF();
//          sbmlToRdfGenerator.aModelFromBioModelsDBToRDF("BIOMD0000000001");
//        sbmlToRdfGenerator.allBioModelsFromFolderToRDF("C:\\Users\\sarala.EBI\\Documents\\GitHub\\ricordo-rdfconverter\\resources\\sbmlxml\\");
//        sbmlToRdfGenerator.aBioModelFromFileToRDF("C:\\Users\\sarala.EBI\\Documents\\GitHub\\ricordo-rdfconverter\\resources\\sbmlxml\\BIOMD0000000239.xml");
//        sbmlToRdfGenerator.bioModelsReleaseSetUp("C:\\Users\\sarala.EBI\\Documents\\GitHub\\ricordo-rdfconverter\\resources\\sbmlfoldertest\\", "(BIOMD|MODEL)\\d{10}"+".xml");

//        ProvenanceGenerator provenanceGenerator = (ProvenanceGeneratorImpl)ctx.getBean("provenanceGenerator");
//        provenanceGenerator.generateProvenace();

    }
}

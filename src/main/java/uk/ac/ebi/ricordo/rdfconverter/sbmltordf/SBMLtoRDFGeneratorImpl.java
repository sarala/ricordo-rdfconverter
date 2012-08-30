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

package uk.ac.ebi.ricordo.rdfconverter.sbmltordf;

import org.apache.log4j.Logger;
import uk.ac.ebi.biomodels.ws.BioModelsWSClient;
import uk.ac.ebi.biomodels.ws.BioModelsWSException;
import uk.ac.ebi.ricordo.rdfconverter.tordf.RDFGenerator;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 10-Feb-2012
 *         Time: 14:57:03
 */
public class SBMLtoRDFGeneratorImpl implements RDFGenerator {

    private final BioModelsWSClient client;
    private final SBMLtoRDFCreatorImpl sbmLtoRDFGenerator;
    Logger logger = Logger.getLogger(SBMLtoRDFGeneratorImpl.class);

    public SBMLtoRDFGeneratorImpl(BioModelsWSClient client, SBMLtoRDFCreatorImpl sbmLtoRDFGenerator) {
        this.client = client;
        this.sbmLtoRDFGenerator = sbmLtoRDFGenerator;
    }

    public void allModelsToRDF(){
        try {
            for(String modelId : client.getAllCuratedModelsId()){
                logger.info("Converting to RDF: " + modelId);
                sbmLtoRDFGenerator.generateSBMLtoRDF(modelId);
            }

        } catch (BioModelsWSException e) {
            e.printStackTrace();
        }
    }

    public void aModelToRDF(String modelId){
        logger.info("Converting to RDF: " + modelId);
        sbmLtoRDFGenerator.generateSBMLtoRDF(modelId);
    }



}

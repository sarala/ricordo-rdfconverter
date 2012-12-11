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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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

    public void allModelsFromBioModelsDBToRDF(){
        try {
            for(String modelId : client.getAllCuratedModelsId()){
                logger.info("Converting to RDF: " + modelId);
                sbmLtoRDFGenerator.generateSBMLtoRDFFromURL(modelId);
            }

        } catch (BioModelsWSException e) {
            e.printStackTrace();
        }
    }

    public void allNonCuratedModelsFromBioModelsDBToRDF(){
        try {
            for(String modelId : client.getAllNonCuratedModelsId()){
                logger.info("Converting to RDF: " + modelId);
                sbmLtoRDFGenerator.generateSBMLtoRDFFromURL(modelId);
            }

        } catch (BioModelsWSException e) {
            e.printStackTrace();
        }
    }

    public void aModelFromBioModelsDBToRDF(String modelId){
        logger.info("Converting to RDF: " + modelId);
        sbmLtoRDFGenerator.generateSBMLtoRDFFromURL(modelId);
    }

    public void allBioModelsFromFolderToRDF(String folderPath) {
        File folder = new File(folderPath);
        if(folder.isDirectory()){
            ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
            for(File file:files){
                String modelId = file.getName().substring(0,file.getName().indexOf("."));
                logger.info("Converting to RDF: " + modelId);
                sbmLtoRDFGenerator.generateSBMLtoRDFFromFile(modelId, file);
            }
        }else{
            logger.info("Path not found: " + folderPath);
        }

    }

    public void aBioModelFromFileToRDF(String filePath) {
        File file = new File(filePath);
        if(file.exists()){
            String modelId = file.getName().substring(0,file.getName().indexOf("."));
            logger.info("Converting to RDF: " + modelId);
            sbmLtoRDFGenerator.generateSBMLtoRDFFromFile(modelId, file);
        }else{
            logger.info("Path not found: " + filePath);
        }
    }

    public void bioModelsReleaseSetUp(String folderPath, String fileNamePattern, boolean batch) {
        File folder = new File(folderPath);
        String [] contentlist = folder.list();
        for(int i =0; i<contentlist.length; i++){
            File content = new File(folder.getPath()+File.separator+contentlist[i]);
            if(content.isDirectory()){
                bioModelsReleaseSetUp(content.getPath(), fileNamePattern, batch);
            }
            else if (content.isFile()){
                if(content.getName().matches(fileNamePattern)){
                    logger.info("Converting to RDF: " + folder.getName());
                    if(!batch)
                        sbmLtoRDFGenerator.setOutputFolder(folder.getPath() + File.separator);
                    sbmLtoRDFGenerator.generateSBMLtoRDFFromFile(folder.getName(), content);
                }
            }
        }
    }
}

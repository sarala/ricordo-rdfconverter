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

import java.io.File;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 10-Feb-2012
 *         Time: 14:57:03
 */
public class SBMLtoRDFGeneratorImpl implements SBMLtoRDFGenerator {
    private final BioModelsWSClient client;
    private final SBMLtoRDFCreatorImpl sbmLtoRDFGenerator;
    Logger logger = Logger.getLogger(SBMLtoRDFGeneratorImpl.class);
    private final boolean IS_INFO_ENABLED = logger.isInfoEnabled();

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

    public void allBioModelsFromFolderToRDF(){
        allBioModelsFromFolderToRDF(sbmLtoRDFGenerator.getInputFolder());
    }

    private void allBioModelsFromFolderToRDF(String folderPath) {
        File folder = new File(folderPath);
        if(folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files.length == 0) {
                logger.info("No SBML/XML files found in this folder " + folderPath);
                return;
            }
            if (IS_INFO_ENABLED) {
                String msg = String.format("%s\t%s %4d %s.", Calendar.getInstance().getTime().
                        toString(), "Started processing", files.length, "models.");
                logger.info(msg);
            }
            for(final File file: files) {
                if(file.isFile()) {
                    final String modelId = file.getName().split("\\.")[0];
                    if (IS_INFO_ENABLED) {
                        logger.info("Converting to RDF: " + modelId);
                    }
                    sbmLtoRDFGenerator.generateSBMLtoRDFFromFile(modelId, file);
                    if (IS_INFO_ENABLED) {
                        logger.info(String.format("...Completed %s", modelId));
                    }
                }
            }
            if (IS_INFO_ENABLED) {
                String msg = String.format("%s\t%s.",
                        Calendar.getInstance().getTime().toString(), "Finished");
                logger.info(msg);
            }
        } else {
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

    /* Generating RDF files for BioModels release
    *  batch (true) will write the output into the folder specified in the sbml-config file
    * */
    public void bioModelsReleaseSetUp(String fileNamePattern, boolean batch) {
        bioModelsReleaseSetUp(sbmLtoRDFGenerator.getInputFolder(),fileNamePattern,batch);
    }

     private void bioModelsReleaseSetUp(String folderPath, String fileNamePattern, boolean batch) {
        File folder = new File(folderPath);
        File[] contentlist = folder.listFiles();
        for(File content: contentlist){
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

    public void pathToModelSetUp(String folderPath, String fileNamePattern) {
        File folder = new File(folderPath);
        File[] contentlist = folder.listFiles();
        for(File f: contentlist){
            if(f.isDirectory()){
                pathToModelSetUp(f.getPath(), fileNamePattern);
            }
            else if (f.isFile()){
                if(f.getName().matches(fileNamePattern)){
                    logger.info("Converting to RDF: " + folder.getName());
                    String outputFolder = sbmLtoRDFGenerator.getOutputFolder();
                    String tempOutputFolder = outputFolder + f.getParentFile().getParentFile().
                            getName() + File.separator;
                    new File(tempOutputFolder).mkdir();
                    sbmLtoRDFGenerator.setOutputFolder(tempOutputFolder);
                    sbmLtoRDFGenerator.generateSBMLtoRDFFromFile(folder.getName(), f);
                    sbmLtoRDFGenerator.setOutputFolder(outputFolder);
                }
            }
        }
    }
}

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

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 19/07/12
 *         Time: 17:34
 */
public interface SBMLtoRDFGenerator {
    public void allModelsFromBioModelsDBToRDF();
    public void allNonCuratedModelsFromBioModelsDBToRDF();
    public void aModelFromBioModelsDBToRDF(String modelId);
    public void allBioModelsFromFolderToRDF();
    public void allBioModelsFromFolderToRDF(String path);
    public void aBioModelFromFileToRDF(String filePath);
    public void bioModelsReleaseSetUp(String fileNamePattern, boolean batch);
    public void pathToModelSetUp(String folderPath, String fileNamePattern);
}

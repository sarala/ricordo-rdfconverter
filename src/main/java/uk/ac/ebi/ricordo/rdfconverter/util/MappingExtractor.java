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

package uk.ac.ebi.ricordo.rdfconverter.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 16/11/12
 *         Time: 20:40
 */
public class MappingExtractor {
    private HashMap<String,String> identMap = new HashMap<String, String>();

    public MappingExtractor(String mappingFile) {
        populateIdentMap(mappingFile);
    }

    private void populateIdentMap(String mappingFile) {
        try {
            Resource resource = new ClassPathResource(mappingFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                int splitPoint=line.lastIndexOf(" ");
                //String [] stringMap = line.split("|");
                identMap.put(line.substring(0,splitPoint), line.substring(splitPoint+1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getIdentMap() {
        return identMap;
    }
}

/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.flex.compiler.internal.codegen.js.node;

import org.apache.flex.compiler.config.Configuration;
import org.apache.flex.compiler.internal.codegen.js.jsc.JSCPublisher;
import org.apache.flex.compiler.internal.projects.FlexJSProject;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class NodePublisher extends JSCPublisher
{
    public NodePublisher(Configuration config, FlexJSProject project)
    {
        super(config, project);
    }

    @Override
    protected void writeHTML(String type, String projectName, String dirPath,
                             String deps, List<String> additionalHTML) throws IOException
    {
        StringBuilder contents = new StringBuilder();
        if ("intermediate".equals(type))
        {
            contents.append("require(\"./library/closure/goog/bootstrap/nodejs\");\n");
            contents.append(deps);
            contents.append("goog.require(\"");
            contents.append(projectName);
            contents.append("\");\n");
            contents.append("new " + projectName + "();");
        }
        else 
        {
            contents.append("new ");
            contents.append("require(\"./");
            contents.append(projectName);
            contents.append("\")");
            contents.append(".");
            contents.append(projectName);
            contents.append("();");
        }
        writeFile(dirPath + File.separator + "index.js", contents.toString(), false);
    }
}

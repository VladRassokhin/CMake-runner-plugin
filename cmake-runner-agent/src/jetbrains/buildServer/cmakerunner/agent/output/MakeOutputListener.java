/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.cmakerunner.agent.output;

import jetbrains.buildServer.cmakerunner.agent.util.PathUtil;
import jetbrains.buildServer.cmakerunner.regexparser.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Vladislav.Rassokhin
 */
public class MakeOutputListener extends RegexParsersBasedOutputListener {

  @Nullable
  private final AtomicReference<File> myCustomPatternsFile;

  public MakeOutputListener(@NotNull final Logger logger,
                            @NotNull final AtomicReference<List<String>> makeTasks,
                            @Nullable final AtomicReference<File> customPatternsFile,
                            final boolean defaultParsersEnabled) {
    super(new MakeParserManager(logger, makeTasks), defaultParsersEnabled ? new String[]{"/make-parser.xml"} : new String[0]);
    myCustomPatternsFile = customPatternsFile;
  }

  @Override
  public void processStarted(@NotNull final String programCommandLine, @NotNull final File workingDirectory) {
    if (myCustomPatternsFile != null && myCustomPatternsFile.get() != null) {
      addParserFromFile(myCustomPatternsFile.get());
    }
    ((MakeParserManager) myManager).setWorkingDirectory(PathUtil.toUnixStylePath(workingDirectory.getAbsolutePath()));
  }

  @Override
  public void processFinished(final int exitCode) {
    ((MakeParserManager) myManager).finishAllTargets();
  }
}

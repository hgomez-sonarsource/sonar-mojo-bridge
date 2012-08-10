/*
 * Sonar maven checks plugin (maven3 support)
 * Copyright (C) 2012 Lars Gohlke
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package de.lgohlke.sonar.maven;

import com.google.common.collect.ImmutableMap;
import de.lgohlke.sonar.maven.plugin.BridgeMojo;
import de.lgohlke.sonar.maven.plugin.ResultTransferHandler;
import de.lgohlke.sonar.maven.plugin.versions.BridgeMojoMapper;

import java.io.File;
import java.util.Map;

public class Maven2ExecutionProcessTest {
  private static final String MAVEN_HOME_KEY = "maven.home";
  private static final String M2_HOME_KEY = "M2_HOME";
  // public static final File MAVEN_HOME = new File("/data/home/lgohlke/development/tools/apache-maven-3.0.4");
  public static final File MAVEN_HOME = new File("/home/lars/development/tools/apache-maven-3.0.4");
  final String SUB_GOAL = "help";
  final String GOAL = "versions:" + SUB_GOAL;
  private MyResultTransferHandler handler;
  private Maven2SonarEmbedder embedder;

  //  @BeforeClass
  //  protected void setUp() throws Exception {
  //    System.setProperty(M2_HOME_KEY, "wrong");
  //    System.setProperty(MAVEN_HOME_KEY, "wrong");
  //
  //    embedder = Maven2SonarEmbedder.configure().
  //        usePomFile("pom.xml").
  //        goal(GOAL).
  //        setAlternativeMavenHome(MAVEN_HOME).
  //        build();
  //    MavenSession mavenSession = field("embedder.mavenSession").ofType(MavenSession.class).in(embedder).get();
  //
  //    Maven3PluginExecutor mavenPluginExecutor = new Maven3PluginExecutor(null, mavenSession);
  //    ClassLoader classLoader = this.getClass().getClassLoader();
  //    BridgeMojoMapper bridgeMojoMapper = new MyBridgeMojoMapper();
  //
  //    handler = (MyResultTransferHandler) bridgeMojoMapper.getGoalToTransferHandlerMap().get(SUB_GOAL);
  //
  //    Maven2ExecutionProcess.decorate(mavenPluginExecutor, classLoader, bridgeMojoMapper);
  //  }

  class MyResultTransferHandler implements ResultTransferHandler<MyResultTransferHandler> {

    private boolean ping;

    public boolean isPing() {
      return ping;
    }

    public void setPing(final boolean ping) {
      this.ping = ping;
    }
  }

  class MyBridgeMojoMapper extends BridgeMojoMapper
  {
    private final Map<String, ResultTransferHandler<?>> map = ImmutableMap.<String, ResultTransferHandler<?>>
        builder().
        put(SUB_GOAL, new MyResultTransferHandler()).
        build();

    @Override
    public Map<String, ResultTransferHandler<?>> getGoalToTransferHandlerMap() {
      return map;
    }

    @Override
    public Map<String, Class<? extends BridgeMojo<?>>> getGoalToBridgeMojoMap() {
      return ImmutableMap.<String, Class<? extends BridgeMojo<?>>>
          builder().
          put(SUB_GOAL, MyBridgeMojo.class).
          build();
    }
  }

  //  @Test
  //  public void shouldDecorate() throws MavenEmbedderException, ClassNotFoundException {
  //    embedder.run();
  //
  //    assertThat(handler.isPing()).isTrue();
  //  }
}
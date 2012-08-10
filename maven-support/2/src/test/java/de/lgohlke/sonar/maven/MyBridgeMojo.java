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

import de.lgohlke.sonar.maven.Maven2ExecutionProcessTest.MyResultTransferHandler;
import de.lgohlke.sonar.maven.plugin.BridgeMojo;
import de.lgohlke.sonar.maven.plugin.ResultTransferHandler;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.versions.HelpMojo;

@Goal("help")
public class MyBridgeMojo extends HelpMojo implements BridgeMojo<MyResultTransferHandler> {

  private MyResultTransferHandler handler;

  @Override
  public void execute() throws MojoExecutionException {
    handler.setPing(true);
  }

  @Override
  public void injectResultHandler(final ResultTransferHandler<?> handler) {
    this.handler = (MyResultTransferHandler) handler;
  }
}
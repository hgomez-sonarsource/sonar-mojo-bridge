/*
 * Sonar maven checks plugin
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

import org.apache.maven.plugin.Mojo;

public abstract class MojoExecutionHandler<ORIGINAL_MOJO extends Mojo, REPLACING_MOJO extends Mojo> {

  @SuppressWarnings("unchecked")
  public final void beforeExecution(final Mojo mojo) {
    beforeExecution2((REPLACING_MOJO) mojo);
  }

  protected abstract void beforeExecution2(final REPLACING_MOJO mojo);

  @SuppressWarnings("unchecked")
  public final void afterExecution(final Mojo mojo) {
    afterExecution2((REPLACING_MOJO) mojo);
  }

  protected abstract void afterExecution2(final REPLACING_MOJO mojo);

  public abstract Class<ORIGINAL_MOJO> getOriginalMojo();

  public abstract Class<REPLACING_MOJO> getReplacingMojo();

}
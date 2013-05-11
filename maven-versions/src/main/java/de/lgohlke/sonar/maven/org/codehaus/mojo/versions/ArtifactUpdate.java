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
package de.lgohlke.sonar.maven.org.codehaus.mojo.versions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.model.Dependency;


@RequiredArgsConstructor
public class ArtifactUpdate {
  @Getter
  private final Dependency dependency;
  @Getter
  private final ArtifactVersion artifactVersion;

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();

    result.append(dependency.getGroupId());
    result.append(":");
    result.append(dependency.getArtifactId());
    result.append(":");
    result.append(dependency.getVersion());

    result.append(" has newer version available: ");
    result.append(artifactVersion.toString());

    return result.toString();
  }
}

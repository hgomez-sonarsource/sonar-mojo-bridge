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
package de.lgohlke.sonar.maven.plugin.versions;

import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.versions.DisplayDependencyUpdatesMojo;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.codehaus.mojo.versions.utils.DependencyComparator;
import org.fest.reflect.reference.TypeRef;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.fest.reflect.core.Reflection.staticMethod;

public class DisplayDependencyUpdatesBridgeMojo extends DisplayDependencyUpdatesMojo implements ResultHandlerHolder {

  public static final String DEPENDENCIES = "Dependencies";
  public static final String DEPENDENCY_MANAGEMENT = "Dependency Management";

  private final Map<String, Map<Dependency, ArtifactVersions>> updateMap = new HashMap<String, Map<Dependency, ArtifactVersions>>();

  protected Boolean processDependencyManagement;
  protected Boolean processDependencies;
  private ResultHandler handler;

  @SuppressWarnings("unchecked")
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    Set<Dependency> dependencyManagement = new TreeSet<Dependency>(new DependencyComparator());
    if (getProject().getDependencyManagement() != null) {
      dependencyManagement.addAll(getProject().getDependencyManagement().getDependencies());
    }

    Set<Dependency> dependencies = new TreeSet<Dependency>(new DependencyComparator());
    dependencies.addAll(getProject().getDependencies());
    if (!Boolean.FALSE.equals(processDependencyManagement)) {
      dependencies = removeDependencyManagment(dependencies, dependencyManagement);
    }

    try
    {
      if (!Boolean.FALSE.equals(processDependencyManagement)) {
        logUpdates(getHelper().lookupDependenciesUpdates(dependencyManagement, false), "Dependency Management");
      }
      if (!Boolean.FALSE.equals(processDependencies)) {
        logUpdates(getHelper().lookupDependenciesUpdates(dependencies, false), "Dependencies");
      }
    } catch (InvalidVersionSpecificationException e)
    {
      throw new MojoExecutionException(e.getMessage(), e);
    } catch (@SuppressWarnings("deprecation") ArtifactMetadataRetrievalException e)
    {
      throw new MojoExecutionException(e.getMessage(), e);
    }

    handler.setResult(updateMap);
  }

  private Set<Dependency> removeDependencyManagment(final Set<Dependency> dependencies, final Set<Dependency> dependencyManagement) {

    final Object[] args = new Object[] {dependencies, dependencyManagement};
    final Class<?>[] parameterTypes = new Class<?>[] {Set.class, Set.class};
    return staticMethod("removeDependencyManagment").
        withReturnType(new TypeRef<Set<Dependency>>() {
        }).
        withParameterTypes(parameterTypes).
        in(DisplayDependencyUpdatesMojo.class).
        invoke(args);
  }

  private void logUpdates(final Map<Dependency, ArtifactVersions> updates, final String section)
  {
    updateMap.put(section, updates);
  }

  public Map<String, Map<Dependency, ArtifactVersions>> getUpdateMap() {
    return updateMap;
  }

  @Override
  public void injectResultHandler(final ResultHandler handler) {
    this.handler = handler;
  }
}

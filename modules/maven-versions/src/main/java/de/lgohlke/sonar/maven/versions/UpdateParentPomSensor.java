/*
 * sonar-mojo-bridge-maven-versions
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
package de.lgohlke.sonar.maven.versions;

import com.google.common.annotations.VisibleForTesting;
import com.thoughtworks.xstream.XStream;
import de.lgohlke.sonar.maven.MavenBaseSensorNG;
import de.lgohlke.sonar.maven.MavenPluginHandlerFactory;
import de.lgohlke.sonar.maven.RuleUtils;
import de.lgohlke.sonar.maven.Rules;
import de.lgohlke.sonar.maven.versions.rules.ParentPomVersion;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.versions.api.DisplayParentUpdateReport;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;

import java.util.Properties;

@Slf4j
@Rules(values = {ParentPomVersion.class})
public class UpdateParentPomSensor extends MavenBaseSensorNG {
    private final static String XML_REPORT = "target/versions_parent_update_report.xml";
    private final static String GOAL = "display-parent-update";
    private final MavenProject mavenProject;

    public UpdateParentPomSensor(RulesProfile rulesProfile, MavenProject mavenProject, ResourcePerspectives resourcePerspectives, Settings settings) {
        super(log, mavenProject, rulesProfile, resourcePerspectives, settings);
        this.mavenProject = mavenProject;
    }

    @Override
    public MavenPluginHandler getMavenPluginHandler(final Project project) {
        final Properties mavenProjectProperties = mavenProject.getProperties();
        mavenProjectProperties.setProperty("xmlReport", XML_REPORT);
        return MavenPluginHandlerFactory.createHandler(Configuration.BASE_IDENTIFIER + GOAL);
    }

    @Override
    public void analyse(Project project, SensorContext context) {
        if (null != mavenProject.getModel().getParent()) {
            DisplayParentUpdateReport report = getReport(XML_REPORT);

            if (null != report.getLatestVersion()) {
                String message = ParentPomVersion.DESCRIPTION + ", currently used is " + report.getCurrentVersion() + " but " +
                        report.getLatestVersion() + " is available";

                int line = mavenProject.getModel().getParent().getLocation("version").getLineNumber();

                Rule rule = RuleUtils.createRuleFrom(ParentPomVersion.class);
                addIssue(message, line, rule);
            }
        }
    }

    @VisibleForTesting
    protected DisplayParentUpdateReport getReport(String xmlReport) {
        XStream xstream = new XStream();
        xstream.setClassLoader(getClass().getClassLoader());

        String xml = getXmlFromReport(xmlReport);
        return (DisplayParentUpdateReport) xstream.fromXML(xml);
    }
}

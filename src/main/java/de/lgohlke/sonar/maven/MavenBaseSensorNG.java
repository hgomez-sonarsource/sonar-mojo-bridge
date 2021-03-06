/*
 * Sonar Mojo Bridge
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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.lgohlke.sonar.Configuration;
import lombok.RequiredArgsConstructor;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.component.mock.MockSourceFile;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.AbstractLanguage;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleParam;
import org.sonar.core.component.ComponentKeys;

import java.io.File;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class MavenBaseSensorNG implements DependsUponMavenPlugin, Sensor {
    private final Logger logger;
    private final MavenProject mavenProject;
    private final RulesProfile rulesProfile;
    private final ResourcePerspectives resourcePerspectives;
    private final Settings settings;

    protected <T> T getXmlAsFromReport(String pathToXmlReport, Class<T> clazz) {
        final File projectDirectory = mavenProject.getOriginalModel().getPomFile().getParentFile();
        return new XmlReader().readXmlFromFile(projectDirectory, pathToXmlReport, clazz);
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        String prop = settings.getProperties().get(Configuration.ANALYSIS_ENABLED);
        if (prop == null) {
            prop = Configuration.DEFAULT;
        }

        boolean activatedByConfiguration = Boolean.parseBoolean(prop);
        boolean activatedByRules = checkIfAtLeastOneRuleIsEnabled();

        return activatedByConfiguration && activatedByRules;
    }

    protected boolean checkIfAtLeastOneRuleIsEnabled() {
        List<Rule> associatedRules = getAssociatedRules();
        for (ActiveRule activeRule : rulesProfile.getActiveRules()) {
            if (associatedRules.contains(activeRule.getRule())) {
                return true;
            }
        }
        return false;
    }

    protected boolean isRuleActive(Class<? extends MavenRule> mavenRule) {
        Rule rule = RuleUtils.createRuleFrom(mavenRule);
        for (ActiveRule activeRule : rulesProfile.getActiveRules()) {
            if (rule.equals(activeRule.getRule())) {
                return true;
            }
        }
        return false;
    }

    private List<Rule> getAssociatedRules() {
        List<Rule> rules = Lists.newArrayList();
        for (Class<? extends MavenRule> ruleClass : getClass().getAnnotation(Rules.class).values()) {
            rules.add(RuleUtils.createRuleFrom(ruleClass));
        }
        return rules;
    }

    protected void addIssue(Project project, String message, int line, Rule rule) {

        Issuable issuable = resourcePerspectives.as(Issuable.class, getPOMComponent(project));
        RuleKey ruleKey = RuleKey.of(rule.getRepositoryKey(), rule.getKey());

        Issue issue = issuable.newIssueBuilder().
                line(line).
                message(message).
                ruleKey(ruleKey).
                build();

        issuable.addIssue(issue);
    }

    @VisibleForTesting
    protected MockSourceFile getPOMComponent(Project project) {
        org.sonar.api.resources.File file = org.sonar.api.resources.File.fromIOFile(mavenProject.getFile(), project);
        file.setLanguage(new AbstractLanguage("xml", "XML") {
            @Override
            public String[] getFileSuffixes() {
                return new String[]{"xml"};
            }
        });

        return MockSourceFile.createMain(ComponentKeys.createEffectiveKey(project, file))
                .setLanguage("XML")
                .setName(file.getLongName())
                .setLongName(file.getLongName())
                .setPath(project.getPath() + "/" + file.getPath())
                .setQualifier(file.getQualifier());
    }

    protected Map<String, String> createRulePropertiesMapFromQualityProfile(Class<? extends MavenRule> ruleClass) {
        Map<String, String> mappedParams = Maps.newHashMap();
        String ruleKey = RuleUtils.createRuleFrom(ruleClass).getKey();
        ActiveRule activeRule = rulesProfile.getActiveRule(Configuration.REPOSITORY_KEY, ruleKey);
        if (null != activeRule) {
            List<ActiveRuleParam> activeRuleParams = activeRule.getActiveRuleParams();
            for (ActiveRuleParam activeRuleParam : activeRuleParams) {
                mappedParams.put(activeRuleParam.getKey(), activeRuleParam.getValue());
            }

            // fill with default values for params not set
            final List<RuleParam> params = activeRule.getRule().getParams();
            for (RuleParam ruleParam : params) {
                if (!mappedParams.containsKey(ruleParam.getKey())) {
                    mappedParams.put(ruleParam.getKey(), ruleParam.getDefaultValue());
                }
            }
        }
        return mappedParams;
    }
}

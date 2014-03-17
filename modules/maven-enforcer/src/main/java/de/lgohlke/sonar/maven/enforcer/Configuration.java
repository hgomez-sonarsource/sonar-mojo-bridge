/*
 * sonar-mojo-bridge-maven-enforcer
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
package de.lgohlke.sonar.maven.enforcer;

import de.lgohlke.sonar.maven.MavenRule;
import de.lgohlke.sonar.maven.enforcer.DependencyConvergence.DependencyConvergenceRule;
import de.lgohlke.sonar.maven.enforcer.DependencyConvergence.DependencyConvergenceViolationAdapter;
import org.sonar.api.Extension;

import java.util.*;

public interface Configuration {
    String BASE_IDENTIFIER = "de.lgohlke.mojo:maven-enforcer-plugin:1.3.1.1:";

    Map<Class<? extends MavenRule>, ViolationAdapter> RULE_ADAPTER_MAP =
            new HashMap<Class<? extends MavenRule>, ViolationAdapter>() {
                {
                    put(DependencyConvergenceRule.class, new DependencyConvergenceViolationAdapter());
                }
            };

    Set<Class<? extends MavenRule>> RULES = RULE_ADAPTER_MAP.keySet();
    Set<Class<? extends Extension>> EXTENSIONS = new HashSet<Class<? extends Extension>>() {{
        add(EnforceSensor.class);
    }};
}

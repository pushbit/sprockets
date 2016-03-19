/*
 * Copyright 2015 pushbit <pushbit@gmail.com>
 * 
 * This file is part of Sprockets.
 * 
 * Sprockets is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Sprockets is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets.immutables.value;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

import org.immutables.value.Value.Modifiable;
import org.immutables.value.Value.Style;

/**
 * Names a {@link Modifiable Modifiable} as {@code Mutable*}, creates a public constructor, and uses
 * unprefixed setters and getters.
 * 
 * @since 3.1.0
 */
@Target({ PACKAGE, TYPE })
@Style(typeModifiable = "Mutable*", create = "new", get = "*", set = "*")
public @interface NamedMutable {
}

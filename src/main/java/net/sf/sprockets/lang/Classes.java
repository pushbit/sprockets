/*
 * Copyright 2015-2017 pushbit <pushbit@gmail.com>
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

package net.sf.sprockets.lang;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import javax.annotation.Nullable;

/**
 * Utility methods for working with Classes.
 *
 * @since 3.0.0
 */
public class Classes {
	private Classes() {
	}

	/**
	 * If the object is not a Class, get its Class. Otherwise get the object as a Class.
	 *
	 * @since 4.0.0
	 */
	public static Class<?> getClass(Object obj) {
		Class<?> cls = obj.getClass();
		return cls != Class.class ? cls : (Class<?>) obj;
	}

	/**
	 * If the object is not a Class, get its Class. Otherwise get the object as a Class. If the
	 * class is anonymous, get a non-anonymous enclosing class.
	 *
	 * @since 4.0.0
	 */
	public static Class<?> getNamedClass(Object obj) {
		Class<?> cls = getClass(obj);
		while (cls != null && cls.isAnonymousClass()) {
			cls = cls.getEnclosingClass();
		}
		return cls;
	}

	/**
	 * <p>
	 * Get the Type that has been specified by the class or an ancestor for the generic type
	 * parameter. For example:
	 * </p>
	 * <pre>{@code
	 * class StringList extends ArrayList<String>
	 *
	 * Classes.getTypeArgument(StringList.class, "E")
	 * // returns: class java.lang.String
	 * }</pre>
	 *
	 * @return null if the type parameter was not found
	 */
	@Nullable
	public static Type getTypeArgument(Class<?> cls, String typeParameter) {
		while (cls != Object.class) { // walk up the Class hierarchy, searching for the type param
			Class<?> parent = cls.getSuperclass();
			Type parentType = cls.getGenericSuperclass();
			if (parentType instanceof ParameterizedType) {
				Type[] args = ((ParameterizedType) parentType).getActualTypeArguments();
				TypeVariable<?>[] params = parent.getTypeParameters();
				for (int i = 0, length = params.length; i < length; i++) {
					if (params[i].getName().equals(typeParameter)) {
						return args[i];
					}
				}
			}
			cls = parent;
		}
		return null;
	}

	/**
	 * <p>
	 * Get the Types that have been specified by the class or its ancestors for the generic type
	 * parameters. For example:
	 * </p>
	 * <pre>{@code
	 * class IntStringMap extends HashMap<Integer, String>
	 *
	 * Classes.getTypeArguments(IntStringMap.class, "K", "V")
	 * // returns: [class java.lang.Integer, class java.lang.String]
	 * }</pre>
	 *
	 * @return null elements for any type parameters that weren't found
	 */
	public static Type[] getTypeArguments(Class<?> cls, String... typeParameters) {
		int requestedParams = typeParameters.length;
		Type[] types = new Type[requestedParams];
		int remaining = requestedParams;
		while (cls != Object.class) {
			Class<?> parent = cls.getSuperclass();
			Type parentType = cls.getGenericSuperclass();
			if (parentType instanceof ParameterizedType) {
				Type[] args = ((ParameterizedType) parentType).getActualTypeArguments();
				TypeVariable<?>[] params = parent.getTypeParameters();
				for (int i = 0, parentParams = params.length; i < parentParams; i++) {
					String name = params[i].getName();
					for (int j = 0; j < requestedParams; j++) { // matches any of the requested?
						if (types[j] == null && typeParameters[j].equals(name)) {
							types[j] = args[i];
							if (--remaining > 0) {
								break;
							} else {
								return types;
							}
						}
					}
				}
			}
			cls = parent;
		}
		return types;
	}
}

/*
 * Copyright 2013-2014 pushbit <pushbit@gmail.com>
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

package net.sf.sprockets.servlet.http;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Deserialises POSTed JSON objects and serialises your objects to the response in JSON format.
 * Override {@link #jsonGet(HttpServletRequest, HttpServletResponse) jsonGet} and/or
 * {@link #jsonPost(Object, HttpServletRequest, HttpServletResponse) jsonPost} to handle the
 * requests. The Content-Type response header is set to {@code application/json; charset=UTF-8}.
 * 
 * @param <I>
 *            type of JSON object that will be POSTed to this servlet and deserialised as the input
 *            object. Can be Void to skip deserialisation.
 * @param <O>
 *            type of object that will be serialised to the response in JSON format. Can be Void to
 *            skip serialisation.
 */
public abstract class JsonServlet<I, O> extends HttpServlet {
	private static final long serialVersionUID = -2301278450465486558L;
	private static final String CONTENT_TYPE = "application/json; charset=UTF-8";

	private final Type mIn;
	private final Type mOut;
	private final Gson mGson;

	public JsonServlet() { // walk up the class hierarchy to find the in/out types for this instance
		Type in = null;
		Type out = null;
		Class<?> clazz = getClass();
		while (clazz != JsonServlet.class) {
			Type sup = clazz.getGenericSuperclass();
			if (sup instanceof ParameterizedType) {
				Type[] types = ((ParameterizedType) sup).getActualTypeArguments();
				TypeVariable<?>[] vars = clazz.getSuperclass().getTypeParameters();
				for (int i = 0; i < vars.length; i++) {
					String name = vars[i].getName();
					if (in == null && name.equals("I")) {
						in = types[i];
					} else if (out == null && name.equals("O")) {
						out = types[i];
					}
				}
				if (in != null && out != null) {
					break;
				}
			}
			clazz = clazz.getSuperclass();
		}
		mIn = in;
		mOut = out;
		mGson = getGson();
	}

	/**
	 * Override to provide your own Gson instance with a custom configuration.
	 */
	protected Gson getGson() {
		return new Gson();
	}

	/**
	 * Calls {@link #jsonGet(HttpServletRequest, HttpServletResponse)}.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		resp.setContentType(CONTENT_TYPE); // must call before getWriter
		write(jsonGet(req, resp), resp);
	}

	/**
	 * Handle a GET request.
	 * 
	 * @return object to serialise to the response in JSON format
	 */
	protected O jsonGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		return null;
	}

	/**
	 * Calls {@link #jsonPost(Object, HttpServletRequest, HttpServletResponse)}.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType(CONTENT_TYPE); // must call before getWriter
		I in = null;
		if (mIn != Void.class) {
			in = mGson.fromJson(req.getReader(), mIn);
		}
		write(jsonPost(in, req, resp), resp);
	}

	/**
	 * Handle a POST request. The POSTed JSON object is deserialised to {@code in}.
	 * 
	 * @param in
	 *            null if type {@code I} is Void
	 * @return object to serialise to the response in JSON format
	 */
	protected O jsonPost(I in, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return null;
	}

	/**
	 * Serialise the output object to the response in JSON format.
	 */
	private void write(O out, HttpServletResponse resp) throws IOException {
		mGson.toJson(out, mOut, resp.getWriter());
	}
}

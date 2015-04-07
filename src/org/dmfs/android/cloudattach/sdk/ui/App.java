/*
 * Copyright (C) 2015 Marten Gajda <marten@dmfs.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.dmfs.android.cloudattach.sdk.ui;

import java.io.IOException;
import java.util.List;

import org.dmfs.android.cloudattach.sdk.R;
import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.QualifiedName;
import org.dmfs.xmlobjects.XmlContext;
import org.dmfs.xmlobjects.android.builder.ReflectionObjectBuilder;
import org.dmfs.xmlobjects.android.pull.AndroidParserContext;
import org.dmfs.xmlobjects.android.pull.ResolveInt;
import org.dmfs.xmlobjects.builder.ListObjectBuilder;
import org.dmfs.xmlobjects.builder.reflection.Attribute;
import org.dmfs.xmlobjects.pull.XmlObjectPull;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;
import org.dmfs.xmlobjects.pull.XmlPath;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;


/**
 * Holds the information about a specific attachment uploader app.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class App
{
	final static XmlContext CONTEXT = new XmlContext();

	private final static String NAMESPACE = "http://dmfs.org/ns/cloudattach";

	final static ElementDescriptor<App> APP = ElementDescriptor.register(QualifiedName.get(NAMESPACE, "app"), new ReflectionObjectBuilder<App>(App.class),
		CONTEXT);
	final static ElementDescriptor<List<App>> APP_LIST = ElementDescriptor.register(QualifiedName.get(NAMESPACE, "app-list"), new ListObjectBuilder<App>(APP),
		CONTEXT);


	/**
	 * Load the list of known attachment apps from the resources.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @return A {@link List} of {@link App} instances.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws XmlObjectPullParserException
	 */
	public static List<App> loadList(Context context) throws XmlPullParserException, IOException, XmlObjectPullParserException
	{
		Resources res = context.getResources();

		XmlResourceParser xmlParser = res.getXml(R.xml.cloudattach_sdk_apps);

		XmlObjectPull pullParser = new XmlObjectPull(xmlParser, new AndroidParserContext(res));
		pullParser.setContext(CONTEXT);

		return pullParser.pull(APP_LIST, null, new XmlPath());
	}

	/**
	 * The package name of the app.
	 */
	@Attribute(name = "packageName")
	String packageName;

	/**
	 * The title of the app.
	 */
	@Attribute(name = "title")
	String title;

	/**
	 * The description of the app.
	 */
	@Attribute(name = "description")
	String description;

	/**
	 * The resource id of the app icon.
	 */
	@Attribute(name = "iconId")
	@ResolveInt(false)
	int iconId;
}

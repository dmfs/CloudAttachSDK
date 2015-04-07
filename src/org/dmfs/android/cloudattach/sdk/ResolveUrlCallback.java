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

package org.dmfs.android.cloudattach.sdk;

import android.net.Uri;


/**
 * Interface of a callback that's called after resolving preview content {@link Uri}.
 * 
 * @author Tristan Heinig <tristan@dmfs.org>
 * @author Marten Gajda <marten@dmfs.org>
 */
public interface ResolveUrlCallback
{
	/**
	 * Called if no app to resolve the given URL could be found.
	 * 
	 * @param id
	 *            The id that has been passed to the resolver method.
	 */
	abstract void onNoPreviewAppFound(long id);


	/**
	 * Called if error occurred during resolving the URL handler.
	 * 
	 * @param id
	 *            The id that has been passed to the resolver method.
	 * @param e
	 *            The exception thrown while resolving the handler for the URL.
	 */
	abstract void onError(long id, Exception e);


	/**
	 * Called when a handler that can load the given URL has been found.
	 * 
	 * @param id
	 *            The id that has been passed to the resolver method.
	 * @param result
	 *            The content {@link Uri} of the preview.
	 */
	abstract void onResult(long id, Uri result);

}
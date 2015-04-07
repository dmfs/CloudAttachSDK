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

import android.graphics.Bitmap;


/**
 * Interface of a handler that's called after loading a preview.
 * 
 * @author Tristan Heinig <tristan@dmfs.org>
 * @author Marten Gajda <marten@dmfs.org>
 */
public interface PreviewLoaderCallback
{

	/**
	 * Called when an error occurs while loading the preview.
	 * 
	 * @param id
	 *            The id that has been passed to the loader method.
	 * @param exception
	 *            The exception that was thrown while loading the preview.
	 */
	public void onError(long id, Exception exception);


	/**
	 * Called if no upload app to handle the given URL was found.
	 * 
	 * @param id
	 *            The id that has been passed to the loader method.
	 */
	public void onNoPreviewAppFound(long id);


	/**
	 * Called when the preview was loaded successfully.
	 * 
	 * @param id
	 *            The id that has been passed to the loader method.
	 * @param preview
	 *            Preview as {@link Bitmap}.
	 */
	public void onPreviewLoaded(long id, Bitmap preview);

}
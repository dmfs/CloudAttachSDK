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

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

import org.dmfs.android.cloudattach.sdk.PreviewLoaderTask.Preview;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;


/**
 * Background task to load a preview from the given content {@link Uri}.
 * 
 * @author Tristan Heinig <tristan@dmfs.org>
 * @author Marten Gajda <marten@dmfs.org>
 */
public class PreviewLoaderTask extends AsyncTask<Preview, Void, Preview>
{
	private WeakReference<Context> mContext;
	private WeakReference<PreviewLoaderCallback> mCallbackRef;

	/**
	 * Internal helper to store request and result.
	 */
	final static class Preview
	{
		public final long id;
		public final Uri uri;
		private AssetFileDescriptor fileDescriptor;
		private Exception error;


		public Preview(long id, Uri uri)
		{
			this.id = id;
			this.uri = uri;
		}


		public Preview(Exception e)
		{
			this.id = 0;
			this.uri = null;
			this.error = e;
		}
	}


	/**
	 * Constructor for {@link PreviewLoaderTask}.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param callback
	 *            Handler for result and errors, must not be <code>null</code>.
	 */
	public PreviewLoaderTask(Context context, PreviewLoaderCallback callback)
	{
		if (context == null)
		{
			throw new NullPointerException("Context must not be null.");
		}
		if (callback == null)
		{
			throw new NullPointerException("Callback must not be null.");
		}

		mContext = new WeakReference<Context>(context);
		mCallbackRef = new WeakReference<PreviewLoaderCallback>(callback);
	}


	/**
	 * Execute this task for the given uri.
	 * 
	 * @param id
	 *            An id for reference. This will be returned to the callback when the preview was loaded or in case of an error.
	 * @param uri
	 *            The {@link Uri} to load the preview from.
	 * @return This instance.
	 */
	public PreviewLoaderTask execute(long id, Uri uri)
	{
		if (uri == null)
		{
			throw new IllegalArgumentException("uri must not be null");
		}

		execute(new Preview(id, uri));
		return this;
	}


	@Override
	protected Preview doInBackground(Preview... previews)
	{
		if (previews == null || previews.length < 1 || previews[0] == null)
		{
			return new Preview(new IllegalArgumentException("no url to load"));
		}

		Preview preview = previews[0];

		Context context = mContext.get();
		if (context == null)
		{
			preview.error = new IllegalStateException("Lost context");
			return preview;
		}

		try
		{
			preview.fileDescriptor = context.getContentResolver().openAssetFileDescriptor(preview.uri, "r");
		}
		catch (FileNotFoundException e)
		{
			preview.error = e;
		}
		return preview;
	}


	@Override
	protected void onPostExecute(Preview preview)
	{
		PreviewLoaderCallback callback = mCallbackRef.get();
		if (callback == null)
		{
			// our callback no longer exists
			return;
		}

		if (preview.error != null)
		{
			callback.onError(preview.id, preview.error);
			return;
		}

		if (preview.fileDescriptor == null)
		{
			// is this even possible?
			callback.onError(preview.id, new FileNotFoundException("asset file descriptor was null"));
			return;
		}

		Bitmap bitmap = BitmapFactory.decodeFileDescriptor(preview.fileDescriptor.getFileDescriptor());
		if (bitmap == null)
		{
			callback.onError(preview.id, new DecodingBitmapException("Could not decode FileDescriptor to Bitmap."));
			return;
		}

		callback.onPreviewLoaded(preview.id, bitmap);
	}

}
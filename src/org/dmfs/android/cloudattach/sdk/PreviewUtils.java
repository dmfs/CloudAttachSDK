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

import java.net.URI;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


/**
 * 
 * General class providing preview functionalities.
 * <p>
 * Loading a preview is done in two steps:
 * <ol>
 * <li>Resolve the preview URL to a content {@link Uri}</li>
 * <li>Load the image data from the content {@link Uri}.</li>
 * </ol>
 * You can run these steps separately (see below) or call one of the following methods to handle this for you:
 * <ul>
 * <li>{@link #getPreview(Context, long, Intent, PreviewLoaderCallback)}</li>
 * <li>{@link #getPreview(Context, long, String, PreviewLoaderCallback)}</li>
 * <li>{@link #getPreview(Context, long, Uri, PreviewLoaderCallback)}</li>
 * <li>{@link #getPreview(Context, long, URI, PreviewLoaderCallback)}</li>
 * </ul>
 * </p>
 * <h2>Resolving the preview URL</h2>
 * <p>
 * This is done by sending an ordered broadcast to all installed attachment upload apps. The fist app that can handle the given URL will respond with a content
 * {@link Uri} that can be used to load a preview.
 * </p>
 * <p>
 * There are four methods available to perform this step (each taking the preview URL in a different way):
 * <ul>
 * <li>{@link #resolveUrl(Context, long, Intent, ResolveUrlCallback)}</li>
 * <li>{@link #resolveUrl(Context, long, String, ResolveUrlCallback)}</li>
 * <li>{@link #resolveUrl(Context, long, Uri, ResolveUrlCallback)}</li>
 * <li>{@link #resolveUrl(Context, long, URI, ResolveUrlCallback)}</li>
 * </ul>
 * You'll receive the resolved {@link Uri} asynchronously though the provided callback.
 * </p>
 * <h2>Loading the image data</h2>
 * <p>
 * If the preview URL could be resolved successfully, the image data can be loaded from the resulting content {@link Uri}. Use the
 * {@link #loadPreview(Context, long, Uri, PreviewLoaderCallback)} to perform this step in a background task. The given callback will receive the image data or
 * an error.
 * </p>
 * 
 * <h2>Example</h2>
 * <p>
 * The following example code resolves the preview URL and loads the preview with a single call.
 * </p>
 * 
 * <pre>
 * <code>
 * PreviewUtils.getPreview(this, 0, "https://db.tt/g8RaiVYH", new PreviewLoaderCallback()
 * {
 * 
 * 	public void onPreviewLoaded(long id, Bitmap preview)
 * 	{
 * 		imageView.setImageBitmap(preview);
 * 	}
 * 
 * 	public void onError(long id, Exception exception)
 * 	{
 * 		// ignore
 * 	}
 * 
 * 	public void onNoPreviewAppFound(long id)
 * 	{
 * 		// ignore
 * 	}
 * 
 * });
 * </code>
 * </pre>
 * 
 * @author Tristan Heinig
 */
public final class PreviewUtils
{

	/**
	 * Global action to call for preview apps. Apps have to declare an intent-filter with this action.
	 */
	public static final String ACTION_BROADCAST = "org.dmfs.android.cloudattach.action.PREVIEW";

	/**
	 * Global key to get a state message from intent extras.
	 */
	public static final String EXTRAS_MESSAGE = "org.dmfs.android.cloudattach.extra.MESSAGE";


	/**
	 * "No instances" constructor.
	 */
	private PreviewUtils()
	{
	}


	/**
	 * Loads a preview for an attachment. The attachment URL is taken from the result {@link Intent} of a former
	 * {@link AttachmentUtils#startAttachmentActivity()} call. The result will be delivered to the given callback, along with the id provided to this method.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param id
	 *            An id to be used as a reference when the result is delivered.
	 * @param activityResultIntent
	 *            Result of the former {@link AttachmentUtils#startAttachmentActivity()} call.
	 * @param callback
	 *            A callback to handle result and errors.
	 * @throws IllegalArgumentException
	 *             if one of the given parameters is invalid or <code>null</code>.
	 */
	public static void getPreview(Context context, long id, Intent activityResultIntent, PreviewLoaderCallback callback) throws NullPointerException
	{
		Uri url = AttachmentUtils.getUrlFromResult(activityResultIntent);
		getPreview(context, id, url, callback);
	}


	/**
	 * Loads a preview for the given attachment URL. The result will be delivered to the given callback, along with the id provided to this method.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param id
	 *            An id to be used as a reference when the result is delivered.
	 * @param urlString
	 *            URL of the attachment.
	 * @param callback
	 *            A callback to handle result and errors.
	 * @throws IllegalArgumentException
	 *             if one of the given parameters is invalid or <code>null</code>.
	 */
	public static void getPreview(final Context context, long id, String urlString, final PreviewLoaderCallback callback) throws NullPointerException
	{
		getPreview(context, id, Uri.parse(urlString), callback);
	}


	/**
	 * Loads a preview for the given attachment URL. The result will be delivered to the given callback, along with the id provided to this method.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param id
	 *            An id to be used as a reference when the result is delivered.
	 * @param url
	 *            URL of the attachment.
	 * @param callback
	 *            A callback to handle result and errors.
	 * @throws IllegalArgumentException
	 *             if one of the given parameters is invalid or <code>null</code>.
	 */
	public static void getPreview(final Context context, long id, URI url, final PreviewLoaderCallback callback) throws NullPointerException
	{
		getPreview(context, id, Uri.parse(url.toString()), callback);
	}


	/**
	 * Loads a preview for the given attachment URL. The result will be delivered to the given callback, along with the id provided to this method.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param id
	 *            An id to be used as a reference when the result is delivered.
	 * @param url
	 *            URL of the attachment.
	 * @param callback
	 *            A callback to handle result and errors.
	 * @throws IllegalArgumentException
	 *             if one of the given parameters is invalid or <code>null</code>.
	 */
	public static void getPreview(final Context context, long id, Uri url, final PreviewLoaderCallback callback) throws NullPointerException
	{
		if (context == null)
		{
			throw new IllegalArgumentException("Context must not be null");
		}
		if (callback == null)
		{
			throw new IllegalArgumentException("Callback must not be null");
		}

		resolveUrl(context, id, url, new ResolveUrlCallback()
		{

			@Override
			public void onResult(long id, Uri uri)
			{
				// we got a content Uri, continue loading the preview
				loadPreview(context, id, uri, callback);
			}


			@Override
			public void onError(long id, Exception e)
			{
				callback.onError(id, e);
			}


			@Override
			public void onNoPreviewAppFound(long id)
			{
				callback.onNoPreviewAppFound(id);
			}
		});
	}


	/**
	 * Resolve the given attachment URL to a content {@link Uri} that points to a preview of the attachment. The attachment URL is taken from the result
	 * {@link Intent} of a former {@link AttachmentUtils#startAttachmentActivity()} call. The resolved content {@link Uri} or any error is delivered to the
	 * given callback.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param id
	 *            An id to be used as a reference when the result is delivered.
	 * @param activityResultIntent
	 *            Intent from onActivityResult call, which holds the URL of the attachment.
	 * @param callback
	 *            Interface to handle result and errors.
	 * @throws IllegalArgumentException
	 *             if one of the given parameters is invalid or <code>null</code>.
	 */
	public static void resolveUrl(Context context, long id, Intent activityResultIntent, final ResolveUrlCallback callback) throws NullPointerException
	{
		resolveUrl(context, id, AttachmentUtils.getUrlFromResult(activityResultIntent), callback);
	}


	/**
	 * Resolve the given attachment URL to a content {@link Uri} that points to a preview of the attachment.The resolved content {@link Uri} or any error is
	 * delivered to the given callback.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param id
	 *            An id to be used as a reference when the result is delivered.
	 * @param url
	 *            URL of the attachment.
	 * @param callback
	 *            Interface to handle result and errors.
	 * @throws IllegalArgumentException
	 *             if one of the given parameters is invalid or <code>null</code>.
	 */
	public static void resolveUrl(Context context, long id, URI url, final ResolveUrlCallback callback) throws NullPointerException
	{
		resolveUrl(context, id, Uri.parse(url.toString()), callback);
	}


	/**
	 * Resolve the given attachment URL to a content {@link Uri} that points to a preview of the attachment.The resolved content {@link Uri} or any error is
	 * delivered to the given callback.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param id
	 *            An id to be used as a reference when the result is delivered.
	 * @param url
	 *            URL of the attachment.
	 * @param callback
	 *            Interface to handle result and errors.
	 * @throws IllegalArgumentException
	 *             if one of the given parameters is invalid or <code>null</code>.
	 */
	public static void resolveUrl(Context context, long id, String url, final ResolveUrlCallback callback) throws NullPointerException
	{
		resolveUrl(context, id, Uri.parse(url), callback);
	}


	/**
	 * Resolve the given attachment URL to a content {@link Uri} that points to a preview of the attachment.The resolved content {@link Uri} or any error is
	 * delivered to the given callback.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param id
	 *            An id to be used as a reference when the result is delivered.
	 * @param url
	 *            URL of the attachment.
	 * @param callback
	 *            Interface to handle result and errors.
	 * @throws IllegalArgumentException
	 *             if one of the given parameters is invalid or <code>null</code>.
	 */
	public static void resolveUrl(Context context, final long id, Uri url, final ResolveUrlCallback callback) throws NullPointerException
	{
		if (context == null)
		{
			throw new IllegalArgumentException("Context must not be null.");
		}
		if (url == null)
		{
			throw new IllegalArgumentException("Url must not be null.");
		}
		if (callback == null)
		{
			throw new IllegalArgumentException("Callback must not be null.");
		}

		try
		{
			if (!"https".equalsIgnoreCase(url.getScheme()) && !"http".equalsIgnoreCase(url.getScheme()))
			{
				throw new IllegalArgumentException("Unsupported URL schema. Only http and https URLs are supported.");
			}

			Intent broadcast = new Intent(ACTION_BROADCAST, url);

			// send the broadcast and handle the result
			context.sendOrderedBroadcast(broadcast, null, new BroadcastReceiver()
			{
				@Override
				public void onReceive(Context context, Intent intent)
				{
					if (getResultCode() == Activity.RESULT_OK)
					{
						callback.onResult(id, Uri.parse(getResultData()));
					}
					else
					{
						Bundle extras = getResultExtras(false);
						if (extras != null)
						{
							callback.onError(id, new Exception(extras.getString(EXTRAS_MESSAGE)));
						}
						else
						{
							callback.onNoPreviewAppFound(id);
						}
					}
				}
			}, null, Activity.RESULT_CANCELED, null, null);
		}
		catch (Exception e)
		{
			callback.onError(id, e);
		}
	}


	/**
	 * Load the preview from the given content {@link Uri}. The preview is loaded asynchronously and the result is delivered to the given
	 * {@link PreviewLoaderCallback}.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param uri
	 *            The content {@link Uri} of the preview provider.
	 * @param callback
	 *            Interface to handle result and errors.
	 * @return The background task.
	 * @throws IllegalArgumentException
	 *             if one of the given parameters is invalid or <code>null</code>.
	 */
	public static PreviewLoaderTask loadPreview(Context context, long id, Uri uri, PreviewLoaderCallback callback) throws NullPointerException
	{
		if (context == null)
		{
			throw new IllegalArgumentException("Context must not be null.");
		}
		if (uri == null)
		{
			throw new IllegalArgumentException("Uri must not be null.");
		}
		if (callback == null)
		{
			throw new IllegalArgumentException("Callback must not be null.");
		}

		try
		{
			PreviewLoaderTask task = new PreviewLoaderTask(context, callback);
			task.execute(id, uri);
			return task;
		}
		catch (Exception e)
		{
			callback.onError(id, e);
		}
		return null;
	}

}

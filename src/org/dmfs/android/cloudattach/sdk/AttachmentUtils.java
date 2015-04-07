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

import java.util.List;

import org.dmfs.android.cloudattach.sdk.ui.AttachmentAppsDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;


/**
 * General class to handle attachments. It bundles methods to select upload content, call for attachment applications immediately and provides Intents for more
 * control.
 * 
 * <h2>Example</h2>
 * 
 * <pre>
 * <code>
 * PreviewUtils.resolvePreview(this, id, "https://db.tt/g8RaiVYH", new PreviewLoaderCallback()
 * {
 * 	public void onPreviewLoaded(long id, Bitmap thumbnail)
 * 	{
 * 		imageView.setImageBitmap(thumbnail);
 * 	}
 * });
 * </code>
 * </pre>
 * 
 * @author Tristan Heinig
 */
public final class AttachmentUtils
{
	/**
	 * global action to call for attachment apps. Apps have to declare an intent-filter with this action
	 */
	public static final String ACTION_ATTACHMENT = "org.dmfs.android.cloudattach.action.ATTACH";
	/**
	 * global key to get url from intent extras.
	 */
	public static final String EXTRAS_URL = "org.dmfs.android.cloudattach.extras.URL";
	/**
	 * global key to get a state message from intent extras.
	 */
	public static final String EXTRAS_ERROR = "org.dmfs.android.cloudattach.extras.ERROR";


	/**
	 * "no instances" constructor.
	 */
	private AttachmentUtils()
	{
	}


	/**
	 * Launches an attachment intent immediately without providing a {@link Uri}. The attachment upload app is expected to handle the content selection. The
	 * attachment result will be returned in {@link Activity#onActivityResult()}.
	 * 
	 * @param activity
	 *            Android {@link Activity} {@link Context}.
	 * @param requestCode
	 *            The request code for reference in {@link Activity#onActivityResult()}.
	 */
	public static void startAttachmentActivity(Activity activity, int requestCode)
	{
		startAttachmentActivity(getAttachmentIntent(activity), activity, requestCode);
	}


	/**
	 * starts an attachment application immediately. The {@link Intent chooserActivityResult} bundles the upload content and is itself the result of an
	 * {@link #startChooserForAttachmentActivity(Activity, int)} call. So it is not correct to use this method standalone. The attachment result will be
	 * returned in {@link Activity#onActivityResult()}.
	 * 
	 * @param chooserActivityResult
	 *            result of the former {@link #startChooserForAttachmentActivity(Activity, int)} call
	 * @param activity
	 *            Android {@link Activity} {@link Context}
	 * @param requestCode
	 *            this code will be returned in {@link Activity#onActivityResult()}
	 */
	public static void startAttachmentActivityFromChooserResult(Intent chooserActivityResult, Activity activity, int requestCode)
	{
		startAttachmentActivity(AttachmentUtils.getAttachmentIntentFromChooserResult(chooserActivityResult, activity), activity, requestCode);
	}


	/**
	 * starts an attachment application immediately. The {@link Uri uri} indicates the upload content. The attachment result will be returned in
	 * {@link Activity#onActivityResult()}.
	 * 
	 * @param uri
	 *            {@link Uri} which points to the upload content
	 * @param activity
	 *            Android {@link Activity} {@link Context}
	 * @param requestCode
	 *            this code will be returned in {@link Activity#onActivityResult()}
	 */
	public static void startAttachmentActivity(Uri uri, Activity activity, int requestCode)
	{
		startAttachmentActivity(AttachmentUtils.getAttachmentIntent(uri, activity), activity, requestCode);
	}


	/**
	 * starts an attachment application immediately. The {@link Uri uri} indicates the upload content. The attachment result will be returned in
	 * {@link Activity#onActivityResult()}.
	 * 
	 * @param uriString
	 *            {@link Uri} as {@link String} which points to the upload content
	 * @param activity
	 *            Android {@link Activity} {@link Context}
	 * @param requestCode
	 *            this code will be returned in {@link Activity#onActivityResult()}
	 */
	public static void startAttachmentActivity(String uriString, Activity activity, int requestCode)
	{
		startAttachmentActivity(AttachmentUtils.getAttachmentIntent(uriString, activity), activity, requestCode);
	}


	private static void startAttachmentActivity(Intent intent, Activity activity, int requestCode)
	{
		List<ResolveInfo> availableApps = activity.getPackageManager().queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
		if (availableApps == null || availableApps.size() == 0)
		{
			AttachmentAppsDialog.show(activity.getFragmentManager());
			return;
		}
		activity.startActivityForResult(intent, requestCode);
	}


	/**
	 * calls the android chooser to select content for attachment applications immediately. Override {@link Activity#onActivityResult()} in your activity to get
	 * the result of the chooser and forward it to {@link #startAttachmentActivityFromChooserResult(Intent, Activity, int)} to call for attachment applications.
	 * The given <b>requestCode</b> indicates the chooser result.
	 * 
	 * @param activity
	 *            Android {@link Activity} {@link Context}
	 * @param requestCode
	 *            this code will be returned in {@link Activity#onActivityResult()}
	 */
	public static void startChooserForAttachmentActivity(Activity activity, int requestCode)
	{
		activity.startActivityForResult(getChooserIntent(), requestCode);
	}


	/**
	 * Returns an intent calling the android chooser. The chooser will provide apps, which listen to one of the following actions
	 * {@link Intent#ACTION_GET_CONTENT} , {@link MediaStore#ACTION_IMAGE_CAPTURE}, {@link MediaStore#ACTION_VIDEO_CAPTURE},
	 * {@link MediaStore.Audio.Media#RECORD_SOUND_ACTION}
	 * 
	 * @return Intent that opens the app chooser.
	 */
	public static Intent getChooserIntent()
	{
		// GET_CONTENT Apps
		Intent getContentIntent = new Intent();
		getContentIntent.setAction(Intent.ACTION_GET_CONTENT);
		getContentIntent.setType("*/*");
		getContentIntent.addCategory(Intent.CATEGORY_OPENABLE);
		// ACTION_IMAGE_CAPTURE Apps
		Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// ACTION_VIDEO_CAPTURE Apps
		Intent captureVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		// RECORD_SOUND_ACTION Apps
		Intent recordSoungIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		Intent finalIntent = Intent.createChooser(getContentIntent, "test");
		finalIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { captureImageIntent, captureVideoIntent, recordSoungIntent });
		return finalIntent;
	}


	/**
	 * returns an empty {@link Intent} to call for attachment applications. The Intent starts the application without initialized {@link Uri}, so the
	 * applications itself will handle the content selection.
	 * 
	 * @param ctx
	 *            Android {@link Activity} {@link Context}
	 * @return {@link Intent} with no {@link Uri} data to call for attachment application
	 */
	public static Intent getAttachmentIntent(Context ctx)
	{
		Intent intent = new Intent(AttachmentUtils.ACTION_ATTACHMENT);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		return intent;
	}


	/**
	 * Takes a {@link String} represation of a content:// of file:// {@link Uri} and returns an intent to call for attachment applications.
	 * 
	 * @param uriString
	 *            {@link Uri Uri} as {@link String} for attachment applications
	 * @param ctx
	 *            Android {@link Activity} {@link Context}
	 * @return {@link Intent} to call for attachment application or null, if uri invalid
	 */
	public static Intent getAttachmentIntent(String uriString, Context ctx)
	{
		return getAttachmentIntent(Uri.parse(uriString), ctx);
	}


	/**
	 * bundles a content:// of file:// {@link Uri} and returns an intent to call for attachment applications.
	 * 
	 * @param uri
	 *            {@link Uri Uri} for attachment applications
	 * @param ctx
	 *            Android {@link Activity} {@link Context}
	 * @return {@link Intent} to call for attachment application or null, if uri invalid
	 */

	public static Intent getAttachmentIntent(Uri uri, Context ctx)
	{
		if (isValid(uri))
		{
			Intent intent = getAttachmentIntent(ctx);
			intent.setData(uri);
			return intent;
		}
		return null;
	}


	/**
	 * You can use this method in combination with {@link #startChooserForAttachmentActivity(Activity, int)}. This method bundles a {@link Uri} and returns an
	 * intent to call for attachment applications. The Uri is part of the result {@link Intent} in {@link Activity#onActivityResult(int, int, Intent)}.
	 * 
	 * @param activityResultIntent
	 *            result {@link Intent} from {@link Activity#onActivityResult(int, int, Intent)}
	 * @param ctx
	 *            Android {@link Activity} {@link Context}
	 * @return {@link Intent} to call for attachment application
	 */
	public static Intent getAttachmentIntentFromChooserResult(Intent activityResultIntent, Context ctx)
	{
		Intent intent = getAttachmentIntent(ctx);
		intent.setData(activityResultIntent.getData());
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		return intent;
	}


	/**
	 * Extracts the {@link Uri url} out of an attachment upload. The url is part of the result {@link Intent} in {@link Activity#onActivityResult()} of a former
	 * {@link #startAttachmentActivity()} call.
	 * 
	 * @param activityResultIntent
	 *            Result {@link Intent} from {@link Activity#onActivityResult(int, int, Intent)}.
	 * @return the upload url as {@link Uri} or null, if the result {@link Intent} contains no url.
	 */
	public static Uri getUrlFromResult(Intent activityResultIntent)
	{
		Uri url = activityResultIntent.getData();
		if (url == null)
		{
			if (activityResultIntent.hasExtra(EXTRAS_URL))
			{
				url = Uri.parse(activityResultIntent.getStringExtra(EXTRAS_URL));
			}
		}
		return url;
	}


	private static boolean isValid(Uri uri)
	{
		return uri != null && (uri.getScheme().equalsIgnoreCase("file") || uri.getScheme().equalsIgnoreCase("content"));
	}
}

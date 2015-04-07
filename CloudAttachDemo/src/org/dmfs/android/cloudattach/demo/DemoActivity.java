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

package org.dmfs.android.cloudattach.demo;

import org.dmfs.android.cloudattach.sdk.AttachmentUtils;
import org.dmfs.android.cloudattach.sdk.PreviewLoaderCallback;
import org.dmfs.android.cloudattach.sdk.PreviewUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Simple demo activity to show how to call the attachment upload app and how to retrieve a preview of an attachment.
 */
public class DemoActivity extends Activity implements OnClickListener
{

	private static final String TAG = "debug DemoActivity";
	private static final int REQUEST_CODE_UPLOAD_ATTACHMENT = 1;
	private static final int REQUEST_CODE_URI_CHOOSER = 2;

	private TextView mUriView;
	private ImageView mImageView;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		findViewById(R.id.btn_1).setOnClickListener(this);
		findViewById(R.id.btn_2).setOnClickListener(this);

		mUriView = (TextView) findViewById(R.id.tv_1);
		mImageView = (ImageView) findViewById(R.id.img_1);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent activityResultIntent)
	{
		if (resultCode == RESULT_OK)
		{
			if (requestCode == REQUEST_CODE_UPLOAD_ATTACHMENT)
			{
				// response from attachment app
				mUriView.setText("attachment url: \n" + AttachmentUtils.getUrlFromResult(activityResultIntent));

				// get a preview of the new attachment
				PreviewUtils.getPreview(this, 0, activityResultIntent, new PreviewLoaderCallback()
				{

					@Override
					public void onPreviewLoaded(long id, Bitmap thumbnail)
					{
						mImageView.setImageBitmap(thumbnail);
					}


					@Override
					public void onError(long id, Exception e)
					{
						Log.e(TAG, "failed to load attachment preview", e);
					}


					@Override
					public void onNoPreviewAppFound(long id)
					{
					}
				});
			}
			if (requestCode == REQUEST_CODE_URI_CHOOSER)
			{
				// called after user selected uri from another app
				AttachmentUtils.startAttachmentActivityFromChooserResult(activityResultIntent, this, REQUEST_CODE_UPLOAD_ATTACHMENT);
			}
		}
	}


	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_1:
			{
				AttachmentUtils.startAttachmentActivity(DemoActivity.this, REQUEST_CODE_UPLOAD_ATTACHMENT);
				break;
			}
			case R.id.btn_2:
			{
				AttachmentUtils.startChooserForAttachmentActivity(this, REQUEST_CODE_URI_CHOOSER);
				break;
			}
		}
	}
}

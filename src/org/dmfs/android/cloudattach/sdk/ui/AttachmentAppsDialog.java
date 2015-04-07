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
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


/**
 * A dialog that asks the user to install an attachment upload app. It loads the list of known apps from the <code>xml/cloudattach_sdk_apps.xml</code> resource.
 * 
 * @author Tristan Heinig <tristan@dmfs.org>
 * @author Marten Gajda <marten@dmfs.org>
 */
public class AttachmentAppsDialog extends DialogFragment implements OnItemClickListener, OnClickListener
{
	private ListView list;
	private List<App> mAppList;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View root = inflater.inflate(R.layout.cloudattach_dialog_available_apps, container, false);
		list = (ListView) root.findViewById(android.R.id.list);
		root.findViewById(android.R.id.button1).setOnClickListener(this);
		return root;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		try
		{
			// load the lost of apps
			mAppList = App.loadList(getActivity());
			AppAdapter adapter = new AppAdapter(getActivity(), R.layout.cloudattach_view_listitem_app, mAppList);
			list.setAdapter(adapter);
			list.setOnItemClickListener(this);
		}
		catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (XmlObjectPullParserException e)
		{
			e.printStackTrace();
		}
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// hide dialog title, we have our own
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// an app has been clicked, fire the Play Store intent
		App app = mAppList.get(position);
		try
		{
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app.packageName));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getActivity().startActivity(intent);
		}
		catch (ActivityNotFoundException e)
		{
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app.packageName));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getActivity().startActivity(intent);
		}

		// close the dialog
		dismiss();
	}


	@Override
	public void onClick(View v)
	{
		// click on cancel button
		getDialog().cancel();
	}


	/**
	 * Show the dialog.
	 * 
	 * @param fragmentManager
	 *            A {@link FragmentManager}.
	 */
	public static void show(FragmentManager fragmentManager)
	{
		new AttachmentAppsDialog().show(fragmentManager, AttachmentAppsDialog.class.getSimpleName());
	}
}
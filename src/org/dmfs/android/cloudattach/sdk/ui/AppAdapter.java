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

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


/**
 * An adapter for {@link App}s.
 * 
 * @author Tristan Heinig <tristan@dmfs.org>
 * @author Marten Gajda <marten@dmfs.org>
 */
class AppAdapter implements ListAdapter
{

	private final int mAppViewRessource;
	private final List<App> mAppList;
	private final LayoutInflater mInflater;


	public AppAdapter(Context context, int appViewResourceId, List<App> appList)
	{
		mAppViewRessource = appViewResourceId;
		mAppList = appList;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View result = convertView;
		if (result == null)
		{
			result = mInflater.inflate(mAppViewRessource, parent, false);
		}

		App app = getItem(position);

		((ImageView) result.findViewById(android.R.id.icon)).setImageResource(app.iconId);
		((TextView) result.findViewById(android.R.id.title)).setText(app.title);
		((TextView) result.findViewById(android.R.id.text1)).setText(app.description);

		return result;
	}


	@Override
	public int getCount()
	{
		return mAppList.size();
	}


	@Override
	public App getItem(int pos)
	{
		return mAppList.get(pos);
	}


	@Override
	public int getViewTypeCount()
	{
		return 1;
	}


	@Override
	public boolean hasStableIds()
	{
		return true;
	}


	@Override
	public boolean isEmpty()
	{
		return mAppList.size() == 0;
	}


	@Override
	public void registerDataSetObserver(DataSetObserver arg0)
	{
		// at present we don't support that
	}


	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0)
	{
		// at present we don't support that
	}


	@Override
	public boolean areAllItemsEnabled()
	{
		return true;
	}


	@Override
	public boolean isEnabled(int arg0)
	{
		return true;
	}


	@Override
	public long getItemId(int position)
	{
		return position;
	}


	@Override
	public int getItemViewType(int position)
	{
		return 0;
	}

}
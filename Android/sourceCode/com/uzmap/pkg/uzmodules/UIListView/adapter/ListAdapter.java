/**
 * APICloud Modules
 * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */
package com.uzmap.pkg.uzmodules.UIListView.adapter;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.util.OtherUtils;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;
import com.uzmap.pkg.uzkit.data.UZWidgetInfo;
import com.uzmap.pkg.uzmodules.UIListView.UIListView;
import com.uzmap.pkg.uzmodules.UIListView.ViewUtils.ViewHolder;
import com.uzmap.pkg.uzmodules.UIListView.ViewUtils.ViewUtil;
import com.uzmap.pkg.uzmodules.UIListView.data.Config;
import com.uzmap.pkg.uzmodules.UIListView.data.ItemData;
import com.uzmap.pkg.uzmodules.UIListView.data.Utils;

public class ListAdapter extends BaseAdapter {

	private Context mContext;
	private Config mConfig;
	private UZWidgetInfo mWInfo;

	private ArrayList<ItemData> mItemDatas;
	private BitmapUtils mBitmapUtils;

	private UZModuleContext mModuleContext;

	public ListAdapter(Context context, UZModuleContext uzContxt, ArrayList<ItemData> itemDatas, Config config, UZWidgetInfo wInfo) {

		this.mContext = context;
		this.mConfig = config;
		this.mItemDatas = itemDatas;
		this.mWInfo = wInfo;

		this.mModuleContext = uzContxt;

		mBitmapUtils = new BitmapUtils(context, OtherUtils.getDiskCacheDir(context, ""));

		// config the default bitmap
		Bitmap placeHolderBitmap = Utils.getBitmapFromLocal(config.itemPlaceholderImg, wInfo);
		mBitmapUtils.configDefaultLoadingImage(placeHolderBitmap);
		mBitmapUtils.configDefaultLoadFailedImage(placeHolderBitmap);

	}

	public ArrayList<ItemData> getItemDatas() {
		return this.mItemDatas;
	}

	public void setData(ArrayList<ItemData> itemDatas) {
		this.mItemDatas = itemDatas;
		this.notifyDataSetChanged();
	}

	public void deleteItem(int index) {
		if (mItemDatas != null) {
			mItemDatas.remove(index);
			notifyDataSetChanged();
		}
	}

	public void deleteAllData() {
		if (mItemDatas != null) {
			mItemDatas.clear();
			notifyDataSetChanged();
		}
	}

	public void insertData(int index, ItemData itemData) {
		if (mItemDatas != null) {
			mItemDatas.add(index, itemData);
			notifyDataSetChanged();
		}
	}

	public void appendData(ArrayList<ItemData> datas) {
		if (mItemDatas != null) {
			mItemDatas.addAll(datas);
			notifyDataSetChanged();
		}
	}

	public BitmapUtils getBitmapUtils() {
		return mBitmapUtils;
	}

	@Override
	public int getCount() {
		return mItemDatas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		ItemData curItemData = mItemDatas.get(arg0);

		ViewHolder viewHolder = null;

		int itemViewId = UZResourcesIDFinder.getResLayoutID("uilistview_item_layout");
		arg1 = View.inflate(mContext, itemViewId, null);

		viewHolder = createViewHolder(arg1);
		arg1.setTag(viewHolder);
		 
		/**
		 * item style setting
		 */
		setItemStyles(viewHolder, mConfig);

		/**
		 * item data setting
		 */
		setItemData(viewHolder, curItemData);

		/**
		 * click listener
		 */
		setClickListener(viewHolder, arg0);

		return arg1;
	}

	public void setClickListener(ViewHolder viewHolder, final int position) {

		viewHolder.mItemIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Utils.callback(mModuleContext, UIListView.EVENT_TYPE_ITEM_IMG_CLICK, position, -1);
			}
		});

		viewHolder.mIconIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Utils.callback(mModuleContext, UIListView.EVENT_TYPE_ITEM_REMARK_CLICK, position, -1);
			}
		});

		viewHolder.mRemarkTv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Utils.callback(mModuleContext, UIListView.EVENT_TYPE_ITEM_REMARK_CLICK, position, -1);
			}
		});

	}

	@SuppressWarnings("deprecation")
	public void setItemStyles(ViewHolder viewHolder, Config config) {

		/**
		 * item border set
		 */
		viewHolder.mBorder.setBackgroundColor(config.borderColor);
		RelativeLayout.LayoutParams borderParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, UZUtility.dipToPix(config.borderWidth));
		viewHolder.mBorder.setLayoutParams(borderParams);

		/**
		 * item height setting
		 */
		FrameLayout.LayoutParams frontViewParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, UZUtility.dipToPix(config.itemHeight));
		viewHolder.mFrontView.setLayoutParams(frontViewParams);

		/**
		 * item background setting
		 */
		viewHolder.mFrontView.setBackgroundDrawable(ViewUtil.addStateDrawable(config.itemBgColor, config.itemActiveBgColor));

		/**
		 * item ImageView setting
		 */
		RelativeLayout.LayoutParams itemIvParams = new RelativeLayout.LayoutParams(UZUtility.dipToPix(config.itemImgWidth), UZUtility.dipToPix(config.itemImgHeight));
		itemIvParams.addRule(RelativeLayout.CENTER_VERTICAL);
		int space = (UZUtility.dipToPix(config.itemHeight) - UZUtility.dipToPix(config.itemImgHeight)) / 2;

		itemIvParams.leftMargin = space;
		itemIvParams.topMargin = space;
		itemIvParams.bottomMargin = space;
		itemIvParams.rightMargin = space;

		viewHolder.mItemIv.setLayoutParams(itemIvParams);

		/**
		 * the title align
		 */
		RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		if ("left".equals(config.itemTitleAlign.trim())) {
			titleParams.addRule(RelativeLayout.RIGHT_OF, viewHolder.mItemIv.getId());
			titleParams.addRule(RelativeLayout.ALIGN_TOP, viewHolder.mItemIv.getId());
		}
		if ("center".equals(config.itemTitleAlign.trim())) {
			titleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			titleParams.addRule(RelativeLayout.ALIGN_TOP, viewHolder.mItemIv.getId());
		}
		if ("right".equals(config.itemTitleAlign.trim())) {
			titleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			titleParams.addRule(RelativeLayout.ALIGN_TOP, viewHolder.mItemIv.getId());
		}

		viewHolder.mTitleTv.setLayoutParams(titleParams);

		/**
		 * title color & size set
		 */
		viewHolder.mTitleTv.setTextColor(config.itemTitleColor);
		viewHolder.mTitleTv.setTextSize(config.itemTitleSize);

		/**
		 * the subTitle align
		 */
		RelativeLayout.LayoutParams subTitleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		if ("left".equals(config.itemSubTitleAlign.trim())) {
			subTitleParams.addRule(RelativeLayout.RIGHT_OF, viewHolder.mItemIv.getId());
			subTitleParams.addRule(RelativeLayout.ALIGN_BOTTOM, viewHolder.mItemIv.getId());
		}
		if ("center".equals(config.itemSubTitleAlign.trim())) {
			subTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			subTitleParams.addRule(RelativeLayout.ALIGN_BOTTOM, viewHolder.mItemIv.getId());
		}
		if ("right".equals(config.itemSubTitleAlign.trim())) {
			subTitleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			subTitleParams.addRule(RelativeLayout.ALIGN_BOTTOM, viewHolder.mItemIv.getId());
		}
		
		viewHolder.mSubTitleTv.setLayoutParams(subTitleParams);

		/**
		 * subTitle color & size set
		 */
		viewHolder.mSubTitleTv.setTextColor(config.itemSubTitleColor);
		viewHolder.mSubTitleTv.setTextSize(config.itemSubTitleSize);

		/**
		 * remark TextView set
		 */
		LinearLayout.LayoutParams remarkTvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		remarkTvParams.gravity = Gravity.CENTER_VERTICAL;
		remarkTvParams.rightMargin = space;
		viewHolder.mRemarkTv.setLayoutParams(remarkTvParams);

		viewHolder.mRemarkTv.setTextSize(config.itemRemarkSize);
		viewHolder.mRemarkTv.setTextColor(config.itemRemarkColor);

		/**
		 * remark ImageView (Icon) set
		 */
		LinearLayout.LayoutParams remarkTextParams = new LinearLayout.LayoutParams(UZUtility.dipToPix(config.itemRemarkIconWidth), UZUtility.dipToPix(config.itemRemarkIconWidth));
		remarkTextParams.gravity = Gravity.CENTER_VERTICAL;
		remarkTextParams.rightMargin = space;
		viewHolder.mIconIv.setLayoutParams(remarkTextParams);

		/**
		 * XXX 防止标题和子标题与备注重叠
		 */
		titleParams.addRule(RelativeLayout.LEFT_OF, viewHolder.remarkGroupLayout.getId());
		subTitleParams.addRule(RelativeLayout.LEFT_OF, viewHolder.remarkGroupLayout.getId());
	}

	public void setItemData(ViewHolder viewHolder, ItemData itemData) {

		viewHolder.mItemIv.setImageBitmap(null);
		viewHolder.mItemIv.setVisibility(View.GONE);

		// Item Image Set
		int whatPath = Utils.checkPath(itemData.imgPath);
		switch (whatPath) {
		case Utils.PATH_IS_LOCAL:
			viewHolder.mItemIv.setVisibility(View.VISIBLE);
			viewHolder.mItemIv.setImageBitmap(Utils.getBitmapFromLocal(itemData.imgPath, mWInfo));
			break;
		case Utils.PATH_IS_HTTP:
			viewHolder.mItemIv.setVisibility(View.VISIBLE);
			mBitmapUtils.display(viewHolder.mItemIv, itemData.imgPath);
			break;
		default:
			viewHolder.mItemIv.setImageBitmap(null);
			itemData.imgPath = null;
			// viewHolder.itemIv.setVisibility(View.GONE);
			break;
		}

		int space = (UZUtility.dipToPix(mConfig.itemHeight) - UZUtility.dipToPix(mConfig.itemImgHeight)) / 2;

		// Title & SubTitle Set
		viewHolder.mTitleTv.setText(itemData.title);
		if (TextUtils.isEmpty(itemData.subTitle)) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, viewHolder.mItemIv.getId());
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			if (TextUtils.isEmpty(itemData.imgPath)) {
				params.leftMargin = space;
			}
			viewHolder.mTitleTv.setLayoutParams(params);
		}

		viewHolder.mSubTitleTv.setText(itemData.subTitle);
		if (TextUtils.isEmpty(itemData.title)) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, viewHolder.mItemIv.getId());
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			if (TextUtils.isEmpty(itemData.imgPath)) {
				params.leftMargin = space;
			}
			viewHolder.mSubTitleTv.setLayoutParams(params);
		}

		if (!TextUtils.isEmpty(itemData.subTitle) && !TextUtils.isEmpty(itemData.title) && TextUtils.isEmpty(itemData.imgPath)) {

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.leftMargin = UZUtility.dipToPix(8);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.bottomMargin = UZUtility.dipToPix(8);
			viewHolder.mSubTitleTv.setLayoutParams(params);
			
			
			RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			titleParams.leftMargin = UZUtility.dipToPix(8); 
			titleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			titleParams.topMargin = UZUtility.dipToPix(8);
			viewHolder.mTitleTv.setLayoutParams(titleParams);

		}

		// RemakText Set
		viewHolder.mRemarkTv.setText(itemData.remark);

		// Icon Image Set
		viewHolder.mIconIv.setImageBitmap(null);
		viewHolder.mIconIv.setVisibility(View.GONE);

		whatPath = Utils.checkPath(itemData.icon);
		switch (whatPath) {
		case Utils.PATH_IS_LOCAL:
			viewHolder.mIconIv.setVisibility(View.VISIBLE);
			viewHolder.mIconIv.setImageBitmap(Utils.getBitmapFromLocal(itemData.icon, mWInfo));
			break;
		case Utils.PATH_IS_HTTP:
			viewHolder.mIconIv.setVisibility(View.VISIBLE);
			mBitmapUtils.display(viewHolder.mIconIv, itemData.icon);
			break;
		default:
			viewHolder.mIconIv.setImageBitmap(null);
			viewHolder.mIconIv.setVisibility(View.GONE);
			break;
		}
	}

	public ViewHolder createViewHolder(View itemView) {

		ViewHolder viewHolder = new ViewHolder();

		/**
		 * right group layout
		 */
		int rightGroupId = UZResourcesIDFinder.getResIdID("right_group");
		viewHolder.remarkGroupLayout = (LinearLayout) itemView.findViewById(rightGroupId);

		int borderId = UZResourcesIDFinder.getResIdID("itemBorder");
		viewHolder.mBorder = itemView.findViewById(borderId);

		/**
		 * the front view of the item
		 */
		int frontViewId = UZResourcesIDFinder.getResIdID("front");
		viewHolder.mFrontView = (RelativeLayout) itemView.findViewById(frontViewId);

		/**
		 * the container of left buttons
		 */
		int leftBtnsLayoutId = UZResourcesIDFinder.getResIdID("left_back");
		viewHolder.leftBtnsLayout = (LinearLayout) itemView.findViewById(leftBtnsLayoutId);

		/**
		 * the container of right buttons
		 */
		int rightBtnsLayoutId = UZResourcesIDFinder.getResIdID("right_back");
		viewHolder.leftBtnsLayout = (LinearLayout) itemView.findViewById(rightBtnsLayoutId);

		/**
		 * the left ImageView of the item
		 */
		int itemIvId = UZResourcesIDFinder.getResIdID("item_iv");
		viewHolder.mItemIv = (ImageView) itemView.findViewById(itemIvId);

		/**
		 * the title of the item
		 */
		int titleId = UZResourcesIDFinder.getResIdID("title_tv");
		viewHolder.mTitleTv = (TextView) itemView.findViewById(titleId);

		/**
		 * the subTitle of the item
		 */
		int subTitleId = UZResourcesIDFinder.getResIdID("subtitle_tv");
		viewHolder.mSubTitleTv = (TextView) itemView.findViewById(subTitleId);

		/**
		 * the remark TextView of the item
		 */
		int remarkTxtId = UZResourcesIDFinder.getResIdID("remark_tv");
		viewHolder.mRemarkTv = (TextView) itemView.findViewById(remarkTxtId);

		/**
		 * the icon ImageView of the item
		 */
		int iconId = UZResourcesIDFinder.getResIdID("icon_iv");
		viewHolder.mIconIv = (ImageView) itemView.findViewById(iconId);

		return viewHolder;
	}

}

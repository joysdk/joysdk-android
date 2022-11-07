package com.joysdk.android.base;

import android.content.Context;

public class ResourceExchange {
	
	private static ResourceExchange self;

	private Context mContext;

	public static ResourceExchange getInstance(Context context){
		if(self == null){
			self = new ResourceExchange(context);
		}
		return self;
	}

	private ResourceExchange(Context context){
		this.mContext = context.getApplicationContext();
	}

	public int getLayoutId(String resName){
		return ResourceUtil.getLayoutId(mContext,resName);
	}
	
	public int getIdId(String resName){
		return ResourceUtil.getId(mContext,resName);
	}
}

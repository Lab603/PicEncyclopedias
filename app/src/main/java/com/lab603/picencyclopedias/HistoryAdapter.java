package com.lab603.picencyclopedias;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.lab603.picencyclopedias.bean.RvItem;

import java.util.ArrayList;

/**
 *
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

	private Context mContext;
	private ArrayList<RvItem> mDataset = new ArrayList<>();
	private ButtonInterface buttonInterface;
//	private ArrayList<ImageView> ivDataset = new ArrayList<>();

	public HistoryAdapter(){
	}

	public void Init_DA(ArrayList<RvItem> dataset){
		mDataset = dataset;
		notifyDataSetChanged();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView mTextView;
		public ImageView imageView;
		public ImageView collection;
		public ViewHolder(View v) {
			super(v);
			mTextView = (TextView) v.findViewById(R.id.layout_item_demo_title);
			imageView = (ImageView) v.findViewById(R.id.layout_item_demo_image);
			collection = (ImageView) v.findViewById(R.id.collection);
		}
	}

	public HistoryAdapter(Context context, ArrayList<RvItem> dataset) {
		mDataset.clear();
		mDataset.addAll(dataset);
		mContext = context;
		notifyDataSetChanged();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_demo, parent, false);
		ViewHolder vh = new ViewHolder(v);
//		notifyDataSetChanged();
		return vh;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		holder.mTextView.setText(mDataset.get(position).getResult_str());
		holder.imageView.setImageBitmap(mDataset.get(position).getBitmap());
		holder.collection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(buttonInterface!=null) {
//                  接口实例化后的而对象，调用重写后的方法
				buttonInterface.onclick(view,position);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	public boolean addItem(RvItem rvItem) {
		Log.e("RvItem适配器",rvItem.getResult_str());
		if (true){
			mDataset.add(0,rvItem);
			notifyItemInserted(0);
//            notifyItemRangeChanged(0, mDataset.size());
//			notifyItemInserted(0);
			return true;
		}
		return false;
	}

	public boolean removeItem(int position) {
		if (position >= 0){
			mDataset.remove(position);
			return true;
		}
		return false;
	}


	public ArrayList<RvItem> getmDataset(){
		return mDataset;
	}

	/**
	 *按钮点击事件需要的方法
	 */
	public void buttonSetOnclick(ButtonInterface buttonInterface){
		this.buttonInterface=buttonInterface;
	}

	/**
	 * 按钮点击事件对应的接口
	 */
	public interface ButtonInterface{
		public void onclick( View view,int position);
	}

}

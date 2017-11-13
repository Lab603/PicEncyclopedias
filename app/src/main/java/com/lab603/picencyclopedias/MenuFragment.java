package com.lab603.picencyclopedias;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dou361.dialogui.DialogUIUtils;
import com.lab603.picencyclopedias.bean.RvItem;
import com.lab603.picencyclopedias.dao.CollectionData;
import com.lab603.picencyclopedias.dao.CollectionDataManager;
import com.lab603.picencyclopedias.dao.ToRVItems;

import org.tensorflow.demo.Classifier;
import org.tensorflow.demo.TensorFlowClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.attr.path;


/**
 * Created by EdwardZhang
 */
public class MenuFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

	private final static String TAG = "AlipayActivity";
	private static final int REQUEST_CODE_CAMERA = 777 ;
	private static final int REQUEST_CODE_PHOTO = 666;

	private FrameLayout fragmentContainer;
	private RecyclerView recyclerView,recyclerView_sv;
	private RecyclerView.LayoutManager layoutManager,layoutManager_sv;

	private AppBarLayout abl_bar;
	private View tl_expand, tl_collapse;
	private View v_expand_mask, v_collapse_mask, v_pay_mask;
	private int mMaskColor;
    private LinearLayout pick_photo_ly,take_photo_ly;
    private ImageView iv_pick,iv_scan;

	private View rootView,setView;
//    private CollectionData collectionData = new CollectionData();
	private RvItem rvItem_sv;
	HistoryAdapter adapter;
	HistoryAdapter adapter_sv = new HistoryAdapter();
	ArrayList<RvItem> itemsData = new ArrayList<>();
	ArrayList<RvItem> saveData = new ArrayList<>();
	ArrayList<String> stringArrayList = new ArrayList<>();
	private StringBuilder stringBuilder = new StringBuilder();
	private CollectionDataManager collectionDataManager;
	private Uri imageUri;
	/**
		Tensorflow data
	 */
	private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
	private static final String LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";
	private static final int NUM_CLASSES = 1001;
	private static final int INPUT_SIZE = 224;
	private static final int IMAGE_MEAN = 117;
	private static final float IMAGE_STD = 1;
	private static final String INPUT_NAME = "input:0";
	private static final String OUTPUT_NAME = "output:0";
	private final TensorFlowClassifier tensorflow = new TensorFlowClassifier();
	private TextView mResultText,setting_result,text_result;
	private Boolean flag = true;
	/**
	 * Create a new instance of the fragment
	 */
	public static MenuFragment newInstance(int index) {
		MenuFragment fragment = new MenuFragment();
		Bundle b = new Bundle();
		b.putInt("index", index);
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (collectionDataManager == null){
			collectionDataManager = new CollectionDataManager(getContext());
			collectionDataManager.openDataBase();//建立本地数据库
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(getArguments().getInt("index", 0) == 0) {
			View view = inflater.inflate(R.layout.fragment_homepage, container, false);
			initMenuList(view);
			return view;
		}else if(getArguments().getInt("index", 1) == 1){
			View view = inflater.inflate(R.layout.fragment_saved, container, false);
			Log.d(getTag(),"我出现了");
			initCameraView(view);
			return view;
		}else{
			View view = inflater.inflate(R.layout.fragment_settings, container, false);
			initHomeView(view);
			return view;
		}
	}

	private void initHomeView(View view) {
		Button DeleteALLButten = (Button) view.findViewById(R.id.deleteallbutton);
		DeleteALLButten.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				collectionDataManager.deleteAllDatas();
				Toast.makeText(getContext(),"Deleted all history",Toast.LENGTH_SHORT).show();
			}
		});
		Button About,First,Conceal,Allow,Support;
		About = (Button) view.findViewById(R.id.aboutPicEncyclopedias);
		First = (Button) view.findViewById(R.id.firstItem);
		Conceal = (Button) view.findViewById(R.id.conceal);
		Allow = (Button) view.findViewById(R.id.allow);
		Support = (Button) view.findViewById(R.id.support);
		About.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setView = View.inflate(getContext(), R.layout.setting_result, null);
				final Dialog dialog = DialogUIUtils.showCustomAlert(getContext(), setView, Gravity.CENTER, true, false).show();
				setView.findViewById(R.id.back_setting_result).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						DialogUIUtils.dismiss(dialog);
					}
				});
				setting_result = (TextView) setView.findViewById(R.id.setting_result);
				setting_result.setText("ABOUT:");
				text_result = (TextView) setView.findViewById(R.id.text_13);
				text_result.setText("Powerd by TensorFlow");
			}
		});
		First.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setView = View.inflate(getContext(), R.layout.setting_result, null);
				final Dialog dialog = DialogUIUtils.showCustomAlert(getContext(), setView, Gravity.CENTER, true, false).show();
				setView.findViewById(R.id.back_setting_result).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						DialogUIUtils.dismiss(dialog);
					}
				});
				setting_result = (TextView) setView.findViewById(R.id.setting_result);
				setting_result.setText("PREFERENCE:");
				text_result = (TextView) setView.findViewById(R.id.text_13);
				text_result.setText("Nothing ;)\n"+"\n"+"Have\n"+"a\n"+"good\n"+"time.\n"+"\n");
			}
		});
		Conceal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setView = View.inflate(getContext(), R.layout.setting_result, null);
				final Dialog dialog = DialogUIUtils.showCustomAlert(getContext(), setView, Gravity.CENTER, true, false).show();
				setView.findViewById(R.id.back_setting_result).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						DialogUIUtils.dismiss(dialog);
					}
				});
				setting_result = (TextView) setView.findViewById(R.id.setting_result);
				setting_result.setText("CONCEAL:");
				text_result = (TextView) setView.findViewById(R.id.text_13);
				text_result.setText("We guarantee that we will not collect any information from you.");
			}
		});
		Allow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setView = View.inflate(getContext(), R.layout.setting_result, null);
				final Dialog dialog = DialogUIUtils.showCustomAlert(getContext(), setView, Gravity.CENTER, true, false).show();
				setView.findViewById(R.id.back_setting_result).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						DialogUIUtils.dismiss(dialog);
					}
				});
				setting_result = (TextView) setView.findViewById(R.id.setting_result);
				setting_result.setText("LICENSE:");
				text_result = (TextView) setView.findViewById(R.id.text_13);
				text_result.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
				text_result.setText("MIT License\n" +
						"\n" +
						"Copyright (c) 2017 Lab603\n" +
						"\n" +
						"Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
						"of this software and associated documentation files (the \"Software\"), to deal\n" +
						"in the Software without restriction, including without limitation the rights\n" +
						"to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
						"copies of the Software, and to permit persons to whom the Software is\n" +
						"furnished to do so, subject to the following conditions:\n" +
						"\n" +
						"The above copyright notice and this permission notice shall be included in all\n" +
						"copies or substantial portions of the Software.\n" +
						"\n" +
						"THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
						"IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
						"FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
						"AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
						"LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
						"OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
						"SOFTWARE.");
			}
		});
		Support.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setView = View.inflate(getContext(), R.layout.setting_result, null);
				final Dialog dialog = DialogUIUtils.showCustomAlert(getContext(), setView, Gravity.CENTER, true, false).show();
				setView.findViewById(R.id.back_setting_result).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						DialogUIUtils.dismiss(dialog);
					}
				});
				setting_result = (TextView) setView.findViewById(R.id.setting_result);
				setting_result.setText("SUPPORT:");
				text_result = (TextView) setView.findViewById(R.id.text_13);
				text_result.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
				text_result.setText("\n" +
									"Repository:\n"+"https://github.com/Lab603/PicEncyclopedias\n" +
									"\n" +
									"App icon by:\n" + "http://http://iconfont.cn\n" +
									"\n" +
									"Use Material design\n" +
									"\n");
			}
		});

	}

	/**
	 * Init the fragment
	 */
	private void initMenuList(View view) {
		mMaskColor = getResources().getColor(R.color.colorStart_Button);
		abl_bar = (AppBarLayout) view.findViewById(R.id.abl_bar);
		tl_expand =  view.findViewById(R.id.tl_expand);
		tl_collapse = view.findViewById(R.id.tl_collapse);
		v_expand_mask = view.findViewById(R.id.v_expand_mask);
		v_collapse_mask = view.findViewById(R.id.v_collapse_mask);
		v_pay_mask = view.findViewById(R.id.v_pay_mask);
		abl_bar.addOnOffsetChangedListener(this);
		fragmentContainer = (FrameLayout) view.findViewById(R.id.fragment_container);
		recyclerView = (RecyclerView) view.findViewById(R.id.fragment_demo_recycler_view);
		recyclerView.setHasFixedSize(true);
		layoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(layoutManager);

		final AssetManager assetManager = getActivity().getAssets();
		tensorflow.initializeTensorFlow(
				assetManager, MODEL_FILE, LABEL_FILE, NUM_CLASSES, INPUT_SIZE, IMAGE_MEAN, IMAGE_STD,
				INPUT_NAME, OUTPUT_NAME);
		take_photo_ly = (LinearLayout) view.findViewById(R.id.take_photo);
		pick_photo_ly = (LinearLayout) view.findViewById(R.id.pick_photo);
		iv_scan = (ImageView) view.findViewById(R.id.iv_scan);
		iv_pick = (ImageView) view.findViewById(R.id.iv_pay);
		iv_scan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent_scan();
			}
		});
		take_photo_ly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent_scan();
			}
		});
		iv_pick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent_menu();
			}
		});

		pick_photo_ly.setOnClickListener(new LinearLayout.OnClickListener(){
			@Override
			public void onClick(View view) {
				Intent_menu();
			}
		});

		ToRVItems toRVItems = new ToRVItems();
		ArrayList<RvItem> rvItems_qy;
		if (collectionDataManager.getCount()!=0){
			rvItems_qy = toRVItems.queryAll(getContext(),collectionDataManager);
			adapter = new HistoryAdapter(getContext(),rvItems_qy);
		}else
			adapter = new HistoryAdapter(getContext(),itemsData);
		recyclerView.setAdapter(adapter);
		final ItemTouchHelper helper = new ItemTouchHelper(new MyCallBack());
		helper.attachToRecyclerView(recyclerView);
		recyclerView.addOnItemTouchListener(new OnItemTouchListener(recyclerView) {
			@Override
			public void onItemClick(RecyclerView.ViewHolder vh) {

			}

			@Override
			public void onItemLongPressClick(RecyclerView.ViewHolder vh) {
				if (vh.getAdapterPosition() != 0) {
					helper.startDrag(vh);
				}
			}
		});
		adapter.buttonSetOnclick(new HistoryAdapter.ButtonInterface() {
			@Override
			public void onclick(View view, int position) {
				if (!adapter.getmDataset().get(position).getSave()){
					RvItem rvItem = adapter.getmDataset().get(position);
					ImageView imageView = (ImageView)view.findViewById(R.id.collection);
					imageView.setImageResource(R.drawable.collection_2);
//					Log.e("rvItem收藏",rvItem.getUri());
					collectionDataManager.insertTOSave(rvItem.getUri(),rvItem.getResult_str());
//					rvItem_sv = adapter.getmDataset().get(position);
//					flag = false;
					adapter.getmDataset().get(position).setSave(true);
					adapter_sv.addItem(rvItem);
					adapter_sv.notifyDataSetChanged();
				}else {
					ImageView imageView = (ImageView)view.findViewById(R.id.collection);
					imageView.setImageResource(R.drawable.collection);
//					flag = true;
				}
			}});
		if (rvItem_sv!=null){
			adapter_sv.addItem(rvItem_sv);
			adapter_sv.notifyDataSetChanged();
		}
	}

	private void initCameraView(View view) {
		fragmentContainer = (FrameLayout) view.findViewById(R.id.fragment_container_camera);
		recyclerView_sv = (RecyclerView) view.findViewById(R.id.fragment_demo_rv_save);
		recyclerView_sv.setHasFixedSize(true);
		layoutManager_sv = new LinearLayoutManager(getActivity());
		recyclerView_sv.setLayoutManager(layoutManager_sv);
		ToRVItems toRVItems = new ToRVItems();
		if (collectionDataManager.getSVCount()!=0)
			adapter_sv.Init_DA(toRVItems.querySave(getContext(),collectionDataManager));
		else
			adapter_sv.Init_DA(saveData);
		adapter_sv.notifyDataSetChanged();
		Log.e("adapter_sv.size",adapter_sv.getItemCount()+"");
		recyclerView_sv.setAdapter(adapter_sv);
		recyclerView_sv.scrollToPosition(0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_CAMERA && resultCode == getActivity().RESULT_OK) {

			//直接取之前保存的路径
			Uri imgUri = imageUri;
			CollectionData collectionData = new CollectionData();
			collectionData.setUri(imgUri.toString());
			Log.e("imgUri", imgUri.toString());
			ContentResolver cr = this.getActivity().getContentResolver();
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(imgUri));
				dealPics(bitmap,collectionData);
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}

		}
		if (requestCode == REQUEST_CODE_PHOTO && resultCode == getActivity().RESULT_OK) {
			Uri uri = data.getData();
			CollectionData collectionData = new CollectionData();
			collectionData.setUri(uri.toString());
			Log.e("uri", uri.toString());
			ContentResolver cr = this.getActivity().getContentResolver();
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
				dealPics(bitmap,collectionData);
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void dealPics(Bitmap bitmap,CollectionData collectionData) {
		CollectionData collection_1 = new CollectionData();
		collection_1.setUri(collectionData.getUri());
		rootView = View.inflate(getContext(), R.layout.pick_result, null);
		final Dialog dialog = DialogUIUtils.showCustomAlert(getContext(), rootView, Gravity.CENTER, true, false).show();
		rootView.findViewById(R.id.back_result).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogUIUtils.dismiss(dialog);
			}
		});
		/* 将Bitmap设定到ImageView */
		final ImageView imageView = (ImageView) rootView.findViewById(R.id.iv01);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		System.out.println(width + "&&" + height);
		float scaleWidth = ((float) INPUT_SIZE) / width;
		float scaleHeight = ((float) INPUT_SIZE) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		imageView.setImageBitmap(newbm);

		final List<Classifier.Recognition> results = tensorflow.recognizeImage(newbm);
		for (final Classifier.Recognition result : results) {
			System.out.println("Result: " + result.getTitle());
		}
		mResultText = (TextView) rootView.findViewById(R.id.t01);
		mResultText.setText("Detected = " + results.get(0).getTitle());
		System.out.println(newbm.getWidth() + "&&" + newbm.getHeight());
		collection_1.setResult(results.get(0).getTitle());
		collection_1.setSave(false);
		adapter.addItem(ToRVItems.toRvItem(getContext(),collection_1));
//		adapter.notifyDataSetChanged();
		recyclerView.scrollToPosition(0);
		collectionDataManager.insertCollectionData(collection_1);

	}
	/**
	 * Refresh
	 */
	public void refresh() {
		if (getArguments().getInt("index", 0) > 0 && recyclerView != null) {
			recyclerView.smoothScrollToPosition(0);
		}
	}
	public  void  Intent_scan(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//path为保存图片的路径，执行完拍照以后能保存到指定的路径下
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TFIMG";
		File localFile = new File(filePath);
		if (!localFile.exists()) {
			localFile.mkdir();
		}
		File file = new File(localFile, System.currentTimeMillis() + ".jpg");
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion < 24) {
			// 从文件中创建uri
			imageUri = Uri.fromFile(file );
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		} else {
			//兼容android7.0 使用共享文件的形式
			ContentValues contentValues = new ContentValues(1);
			contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
			imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		}
		startActivityForResult(intent, REQUEST_CODE_CAMERA);


	}

	public void Intent_menu(){
		Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
		intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
		intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
		startActivityForResult(intent, REQUEST_CODE_PHOTO);
	}

	/**
	 * Called when a fragment will be displayed
	 */
	public void willBeDisplayed() {
		// Do what you want here, for example animate the content
		if (fragmentContainer != null) {
			Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
			fragmentContainer.startAnimation(fadeIn);
		}
	}

	/**
	 * Called when a fragment will be hidden
	 */
	public void willBeHidden() {
		if (fragmentContainer != null) {
			Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
			fragmentContainer.startAnimation(fadeOut);
		}
	}

	@Override
	public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
		Log.d(TAG, "verticalOffset="+verticalOffset);
		int offset = Math.abs(verticalOffset);
		int total = appBarLayout.getTotalScrollRange();
		int alphaIn = offset;
		int alphaOut = (200-offset)<0?0:200-offset;
		int maskColorIn = Color.argb(alphaIn, Color.red(mMaskColor),
				Color.green(mMaskColor), Color.blue(mMaskColor));
		int maskColorInDouble = Color.argb(alphaIn*2, Color.red(mMaskColor),
				Color.green(mMaskColor), Color.blue(mMaskColor));
		int maskColorOut = Color.argb(alphaOut*2, Color.red(mMaskColor),
				Color.green(mMaskColor), Color.blue(mMaskColor));
		if (offset <= total / 2) {
			tl_expand.setVisibility(View.VISIBLE);
			tl_collapse.setVisibility(View.GONE);
			v_expand_mask.setBackgroundColor(maskColorInDouble);
		} else {
			tl_expand.setVisibility(View.GONE);
			tl_collapse.setVisibility(View.VISIBLE);
			v_collapse_mask.setBackgroundColor(maskColorOut);
		}
		v_pay_mask.setBackgroundColor(maskColorIn);
	}

	public class MyCallBack extends ItemTouchHelper.Callback {
		/**
		 * 拖动标识
		 */
		private int dragFlags;
		/**
		 * 删除滑动标识
		 */
		private int swipeFlags;

		@Override
		public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
			dragFlags = 0;
			swipeFlags = 0;
			if (recyclerView.getLayoutManager() instanceof GridLayoutManager
					|| recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
				dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
						| ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
			} else {
				dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
				if (viewHolder.getAdapterPosition() >= 0)
					swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
			}
			return makeMovementFlags(dragFlags, swipeFlags);
		}

		@Override
		public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//			int fromPosition = viewHolder.getAdapterPosition();
//			int toPosition = target.getAdapterPosition();
//			if (toPosition >= 0) {
//				if (fromPosition < toPosition)
//					//向下拖动
//					for (int i = fromPosition; i < toPosition; i++) {
//						Collections.swap(itemsData, i, i + 1);
//					}
//				else {
//					//向上拖动
//					for (int i = fromPosition; i > toPosition; i--) {
//						Collections.swap(itemsData, i, i - 1);
//					}
//				}
//				recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
//			}
			return true;
		}

		@Override
		public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
			int positon = viewHolder.getAdapterPosition();
			recyclerView.getAdapter().notifyItemRemoved(positon);
			collectionDataManager.deleteCollectionData(adapter.getmDataset().get(positon).getUri());
			adapter.getmDataset().remove(positon);
//
//			recyclerView_sv.getAdapter().notifyItemRemoved(positon);
//			collectionDataManager.deleteCollectionData(adapter_sv.getmDataset().get(positon).getUri());
//			adapter_sv.getmDataset().remove(positon);
		}

		@Override
		public boolean isLongPressDragEnabled() {
			return false;
		}

		@Override
		public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
			if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
				viewHolder.itemView.setPressed(true);
			}
			super.onSelectedChanged(viewHolder, actionState);
		}

		@Override
		public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
			super.clearView(recyclerView, viewHolder);
			viewHolder.itemView.setPressed(false);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(getContext(),"save",Toast.LENGTH_SHORT).show();
	}
}
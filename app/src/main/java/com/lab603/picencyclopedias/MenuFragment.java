package com.lab603.picencyclopedias;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.lab603.picencyclopedias.PicPhotoSimple.SampleAdapter;
import com.lab603.picencyclopedias.pickphotoview.PickPhotoActivity;
import com.lab603.picencyclopedias.pickphotoview.PickPhotoView;
import com.lab603.picencyclopedias.pickphotoview.model.PickData;
import com.lab603.picencyclopedias.pickphotoview.util.PickConfig;
import com.werb.permissionschecker.PermissionChecker;

import org.tensorflow.demo.Classifier;
import org.tensorflow.demo.TensorFlowClassifier;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by EdwardZhang
 */
public class MenuFragment extends Fragment {

	private FrameLayout fragmentContainer;
	private RecyclerView recyclerView;
	private RecyclerView.LayoutManager layoutManager;
	private PickData pickData;
	private PermissionChecker permissionChecker;
	private String[] PERMISSIONS = new String[]{
			Manifest.permission.CAMERA,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};
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
	private TextView mResultText;

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
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(getArguments().getInt("index", 0) == 0) {
			View view = inflater.inflate(R.layout.fragment_demo_list, container, false);
			initMenuList(view);
			return view;
		}else if(getArguments().getInt("index", 1) == 1){

			View view = inflater.inflate(R.layout.fragment_camera, container, false);
			Log.d(getTag(),"我出现了");
			initCameraView(view);
			return view;
		}else{
			View view = inflater.inflate(R.layout.pk_home, container, false);
			return view;
		}
    }

	/**
	 * Init the fragment
	 */
	private void initMenuList(View view) {

		fragmentContainer = (FrameLayout) view.findViewById(R.id.fragment_container);
		recyclerView = (RecyclerView) view.findViewById(R.id.fragment_demo_recycler_view);
		recyclerView.setHasFixedSize(true);
		layoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(layoutManager);

		ArrayList<String> itemsData = new ArrayList<>();
		//TODO adapte image for everyitem.
		for (int i = 0; i < 50; i++) {
			itemsData.add("Fragment " + getArguments().getInt("index", -1) + " / Item " + i);

		}

		DemoAdapter adapter = new DemoAdapter(itemsData);
		recyclerView.setAdapter(adapter);
	}

	private void initCameraView(View view) {
		// test1 load tensorflow
		final AssetManager assetManager = getActivity().getAssets();
		tensorflow.initializeTensorFlow(
				assetManager, MODEL_FILE, LABEL_FILE, NUM_CLASSES, INPUT_SIZE, IMAGE_MEAN, IMAGE_STD,
				INPUT_NAME, OUTPUT_NAME);

		// test1 end
		permissionChecker = new PermissionChecker(getActivity());
		if(permissionChecker.isLackPermissions(PERMISSIONS)){
			permissionChecker.requestPermissions();
		}


		final Button button = (Button) view.findViewById(R.id.b01);
		button.setText("Pick A Photo");
		button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
//				new PickPhotoView.Builder(getActivity())
//						.setPickPhotoSize(1)
//						.setShowCamera(true)
//						.setSpanCount(3)
//						.setLightStatusBar(true)
//						.setStatusBarColor("#ffffff")
//						.setToolbarColor("#ffffff")
//						.setToolbarIconColor("#000000")
//						.setClickSelectable(false)
//						.setShowGif(true)
//						.start();
				Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
				intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
				intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
				startActivityForResult(intent, 1);

			}

		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == getActivity().RESULT_OK) {
			Uri uri = data.getData();
			Log.e("uri", uri.toString());
			ContentResolver cr = this.getActivity().getContentResolver();
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
				dealPics(bitmap);
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == 0) {
//			return;
//		}
//		if (data == null) {
//			return;
//		}
//		if (requestCode == PickConfig.PICK_PHOTO_DATA) {
//			ArrayList<String> selectPaths = (ArrayList<String>) data.getSerializableExtra(PickConfig.INTENT_IMG_LIST_SELECT);
//			Uri uri = Uri.parse(selectPaths.get(0));
//			Log.e("uri", uri.toString());
//			ContentResolver cr = this.getActivity().getContentResolver();
//			try {
//				Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
//				dealPics(bitmap);
//			} catch (FileNotFoundException e) {
//				Log.e("Exception", e.getMessage(), e);
//			}
//		}

//	}



	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case PermissionChecker.PERMISSION_REQUEST_CODE:
				if (!permissionChecker.hasAllPermissionsGranted(grantResults)) {
					permissionChecker.showDialog();
				}
				break;
		}
	}


	private void dealPics(Bitmap bitmap) {
		final ImageView imageView = (ImageView) getView().findViewById(R.id.iv01);
                /* 将Bitmap设定到ImageView */

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
		mResultText = (TextView) getView().findViewById(R.id.t01);
		mResultText.setText("Detected = " + results.get(0).getTitle());

		System.out.println(newbm.getWidth() + "&&" + newbm.getHeight());


	}
	/**
	 * Refresh
	 */
	public void refresh() {
		if (getArguments().getInt("index", 0) > 0 && recyclerView != null) {
			recyclerView.smoothScrollToPosition(0);
		}
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
}

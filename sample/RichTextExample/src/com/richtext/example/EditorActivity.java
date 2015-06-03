package com.richtext.example;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.MediaColumns;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.richtext.example.ResizeLinearLayout.OnResizeListener;

/**
 * 灵感编辑
 * 
 * @author HUST_LH
 * 
 */
@SuppressLint("HandlerLeak")
public class EditorActivity extends Activity {

	private ResizeLinearLayout baseContent;

	private EditText articleTitle;

	private RichEditText contentRichEditText;

	private TextView completeImg;

	private ImageView galleryImg;


	private int appHeight;
	private int baseLayoutHeight;

	private int currentStatus;
	private static final int SHOW_TOOLS = 1;
	private static final int SHOW_KEY_BOARD = 2;
	private static final int RESIZE_LAYOUT = 1;

	private boolean flag = false; // 控制何时显示下方tools

	private InputHandler inputHandler = new InputHandler();

	private class InputHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case RESIZE_LAYOUT:
				if (msg.arg1 == SHOW_TOOLS) {
					currentStatus = SHOW_TOOLS;
				} else {
					currentStatus = SHOW_KEY_BOARD;
					baseLayoutHeight = baseContent.getHeight();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);
		baseContent = (ResizeLinearLayout) findViewById(R.id.editor_base_content);

		completeImg = (TextView) findViewById(R.id.editor_edit_complete);
		galleryImg = (ImageView) findViewById(R.id.editor_gallery_img);

		articleTitle = (EditText) findViewById(R.id.editor_article_title);
		contentRichEditText = (RichEditText) findViewById(R.id.editor_edit_area);

		appHeight = getAppHeight();
		initImageLoader(this);
		init();
	}

	private void init() {
		baseContent.setOnResizeListener(new OnResizeListener() {
			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {
				// TODO Auto-generated method stub
				int selector = SHOW_TOOLS;
				if (h < oldh) {
					selector = SHOW_KEY_BOARD;
				}
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = selector;
				inputHandler.sendMessage(msg);
			}
		});
		// 完成
		completeImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(EditorActivity.this,
						contentRichEditText.getText().toString(),
						Toast.LENGTH_SHORT).show();
			}
		});
		galleryImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gallery();
			}
		});
	}

	/**
	 * 获取应用显示区域高度。。。 PS:该方法放到工具类使用会报NPE ，怀疑是没有传入activity所致，没有深究
	 * 
	 * @return
	 */
	public int getAppHeight() {
		/**
		 * 获取屏幕物理尺寸高(单位：px)
		 */
		DisplayMetrics ds = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(ds);

		/**
		 * 获取设备状态栏高度
		 */
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, top = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			top = getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		/**
		 * 屏幕高度减去状态栏高度即为应用显示区域高度
		 */
		return ds.heightPixels - top;
	}

	/**
	 * 系统软键盘与工具栏的切换显示
	 */
	private void showTools(int id) {
		if (id == R.id.editor_gallery_img) {
			flag = false;
			// if (currentStatus == SHOW_TOOLS &&
			// contentRichEditText.hasFocus()) {
			if (currentStatus == SHOW_TOOLS) {
				showSoftKeyBoard();
			}
		} else {
			flag = true;
			if (currentStatus == SHOW_KEY_BOARD) {
				showSoftKeyBoard();
			}
		}
	}

	/**
	 * 反复切换系统软键盘
	 */
	private void showSoftKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 工具栏添加图片的逻辑
	 */
	public void gallery() {
		// 调用系统图库
		// Intent getImg = new Intent(Intent.ACTION_GET_CONTENT);
		// getImg.addCategory(Intent.CATEGORY_OPENABLE);
		// getImg.setType("image/*");
		Intent getImg = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(getImg, 1001);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1001: {
				// 添加图片
				Bitmap originalBitmap = null;
				Uri originalUri = data.getData();
				// try {
				// originalBitmap = BitmapFactory.decodeStream(resolver
				// .openInputStream(originalUri));
				// originalBitmap =
				// ImageUtils.loadImage(resolver.openInputStream(originalUri));
				originalBitmap = ImageLoader.getInstance().loadImageSync(
						originalUri.toString());
				if (originalBitmap != null) {
					contentRichEditText.addImage(originalBitmap,
							getAbsoluteImagePath(originalUri));
				} else {
					Toast.makeText(this, "获取图片失败", Toast.LENGTH_LONG).show();
				}
				//
				// } catch (FileNotFoundException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// Toast.makeText(this, e.getMessage(),
				// Toast.LENGTH_LONG).show();
				// }
				break;
			}
			default:
				break;
			}
		}
	}

	/**
	 * 获取指定uri的本地绝对路径
	 * 
	 * @param uri
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected String getAbsoluteImagePath(Uri uri) {
		// can post image
		String[] proj = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, proj, // Which columns to return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)

		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}
	
	public void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
		
	}
	
}

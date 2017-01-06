package com.richtext.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class RichEditText extends EditText {

	private Context mContext;

	private Editable mEditable;
	public RichEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public RichEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public RichEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	/**
	 * 添加一个图片
	 * 
	 * @param bitmap
	 * @param uri
	 */
	public void addImage(Bitmap bitmap, String filePath) {
		Log.i("imgpath", filePath);
		String pathTag = "<img src=\"" + filePath + "\"/>";
		SpannableString spanString = new SpannableString(pathTag);
		// 获取屏幕的宽高
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int bmWidth = bitmap.getWidth();//图片高度
		int bmHeight = bitmap.getHeight();//图片宽度
		int zoomWidth =  getWidth() - (paddingLeft + paddingRight);
		int zoomHeight = (int) (((float)zoomWidth / (float)bmWidth) * bmHeight);
		Bitmap newBitmap = zoomImage(bitmap, zoomWidth,zoomHeight);
		ImageSpan imgSpan = new ImageSpan(mContext, newBitmap);
		spanString.setSpan(imgSpan, 0, pathTag.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if(mEditable==null){
			mEditable = this.getText(); // 获取edittext内容
		}
		int start = this.getSelectionStart(); // 设置欲添加的位置
		start = start > mEditable.length() ? mEditable.length() : start;  // ensure not to get IndexOutOfBoundsException;
		mEditable.insert(start, spanString); // 设置spanString要添加的位置
		this.setText(mEditable);
		this.setSelection(start, spanString.length());
	}
	/**
	 * 
	 * @param bitmap
	 * @param filePath
	 * @param start
	 * @param end
	 */
	public void addImage(Bitmap bitmap, String filePath,int start,int end) {
		Log.i("imgpath", filePath);
		String pathTag = "<img src=\"" + filePath + "\"/>";
		SpannableString spanString = new SpannableString(pathTag);
		// 获取屏幕的宽高
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int bmWidth = bitmap.getWidth();//图片高度
		int bmHeight = bitmap.getHeight();//图片宽度
		int zoomWidth =  getWidth() - (paddingLeft + paddingRight);
		int zoomHeight = (int) (((float)zoomWidth / (float)bmWidth) * bmHeight);
		Bitmap newBitmap = zoomImage(bitmap, zoomWidth,zoomHeight);
		ImageSpan imgSpan = new ImageSpan(mContext, newBitmap);
		spanString.setSpan(imgSpan, 0, pathTag.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		Editable editable = this.getText(); // 获取edittext内容
		editable.delete(start, end);//删除
		editable.insert(start, spanString); // 设置spanString要添加的位置
	}
	/**
	 * 
	 * @param bitmap
	 * @param filePath
	 * @param start
	 * @param end
	 */
	public void addDefaultImage(String filePath,int start,int end) {
		Log.i("imgpath", filePath);
		String pathTag = "<img src=\"" + filePath + "\"/>";
		SpannableString spanString = new SpannableString(pathTag);
		// 获取屏幕的宽高
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_img);
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int bmWidth = bitmap.getWidth();//图片高度
		int bmHeight = bitmap.getHeight();//图片宽度
		int zoomWidth = getWidth() - (paddingLeft + paddingRight);
		int zoomHeight = (int) (((float)zoomWidth / (float)bmWidth) * bmHeight);
		Bitmap newBitmap = zoomImage(bitmap, zoomWidth,zoomHeight);
		ImageSpan imgSpan = new ImageSpan(mContext, newBitmap);
		spanString.setSpan(imgSpan, 0, pathTag.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if(mEditable==null){
			mEditable = this.getText(); // 获取edittext内容
		}
		mEditable.delete(start, end);//删除
		mEditable.insert(start, spanString); // 设置spanString要添加的位置
	}
	
	/**
	 * 对图片进行缩放
	 * @param bgimage
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	private Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		//如果宽度为0 保持原图
		if(newWidth == 0){
			newWidth = width;
			newHeight = height;
		}
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

	public void setRichText(String text){
		this.setText("");
		this.mEditable = this.getText();
		this.mEditable.append(text);
		//遍历查找
		String str ="<img src=\"([/\\w\\W/\\/.]*)\"\\s*/>";
		Pattern pattern = Pattern.compile(str);
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()){
			final String localFilePath = matcher.group(1);
			String matchString = matcher.group();
			final int matchStringStartIndex = text.indexOf(matchString);
			final int matchStringEndIndex = matchStringStartIndex + matchString.length();
			ImageLoader.getInstance().loadImage(localFilePath,getDisplayImageOptions(),new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String uri, View arg1) {
					// TODO Auto-generated method stub
					//插入一张默认图片
					addDefaultImage(localFilePath, matchStringStartIndex, matchStringEndIndex);
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoadingComplete(String uri, View arg1, Bitmap bitmap) {
					// TODO Auto-generated method stub
					addImage(bitmap, uri,matchStringStartIndex,matchStringEndIndex);
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		this.setText(mEditable);
	}
	
	private  DisplayImageOptions getDisplayImageOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
	        .showImageOnLoading(R.drawable.default_img)
	        .showImageForEmptyUri(R.drawable.default_img)
	        .showImageOnFail(R.drawable.default_img)
	        .cacheOnDisk(true)
	        .cacheInMemory(true)
	        .bitmapConfig(Bitmap.Config.RGB_565)
	        .considerExifParams(true).build();
		return options;
	}
	
	public String getRichText(){
		return this.getText().toString();
	}
}

package com.lm.musicplayerdemo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * 显示歌词用的View
 * 
 * @author android
 */
public class LyricView extends TextView {
	/**
	 * 歌词显示窗口的高度
	 */
	private float mHight;

	/**
	 * 歌词显示窗口的宽度
	 */
	private float mWidth;

	/**
	 * 当前高亮歌词的画笔
	 */
	private Paint mCurrentPaint;

	/**
	 * 当前非高亮歌词的画笔
	 */
	private Paint mNotCurrentPaint;

	/**
	 * 歌词间距高度
	 */
	private float mTextHeight = 40;

	/**
	 * 歌词字体的大小
	 */
	private float mTextSize = 25;

	/**
	 * 歌词行数
	 */
	private int mIndex = 0;

	/**
	 * 歌词中的句子实体集,即歌词文件中的所有歌词行
	 */
	private List<LyricContent> mSentenceEntities = new ArrayList<LyricContent>();

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public LyricView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * 设置歌词的句子实体集
	 * 
	 * @param sentenceentities
	 */
	public void setSentenceEntities(List<LyricContent> sentenceentities) {
		this.mSentenceEntities = sentenceentities;
	}

	/**
	 * 设置歌词的行数
	 * 
	 * @param index
	 *            歌词行数
	 */
	public void setIndex(int index) {
		this.mIndex = index;
	}

	/**
	 * 初始化绘制歌词的画笔
	 */
	private void init() {
		setFocusable(true);

		// 设置高亮部分歌词所需的画笔
		mCurrentPaint = new Paint();
		mCurrentPaint.setAntiAlias(true);
		mCurrentPaint.setTextAlign(Paint.Align.CENTER);

		// 设置当前非高亮歌词所需的画笔
		mNotCurrentPaint = new Paint();
		mNotCurrentPaint.setAntiAlias(true);
		mNotCurrentPaint.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (canvas == null) {
			return;
		}

		// 设置画笔的颜色
		mCurrentPaint.setColor(Color.BLUE);
		mNotCurrentPaint.setColor(Color.BLACK);

		// 设置画笔字体的大小和字体
		mCurrentPaint.setTextSize(mTextSize);
		mCurrentPaint.setTypeface(Typeface.SERIF);
		mNotCurrentPaint.setTextSize(mTextSize);
		mNotCurrentPaint.setTypeface(Typeface.SERIF);

		try {
			// 在歌词显示区域中间绘制当前显示的歌词
			canvas.drawText(mSentenceEntities.get(mIndex).getLyric(),
					mWidth / 2, mHight / 2, mCurrentPaint);

			// 临时变量,用来定位当前行之外的歌词的Y坐标位置
			float tempY = mHight / 2;
			// 绘制当前行之前的歌词
			for (int i = mIndex - 1; i >= 0; i--) {
				// 向上移动Y坐标
				tempY = tempY - mTextHeight;
				canvas.drawText(mSentenceEntities.get(i).getLyric(),
						mWidth / 2, tempY, mNotCurrentPaint);
			}

			tempY = mHight / 2;
			// 绘制当前行之后的歌词
			for (int i = mIndex + 1; i < mSentenceEntities.size(); i++) {
				// 向下移动Y坐标
				tempY = tempY + mTextHeight;
				canvas.drawText(mSentenceEntities.get(i).getLyric(),
						mWidth / 2, tempY, mNotCurrentPaint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		this.mWidth = w;
		this.mHight = h;
	}

}

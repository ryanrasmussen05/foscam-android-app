package com.rhino.foscam.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
 
public class ZoomImageView extends ImageView implements OnTouchListener {
 
	//matrices used to move and zoom image
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	
	//we can be in one of these 3 states
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	
	private int mode = NONE;
	
	private int viewWidth = -1;
	private int viewHeight = -1;
	private int bitmapWidth = -1;
	private int bitmapHeight = -1;
	
	private float minScale = 1;
	private float maxScale = 10;
	
	//needed for zooming
	private PointF startPoint = new PointF();
	private PointF middlePoint = new PointF();
	private float oldDist = 1f;
	
	public ZoomImageView(Context context) {
		this(context, null, 0);
	}
	
	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setOnTouchListener(this);
	}
	
	@Override
	public void onSizeChanged (int newWidth, int newHeight, int oldWidth, int oldHeight) {
		super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
		viewWidth = newWidth;
		viewHeight = newHeight;
	}
	
	public void setBitmap(Bitmap bitmap) {
		if(bitmap != null) {
			bitmapWidth = bitmap.getWidth();
			bitmapHeight = bitmap.getHeight();
			middlePoint.x = (viewWidth / 2) - (bitmapWidth / 2);
			middlePoint.y = (viewHeight / 2) - (bitmapHeight / 2);
			
			float stretchRatio = (float)viewWidth / (float)bitmapWidth;
			minScale = stretchRatio;
			
			matrix = new Matrix();
			matrix.postTranslate(middlePoint.x, middlePoint.y);
			matrix.postScale(stretchRatio, stretchRatio, viewWidth / 2, viewHeight / 2);
			this.setImageMatrix(matrix);
			
			middlePoint.x = viewWidth / 2;
			middlePoint.y = viewHeight / 2;
			
			setImageBitmap(bitmap);
		}
	}
	
	public void updateBitmap(Bitmap bitmap) {
		setImageBitmap(bitmap);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		
		case MotionEvent.ACTION_DOWN:
			startPoint.set(event.getX(), event.getY());
			mode = DRAG;
			break;
			
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if(oldDist > 10f) {
				savedMatrix.set(matrix);
				mode = ZOOM;
			}
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
			
		case MotionEvent.ACTION_MOVE:
			if(mode == DRAG) {
				drag(event);
			} else if(mode == ZOOM) {
				zoom(event);
			}
			break;
		}
		
		return true;
	}
	
	public void drag(MotionEvent event) {
		float[] values = new float[9];
		matrix.getValues(values);
		
		float leftEdge = values[Matrix.MTRANS_X];
		float rightEdge = (bitmapWidth * values[Matrix.MSCALE_X]) + leftEdge;
		float topEdge = values[Matrix.MTRANS_Y];
		float bottomEdge = (bitmapHeight * values[Matrix.MSCALE_Y]) + topEdge;
		boolean allowScrollLeft = (rightEdge > viewWidth) || (leftEdge > 0 && rightEdge < viewWidth);
		boolean allowScrollRight = (leftEdge < 0) || (leftEdge > 0 && rightEdge < viewWidth);
		boolean allowScrollDown = (topEdge < 0) || (topEdge > 0 && bottomEdge < viewHeight);
		boolean allowScrollUp = (bottomEdge > viewHeight) || (topEdge > 0 && bottomEdge < viewHeight);			

		float dx = event.getX() - startPoint.x;
		float dy = event.getY() - startPoint.y;
		
		startPoint.x = event.getX();
		startPoint.y = event.getY();

		if(dx > 0 && !allowScrollRight) {dx = 0;}
		else if(dx < 0 && !allowScrollLeft) {dx = 0;}
		
		if(dy > 0 && !allowScrollDown) {dy = 0;}
		else if(dy < 0 && !allowScrollUp) {dy = 0;}
		
		matrix.postTranslate(dx, dy);
		this.setImageMatrix(matrix);
	}
	
	public void zoom(MotionEvent event) {
		float newDist = spacing(event);
		
		boolean zoomIn = newDist > oldDist;
		float[] values = new float[9];
		matrix.getValues(values);
		
		if(zoomIn && (values[Matrix.MSCALE_X] > maxScale)) {return;}
		if(!zoomIn && (values[Matrix.MSCALE_X] < minScale)) {return;}
		
		if(newDist > 10f) {
			matrix.set(savedMatrix);
			float scale = (newDist / oldDist);
			matrix.postScale(scale, scale, middlePoint.x, middlePoint.y);
			this.setImageMatrix(matrix);
		}
	}
	
	//determine space between fingers
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float)Math.sqrt(x * x + y * y);
	}
	
}
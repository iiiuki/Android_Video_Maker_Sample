package com.hiep.video.maker.ui.edit.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.hiep.video.maker.R;


@SuppressLint("ClickableViewAccessibility")
public class ViewCircle extends View {
    private int circleColor =  Color.RED;
    private Paint paint;
    private Paint paintStroke;
    private Paint paintStroke1;
    private boolean isTouch=false;
    
    
    private float radius;
    private boolean isStroke=false;
    
    public ViewCircle(Context context)  {
        super(context);
        init(context, null);
    }

    public ViewCircle(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        paint = new Paint();
        paint.setAntiAlias(true);
        
        
        paintStroke=new Paint();
        paintStroke.setAntiAlias(true);
        paintStroke.setColor(getResources().getColor(R.color.trgb_5483af));

        paintStroke1=new Paint();
        paintStroke1.setAntiAlias(true);
        paintStroke1.setColor(Color.WHITE);
    }

    
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();

        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();

        int usableWidth = w - (pl + pr);
        int usableHeight = h - (pt + pb);
        
        int cx = pl + (usableWidth / 2);
        int cy = pt + (usableHeight / 2);
        
        if(!isStroke){
        	 radius = Math.min(usableWidth, usableHeight) / 2;
        	 if(isTouch){
                 paintStroke.setColor(Color.WHITE);
             	 canvas.drawCircle(cx, cy, radius, paintStroke);
             }
             paint.setColor(circleColor);
             canvas.drawCircle(cx, cy, radius-3, paint);
        }else{
        	 if(isTouch){
             	 canvas.drawCircle(cx, cy, radius+3, paintStroke);
                 canvas.drawCircle(cx, cy, radius+1, paintStroke1);
             }
             //paint.setColor(circleColor);
             paint.setColor(getResources().getColor(android.R.color.black));
             canvas.drawCircle(cx, cy, radius, paint);
        }
    }

	public boolean isTouch() {
		return isTouch;
	}

	public void setTouch(boolean isTouch) {
		this.isTouch = isTouch;
		invalidate();
	}
	 public void setCircleColor(int circleColor){
        this.circleColor = circleColor;
        invalidate();
    }

    public int getCircleColor(){
        return circleColor;
    }

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		invalidate();
	}

	public boolean isStroke() {
		return isStroke;
	}

	public void setStroke(boolean isStroke) {
		this.isStroke = isStroke;
		invalidate();
	}

    public void setPaintStroke(String color){
        paintStroke.setColor(Integer.parseInt(color));
        this.invalidate();
    }
}

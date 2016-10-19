package com.hiep.video.maker.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class EFStrokeTextView extends TextView {

    // fields
    private int mStrokeColor =Color.TRANSPARENT;
    private int mStrokeWidth = 0;
    private TextPaint mStrokePaint;
    private int alphaStroke = 255;
    
    // constructors
    public EFStrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EFStrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EFStrokeTextView(Context context) {
        super(context);
    }

    // getters + setters
    public void setStrokeColor(int color) {
        mStrokeColor = color;
    }

    public void setStrokeWidth(int width) {
        mStrokeWidth = width;
    }
    
    

    public int getmStrokeColor() {
		return mStrokeColor;
	}


	public int getmStrokeWidth() {
		return mStrokeWidth;
	}

	
	public int getAlphaStroke() {
		return alphaStroke;
	}

	public void setAlphaStroke(int alphaStroke) {
		this.alphaStroke = alphaStroke;
	}

    @Override
    public boolean onSetAlpha(int alpha) {
      setTextColor(getTextColors().withAlpha(alpha));
      setHintTextColor(getHintTextColors().withAlpha(alpha));
      setLinkTextColor(getLinkTextColors().withAlpha(alpha));
      return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        
        // lazy load
        if (mStrokePaint == null) {
            mStrokePaint = new TextPaint();
        }
        
        // copy
        TextPaint paint = getPaint();
        mStrokePaint.setTextSize(paint.getTextSize());
        mStrokePaint.setTypeface(paint.getTypeface());
        mStrokePaint.setFlags(paint.getFlags());
        mStrokePaint.setAlpha(paint.getAlpha());
        
        
        // custom
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        mStrokePaint.setAlpha(alphaStroke);
        
        String text = getText().toString();
        canvas.drawText(text, (getWidth() - mStrokePaint.measureText(text)) / 2, getBaseline(), mStrokePaint);
        super.onDraw(canvas);
    }

}
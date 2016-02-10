package com.sqsmv.productloc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.onbarcode.barcode.android.Code128;

public class BarcodeView extends View
{
    Code128 barcode;

    public BarcodeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        try
        {
            RectF bounds = new RectF(0, 0, 0, 0);
            barcode.drawBarcode(canvas, bounds);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void drawBarcode(Code128 inBarcode)
    {
        barcode = inBarcode;
        invalidate();
    }
}



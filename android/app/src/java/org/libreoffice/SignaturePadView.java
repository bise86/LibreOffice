/* -*- Mode: Java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*- */
package org.libreoffice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/** Small touch canvas used to create a handwritten signature image. */
final class SignaturePadView extends View {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Path> mPaths = new ArrayList<>();
    private Path mCurrentPath;

    SignaturePadView(Context context) {
        super(context);
        float density = getResources().getDisplayMetrics().density;
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2.5f * density);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        setBackgroundColor(Color.WHITE);
        setFocusable(true);
        setContentDescription(context.getString(R.string.signature_title));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Path path : mPaths) {
            canvas.drawPath(path, mPaint);
        }
        if (mCurrentPath != null) {
            canvas.drawPath(mCurrentPath, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentPath = new Path();
                mCurrentPath.moveTo(event.getX(), event.getY());
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentPath != null) {
                    mCurrentPath.lineTo(event.getX(), event.getY());
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mCurrentPath != null) {
                    if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                        mCurrentPath.lineTo(event.getX(), event.getY());
                        performClick();
                    }
                    mPaths.add(mCurrentPath);
                    mCurrentPath = null;
                    invalidate();
                }
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    void clear() {
        mPaths.clear();
        mCurrentPath = null;
        invalidate();
    }

    boolean isEmpty() {
        return mPaths.isEmpty() && mCurrentPath == null;
    }

    Bitmap toBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(Math.max(getWidth(), 1), Math.max(getHeight(), 1),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
        for (Path path : mPaths) {
            canvas.drawPath(path, mPaint);
        }
        if (mCurrentPath != null) {
            canvas.drawPath(mCurrentPath, mPaint);
        }
        return bitmap;
    }
}

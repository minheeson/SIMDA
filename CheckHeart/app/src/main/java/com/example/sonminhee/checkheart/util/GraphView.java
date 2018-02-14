package com.example.sonminhee.checkheart.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.example.sonminhee.checkheart.R;

/**
 * Created by yoodanbee on 2017. 10. 11..
 */

public class GraphView extends View {
    private ShapeDrawable mLineShape;
    private Paint mPointPaint;

    private float mThickness;
    private int[] mPoints, mPointX, mPointY;
    private int mPointSize, mPointRadius, mLineColor, mUnit, mOrigin, mDivide;

    static final String TAG = "GraphView";

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setTypes(context, attrs);
    }

    //그래프 옵션을 받는다
    private void setTypes(Context context, AttributeSet attrs) {
        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.GraphView);

        mPointPaint = new Paint();
        mPointPaint.setColor(types.getColor(R.styleable.GraphView_pointColor, Color.BLACK));
        mPointSize = (int) types.getDimension(R.styleable.GraphView_pointSize, 10);
        mPointRadius = mPointSize / 2;

        mLineColor = types.getColor(R.styleable.GraphView_lineColor, Color.BLACK);
        mThickness = types.getDimension(R.styleable.GraphView_lineThickness, 1);
    }

    //그래프 정보를 받는다
    public void setPoints(int[] points, int unit, int origin, int divide) {
        mPoints = points;   //y축 값 배열

        mUnit = unit;       //y축 단위
        mOrigin = origin;   //y축 원점
        mDivide = divide;   //y축 값 갯수
    }

    //그래프를 만든다
    public void draw() {
        Log.i(TAG, " :: draw() :: ");
        Path path = new Path();

        int height = getHeight();
        int[] points = mPoints;

        float gapx = (float) getWidth() / points.length;

        float gapy = (height - mPointSize) / mDivide;

        float halfgab = gapx / 2;

        int length = points.length;
        mPointX = new int[length];
        mPointY = new int[length];

        for (int i = 0; i < length; i++) {
            int x = (int) (halfgab + (i * gapx));
            int y = (int) (height - mPointRadius - (((points[i] / mUnit) - mOrigin) * gapy));

            mPointX[i] = x;
            mPointY[i] = y;

            if (i == 0)
                path.moveTo(x, y);
            else
                path.lineTo(x, y);
        }

        ShapeDrawable shape = new ShapeDrawable(new PathShape(path, 1, 1));
        shape.setBounds(0, 0, 1, 1);

        Paint paint = shape.getPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mLineColor);
        paint.setStrokeWidth(mThickness);
        paint.setAntiAlias(true);

        mLineShape = shape;
    }

    public void drawForBeforeDrawView() {
        Log.i(TAG, " :: drawForBeforeDrawView() :: ");
        //draw();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                draw();
                Log.i(TAG, " :: drawForBeforeDrawView() :: onGlobalLayout() ::");

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLineShape != null)
            mLineShape.draw(canvas);

        if (mPointX != null && mPointY != null) {
            int length = mPointX.length;
            for (int i = 0; i < length; i++)
                canvas.drawCircle(mPointX[i], mPointY[i], mPointRadius, mPointPaint);
        }
    }
}

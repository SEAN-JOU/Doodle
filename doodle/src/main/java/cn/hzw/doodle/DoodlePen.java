package cn.hzw.doodle;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import cn.hzw.doodle.core.IDoodle;
import cn.hzw.doodle.core.IDoodleItem;
import cn.hzw.doodle.core.IDoodlePen;

/**
 * 常用画笔
 */
public enum DoodlePen implements IDoodlePen {

    HAND, // 手绘
    COPY, // 仿制
    ERASER, // 橡皮擦
    TEXT(true), // 文本
    BITMAP(true); // 贴图

    private boolean mIsSelectable = false; // 画笔绘制的item是否可选
    private CopyLocation mCopyLocation;
    private Matrix mMatrix;

    DoodlePen() {
        this(false);
    }

    DoodlePen(boolean isSelectable) {
        mIsSelectable = isSelectable;
    }

    @Override
    public void config(IDoodleItem item, Paint paint) {
        DoodleItemBase doodleItem = (DoodleItemBase) item;
        if (doodleItem.getPen() == DoodlePen.COPY) { // 仿制需要偏移图片
            // 根据旋转值获取正确的旋转底图
            float transX = 0, transY = 0;
            float transXSpan = 0, transYSpan = 0;
            CopyLocation copyLocation = ((DoodlePath) item).getCopyLocation();
            // 仿制时需要偏移图片
            if (copyLocation != null) {
                transXSpan = copyLocation.getTouchStartX() - copyLocation.getCopyStartX();
                transYSpan = copyLocation.getTouchStartY() - copyLocation.getCopyStartY();
            }
            mMatrix.reset();
            mMatrix.postTranslate(-transX + transXSpan, -transY + transYSpan);
            if (item.getColor() instanceof DoodleColor) {
                ((DoodleColor) item.getColor()).setMatrix(mMatrix);
            }
        }
    }

    /**
     * 画笔制作的item是否可选，用于旋转、移动等特定操作
     *
     * @return
     */
    public boolean isSelectable() {
        return mIsSelectable;
    }

    public CopyLocation getCopyLocation() {
        if (this != COPY) {
            return null;
        }
        if (mCopyLocation == null) {
            synchronized (this) {
                if (mCopyLocation == null) {
                    mCopyLocation = new CopyLocation();
                    mMatrix = new Matrix();
                }
            }
        }
        return mCopyLocation;
    }

    @Override
    public void drawHelpers(Canvas canvas, IDoodle doodle) {
        if (this == COPY) {
            mCopyLocation.drawItSelf(canvas, doodle.getSize());
        }
    }

    @Override
    public IDoodlePen copy() {
        return this;
    }
}
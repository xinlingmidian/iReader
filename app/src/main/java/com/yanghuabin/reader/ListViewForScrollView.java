package com.yanghuabin.reader;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by 18016 on 2018/2/8.
 */

public class ListViewForScrollView extends ListView {
    View header;// 顶部布局文件
    View footer;// 底部布局文件
    int headerHeight;// 顶部布局文件的高度
    int firstVisibleItem;// 当前第一个可见的item的位置
    int scrollState;// listview 当前滚动状态
    boolean isRemark;// 标记，当前是在listview最顶端摁下的
    int startY, startX;// 摁下时的Y值

    int state;// 当前的状态
    final int NONE = 0;// 正常状态
    final int PULL = 1;// 提示下拉状态
    final int RELESE = 2;// 提示释放状态
    final int REFLASHING = 3;// 刷新状态
    IReflashListener iReflashListener;//刷新数据的接口
    ILoadMoreDataListener iLoadMoreDataListener;//加载更多数据接口
    private int mTouchSlop, downX, spaceMove, tempCount;
    private boolean isFirshMeasure = true;

    private int totalItemCount;//总的显示数量
    private int lastVisibleItem; //最后一个可见的item
    private boolean isLoading; //是否正在加载更多数据
    private final Runnable smoothToRefreshStag = new Runnable() {
        @Override
        public void run() {
            if (spaceMove > 0) {
                spaceMove -= 10;
                //topPadding(spaceMove);
                postDelayed(this, 10);
            }
        }
    };

    public ListViewForScrollView(Context context) {
        super(context);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem == 0) {
                    isRemark = true;
                    startY = (int) ev.getY();
                    startX = (int) ev.getX();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                if (state == RELESE) {
                    state = REFLASHING;
                    // 加载最新数据
                    tempCount = 0;
                    //reflashViewByState();
                    iReflashListener.onReflash();
                } else if (state == PULL) {
                    state = NONE;
                    isRemark = false;
                    //reflashViewByState();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }




    public interface IReflashListener{
        void onReflash();
    }
    public interface ILoadMoreDataListener{
        void onLoadMoreData();
    }

    /**
     * 判断移动过程操作
     *
     * @param ev
     */
    private void onMove(MotionEvent ev) {
        if (!isRemark) {
            return;
        }
        int tempY = (int) ev.getY();
        int space = tempY - startY;
        int spaceX = (int) (startX - ev.getX());
//		Log.i("debug", "space " + space + " mTouchSlop " + mTouchSlop);
        if (space < mTouchSlop) {
            return;
        }

        int topPadding = space - headerHeight;
        spaceMove = topPadding;
        /*switch (state) {
            case NONE:
                if (space > 0) {
                    state = PULL;
                    //reflashViewByState();
                }
                break;
            case PULL:
                topPadding(topPadding);
                if (space > headerHeight + 30
                        && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELESE;
                    //reflashViewByState();
                }
                break;
            case RELESE:
                topPadding(topPadding);
                if (space < headerHeight + 30) {
                    state = PULL;
                    //reflashViewByState();
                } else if (space <= 0) {
                    state = NONE;
                    isRemark = false;
                    //reflashViewByState();
                }
                break;
        }*/
    }
}

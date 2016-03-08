package com.example.thanyapat.parkinapplication;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Tong on 1/20/2016.
 */
public class SlidePanel extends LinearLayout {
    private ViewDragHelper mDragHelper;
    protected ViewGroup whitePanel;
    protected ViewGroup namePanel;
    protected  ImageView staticMap;
    protected TextView nameText;
    protected  ViewGroup butPane;
    private Status status;
    private Animation show , hide, expand , shrink;
    MapFragment mapFrag;
    LayoutInflater mInflater;

    private ImageButton timerBtn;

    private int mDraggingBorder;
    private int mVerticalRange;
    private boolean mIsOpen;
    private final double AUTO_OPEN_SPEED_LIMIT = 1000.0;
    private int mDraggingState = 0;
    LayoutParams params ;
    DragHelperCallback dragHelper = new DragHelperCallback();

    public SlidePanel(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        init();

    }
    public SlidePanel(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        init();
    }
    public SlidePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init();
    }
    public void init()
    {
        mInflater.inflate(R.layout.slide_panel, this, true);
        //hiddenPanel = (ViewGroup)mapFrag.findViewById(R.id.hidden_panel);
        whitePanel = (ViewGroup)findViewById(R.id.white_pane);
        namePanel = (ViewGroup)findViewById(R.id.name_pane);
        nameText = (TextView)findViewById(R.id.name_text);
        butPane  = (ViewGroup)findViewById(R.id.buttonPane);
        staticMap = (ImageView)findViewById(R.id.static_map);
        params = (LayoutParams) whitePanel.getLayoutParams();
        //slidingLayout = (SlidingUpPanelLayout)mapFrag.findViewById(R.id.sliding_layout);
        status = Status.HIDDEN;
        this.setVisibility(View.INVISIBLE);
        mDragHelper = ViewDragHelper.create(this, 1.0f, dragHelper);
    }



    public void initAnim(MapFragment mapFrag){
        this.mapFrag = mapFrag;
        show = AnimationUtils.loadAnimation(mapFrag.getContext(), R.anim.show);
        hide = AnimationUtils.loadAnimation(mapFrag.getContext(), R.anim.hide);
    }

    public void initTimerBtn(final ParkingArea area){
        timerBtn = (ImageButton)findViewById(R.id.timerBut);
        timerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("SlidePanel","TimerBtn Clicked");
                ((MainActivity) mapFrag.getActivity()).setParkingAreaInTimerFragment(area);
                ((MainActivity) mapFrag.getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, ((MainActivity) mapFrag.getActivity()).fragmentList.get("timer")).commit();
            }
        });
    }

    public void show(ParkingArea area){

        //params.height = this.getHeight() - namePanel.getHeight();
        //whitePanel.setLayoutParams(params);
        if(status.equals(Status.HIDDEN)){

            setVisibility(View.VISIBLE);
            startAnimation(show);
            status = Status.SHOW;
            //hashMarker.get(marker.getId())
        }
        //setName
        nameText.setText(area.getName());
        String lat = "48.858235";
        String lng = "2.294571";
        setStaticMap(lat, lng);
        initTimerBtn(area);
    }
    public void hide(){
        if(status.equals(Status.SHOW)){
            startAnimation(hide);
            setVisibility(View.INVISIBLE);
            status = Status.HIDDEN;
        }
    }
    public void setStaticMap(String lat, String lng){
        final int IMAGE_WIDTH = 400;
        final int IMAGE_HEIGHT = 200;
        String url = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=15&size="+ IMAGE_WIDTH +"x"+IMAGE_HEIGHT+"&sensor=false";
        //String url = "http://maps.google.com/maps/api/staticmap?center=48.858235,2.294571&zoom=15&size=300x100&sensor=false";
        new DownloadImageTask(staticMap).execute(url);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {


        if (isTarget(event) /*&& mDragHelper.shouldInterceptTouchEvent(event)*/) {
            //Log.e("intercept", "intercept true");
            return true;
        } else {
            //Log.e("intercept", "intercept false");
            return false;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isTarget(event) || isMoving()) {
            mDragHelper.processTouchEvent(event);
            //Log.e("touch", "touch true");
            return true;
        } else {
            //Log.e("touch", "touch false");
            return super.onTouchEvent(event);
        }
    }

    private boolean isTarget(MotionEvent event) {
        int[] location = new int[2];
        //whitePanel.getLocationOnScreen(location);
        //int upperLimit = location[1] + whitePanel.getMeasuredHeight();
        //int lowerLimit = location[1] - namePanel.getMeasuredHeight();
        butPane.getLocationOnScreen(location);
        int upperLimit = location[1] + butPane.getMeasuredHeight();
        int lowerLimit = location[1] - butPane.getMeasuredHeight();
        int y = (int) event.getRawY();
        //Log.e("touch", "upper target: " + upperLimit +" & lower target: "+ lowerLimit);
        //Log.e("touch", "touch at: " + y);
        return (y > lowerLimit && y < upperLimit);
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View view, int pointerId) {
            //Log.e("capture", "Capture white is " + (view.getId() == R.id.white_pane) + " but it capture " + view.getId());
            //return (view.getId() == R.id.white_pane);
            return true;
        }
        @Override
        public void onViewDragStateChanged(int state) {
            if (state == mDraggingState) { // no change
                return;
            }
            if ((mDraggingState == ViewDragHelper.STATE_DRAGGING || mDraggingState == ViewDragHelper.STATE_SETTLING) &&
                    state == ViewDragHelper.STATE_IDLE) {
                // the view stopped from moving.

                if (mDraggingBorder == 0) {
                    onStopDraggingToClosed();
                } else if (mDraggingBorder == mVerticalRange) {
                    mIsOpen = true;
                }
            }
            if (state == ViewDragHelper.STATE_DRAGGING) {
                onStartDragging();
            }
            mDraggingState = state;
            Log.e("Drag" , "State changed : " + state);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //mDraggingBorder += dy;
            mDraggingBorder = top;

           // params.height = whitePanel.getHeight() - top;
           // whitePanel.setLayoutParams(params);

            //Log.e("Drag", "y: " + whitePanel.getY() + ", height: " + whitePanel.getHeight() +", top: " + top);]
        }

        public int getViewVerticalDragRange(View child) {
            return mVerticalRange;
        }
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = mVerticalRange;



            Log.e("Drag" , "clamp at topBound = " + topBound + " and bottom = " + bottomBound + " and top= " + top  + " and border= "+ mDraggingBorder);
            //return Math.min(Math.max(top, topBound), bottomBound);

            //to prevent exceed top
            //if(Math.abs(mDraggingBorder + 30 ) > bottomBound)return 0;
            return Math.min(top, bottomBound);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final float rangeToCheck = mVerticalRange;
            Log.e("Drag" , "Released. rangeToCheck: " + rangeToCheck + " and mDraggingBorder: " + mDraggingBorder);
            if (mDraggingBorder == 0) {
                mIsOpen = false;
                return;
            }
            if (Math.abs(mDraggingBorder) >= rangeToCheck) {
                //mDraggingBorder = (int)rangeToCheck * -1;
                mIsOpen = true;
                //return;
            }
            Log.e("Drag" , "Released. rangeToCheck: " + rangeToCheck + " and mDraggingBorder (new): " + mDraggingBorder);
            Log.e("Drag", "Released. y velocity = " + yvel);
            boolean settleToOpen = false;

            if (yvel > AUTO_OPEN_SPEED_LIMIT) { // speed has priority over position
                settleToOpen = true;
                //params.height = 210;
                //mDraggingBorder = 0;

            } else if (yvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
                //params.height = (int)(rangeToCheck - 70);
                //mDraggingBorder = mVerticalRange * -1;

            } else if (Math.abs(mDraggingBorder ) > rangeToCheck / 2) {
                settleToOpen = true;
                //params.height = (int)(rangeToCheck -70) ;
                //mDraggingBorder = mVerticalRange * -1;
            } else if (Math.abs(mDraggingBorder) < rangeToCheck / 2) {
                settleToOpen = false;
                //params.height = 210;
                //mDraggingBorder = 0;
            }
            //whitePanel.setLayoutParams(params);
           //int settleDestY = 0;
            move(settleToOpen);

            //Log.e("size" , "height = " + params.height);
            //Log.d("position", "position = " + SlidePanel.this.getY());
        }
        public void move(boolean settleToOpen){
            int settleDestY = settleToOpen ? mVerticalRange : 0;

            //if(yvel > AUTO_OPEN_SPEED_LIMIT) settleDestY = 0;
            //if(mDraggingBorder < 0)settleDestY = settleDestY * -1;
            //Log.e("drag" , "settle Dest Y : " + settleDestY );

            if(mDragHelper.settleCapturedViewAt(0, settleDestY)) {
                ViewCompat.postInvalidateOnAnimation(SlidePanel.this);
            }
        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //mVerticalRange = (int) (h * 0.66);
        int inPixel = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());
        mVerticalRange = h - inPixel;
        //mVerticalRange = h;

        Log.d("size" , "size changed: h = " + h);

        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void onStopDraggingToClosed() {
        // To be implemented
        Log.e("Drag" , "On stop drag to close");
    }

    private void onStartDragging() {
        Log.e("Drag" , "On start drag");
    }
    @Override
    public void computeScroll() { // needed for automatic settling.
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlidePanel.this);
        }
    }

    public boolean isMoving() {
        return (mDraggingState == ViewDragHelper.STATE_DRAGGING ||
                mDraggingState == ViewDragHelper.STATE_SETTLING);
    }

    public boolean isOpen() {
        if(status == Status.SHOW){
            return false;
        }else{
            return mIsOpen;
        }
    }

}



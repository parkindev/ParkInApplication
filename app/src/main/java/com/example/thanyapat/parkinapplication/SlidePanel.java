package com.example.thanyapat.parkinapplication;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tong on 1/20/2016.
 */
public class SlidePanel extends LinearLayout {
    private ParkingArea parkingArea;
    private ViewDragHelper mDragHelper;
    protected ViewGroup whitePanel;
    protected ViewGroup namePanel;
    protected TextView nameText;
    protected TextView capaText;
    protected TextView timeText;
    protected TextView addressText;
    protected TextView priceText;
    protected TextView priceValue;
    protected TextView reviewCount;
    protected TextView reviewAvg;
    protected  ViewGroup butPane;
    protected CustomScroll scrollView;
    protected TableLayout tableLayout;
    private Status status;
    protected CircularSeekBar circularSeekBar;
    private Animation show , hide, expand , shrink;
    MapFragment mapFrag;
    LayoutInflater mInflater;
    //review attribut
    LayoutInflater inflater;
    private LinearLayout reviewPane;
    protected Button reviewSend;
    protected EditText reviewString;
    //pic attribute
    private HashMap<String , Bitmap> picMap;
    protected  ImageView staticMap;
    protected  ImageView areaPic;
    protected String urlPic;
    protected ProgressBar progressIm;
    protected  ProgressBar progressStatic;
    //drag attribute
    private int mDraggingBorder;
    private int mVerticalRange;
    private boolean mIsOpen;
    private final double AUTO_OPEN_SPEED_LIMIT = 1000.0;
    private int mDraggingState = 0;
    LayoutParams params ;
    DragHelperCallback dragHelper = new DragHelperCallback();

    private ImageButton navBut;
    Context context;
    private String userName;
    int inviReview;

    public SlidePanel(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        this.context = context;
        init();

    }
    public SlidePanel(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        this.context = context;
        init();
    }
    public SlidePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init();
        this.context = context;
    }
    public void init()
    {
        inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.slide_panel, this, true);
        //hiddenPanel = (ViewGroup)mapFrag.findViewById(R.id.hidden_panel);
        whitePanel = (ViewGroup)findViewById(R.id.white_pane);
        namePanel = (ViewGroup)findViewById(R.id.name_pane);
        nameText = (TextView)findViewById(R.id.name_text);
        capaText = (TextView)findViewById(R.id.capacity);
        priceText = (TextView)findViewById(R.id.price_text);
        priceValue = (TextView)findViewById(R.id.price);
        addressText = (TextView)findViewById(R.id.address);
        timeText = (TextView)findViewById(R.id.time_text);
        reviewCount = (TextView)findViewById(R.id.review_count);
        reviewAvg = (TextView)findViewById(R.id.review_avg);
        reviewSend = (Button)findViewById(R.id.review_send);
        reviewString = (EditText)findViewById(R.id.review_string);
        butPane  = (ViewGroup)findViewById(R.id.buttonPane);
        staticMap = (ImageView)findViewById(R.id.static_map);
        reviewPane = (LinearLayout)findViewById(R.id.review_pane);
        areaPic = (ImageView)findViewById(R.id.area_pic);
        scrollView = (CustomScroll)findViewById(R.id.scroll_view);
        progressIm = (ProgressBar)findViewById(R.id.progress_image);
        progressStatic = (ProgressBar)findViewById(R.id.progress_static);
        circularSeekBar = (CircularSeekBar)findViewById(R.id.circular_seek);
        tableLayout = (TableLayout)findViewById(R.id.table_layout);
        //slidingLayout = (SlidingUpPanelLayout)mapFrag.findViewById(R.id.sliding_layout);
        status = Status.HIDDEN;
        this.setVisibility(View.INVISIBLE);
        mDragHelper = ViewDragHelper.create(this, 1.0f, dragHelper);

        picMap = new HashMap<String, Bitmap>();
        navBut = (ImageButton)findViewById(R.id.navBut);

        //scrollView.
        setButListener();
        setCircularSeekbar();
        //setScrollView();
    }
    public void initUser(){
        MainActivity main = (MainActivity)mapFrag.getActivity();
        try {
            userName = main.response.getJSONObject().getString("name");
        }catch (JSONException e){
            e.getStackTrace();
        }
    }
    public void setCircularSeekbar(){
        //circularSeekBar= new CircularSeekBar(mapFrag.getActivity());
        circularSeekBar.setMaxProgress(5);
        circularSeekBar.setProgress(5);
        circularSeekBar.setAngle(355);
        circularSeekBar.setBarWidth(40);
        circularSeekBar.setBackGroundColor(Color.WHITE);
        circularSeekBar.setProgressColor(Color.rgb(0, 136, 43));
       // circularSeekBar.setAdjustmentFactor(0.5f);
       // circularSeekBar.setRingBackgroundColor(Color.GREEN);
        circularSeekBar.setSeekBarChangeListener(new CircularSeekBar.OnSeekChangeListener(){
            @Override
            public void onProgressChange(CircularSeekBar view, int newProgress) {
                //Log.d("SeekBar", "Progress:" + view.getProgress() + "/" + view.getMaxProgress());
                int progress = view.getProgress();
                if(progress == 0)circularSeekBar.setProgressColor(Color.RED);
                if(progress == 1)circularSeekBar.setProgressColor(Color.RED);
                if(progress == 2)circularSeekBar.setProgressColor(Color.rgb(222,106,16));
                else if(progress == 3)circularSeekBar.setProgressColor(Color.rgb(222,189,35));
                else if(progress == 4)circularSeekBar.setProgressColor(Color.rgb(112,196,65));
                else if(progress == 5)circularSeekBar.setProgressColor(Color.rgb(0,136,43));
                circularSeekBar.setRating(progress);
            }
        });
        circularSeekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });
        circularSeekBar.invalidate();

    }
    public void setButListener(){
        navBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "navBut is clicked");
            }
        });
        reviewSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject review = new ParseObject("Review");
                review.put("rating", Integer.parseInt("" +circularSeekBar.getProgress()));
                review.put("comment", reviewString.getText().toString());
                review.put("areaId", parkingArea.getId());
                review.put("userName", userName);
                review.saveInBackground();
                Toast.makeText(mapFrag.getActivity(), "Thank you. Review has been sent",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    public void initAnim(MapFragment mapFrag){
        this.mapFrag = mapFrag;
        show = AnimationUtils.loadAnimation(mapFrag.getContext(), R.anim.show);
        hide = AnimationUtils.loadAnimation(mapFrag.getContext(), R.anim.hide);

    }
    public void show(ParkingArea area){
        initUser();
        scrollView.clearFocus();
        scrollView.fullScroll(ScrollView.FOCUS_UP);

        reviewString.setText("");
        circularSeekBar.setProgress(5);
        circularSeekBar.setAngle(355);
        //params.height = this.getHeight() - namePanel.getHeight();
        //whitePanel.setLayoutParams(params);
        if(status.equals(Status.HIDDEN)){
            setVisibility(View.VISIBLE);
            /*ObjectAnimator anim = ObjectAnimator.ofFloat(whitePanel, View.Y, this.getHeight(), this.getHeight() - butPane.getHeight());
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(namePanel, View.Y, this.getHeight(), this.getHeight() - butPane.getHeight() - namePanel.getHeight());
            anim2.setDuration(300);
            anim.setDuration(300);
            anim.setInterpolator(new AccelerateInterpolator());
            anim2.setInterpolator(new AccelerateInterpolator());
            anim.start();
            anim2.start();*/
            startAnimation(show);
            status = Status.SHOW;
            setFab(true);
            //hashMarker.get(marker.getId())
        }
        //setParkingArea
        this.parkingArea = area;
        //setName
        nameText.setText(area.getName());
        //setFreeHour
        int freeHour = 0;
        if(area.getFreeHour()!= 0){
            freeHour = setFreeHour(area);
        }else{
            priceValue.setText("");
            priceText.setText("");
        }
        //setPrice
        if(area.getPrice() != null) {
            setPriceText(area, freeHour);
        }

        //setCapa
        if(area.getCapacity()!= 0){
            capaText.setText(""+area.getCapacity());
        }else{
            capaText.setText("-");
        }
        //setTime
        if(area.getOpenTime() != null && area.getCloseTime() != null){
            try{
                int index = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                if(area.getOpenTime().getInt(index) ==0 && area.getCloseTime().getInt(index) == 24){
                    timeText.setText("24 Hours");
                }else{
                    timeText.setText("" + area.getOpenTime().getInt(index) + ":00 - " + area.getCloseTime().getInt(index) + ":00");
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //setAddress
        addressText.setText(area.getAddress());
        //setPic
        areaPic.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test));
        if(area.getPicURL()!=null) {
            urlPic = area.getPicURL();
            new DownloadImageTask(areaPic, picMap, progressIm).execute(urlPic);
        }else{
            progressIm.setVisibility(View.INVISIBLE);
        }
        //set Static Map
        String lat = "" +area.getLat();
        String lng = "" +area.getLong();
        setStaticMap(lat, lng);
        //setReview
        setReview(area);
    }
    public void hide(){
        if(status.equals(Status.SHOW)){
            startAnimation(hide);
            setVisibility(View.INVISIBLE);
            status = Status.HIDDEN;

        }
    }
    public void setReview(ParkingArea area){
        reviewPane.removeAllViews();
        inviReview = View.VISIBLE;
        //query
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.whereEqualTo("areaId", area.getId());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> reviewList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + reviewList.size() + " review(s)");
                    int count = reviewList.size();
                    double sum = 0;
                    for (ParseObject r : reviewList) {
                        View view = inflater.inflate(R.layout.review_layout, null);
                        TextView name = (TextView) view.findViewById(R.id.user_name);
                        TextView comment = (TextView) view.findViewById(R.id.user_comment);
                        ImageView pic = (ImageView) view.findViewById(R.id.rating_pic);
                        int rating = r.getInt("rating");
                        sum += rating;
                        switch (rating) {
                            case 1:
                                pic.setImageResource(R.drawable.rate1);
                                break;
                            case 2:
                                pic.setImageResource(R.drawable.rate2);
                                break;
                            case 3:
                                pic.setImageResource(R.drawable.rate3);
                                break;
                            case 4:
                                pic.setImageResource(R.drawable.rate4);
                                break;
                            case 5:
                                pic.setImageResource(R.drawable.rate5);
                                break;
                        }
                        //check if this user has commented or not

                        if (r.getString("userName").equals(userName)) {
                            inviReview = View.GONE;
                            Log.e("Review", "FUCKING GONEEEE MOTHERFUCKER");
                        }

                        name.setText(r.getString("userName"));
                        comment.setText(r.getString("comment"));
                        reviewPane.addView(view);

                    }
                    reviewSend.setVisibility(inviReview);
                    reviewString.setVisibility(inviReview);
                    circularSeekBar.setVisibility(inviReview);
                    scrollView.clearFocus();
                    reviewCount.setText("" + count + " review(s)");
                    if (count == 0) {
                        reviewAvg.setText("-");
                    } else {
                        double average = sum / (double) count;
                        // Log.e("Review" , "Real average: " + average);
                        double remainer = average % 1;
                        ///Log.e("Review" , "Remainer: " + remainer);
                        if (remainer == 0 || remainer <= 0.25) average = (int) average;
                        else if (remainer > 0.25 && remainer <= 0.75)
                            average = (double) ((int) average) + 0.5;
                        else average = (double) ((int) average) + 1;
                        reviewAvg.setText("" + average);
                        //Log.e("Review" , "Estimate average: " + average);
                    }


                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });




    }
    public void setFab(boolean isUp){
        int invi = isUp? View.INVISIBLE: View.VISIBLE;
        mapFrag.getRootView().findViewById(R.id.search_fab).setVisibility(invi);
        mapFrag.getRootView().findViewById(R.id.location_fab).setVisibility(invi);
    }
    public int setFreeHour(ParkingArea area){
        if(area.getFreeHour()< 60){
            priceText.setText(area.getFreeHour() + " Minutes");
        }else{
            priceText.setText("" +(area.getFreeHour()/60));
            if((area.getFreeHour()% 60) != 0)priceText.setText(priceText.getText() + " " + (area.getFreeHour()%60) +" Minutes");
        }
        priceValue.setText("FREE");
        return area.getFreeHour()/60; // return freeHour in Hour unit
    }
    public void setPriceText(ParkingArea area , int freeHour){
        String enter;
        final int CALCULATE_HOUR = 10;
        try {
            int pricePreviousHour = (int) area.getPrice().getJSONArray
                    ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                            || (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                            ? "weekEndPrice" : "weekDayPrice")
                    .getJSONArray(Calendar.HOUR_OF_DAY)
                    .get(freeHour);
            int start = freeHour;
            int end = freeHour;
            for(int i = freeHour+ 1 ; i < CALCULATE_HOUR ; i ++){
                if(area.getFreeHour() == 0 && start == 0){
                    enter ="";
                }else{
                    enter = "\n";
                }
                int priceThisHour = (int) area.getPrice().getJSONArray
                        ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                                || (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                                ? "weekEndPrice" : "weekDayPrice")
                        .getJSONArray(Calendar.HOUR_OF_DAY).get(i);
                //Log.e(" Price" , "i = "+ i +" ,previous = " + pricePreviousHour + " , this = " + priceThisHour + " , start = " + start + " , end = " + end);
                if(pricePreviousHour != priceThisHour){
                    if(start == end){
                        priceText.setText(priceText.getText() + enter + (start+1));
                    }else{
                        priceText.setText(priceText.getText() + enter + (start+1) + " - " + (end+1));
                    }
                }else if(i == 9){
                    if(start == end){
                        priceText.setText(priceText.getText() + enter + (start+1));
                    }else{
                        if(start ==0)priceText.setText(priceText.getText() + enter + "Per Hour");
                        else priceText.setText(priceText.getText() + enter + (start+1) + "+ " );
                    }
                }

                if(pricePreviousHour != priceThisHour || i ==9) {
                    if (pricePreviousHour == 0) {
                        priceValue.setText(priceValue.getText() + enter +"FREE");
                    } else {
                        priceValue.setText(priceValue.getText() + enter + pricePreviousHour);
                    }
                    start = i;
                }
                end = i;
                pricePreviousHour = priceThisHour;

            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setStaticMap(String lat, String lng){
        final int IMAGE_WIDTH = 850;
        final int IMAGE_HEIGHT = 350;
        String url = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng +
                "&zoom=17&size="+ IMAGE_WIDTH +"x"+IMAGE_HEIGHT+"&sensor=false"+"&markers=color:blue%7C" + lat + "," + lng;
        //String url = "http://maps.google.com/maps/api/staticmap?center=48.858235,2.294571&zoom=15&size=300x100&sensor=false";
        new DownloadImageTask(staticMap , picMap , progressStatic).execute(url);
    }
    public void setScrollView(){
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                if(scrollY == 0)SlidePanel.this.hide();
                //Log.e("Scroll", "onScrollChanged: "+ scrollY );

            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {


        if (isTarget(event) && mDragHelper.shouldInterceptTouchEvent(event)) {
            //Log.e("intercept", "intercept true");
            return true;
        } else {
            //Log.e("intercept", "intercept false");
            //navBut.callOnClick();
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
        int upperLimit = location[1] + butPane.getMeasuredHeight() + namePanel.getMeasuredHeight();
        int lowerLimit = location[1]/* - butPane.getMeasuredHeight()*/;
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
            Log.e("Drag", "State changed : " + state);
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



            //Log.e("Drag" , "clamp at topBound = " + topBound + " and bottom = " + bottomBound + " and top= " + top  + " and border= "+ mDraggingBorder);
            //return Math.min(Math.max(top, topBound), bottomBound);

            //to prevent exceed top
            //if(Math.abs(mDraggingBorder + 30 ) > bottomBound)return 0;
            return Math.min(top, bottomBound);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final float rangeToCheck = mVerticalRange;
            //Log.e("Drag" , "Released. rangeToCheck: " + rangeToCheck + " and mDraggingBorder: " + mDraggingBorder);
            if (mDraggingBorder == 0) {
                mIsOpen = false;
                return;
            }
            if (Math.abs(mDraggingBorder) >= rangeToCheck) {
                //mDraggingBorder = (int)rangeToCheck * -1;
                mIsOpen = true;
                //return;
            }
           // Log.e("Drag" , "Released. rangeToCheck: " + rangeToCheck + " and mDraggingBorder (new): " + mDraggingBorder);
            //Log.e("Drag", "Released. y velocity = " + yvel);
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
            setFab(!settleToOpen);

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



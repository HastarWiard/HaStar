package com.wizard.hastar.ui.money_manager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.jaeger.library.StatusBarUtil;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.EventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wizard.hastar.MyApplication;
import com.wizard.hastar.R;
import com.wizard.hastar.adapter.DialogTagChooseGridViewAdapter;
import com.wizard.hastar.adapter.MySwipeableItemAdapter;
import com.wizard.hastar.base.BaseActivity;
import com.wizard.hastar.ui.money_manager.model.Record;
import com.wizard.hastar.ui.money_manager.util.RecordManager;
import com.wizard.hastar.ui.money_manager.util.SettingManager;
import com.wizard.hastar.util.HaStarUtil;
import com.wizard.hastar.util.ToastUtil;
import com.wizard.hastar.widget.CustomSliderView;
import com.wizard.hastar.widget.DoubleSliderClickListener;
import com.wizard.hastar.widget.MyGridView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class AccountBookListViewActivity extends BaseActivity
        implements
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        MySwipeableItemAdapter.OnItemDeleteListener,
        MySwipeableItemAdapter.OnItemClickListener {

    private MaterialSearchView searchView;

    private Context mContext;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private RecyclerView.Adapter wrappedAdapter;
    private RecyclerViewSwipeManager recyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager recyclerViewTouchActionGuardManager;

    private MySwipeableItemAdapter mAdapter;

    private TextView emptyTip;

    private int lastPosition;
    private boolean undid = false;

    private final int EDITTING_RECORD = 0;

    private VerticalRecyclerViewFastScroller verticalRecyclerViewFastScroller;

    private double originalSum;

    private CircleImageView profileImage;
    private SliderLayout mDemoSlider;
    private FrameLayout infoLayout;

    private TextView titleExpense;
    private TextView titleSum;
    private SliderLayout titleSlider;

    private TextView userName;
    private TextView userEmail;

    private final double MIN_MONEY = 0;
    private final double MAX_MONEY = 99999;

    private double LEFT_MONEY = HaStarUtil.INPUT_MIN_EXPENSE;
    private double RIGHT_MONEY = HaStarUtil.INPUT_MAX_EXPENSE;
    private int TAG_ID = -1;
    private Calendar LEFT_CALENDAR = null;
    private Calendar RIGHT_CALENDAR = null;

    private TextView setMoney;
    private TextView noMoney;
    private TextView setTime;
    private TextView noTime;
    private TextView setTag;
    private TextView noTag;
    private TextView select;

    private TextView leftExpense;
    private TextView rightExpense;
    private TextView leftTime;
    private TextView rightTime;
    private ImageView tagImage;
    private TextView tagName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_book_list_view);
        mContext = this;

        userName = (TextView) findViewById(R.id.user_name);
        userEmail = (TextView) findViewById(R.id.user_email);


        int size = RecordManager.getInstance(MyApplication.getAppContext()).RECORDS.size();
        if (RecordManager.getInstance(MyApplication.getAppContext()).SELECTED_RECORDS == null) {
            RecordManager.getInstance(MyApplication.getAppContext()).SELECTED_RECORDS = new LinkedList<>();
        }
        RecordManager.getInstance(MyApplication.getAppContext()).SELECTED_RECORDS.clear();
        for (int i = 0; i < size; i++) {
            Record record = new Record();
            record.set(RecordManager.RECORDS.get(i));
            RecordManager.getInstance(MyApplication.getAppContext()).SELECTED_RECORDS.add(record);
        }
        RecordManager.getInstance(MyApplication.getAppContext()).SELECTED_SUM = Double.valueOf(RecordManager.getInstance(MyApplication.getAppContext()).SUM);
        originalSum = RecordManager.getInstance(MyApplication.getAppContext()).SELECTED_SUM;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("");

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        StatusBarUtil.setColorForDrawerLayout(this, mDrawer, getResources().getColor(R.color.colorAccent), 0);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setHint(mContext.getResources().getString(R.string.input_remark_to_search));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                progressDialog = new MaterialDialog.Builder(mContext)
                        .title(R.string.selecting_title)
                        .content(R.string.selecting_content)
                        .cancelable(false)
                        .progress(true, 0)
                        .show();
                new SelectRecordsByRemark(query).execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                Log.d("Saver", "onSearchViewShown");
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                Log.d("Saver", "onSearchViewClosed");
            }
        });

        emptyTip = (TextView) findViewById(R.id.empty_tip);
        emptyTip.setTypeface(HaStarUtil.GetTypeface());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        recyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        recyclerViewTouchActionGuardManager.
                setInterceptVerticalScrollingWhileAnimationRunning(true);
        recyclerViewTouchActionGuardManager.setEnabled(true);

        recyclerViewSwipeManager = new RecyclerViewSwipeManager();

        mAdapter = new MySwipeableItemAdapter(mContext, RecordManager.SELECTED_RECORDS, this, this);
        mAdapter.setEventListener(new MySwipeableItemAdapter.EventListener() {

            @Override
            public void onItemRemoved(int position) {
                activityOnItemRemoved(position);
            }

            @Override
            public void onItemPinned(int position) {
                activityOnItemPinned(position);
            }

            @Override
            public void onItemViewClicked(View v, boolean pinned) {
                int position = recyclerView.getChildAdapterPosition(v);
                if (position != RecyclerView.NO_POSITION) {
                    activityOnItemClicked(position);
                }
            }
        });

        adapter = mAdapter;
        wrappedAdapter = recyclerViewSwipeManager.createWrappedAdapter(mAdapter);
        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        animator.setSupportsChangeAnimations(false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(wrappedAdapter);
        recyclerView.setItemAnimator(animator);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            recyclerView.addItemDecoration(
                    new ItemShadowDecorator(
                            (NinePatchDrawable) ContextCompat.getDrawable(
                                    mContext, R.drawable.material_shadow_z1)));
        }
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(
                ContextCompat.getDrawable(mContext, R.drawable.list_divider_h), true));

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop
        recyclerViewTouchActionGuardManager.attachRecyclerView(recyclerView);
        recyclerViewSwipeManager.attachRecyclerView(recyclerView);

        verticalRecyclerViewFastScroller
                = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        verticalRecyclerViewFastScroller.setRecyclerView(recyclerView);

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerView.setOnScrollListener(
                verticalRecyclerViewFastScroller.getOnScrollListener());

        HaStarUtil.backupCoCoinRecord = null;

        if (RecordManager.SELECTED_RECORDS.size() == 0) {
            emptyTip.setVisibility(View.VISIBLE);
            verticalRecyclerViewFastScroller.setVisibility(View.INVISIBLE);
        } else {
            emptyTip.setVisibility(View.GONE);
            verticalRecyclerViewFastScroller.setVisibility(View.VISIBLE);
        }

        infoLayout = (FrameLayout) mDrawer.findViewById(R.id.info_layout);
        LinearLayout.LayoutParams infoLayoutParams = new LinearLayout.LayoutParams(infoLayout.getLayoutParams());
        infoLayoutParams.setMargins(0, HaStarUtil.getStatusBarHeight() - HaStarUtil.dpToPx(30), 0, 0);
        infoLayout.setLayoutParams(infoLayoutParams);

        profileImage = (CircleImageView) mDrawer.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SettingManager.getInstance().getLoggenOn()) {
                    ToastUtil.displayShortToast(mContext, "请在设置界面更改头像");
                } else {
                    ToastUtil.displayShortToast(mContext, "请在设置界面登录或注册");
                }
            }
        });

        mDemoSlider = (SliderLayout) findViewById(R.id.slider);

        HashMap<String, Integer> urls = HaStarUtil.GetDrawerTopUrl();

        for (String name : urls.keySet()) {
            CustomSliderView customSliderView = new CustomSliderView(this);
            // initialize a SliderLayout
            customSliderView
                    .image(urls.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            mDemoSlider.addSlider(customSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));

        titleExpense = (TextView) findViewById(R.id.title_expense);
        titleExpense.setTypeface(HaStarUtil.typefaceLatoLight);
        titleExpense.setText(HaStarUtil.GetInMoney((int) (double) RecordManager.getInstance(mContext).SELECTED_SUM));

        titleSum = (TextView) findViewById(R.id.title_sum);
        titleSum.setTypeface(HaStarUtil.typefaceLatoLight);
        titleSum.setText(RecordManager.getInstance(mContext).SELECTED_RECORDS.size() + "'s");

//        titleSlider = (SliderLayout)findViewById(R.id.title_slider);
//        titleSlider.getLayoutParams().height = 48;
//        titleSlider.getLayoutParams().width = 400 - HaStarUtil.dpToPx(60 * 2);
//
//        HashMap<String, Integer> urls2 = HaStarUtil.getTransparentUrls();
//
//        CustomTitleSliderView customTitleSliderView = new CustomTitleSliderView(0 + "'s", CoCoinFragmentManager.NUMBER_SLIDER);
//        customTitleSliderView
//                .image(urls2.get("0"))
//                .setScaleType(BaseSliderView.ScaleType.Fit);
//        titleSlider.addSlider(customTitleSliderView);
//
//        customTitleSliderView = new CustomTitleSliderView(HaStarUtil.GetInMoney(0), CoCoinFragmentManager.EXPENSE_SLIDER);
//        customTitleSliderView
//                .image(urls2.get("1"))
//                .setScaleType(BaseSliderView.ScaleType.Fit);
//        titleSlider.addSlider(customTitleSliderView);
//
//        titleSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
//        titleSlider.setCustomAnimation(new DescriptionAnimation());
//        titleSlider.setDuration(3000);
//        titleSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));

        ((TextView) findViewById(R.id.tag_title)).setTypeface(HaStarUtil.GetTypeface());
        ((TextView) findViewById(R.id.tag_title_expense)).setTypeface(HaStarUtil.GetTypeface());
        ((TextView) findViewById(R.id.tag_title_time)).setTypeface(HaStarUtil.GetTypeface());
        ((TextView) findViewById(R.id.tag_title_tag)).setTypeface(HaStarUtil.GetTypeface());

        setMoney = (TextView) findViewById(R.id.select_expense);
        setMoney.setTypeface(HaStarUtil.GetTypeface());
        setMoney.setOnClickListener(this);
        noMoney = (TextView) findViewById(R.id.no_expense);
        noMoney.setTypeface(HaStarUtil.GetTypeface());
        noMoney.setOnClickListener(this);
        setTime = (TextView) findViewById(R.id.select_time);
        setTime.setTypeface(HaStarUtil.GetTypeface());
        setTime.setOnClickListener(this);
        noTime = (TextView) findViewById(R.id.no_time);
        noTime.setTypeface(HaStarUtil.GetTypeface());
        noTime.setOnClickListener(this);
        setTag = (TextView) findViewById(R.id.select_tag);
        setTag.setTypeface(HaStarUtil.GetTypeface());
        setTag.setOnClickListener(this);
        noTag = (TextView) findViewById(R.id.no_tag);
        noTag.setTypeface(HaStarUtil.GetTypeface());
        noTag.setOnClickListener(this);
        select = (TextView) findViewById(R.id.select);
        select.setTypeface(HaStarUtil.GetTypeface());
        select.setOnClickListener(this);

        leftExpense = (TextView) findViewById(R.id.left_expense);
        leftExpense.setTypeface(HaStarUtil.GetTypeface());
        rightExpense = (TextView) findViewById(R.id.right_expense);
        rightExpense.setTypeface(HaStarUtil.GetTypeface());
        leftTime = (TextView) findViewById(R.id.left_time);
        leftTime.setTypeface(HaStarUtil.GetTypeface());
        rightTime = (TextView) findViewById(R.id.right_time);
        rightTime.setTypeface(HaStarUtil.GetTypeface());
        tagImage = (ImageView) findViewById(R.id.tag_image);
        tagName = (TextView) findViewById(R.id.tag_name);
        tagName.setTypeface(HaStarUtil.GetTypeface());

        setConditions();

        loadLogo();
    }

    private void setConditions() {
        if (LEFT_MONEY == MIN_MONEY)
            leftExpense.setText(mContext.getResources().getString(R.string.any));
        else leftExpense.setText(HaStarUtil.GetInMoney((int) LEFT_MONEY));
        if (RIGHT_MONEY == MAX_MONEY)
            rightExpense.setText(mContext.getResources().getString(R.string.any));
        else rightExpense.setText(HaStarUtil.GetInMoney((int) RIGHT_MONEY));
        if (LEFT_CALENDAR == null)
            leftTime.setText(mContext.getResources().getString(R.string.any));
        else {
            String dateString
                    = HaStarUtil.GetMonthShort(LEFT_CALENDAR.get(Calendar.MONTH) + 1)
                    + " " + LEFT_CALENDAR.get(Calendar.DAY_OF_MONTH) + " " +
                    LEFT_CALENDAR.get(Calendar.YEAR);
            leftTime.setText(dateString);
        }
        if (RIGHT_CALENDAR == null)
            rightTime.setText(mContext.getResources().getString(R.string.any));
        else {
            String dateString
                    = HaStarUtil.GetMonthShort(RIGHT_CALENDAR.get(Calendar.MONTH) + 1)
                    + " " + RIGHT_CALENDAR.get(Calendar.DAY_OF_MONTH) + " " +
                    RIGHT_CALENDAR.get(Calendar.YEAR);
            rightTime.setText(dateString);
        }
        if (TAG_ID == -1) {
            tagImage.setImageResource(R.drawable.tags_icon);
            tagName.setText(mContext.getResources().getString(R.string.any));
        } else {
            tagImage.setImageDrawable(HaStarUtil.GetTagIconDrawable(TAG_ID));
            tagName.setText(HaStarUtil.GetTagName(TAG_ID));
        }
    }

    private void changeTitleSlider() {
        titleExpense = (TextView) findViewById(R.id.title_expense);
        titleExpense.setText(HaStarUtil.GetInMoney((int) (double) RecordManager.getInstance(mContext).SELECTED_SUM));

        titleSum = (TextView) findViewById(R.id.title_sum);
        titleSum.setText(RecordManager.getInstance(mContext).SELECTED_RECORDS.size() + "'s");

//        titleSlider.stopAutoCycle();
//
//        if (CoCoinFragmentManager.numberCustomTitleSliderView != null)
//            CoCoinFragmentManager.numberCustomTitleSliderView.setTitle(RecordManager.getInstance(CoCoinApplication.getAppContext()).SELECTED_RECORDS.size() + "'s");
//        if (CoCoinFragmentManager.expenseCustomTitleSliderView != null)
//            CoCoinFragmentManager.expenseCustomTitleSliderView.setTitle(HaStarUtil.GetInMoney((int)(double)RecordManager.getInstance(CoCoinApplication.getAppContext()).SELECTED_SUM));
//
//        titleSlider.startAutoCycle();
    }

    private MaterialDialog progressDialog;

    @Override
    public void onSelectSumChanged() {
        changeTitleSlider();
    }

    private MaterialDialog dialog;
    private View dialogView;

    @Override
    public void onItemClick(int position) {
        position = RecordManager.SELECTED_RECORDS.size() - 1 - position;
        String subTitle;
        double spend = RecordManager.SELECTED_RECORDS.get(position).getMoney();
        int tagId = RecordManager.SELECTED_RECORDS.get(position).getTag();
        if ("zh".equals(HaStarUtil.GetLanguage())) {
            subTitle = HaStarUtil.GetSpendString((int) spend) +
                    "于" + HaStarUtil.GetTagName(tagId);
        } else {
            subTitle = "Spend " + (int) spend +
                    "in " + HaStarUtil.GetTagName(tagId);
        }
        dialog = new MaterialDialog.Builder(mContext)
                .icon(HaStarUtil.GetTagIconDrawable(RecordManager.SELECTED_RECORDS.get(position).getTag()))
                .limitIconToDefaultSize()
                .title(subTitle)
                .customView(R.layout.dialog_a_record, true)
                .positiveText(R.string.get)
                .show();
        dialogView = dialog.getCustomView();
        TextView remark = (TextView) dialogView.findViewById(R.id.remark);
        TextView date = (TextView) dialogView.findViewById(R.id.date);
        remark.setText(RecordManager.SELECTED_RECORDS.get(position).getRemark());
        date.setText(RecordManager.SELECTED_RECORDS.get(position).getCalendarString());
    }

    public class SelectRecordsByRemark extends AsyncTask<String, Void, String> {

        private String sub;

        public SelectRecordsByRemark(String sub) {
            this.sub = sub;
        }

        @Override
        protected String doInBackground(String... params) {
            RecordManager.getInstance(mContext).SELECTED_SUM = 0d;
            if (RecordManager.getInstance(mContext).SELECTED_RECORDS == null) {
                RecordManager.getInstance(mContext).SELECTED_RECORDS = new LinkedList<>();
            } else {
                RecordManager.getInstance(mContext).SELECTED_RECORDS.clear();
            }
            int size = RecordManager.getInstance(mContext).RECORDS.size();
            for (int i = 0; i < size; i++) {
                Record record = new Record();
                record.set(RecordManager.getInstance(mContext).RECORDS.get(i));
                if (inRemark(record, sub)) {
                    RecordManager.getInstance(mContext).SELECTED_SUM += record.getMoney();
                    RecordManager.getInstance(mContext).SELECTED_RECORDS.add(record);
                }
            }

            originalSum = RecordManager.getInstance(mContext).SELECTED_SUM;
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mAdapter.notifyDataSetChanged();

            changeTitleSlider();

            if (RecordManager.SELECTED_RECORDS.size() == 0) {
                emptyTip.setVisibility(View.VISIBLE);
                verticalRecyclerViewFastScroller.setVisibility(View.INVISIBLE);
            } else {
                emptyTip.setVisibility(View.GONE);
                verticalRecyclerViewFastScroller.setVisibility(View.VISIBLE);
            }

            if (progressDialog != null) progressDialog.cancel();
        }
    }

    public class SelectRecords extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            RecordManager.getInstance(mContext).SELECTED_SUM = 0d;
            if (RecordManager.getInstance(mContext).SELECTED_RECORDS == null) {
                RecordManager.getInstance(mContext).SELECTED_RECORDS = new LinkedList<>();
            } else {
                RecordManager.getInstance(mContext).SELECTED_RECORDS.clear();
            }
            int size = RecordManager.getInstance(mContext).RECORDS.size();
            for (int i = 0; i < size; i++) {
                Record record = new Record();
                record.set(RecordManager.getInstance(mContext).RECORDS.get(i));
                if (inMoney(record) && inTag(record) && inTime(record)) {
                    RecordManager.getInstance(mContext).SELECTED_SUM += record.getMoney();
                    RecordManager.getInstance(mContext).SELECTED_RECORDS.add(record);
                }
            }

            originalSum = RecordManager.getInstance(mContext).SELECTED_SUM;
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mAdapter.notifyDataSetChanged();

            changeTitleSlider();

            if (RecordManager.SELECTED_RECORDS.size() == 0) {
                emptyTip.setVisibility(View.VISIBLE);
                verticalRecyclerViewFastScroller.setVisibility(View.INVISIBLE);
            } else {
                emptyTip.setVisibility(View.GONE);
                verticalRecyclerViewFastScroller.setVisibility(View.VISIBLE);
            }

            if (progressDialog != null) progressDialog.cancel();
        }
    }

    private void selectRecords() {
        mDrawer.closeDrawers();
        progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.selecting_title)
                .content(R.string.selecting_content)
                .cancelable(false)
                .progress(true, 0)
                .show();
        new SelectRecords().execute();
    }

    private boolean inMoney(Record record) {
        return LEFT_MONEY <= record.getMoney() && record.getMoney() <= RIGHT_MONEY;
    }

    private boolean inTag(Record record) {
        if (TAG_ID == -1) return true;
        else return record.getTag() == TAG_ID;
    }

    private boolean inTime(Record record) {
        if (LEFT_CALENDAR == null || RIGHT_CALENDAR == null) return true;
        else
            return !record.getCalendar().before(LEFT_CALENDAR) && !record.getCalendar().after(RIGHT_CALENDAR);
    }

    private boolean inRemark(Record record, String sub) {
        return record.getRemark().contains(sub);
    }

    @Override
    protected void onStop() {
        mDemoSlider.stopAutoCycle();
//        titleSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onResume() {
        if (mDemoSlider != null) mDemoSlider.startAutoCycle();
        if (RecordManager.SELECTED_RECORDS == null) selectRecords();
        else {
//            if (titleSlider != null) titleSlider.startAutoCycle();
        }
        super.onResume();
    }

    private void activityOnItemRemoved(int position) {

        if (RecordManager.SELECTED_RECORDS.size() == 0) {
            emptyTip.setVisibility(View.VISIBLE);
            verticalRecyclerViewFastScroller.setVisibility(View.INVISIBLE);
        }

        lastPosition = RecordManager.SELECTED_RECORDS.size() - position;
        undid = false;
        Snackbar snackbar =
                Snackbar
                        .with(mContext)
                        .type(SnackbarType.MULTI_LINE)
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                        .position(Snackbar.SnackbarPosition.BOTTOM)
                        .margin(15, 15)
                        .backgroundDrawable(HaStarUtil.GetSnackBarBackground(-3))
                        .text(mContext.getResources().getString(R.string.deleting))
                        .textTypeface(HaStarUtil.GetTypeface())
                        .textColor(Color.WHITE)
                        .actionLabelTypeface(HaStarUtil.GetTypeface())
                        .actionLabel(mContext.getResources()
                                .getString(R.string.undo))
                        .actionColor(Color.WHITE)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                RecordManager.SELECTED_RECORDS.add(lastPosition, HaStarUtil.backupCoCoinRecord);
                                RecordManager.SELECTED_SUM += HaStarUtil.backupCoCoinRecord.getMoney();
                                changeTitleSlider();
                                HaStarUtil.backupCoCoinRecord = null;
                                LinearLayoutManager linearLayoutManager
                                        = (LinearLayoutManager) recyclerView.getLayoutManager();
                                int firstVisiblePosition = linearLayoutManager
                                        .findFirstCompletelyVisibleItemPosition();
                                int lastVisiblePosition = linearLayoutManager
                                        .findLastCompletelyVisibleItemPosition();
                                final int insertPosition
                                        = RecordManager.SELECTED_RECORDS.size() - 1 - lastPosition;
                                if (firstVisiblePosition < insertPosition
                                        && insertPosition <= lastVisiblePosition) {

                                } else {
                                    recyclerView.scrollToPosition(insertPosition);
                                }
                                mAdapter.notifyItemInserted(insertPosition);
                                mAdapter.notifyDataSetChanged();

                                if (RecordManager.SELECTED_RECORDS.size() != 0) {
                                    emptyTip.setVisibility(View.GONE);
                                    verticalRecyclerViewFastScroller.setVisibility(View.VISIBLE);
                                }
                            }
                        })
                        .eventListener(new EventListener() {
                            @Override
                            public void onShow(Snackbar snackbar) {

                            }

                            @Override
                            public void onShowByReplace(Snackbar snackbar) {

                            }

                            @Override
                            public void onShown(Snackbar snackbar) {

                            }

                            @Override
                            public void onDismiss(Snackbar snackbar) {
                                if (HaStarUtil.backupCoCoinRecord != null) {
                                    RecordManager.deleteRecord(HaStarUtil.backupCoCoinRecord, true);
                                }
                                HaStarUtil.backupCoCoinRecord = null;
                            }

                            @Override
                            public void onDismissByReplace(Snackbar snackbar) {
                                if (HaStarUtil.backupCoCoinRecord != null) {
                                    RecordManager.deleteRecord(HaStarUtil.backupCoCoinRecord, true);
                                }
                                HaStarUtil.backupCoCoinRecord = null;
                            }

                            @Override
                            public void onDismissed(Snackbar snackbar) {
                                if (HaStarUtil.backupCoCoinRecord != null) {
                                    RecordManager.deleteRecord(HaStarUtil.backupCoCoinRecord, true);
                                }
                                HaStarUtil.backupCoCoinRecord = null;
                            }
                        });
        SnackbarManager.show(snackbar);
    }

    private void activityOnItemPinned(int position) {
        mAdapter.notifyItemChanged(position);
        Intent intent = new Intent(mContext, EditRecordActivity.class);
        intent.putExtra("POSITION", position);
        startActivityForResult(intent, EDITTING_RECORD);
    }

    private void activityOnItemClicked(int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EDITTING_RECORD:
                if (resultCode == RESULT_OK) {
                    final int position = data.getIntExtra("POSITION", -1);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setPinned(false, position);
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 500);
                    changeTitleSlider();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_book_list_view, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }


    @Override
    protected void initData() {
        //TODO
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.END)) {
            mDrawer.closeDrawers();
            return;
        }
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {

        SettingManager.getInstance().setRecordIsUpdated(true);

        if (RecordManager.SELECTED_SUM != originalSum) {
            SettingManager.getInstance().setTodayViewMonthExpenseShouldChange(true);
        }

        if (HaStarUtil.backupCoCoinRecord != null) {
            RecordManager.deleteRecord(HaStarUtil.backupCoCoinRecord, true);
        }
        HaStarUtil.backupCoCoinRecord = null;

        super.finish();
    }

    @Override
    public void onDestroy() {
        if (recyclerViewSwipeManager != null) {
            recyclerViewSwipeManager.release();
            recyclerViewSwipeManager = null;
        }

        if (recyclerViewTouchActionGuardManager != null) {
            recyclerViewTouchActionGuardManager.release();
            recyclerViewTouchActionGuardManager = null;
        }

        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }

        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter);
            wrappedAdapter = null;
        }
        mAdapter = null;
        layoutManager = null;

        doubleSliderClickListener = null;

        RecordManager.SELECTED_RECORDS.clear();
        RecordManager.SELECTED_RECORDS = null;
        RecordManager.SELECTED_SUM = 0d;

        super.onDestroy();
    }

    private void loadLogo() {
        //TODO 加载头像
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_expense:
                setExpense();
                break;
            case R.id.no_expense:
                LEFT_MONEY = HaStarUtil.INPUT_MIN_EXPENSE;
                RIGHT_MONEY = HaStarUtil.INPUT_MAX_EXPENSE;
                setConditions();
                break;
            case R.id.select_time:
                setCalendar();
                break;
            case R.id.no_time:
                LEFT_CALENDAR = null;
                RIGHT_CALENDAR = null;
                setConditions();
                break;
            case R.id.select_tag:
                setTag();
                break;
            case R.id.no_tag:
                TAG_ID = -1;
                setConditions();
                break;
            case R.id.select:
                selectRecords();
                break;
            default:
                break;
        }
    }

    private double inputNumber = -1;

    private void setExpense() {
        inputNumber = -1;
        new MaterialDialog.Builder(mContext)
                .title(R.string.set_expense)
                .content(R.string.set_left_expense)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("≥" + (int) (double) HaStarUtil.INPUT_MIN_EXPENSE, "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        try {
                            inputNumber = Double.valueOf(String.valueOf(input));
                            if (inputNumber < HaStarUtil.INPUT_MIN_EXPENSE || inputNumber > HaStarUtil.INPUT_MAX_EXPENSE)
                                inputNumber = -1;
                        } catch (NumberFormatException n) {
                            inputNumber = -1;
                        }
                        if (inputNumber == -1)
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        else dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                })
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            LEFT_MONEY = inputNumber;
                            inputNumber = -1;
                            new MaterialDialog.Builder(mContext)
                                    .title(R.string.set_expense)
                                    .content(R.string.set_right_expense)
                                    .positiveText(R.string.ok)
                                    .negativeText(R.string.cancel)
                                    .inputType(InputType.TYPE_CLASS_NUMBER)
                                    .input("≤" + (int) (double) HaStarUtil.INPUT_MAX_EXPENSE, "", new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, CharSequence input) {
                                            try {
                                                inputNumber = Double.valueOf(String.valueOf(input));
                                                if (inputNumber < HaStarUtil.INPUT_MIN_EXPENSE || inputNumber > HaStarUtil.INPUT_MAX_EXPENSE)
                                                    inputNumber = -1;
                                            } catch (NumberFormatException n) {
                                                inputNumber = -1;
                                            }
                                            if (inputNumber == -1)
                                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                            else
                                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                        }
                                    })
                                    .onAny(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            if (which == DialogAction.POSITIVE) {
                                                RIGHT_MONEY = inputNumber;
                                                setConditions();
                                            }
                                        }
                                    })
                                    .alwaysCallInputCallback()
                                    .show();
                        }
                    }
                })
                .alwaysCallInputCallback()
                .show();
    }

    private boolean isFrom = true;

    private void setCalendar() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle(mContext.getResources().getString(R.string.set_left_calendar));
        dpd.show(((Activity) mContext).getFragmentManager(), "Datepickerdialog");
        isFrom = true;
    }

    private int fromYear, fromMonth, fromDay;
    private Calendar to = Calendar.getInstance();
    private Calendar from = Calendar.getInstance();

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (isFrom) {
            fromYear = year;
            fromMonth = monthOfYear + 1;
            fromDay = dayOfMonth;
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.setTitle(mContext.getResources().getString(R.string.set_right_calendar));
            dpd.show(((Activity) mContext).getFragmentManager(), "Datepickerdialog");
            isFrom = false;
        } else {
            from.set(fromYear, fromMonth - 1, fromDay, 0, 0, 0);
            from.add(Calendar.SECOND, 0);

            to.set(year, monthOfYear, dayOfMonth, 23, 59, 59);
            to.add(Calendar.SECOND, 0);

            if (to.before(from)) {
                ToastUtil.displayShortToast(this, "起始时间有误");
            } else {
                LEFT_CALENDAR = (Calendar) from.clone();
                RIGHT_CALENDAR = (Calendar) to.clone();
                setConditions();
            }
        }
    }

    private MyGridView myGridView;
    private DialogTagChooseGridViewAdapter dialogTagChooseGridViewAdapter;
    private MaterialDialog tagSelectDialog;
    private View tagSelectDialogView;

    private void setTag() {
        tagSelectDialog = new MaterialDialog.Builder(this)
                .title(R.string.set_tag)
                .customView(R.layout.dialog_select_tag, false)
                .negativeText(R.string.cancel)
                .show();
        tagSelectDialogView = tagSelectDialog.getCustomView();
        myGridView = (MyGridView) tagSelectDialogView.findViewById(R.id.grid_view);
        dialogTagChooseGridViewAdapter = new DialogTagChooseGridViewAdapter(mContext);
        myGridView.setAdapter(dialogTagChooseGridViewAdapter);

        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                tagSelectDialog.dismiss();
                TAG_ID = RecordManager.getInstance(mContext).TAGS.get(position + 2).getId();
                tagImage.setImageDrawable(HaStarUtil.GetTagIconDrawable(TAG_ID));
                tagName.setText(HaStarUtil.GetTagName(TAG_ID));
            }
        });
    }

    private DoubleSliderClickListener doubleSliderClickListener = new DoubleSliderClickListener() {
        @Override
        public void onSingleClick(BaseSliderView v) {

        }

        @Override
        public void onDoubleClick(BaseSliderView v) {
            if (recyclerView != null) recyclerView.scrollToPosition(0);
        }
    };

    private void toggleRightSliding() {//该方法控制右侧边栏的显示和隐藏
        if (mDrawer.isDrawerOpen(GravityCompat.END)) {
            mDrawer.closeDrawer(GravityCompat.END);//关闭抽屉
        } else {
            mDrawer.openDrawer(GravityCompat.END);//打开抽屉
        }
    }

}

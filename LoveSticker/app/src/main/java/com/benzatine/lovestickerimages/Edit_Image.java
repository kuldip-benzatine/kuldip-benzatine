package com.benzatine.lovestickerimages;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.benzatine.lovestickerimages.Data.ImageGetSet;
import com.benzatine.lovestickerimages.StickerImageView.EmojiStickerImageView;
import com.benzatine.lovestickerimages.StickerImageView.EmojiStickerView;
import com.benzatine.lovestickerimages.StickerImageView.StickerImageView;
import com.benzatine.lovestickerimages.StickerImageView.StickerView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.sdsmdg.tastytoast.ErrorToastView;
import com.sdsmdg.tastytoast.SuccessToastView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class Edit_Image extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, TabLayout.OnTabSelectedListener {

    static Context ctx;

    ImageView imgMain;
    ImageView imgSave;
    ImageView imgUndo;
    ImageView imgReset;
    static ImageView imgClose;
    ImageView imgRotate;
    ImageView imgSticker;
    ImageView imgText;
    ImageView imgEdit;
    ImageView imgColorPicker;
    ImageView imgShare;

    private ViewGroup mRrootLayout;

    private int _yDelta;
    static RelativeLayout /*layoutText,*/ llSticker_root;
    LinearLayout ColorPicker_layout;
    LinearLayout Animation_Layout;
    static LinearLayout rlGridView;
    EditText moveEdit;
    RelativeLayout frameScreen;


    static EmojiStickerImageView Emoji_iv_sticker;
    static StickerImageView iv_sticker;
    boolean IsDiscardButoon = false;
    static String SAVE = null;
    static Uri SharePath;

    static boolean StickerOn = false;

    public static int selectColor;

    RelativeLayout relativeLayout;
    Bitmap finalBitmap = null, rotatedBitmap = null;

    static String CameraId = "";

    public static boolean isAnimation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.edit_image);
        initAdd();
        initbanAdd();
        ctx = this;

        Firstoad();

        DrawingLoad();

        TabLayout();
        TextMove();

        init_action();
        showFullAdd();
    }

    LinearLayout linlaHeaderProgress;
    LinearLayout llSelectColor;
    ImageButton imgbtnStickerClose;

    public void init() {
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        llSelectColor = (LinearLayout) findViewById(R.id.llSelectColor);

        frameScreen = (RelativeLayout) findViewById(R.id.frameScreen);

        moveEdit = (EditText) findViewById(R.id.moveEdit);

        imgMain = (ImageView) findViewById(R.id.imgBitmap);

        imgSave = (ImageView) findViewById(R.id.imgSave);
        imgUndo = (ImageView) findViewById(R.id.imgUndo);
        imgReset = (ImageView) findViewById(R.id.imgReset);
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgRotate = (ImageView) findViewById(R.id.imgRotate);
        imgSticker = (ImageView) findViewById(R.id.imgSticker);
        imgText = (ImageView) findViewById(R.id.imgText);
        imgEdit = (ImageView) findViewById(R.id.imgEdit);
        imgShare = (ImageView) findViewById(R.id.imgShare);
        imgColorPicker = (ImageView) findViewById(R.id.imgColorPicker);

        llSticker_root = (RelativeLayout) findViewById(R.id.llSticker_root);
        ColorPicker_layout = (LinearLayout) findViewById(R.id.ColorPicker_layout);
        Animation_Layout = (LinearLayout) findViewById(R.id.Animation_Layout);

        imgbtnStickerClose = (ImageButton) findViewById(R.id.imgbtnStickerClose);
    }

    public void init_action() {
        imgSave.setOnClickListener(this);
        imgUndo.setOnClickListener(this);
        imgReset.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        imgRotate.setOnClickListener(this);
        imgSticker.setOnClickListener(this);
        imgText.setOnClickListener(this);
        imgEdit.setOnClickListener(this);
        moveEdit.setOnClickListener(this);
        imgShare.setOnClickListener(this);
        imgbtnStickerClose.setOnClickListener(this);
        setColorPicker();
        reloadFullAdd();
    }

    private static InterstitialAd mInterstitialAd;

    public void initAdd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.key_admob_interstitials));
        if (Utils.isOnline(this)) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    public static void showFullAdd() {
        if (Utils.isOnline(ctx)) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial was reloaded loaded yet.");
                showAdd = true;
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        }
    }

    static AdView mAdView;
    static AdRequest adRequest;

    public void initbanAdd() {
        mAdView = (AdView) findViewById(R.id.adView);
        if (Utils.isOnline(this)) {
            adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    static Boolean showAdd = false;

    public static void reloadFullAdd() {
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (showAdd) {
                    showFullAdd();
                    showAdd = false;
                }
            }
        });
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                adRequest = new AdRequest.Builder().build();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    Bitmap icon;
    Bitmap bitmap2;

    public void setColorPicker() {
        icon = BitmapFactory.decodeResource(getResources(), R.drawable.color_picker2);
        bitmap2 = createSingleImageFromMultipleImages(icon, 50f, Color.BLACK);
        imgColorPicker.setImageBitmap(bitmap2);
    }

    String CheckActivity;

    public void Firstoad() {

        Bundle extras = getIntent().getExtras();
        CheckActivity = extras.getString("CheckActivity");
//        CaptureURI = Uri.parse(extras.getString("EditImage"));
        init();
        CameraId = "";
        if (CheckActivity.equals("Main")) {
            CameraId = extras.getString("CameraId");
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            imgMain.setLayoutParams(layoutParams);
            new LoadImageTask().execute();
        } else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            imgMain.setLayoutParams(layoutParams);
            //openImageChooser();
            if (getIntent().getStringExtra("path") != null) {
                imgPath = getIntent().getStringExtra("path");
                setGellaryImage();
            }
        }
    }

    int treeHeight = 0, treeWidth = 0;

    private class LoadImageTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            linlaHeaderProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                ImageGetSet Product = new ImageGetSet();
                byte[] mData = Product.getImage_byteArray();
                Bitmap bmp = BitmapFactory.decodeByteArray(mData, 0, mData.length);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    finalBitmap = bmp;
                } else {
                    Matrix rotateMatrix = new Matrix();
                    if (CameraId.equals("FontCamera")) {
                        rotateMatrix.postRotate(-90);
                        finalBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), rotateMatrix, true);
                        bmp.recycle();
                    } else {
                        rotateMatrix.postRotate(90);
                        finalBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), rotateMatrix, true);
                        bmp.recycle();
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (CheckActivity.equals("Main")) {
                    imgMain.setImageBitmap(finalBitmap);
                    imgMain.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    imgMain.setImageBitmap(finalBitmap);
                }
                ViewTreeObserver vto = imgMain.getViewTreeObserver();
                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        imgMain.getViewTreeObserver().removeOnPreDrawListener(this);
                        treeHeight = imgMain.getMeasuredHeight();
                        treeWidth = imgMain.getMeasuredWidth();
                        return true;
                    }
                });
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    String imgPath = "";

    public void setGellaryImage() {
        try {
            finalBitmap = setPic(imgPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        imgMain.setImageBitmap(finalBitmap);

        if (finalBitmap != null) {
            int h = finalBitmap.getHeight();
            int w = finalBitmap.getWidth();
            if (h < w) {
                imgRotate.setVisibility(View.VISIBLE);
            }
        }

        ViewTreeObserver vto = imgMain.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imgMain.getViewTreeObserver().removeOnPreDrawListener(this);
                treeHeight = imgMain.getMeasuredHeight();
                treeWidth = imgMain.getMeasuredWidth();
                //tv.setText("Height: " + finalHeight + " Width: " + finalWidth);
                return true;
            }
        });
    }

    static boolean IsRotate;

    public void ClickRotate() {
        if (IsRotate) {
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(0);
            rotatedBitmap = Bitmap.createBitmap(finalBitmap, 0, 0, finalBitmap.getWidth(), finalBitmap.getHeight(), rotateMatrix, false);
//            finalBitmap.recycle();
            imgMain.setImageBitmap(rotatedBitmap);
            if (CheckActivity.equals("Main")) {
                imgMain.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            IsRotate = false;
        } else {
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(90);
            rotatedBitmap = Bitmap.createBitmap(finalBitmap, 0, 0, finalBitmap.getWidth(), finalBitmap.getHeight(), rotateMatrix, false);
//            finalBitmap.recycle();
            imgMain.setImageBitmap(rotatedBitmap);
//            imgMain.setScaleType(ImageView.ScaleType.FIT_CENTER);
            IsRotate = true;
        }
        ViewTreeObserver vto = imgMain.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imgMain.getViewTreeObserver().removeOnPreDrawListener(this);
                treeHeight = imgMain.getMeasuredHeight();
                treeWidth = imgMain.getMeasuredWidth();
                //tv.setText("Height: " + finalHeight + " Width: " + finalWidth);
                return true;
            }
        });
    }

    private WTDrawingView wtDrawingView;

    public void DrawingLoad() {
//        mPaint = new Paint();
//        mPaint.setAntiAlias(true);
//        mPaint.setDither(true);
////        mPaint.setColor(Color.GREEN);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeJoin(Paint.Join.ROUND);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setStrokeWidth(12);
//
//        rlGridView = (RelativeLayout) findViewById(R.id.gv_root);
//        relativeLayout = (RelativeLayout) findViewById(R.id.edit_image);
//        relativeLayout.addView(dv);
//        relativeLayout.setDrawingCacheEnabled(true);
        relativeLayout = (RelativeLayout) findViewById(R.id.edit_image);

        rlGridView = (LinearLayout) findViewById(R.id.gv_root);
        wtDrawingView = (WTDrawingView) findViewById(R.id.wtDrawingView);
//        wtDrawingView.bringToFront();
    }

//    public void GridViewLoad() {
//
//        Document doc = null;
//        try {
//            doc = getXMLDocument(getAssets().open("listdata.xml"));
//        } catch (IOException e1) {
//            e1.printStackTrace();
//            finish();
//        }
//        Element root = doc.getDocumentElement();
//        NodeList Nodes = root.getElementsByTagName("sticker");
//        cntList = Nodes.getLength();
//
////        gvSticker.setAdapter(new ImageAdapter(ctx, StickerOnClick));
//
////        gvSticker.setAdapter(new mDataListAdapter(ctx, cntList));
//
////        assetManager = getAssets();
////        try {
////            imgPath = null;
////            imgPath = assetManager.list("");
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//
////        gvSticker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////
////                hideStickerLayout();
////
////                InputStream is = null;
////                try {
////                    is = assetManager.open(String.valueOf(imgPath[position]));
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////                Bitmap bitmap = BitmapFactory.decodeStream(is);
////                iv_sticker = new StickerImageView(ctx);
//////                iv_sticker.setImageDrawable(getResources().getDrawable(R.drawable.c10));
////                iv_sticker.setImageBitmap(bitmap);
////                relativeLayout.addView(iv_sticker);
////            }
////        });
//    }

    private TabLayout tabLayout;
    ViewPager viewPager;
    int[] ICONS;

    public void TabLayout() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        /*tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());*/
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        ICONS = new int[]{
                R.drawable.ic_sticker_love_sel,
                R.drawable.ic_sticker_text,
                R.drawable.ic_sticker_line,
                R.drawable.ic_sticker_unicorn,
                R.drawable.ic_sticker_emoji
        };

        tabLayout.getTabAt(0).setIcon(ICONS[0]);
        tabLayout.getTabAt(1).setIcon(ICONS[1]);
        tabLayout.getTabAt(2).setIcon(ICONS[2]);
        tabLayout.getTabAt(3).setIcon(ICONS[3]);
        tabLayout.getTabAt(4).setIcon(ICONS[4]);

        viewPager.setCurrentItem(0);
        tabLayout.setOnTabSelectedListener(this);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StickerActivity(), "");
        adapter.addFragment(new TextEmojiActivity(), "");
        adapter.addFragment(new LineEmojiActivity(), "");
        adapter.addFragment(new OtherEmojiActivity(), "");
        adapter.addFragment(new EmojiActivity(), "");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()) {
            case 0:
                tabLayout.getTabAt(0).setIcon(R.drawable.ic_sticker_love_sel);
                tabLayout.getTabAt(1).setIcon(ICONS[1]);
                tabLayout.getTabAt(2).setIcon(ICONS[2]);
                tabLayout.getTabAt(3).setIcon(ICONS[3]);
                tabLayout.getTabAt(4).setIcon(ICONS[4]);
                break;
            case 1:
                tabLayout.getTabAt(0).setIcon(R.drawable.ic_sticker_love);
                tabLayout.getTabAt(2).setIcon(ICONS[2]);
                tabLayout.getTabAt(1).setIcon(R.drawable.ic_sticker_text_sel);
                tabLayout.getTabAt(3).setIcon(ICONS[3]);
                tabLayout.getTabAt(4).setIcon(ICONS[4]);
                break;
            case 2:
                tabLayout.getTabAt(0).setIcon(R.drawable.ic_sticker_love);
                tabLayout.getTabAt(1).setIcon(ICONS[1]);
                tabLayout.getTabAt(3).setIcon(ICONS[3]);
                tabLayout.getTabAt(2).setIcon(R.drawable.ic_sticker_line_sel);
                tabLayout.getTabAt(4).setIcon(ICONS[4]);
                break;
            case 3:
                tabLayout.getTabAt(0).setIcon(R.drawable.ic_sticker_love);
                tabLayout.getTabAt(1).setIcon(ICONS[1]);
                tabLayout.getTabAt(2).setIcon(ICONS[2]);
                tabLayout.getTabAt(3).setIcon(R.drawable.ic_sticker_unicorn_sel);
                tabLayout.getTabAt(4).setIcon(ICONS[4]);
                break;
            case 4:
                tabLayout.getTabAt(0).setIcon(R.drawable.ic_sticker_love);
                tabLayout.getTabAt(1).setIcon(ICONS[1]);
                tabLayout.getTabAt(2).setIcon(ICONS[2]);
                tabLayout.getTabAt(3).setIcon(ICONS[3]);
                tabLayout.getTabAt(4).setIcon(R.drawable.ic_sticker_emoji_sel);
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public static StickerView LastSelectedView;
    public static EmojiStickerView EmojiLastSelectedView;

//    OnClickListener StickerOnClick = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (StickerOn) {
//                hideStickerLayout();
//            }
//
//            ImageView im = (ImageView) v;
//            String tag = (String) im.getTag();
//
//            assetManager = getAssets();
//            iv_sticker = new StickerImageView(ctx);
//            try {
//                Bitmap b = BitmapFactory.decodeStream(assetManager.open(tag));
//                iv_sticker.setImageBitmap(b);
//
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (b.getWidth() * 1.5), (int) (b.getHeight() * 1.5));
//                params.addRule(RelativeLayout.CENTER_IN_PARENT);
////                params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
//                iv_sticker.setLayoutParams(params);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            LastSelectedView = iv_sticker;
//            llSticker_root.addView(iv_sticker);
//        }
//    };
    LinearLayout TextRoot;
    public void TextMove() {

        ColorPicker_layout.setVisibility(View.GONE);
        TextRoot = (LinearLayout) findViewById(R.id.root);
        //layoutText = (RelativeLayout) mRrootLayout.findViewById(R.id.layoutText);

//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(finalBitmap.getWidth() ,finalBitmap.getHeight());
//        layoutText.setLayoutParams(params);

        moveEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
//                moveEdit.clearComposingText();
                if (moveEdit.getText().toString().trim().length() != 0) {
//                    moveEdit.setVisibility(View.GONE);
                } else {
                    //layoutText.setVisibility(View.GONE);
                    moveEdit.setVisibility(View.GONE);
                    HideKeyBoard(moveEdit);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (moveEdit.getText().toString().trim().length() != 0) {
//                    moveText.setText(moveEdit.getText());
//                    moveEdit.setVisibility(View.GONE);
//                } else {
//                    moveText.setText("");
//                    layoutText.setVisibility(View.GONE);
//                    moveEdit.setVisibility(View.GONE);
//                }
            }
        });
        moveEdit.setOnTouchListener(this);
    }

    private Bitmap setPic(String mCurrentPhotoPath) throws Exception {
        // Get the dimensions of the View
        int targetW = getWindowManager().getDefaultDisplay().getWidth();
        int targetH = getWindowManager().getDefaultDisplay().getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        return bitmap;
    }

    @Override
    public void onBackPressed() {
        if (SAVE == null || SAVE == "2") {
            Close_Dialog();
            IscloseEditpage = true;
        } else {
            finish();
        }
    }

    boolean IsSelectSticker;
    public static boolean IsOpenColorImage = false;

    @Override
    public void onClick(View v) {
        if (v == imgSave) {
            HideKeyBoard(v);
            if (StickerOn) {
                hideStickerLayout();
            }
            EditInvisible();
            RemoveStickerBorder();
            if (SAVE == null) {
                EditInvisible();
                SaveBitmapImage();
            } else if (SAVE == "2") {
                EditInvisible();
                IsDiscardButoon = true;
                Close_Dialog();
            }
        } else if (v == imgUndo) {
            wtDrawingView.undo();
            HideKeyBoard(v);
        } else if (v == imgReset) {
            alertResetDialog();
            HideKeyBoard(v);
        } else if (v == imgClose) {
            didTapButton(imgClose);
            EditInvisible();
            if (StickerOn) {
                hideStickerLayout();
            } else {
                Close_Dialog();
            }
            HideKeyBoard(v);
            CheckText();
            IscloseEditpage = true;

            imgText.setImageResource(R.drawable.ic_text);
            imgRotate.setImageResource(R.drawable.ic_rotate);
            imgSticker.setImageResource(R.drawable.ic_sticker);
            imgEdit.setImageResource(R.drawable.ic_edit);
        } else if (v == imgRotate) {
            didTapButton(imgRotate);
            ClickRotate();
            if (SAVE == "1") {
                SAVE = "2";
            }
            if (StickerOn) {
                hideStickerLayout();
            }
            HideKeyBoard(v);
            CheckText();
            EditInvisible();
            imgText.setImageResource(R.drawable.ic_text);
            imgRotate.setImageResource(R.drawable.ic_rotate);
            imgSticker.setImageResource(R.drawable.ic_sticker);
            imgEdit.setImageResource(R.drawable.ic_edit);

        } else if (v == imgSticker) {
            isAnimation = true;
            rlGridView.setVisibility(View.VISIBLE);
            didTapButton(imgSticker);
            EditInvisible();
            RemoveStickerBorder();
            showStickerLayout();
            HideKeyBoard(v);
            CheckText();
            //TabLayout();
//czczcxzczczc
//            imgSticker.setImageResource(R.drawable.ic_sticker);
//            imgRotate.setImageResource(R.drawable.ic_rotate_unselect);
//            imgText.setImageResource(R.drawable.ic_text_unselect);
//            imgEdit.setImageResource(R.drawable.ic_edit_unselect);

            llSticker_root.bringToFront();
            wtDrawingView.invalidate();

            if (SAVE == "1") {
                SAVE = "2";
            }
            IsSelectSticker = false;
        } else if (v == imgText) {

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) moveEdit.getLayoutParams();
            params.leftMargin = 0;
            params.topMargin = 300;
            moveEdit.setLayoutParams(params);

            didTapButton(imgText);
            EditInvisible();
            selectColor = 0;
            moveEdit.setVisibility(View.VISIBLE);
            moveEdit.setOnTouchListener(this);
            moveEdit.requestFocus();
            if (StickerOn) {
                hideStickerLayout();
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            imgText.setImageResource(R.drawable.ic_text);
            imgRotate.setImageResource(R.drawable.ic_rotate);
            imgSticker.setImageResource(R.drawable.ic_sticker);
            imgEdit.setImageResource(R.drawable.ic_edit);

            if (SAVE == "1") {
                SAVE = "2";
            }

            IsSelectSticker = false;
        } else if (v == imgEdit) {
            didTapButton(imgEdit);
//            openDialog(true);
            EditVisible();
//            imgColorPicker.setOnTouchListener(imgSourceOnTouchListener);
            imgColorPicker.setOnTouchListener(this);
            CheckText();
            HideKeyBoard(v);
            if (StickerOn) {
                hideStickerLayout();
            }

            imgEdit.setImageResource(R.drawable.ic_edit);
            imgRotate.setImageResource(R.drawable.ic_rotate);
            imgSticker.setImageResource(R.drawable.ic_sticker);
            imgText.setImageResource(R.drawable.ic_text);

            wtDrawingView.bringToFront();
            llSticker_root.invalidate();

            if (IsSelectSticker) {
                llSticker_root.bringToFront();
                wtDrawingView.invalidate();
                IsSelectSticker = false;
                imgColorPicker.setVisibility(View.GONE);
                imgUndo.setVisibility(View.GONE);
                imgReset.setVisibility(View.GONE);
                IsOpenColorImage = false;
            } else {
                wtDrawingView.bringToFront();
                llSticker_root.invalidate();
                IsSelectSticker = true;
                imgColorPicker.setVisibility(View.VISIBLE);
                imgUndo.setVisibility(View.VISIBLE);
                imgReset.setVisibility(View.VISIBLE);
                IsOpenColorImage = true;
            }
            if (SAVE == "1") {
                SAVE = "2";
            }
        } else if (v == moveEdit) {
            //EditInvisible();
//            if (moveEdit.getText().toString().trim().length() != 0) {
//                moveText.setText(moveEdit.getText());
//                moveEdit.setVisibility(View.GONE);
//            } else {
//                layoutText.setVisibility(View.GONE);
//                moveEdit.setVisibility(View.GONE);
//            }
        } else if (v == imgShare) {
            if (SAVE == "1") {
                ShareImage();
            } else {
                SaveBitmapImage();
                ShareImage();
            }
        } else if (v == imgbtnStickerClose) {
            hideStickerLayout();
        }
//        if (v == imgMail) {
////            shareType = 3;
//            if (CheckInterNet()) {
//                if (SaveCheck) {
////                    new getHtmlpageLinkTask().execute();
//                    Mail_Share();
//                } else {
//                    SaveBitmapImage();
////                    new getHtmlpageLinkTask().execute();
//                    Mail_Share();
//                }
//            } else {
//                Toast.makeText(ctx, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
//            }
//        }
//        if (v == imgWhatsapp) {
////            shareType = 2;
//            if (CheckInterNet()) {
//                if (SaveCheck) {
////                    new getHtmlpageLinkTask().execute();
//                    Whatsapp_Share();
//                } else {
//                    SaveBitmapImage();
////                    new getHtmlpageLinkTask().execute();
//                    Whatsapp_Share();
//                }
//            } else {
//                Toast.makeText(ctx, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
//            }
//        }
//        if (v == imgTwitter) {
////            shareType = 1;
//            if (CheckInterNet()) {
//                if (SaveCheck) {
////                    new getHtmlpageLinkTask().execute();
//                    Twitter_Share();
//                } else {
//                    SaveBitmapImage();
////                    new getHtmlpageLinkTask().execute();
//                    Twitter_Share();
//                }
//            } else {
//                Toast.makeText(ctx, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
//            }
//        }
//        if (v == imgFB) {
////            shareType = 0;
//            if (CheckInterNet()) {
//                if (SaveCheck) {
////                    new getHtmlpageLinkTask().execute();
//                    Facebook_Share();
//                } else {
//                    SaveBitmapImage();
////                    new getHtmlpageLinkTask().execute();
//                    Facebook_Share();
//                }
//            } else {
//                Toast.makeText(ctx, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    public void didTapButton(View button) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.button_bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        Button_Animation interpolator = new Button_Animation(0.2, 20);
        myAnim.setInterpolator(interpolator);

        button.startAnimation(myAnim);
    }

    public void ShareImage() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, SharePath);
        String link = "https://play.google.com/store/apps/details?id=com.itflash.loverstickers&hl=en";
        shareIntent.putExtra(Intent.EXTRA_TEXT, link);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Love Sticker"));
    }

    public void HideKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

//    public Document getXMLDocument(InputStream stream) {
//        Document document = null;
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//
//            try {
//                document = builder.parse(stream);
//            } catch (SAXException e) {
//                e.printStackTrace();
//            } finally {
//                if (stream != null)
//                    stream.close();
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        }
//        document.getDocumentElement().normalize();
//        return document;
//    }

    public void EditInvisible() {
        ColorPicker_layout.setVisibility(View.GONE);
        imgEdit.setBackgroundColor(0);
        selectColor = 0;
        imgUndo.setVisibility(View.GONE);
        imgReset.setVisibility(View.GONE);
    }

    public void EditVisible() {
        ColorPicker_layout.setVisibility(View.VISIBLE);
        imgUndo.setVisibility(View.VISIBLE);
        imgReset.setVisibility(View.VISIBLE);
    }

    public void CheckText() {
        if (moveEdit.getText().toString().trim().length() == 0) {
            moveEdit.setVisibility(View.GONE);
        }
    }

//    private void Whatsapp_Share() {
//        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//        whatsappIntent.setType("image/jpeg");
//        whatsappIntent.putExtra(Intent.EXTRA_STREAM, SharePath);
//        whatsappIntent.setPackage("com.whatsapp");
////        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
//        try {
//            ctx.startActivity(whatsappIntent);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(ctx, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void Facebook_Share() {
//        Intent FacebookIntent = new Intent(Intent.ACTION_SEND);
//        FacebookIntent.setPackage("com.facebook.katana");
//        FacebookIntent.setType("image/*");
//        FacebookIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + SharePath));
//        startActivity(FacebookIntent);
//    }
//
//    private void Twitter_Share() {
//        Intent TwitterIntent = new Intent(Intent.ACTION_SEND);
//        TwitterIntent.setType("image/jpeg");
//        TwitterIntent.putExtra(Intent.EXTRA_STREAM, SharePath);
//        TwitterIntent.setPackage("com.twitter.android");
//        try {
//            ctx.startActivity(TwitterIntent);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(ctx, "Twitter have not been installed.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void Mail_Share() {
//        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//        emailIntent.setType("image/jpeg");
//        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
//        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Love Sticker");
//        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
//        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + SharePath));
//        emailIntent.setPackage("com.google.android.gm");
//        startActivity(emailIntent);
//    }

    public void alertResetDialog() {
        final Dialog dialog = new Dialog(ctx, R.style.Theme_Close_CustomDialog);
        dialog.setContentView(R.layout.reset_custom_dialog);

        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        ImageView btnCancel = (ImageView) dialog.findViewById(R.id.btnCancel);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                imgSticker.setImageResource(R.drawable.ic_sticker);
                imgRotate.setImageResource(R.drawable.ic_rotate);
                imgText.setImageResource(R.drawable.ic_text);
                imgEdit.setImageResource(R.drawable.ic_edit);
                EditInvisible();
                IsSelectSticker = false;
                imgUndo.setVisibility(View.GONE);
                imgReset.setVisibility(View.GONE);
                wtDrawingView.clear();
                llSticker_root.bringToFront();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
//        Window window = dialog.getWindow();
//        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

//    private void alertResetDialog() {
//        AlertDialog.Builder dialog = createAlert(ctx, "Love Sticker", "Are sure reset Drawing ?");
//
//        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                imgSticker.setImageResource(R.drawable.ic_sticker);
//                imgRotate.setImageResource(R.drawable.ic_rotate);
//                imgText.setImageResource(R.drawable.ic_text);
//                imgEdit.setImageResource(R.drawable.ic_edit);
//                EditInvisible();
////                dv.clickReset();
//                wtDrawingView.clear();
//            }
//        });
//        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            boolean success = deleteDir(new File(String.valueOf(dir)));
            if (!success) {
                return false;
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    //-------------------poup color box---------------
//    private void openDialog(boolean supportsAlpha) {
//        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentColor, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
//            @Override
//            public void onOk(AmbilWarnaDialog dialog, int color) {
//                currentColor = color;
//                selectColor = color;
////                colorLayout.setBackgroundColor(color);
//            }
//
//            @Override
//            public void onCancel(AmbilWarnaDialog dialog) {
//                Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
//            }
//        });
//        dialog.show();
//    }

//    @Override
//    public void colorChanged(int color) {
//        mPaint.setColor(color);
//
//    }

    //--------------Text Move----------------------
    public boolean onTouch(View view, MotionEvent event) {
        if (view == moveEdit) {
            ColorPicker_layout.setVisibility(View.GONE);
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            if (IsSelectSticker) {
                llSticker_root.bringToFront();
                wtDrawingView.invalidate();
                IsSelectSticker = false;
                EditInvisible();
                IsOpenColorImage = false;
            }
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    //HideKeyBoard(view);
                    LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) view.getLayoutParams();
//                _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
//                layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
//                layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);
                    break;
            }
            TextRoot.invalidate();
//        else if(view == imgColorPicker){
//            float eventX = event.getX();
//            float eventY = event.getY();
//            float[] eventXY = new float[]{eventX, eventY};
//
//            Matrix invertMatrix = new Matrix();
//            ((ImageView) view).getImageMatrix().invert(invertMatrix);
//
//            invertMatrix.mapPoints(eventXY);
//            int x = Integer.valueOf((int) eventXY[0]);
//            int y = Integer.valueOf((int) eventXY[1]);
//
//            Drawable imgDrawable = ((ImageView) view).getDrawable();
//            Bitmap bitmap = ((BitmapDrawable) imgDrawable).getBitmap();
//
//            if (x < 0) {
//                x = 0;
//            } else if (x > bitmap.getWidth() - 1) {
//                x = bitmap.getWidth() - 1;
//            }
//
//            if (y < 0) {
//                y = 0;
//            } else if (y > bitmap.getHeight() - 1) {
//                y = bitmap.getHeight() - 1;
//            }
//
//            int touchedRGB = bitmap.getPixel(x, y);
//
//            imgEdit.setBackgroundColor(touchedRGB);
//            selectColor = touchedRGB;
//            wtDrawingView.setStrokeColor(selectColor);
//
//            //-------------------ImageView Round Background---------------------
//            ShapeDrawable biggerCircle = new ShapeDrawable(new OvalShape());
//            biggerCircle.setIntrinsicHeight(30);
//            biggerCircle.setIntrinsicWidth(30);
////            biggerCircle.setBounds(new Rect(30, 30, 30, 30));
//            biggerCircle.getPaint().setColor(selectColor);//you can give any color here
//            imgEdit.setBackgroundDrawable(biggerCircle);
//        }
        } else if (view == imgColorPicker) {
            float eventX = event.getX();
            float eventY = event.getY();
            float[] eventXY = new float[]{eventX, eventY};

            Matrix invertMatrix = new Matrix();
            ((ImageView) view).getImageMatrix().invert(invertMatrix);

            invertMatrix.mapPoints(eventXY);
            int x = Integer.valueOf((int) eventXY[0]);
            int y = Integer.valueOf((int) eventXY[1]);

            Drawable imgDrawable = getResources().getDrawable(R.drawable.color_picker2);//(ImageView) view).getDrawable();
            Bitmap bitmap = ((BitmapDrawable) imgDrawable).getBitmap();

            if (x < 0) {
                x = 0;
            } else if (x > bitmap.getWidth() - 1) {
                x = bitmap.getWidth() - 1;
            }

            if (y < 0) {
                y = 0;
            } else if (y > bitmap.getHeight() - 1) {
                y = bitmap.getHeight() - 1;
            }

            if (y - 15 > 0) {
                int touchedRGB = bitmap.getPixel(bitmap.getWidth() / 2, y - 15);
                //if(y > 0 && y < (bitmap.getHeight()+120))
                selectColor = touchedRGB;
                wtDrawingView.setStrokeColor(selectColor);
                imgColorPicker.setImageBitmap(createSingleImageFromMultipleImages(icon, y, touchedRGB));
                llSelectColor.setY(y + 80);
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            llSelectColor.setLayoutParams(params);

            //-------------------ImageView Round Background---------------------
            ShapeDrawable biggerCircle = new ShapeDrawable(new OvalShape());
            biggerCircle.setIntrinsicHeight(80);
            biggerCircle.setIntrinsicWidth(80);
            biggerCircle.setBounds(new Rect(30, 30, 30, 30));
            biggerCircle.getPaint().setColor(selectColor);//you can give any color here
            llSelectColor.setBackgroundDrawable(biggerCircle);

            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    TranslateAnimation gone = new TranslateAnimation(0f, 200f, 0f, 0f);  // might need to review the docs
                    gone.setDuration(100); // set how long you want the animation

                    llSelectColor.setAnimation(gone);
                    llSelectColor.setVisibility(View.GONE);
                    break;
                case MotionEvent.ACTION_DOWN:
                    /*TranslateAnimation visible = new TranslateAnimation(300f, 0f, 0f, 0f);  // might need to review the docs
                    visible.setDuration(50); // set how long you want the animation
                    llSelectColor.setAnimation(visible);*/
                    llSelectColor.setVisibility(View.VISIBLE);
                    break;
            }

            //imgEdit.setBackgroundColor(touchedRGB);
            //int selectColor = touchedRGB;
            return true;
        }
        return true;
    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, float y, int color) {
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth() * 4, firstImage.getHeight() + 100, firstImage.getConfig());
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(firstImage, result.getWidth() / 4, 60f, null);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);

        canvas.drawCircle(result.getWidth() / 3 + 6, y + 20, 40, paint);
        return result;
    }

    //    class SketchSheetView extends View {
//
//
//        SketchSheetView myView;
//
//        private ArrayList<DrawingClass> DrawingClassArrayList = new ArrayList<DrawingClass>();
//
//        final ArrayList<ArrayList<DrawingClass>> temp = new ArrayList<>();
//
//        ArrayList<ArrayList<DrawingClass>> temp2 = new ArrayList<>();
//
//
//        public SketchSheetView(Context context) {
//
//            super(context);
//
//            bitmap = Bitmap.createBitmap(820, 480, Bitmap.Config.ARGB_4444);
//            canvas = new Canvas(bitmap);
//            this.setBackgroundColor(Color.TRANSPARENT);
//
//            paint = new Paint();
//
//        }
//
//        @Override
//        public boolean onTouchEvent(MotionEvent event) {
//
////            paint.reset();
//
//            paint.setColor(selectColor);
//
//            DrawingClass pathWithPaint = new DrawingClass();
//
////            canvas.drawPath(path2, paint);
//
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//                path2.moveTo(event.getX(), event.getY());
//
//                path2.lineTo(event.getX(), event.getY());
//
//                invalidate();
//
//
//            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//
//                DrawingClassArrayList.clear();
//
//                path2.lineTo(event.getX(), event.getY());
//
//                pathWithPaint.setPath(path2);
//
//                pathWithPaint.setPaint(paint);
//
//                pathWithPaint.setPaintColor(selectColor);
//
//                DrawingClassArrayList.add(pathWithPaint);
//
//
//                invalidate();
//
//            }
//
////            draw(path2, paint);
//
//            return true;
//        }
//
//        public void draw(Path path, Paint mPaint) {
//
//            canvas.drawPath(path, mPaint);
//
//
//        }
//
//
//        @Override
//        protected void onDraw(final Canvas canvas) {
//            super.onDraw(canvas);
//
//
////            canvas.drawPath(path2, paint);
////
//            if (DrawingClassArrayList.size() > 0) {
//
//                temp2 = new ArrayList<>();
//
//                temp2.add(DrawingClassArrayList);
//
//                canvas.drawPath(
//
//                        DrawingClassArrayList.get(DrawingClassArrayList.size() - 1).getPath(),
//
//                        DrawingClassArrayList.get(DrawingClassArrayList.size() - 1).getPaint());
//            }
//        }
//    }
//
//    public static class DrawingClass {
//
//        Path DrawingClassPath;
//        static Paint DrawingClassPaint = null;
//        int paintColor;
//
//        public int getPaintColor() {
//            return paintColor;
//        }
//
//        public void setPaintColor(int paintColor) {
//            this.paintColor = paintColor;
//        }
//
//
//        public Path getPath() {
//
//            return DrawingClassPath;
//        }
//
//        public void setPath(Path path) {
//            this.DrawingClassPath = path;
//        }
//
//
//        public Paint getPaint() {
//            return DrawingClassPaint;
//        }
//
//        public void setPaint(Paint paint) {
//            this.DrawingClassPaint = paint;
//        }
//    }
//--------------Drawing Canvas-------------------
//----------------------------------------------------
//
//    public class DrawingView extends View {
//
//        public int width;
//        public int height;
//        private Paint mBitmapPaint;
//        Context context;
//        private Paint circlePaint;
//        private Path circlePath;
//        DataListView Poduct = new DataListView(mBitmap);
//
//        public DrawingView(Context c) {
//            super(c);
//            context = c;
//            mPath = new Path();
//            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
//            mCanvas = new Canvas();
//            circlePaint = new Paint();
//            circlePath = new Path();
//            circlePaint.setAntiAlias(true);
//            circlePaint.setColor(Color.TRANSPARENT);
//            circlePaint.setStyle(Paint.Style.STROKE);
//            circlePaint.setStrokeJoin(Paint.Join.MITER);
//            circlePaint.setStrokeWidth(4f);
//        }
//
//        @Override
//        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//            super.onSizeChanged(w, h, oldw, oldh);
//
//            if (mBitmap == null) {
//                wi = w;
//                he = h;
//                mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//                mCanvas = new Canvas(mBitmap);
////            this.setBackgroundColor(Color.TRANSPARENT);
//            }
//
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas) {
//            super.onDraw(canvas);
//
////            for (Path p : paths) {
////                canvas.drawPath(p, mPaint);
////            }
////            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
////            canvas.drawPath(mPath, mPaint);
////            canvas.drawPath(circlePath, circlePaint);
////            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
//
//            for (Path p : paths) {
//
//                for (Bitmap b : mBit) {
//                    canvas.drawBitmap(b, 0, 0, mBitmapPaint);
//                }
//                mPaint.setColor(colorsMap.get(p));
////                canvas.drawPath(p, mPaint);
//            }
//
//            mPaint.setColor(selectColor);
//            canvas.drawPath(mPath, mPaint);
//
//        }
//
//        private void touch_start(float x, float y) {
//            mPath.reset();
//            mPath.moveTo(x, y);
//            mX = x;
//            mY = y;
//        }
//
//        private void touch_move(float x, float y) {
//            float dx = Math.abs(x - mX);
//            float dy = Math.abs(y - mY);
//            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
//                mX = x;
//                mY = y;
//
//                circlePath.reset();
//                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
//            }
//        }
//
//        private void touch_up() {
////            ColorPicker_layout.setVisibility(GONE);
//            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
//            mBitmap = Bitmap.createBitmap(wi, he, conf); // this creates a MUTABLE bitmap
//            mCanvas = new Canvas(mBitmap);
//            if (mBit == null) {
//                mBit = new ArrayList<Bitmap>();
//                //mBit.add(mBitmap);
//            } else {
//                //mBit.add(mBitmap);
//            }
//            Poduct = new DataListView(mBitmap);
//
//            mPath.lineTo(mX, mY);
//            mCanvas.drawPath(mPath, mPaint);
//
//            mPath = new Path();
//            paths.add(mPath);
//            colorsMap.put(mPath, selectColor);
//            mPath = new Path();
//            mPath.reset();
//
//            getDrawingCache();
//            mCanvas = new Canvas(mBitmap);
//            Poduct.setImage(mBitmap);
//            mBit.add(mBitmap);
//            BitmapaArray.add(Poduct);
//        }
//
//        private void clickUndo() {
//            if (paths.size() > 0) {
//                mBit.remove(mBit.size() - 1);
//                undonePaths.add(paths.remove(paths.size() - 1));
//                invalidate();
//            } else {
//                Log.i("undo", "Undo elsecondition");
//            }
//
//        }
//
//        private void clickReset() {
//            if (paths.size() > 0) {
//                mBit.removeAll(mBit);
//                paths.removeAll(paths);
//                undonePaths.removeAll(undonePaths);
//                invalidate();
//            } else {
//                Log.i("reset", "Undo elsecondition");
//            }
//
//        }
//
//
//        @Override
//        public boolean onTouchEvent(MotionEvent event) {
//
//            if (selectColor != 0) {
//                mPaint.setColor(selectColor);
//
//                float x = event.getX();
//                float y = event.getY();
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        touch_start(x, y);
//                        invalidate();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        touch_move(x, y);
//                        invalidate();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        touch_up();
//                        invalidate();
//                        break;
//                }
//            }
//
//            return true;
//        }
//    }

    public void showStickerLayout() {

        if (StickerActivity.gvSticker != null) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
            animation.setDuration(500);
            StickerActivity.gvSticker.setAnimation(animation);
            StickerActivity.gvSticker.animate();
            animation.start();
            rlGridView.setVisibility(View.VISIBLE);
            StickerOn = true;
        }
        //imgClose.setImageResource(R.drawable.ic_header_back);
    }

    public static void hideStickerLayout() {
        Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.bottom_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rlGridView.setVisibility(View.GONE);
                StickerOn = false;
                imgClose.setImageResource(R.drawable.ic_cancel);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rlGridView.startAnimation(animation);
    }

    //---------------Save in Gallery-------------------
    public void SaveBitmapImage() {
        RemoveStickerBorder();
        FileOutputStream fos = null;
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + "/Love Sticker");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {
                // Do something on success
                frameScreen.invalidate();

                Bitmap map = ConvertToBitmap(frameScreen);
                final File mypath = new File(Environment.getExternalStorageDirectory() + "/Love Sticker/" + currentTimeMillis() + "image.jpg");
                fos = new FileOutputStream(mypath);

                int topP = (frameScreen.getHeight() - imgMain.getHeight()) / 2;// - map.getHeight();
                Bitmap crt = Bitmap.createBitmap(map, 0, topP, treeWidth, treeHeight);

                crt.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                SuccessToast("Save Successfully");

                SAVE = "1";
                SharePath = Uri.fromFile(mypath);
//------------------------Refresh Gallery--------------------------------------
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, SharePath));
            } else {
                ErrorToast("Image not Save");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } //finally {
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //}
    }

    protected Bitmap ConvertToBitmap(RelativeLayout layout) {
        //int SaveBitmapWidth = finalBitmap.getWidth();
        //int SaveBitmapHeight = finalBitmap.getHeight();

        //DisplayMetrics displayMetrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //int height = displayMetrics.heightPixels;
        //int newWidth = displayMetrics.widthPixels;

        //float scaleFactor = (float) newWidth / (float) SaveBitmapWidth;
        //int newHeight = (int) (SaveBitmapHeight * scaleFactor);

        layout.setDrawingCacheEnabled(true);
//        layout.measure(View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(newHeight, View.MeasureSpec.UNSPECIFIED));
//        if (CheckActivity.equals("Main")) {
//            layout.measure(View.MeasureSpec.makeMeasureSpec(treeWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(treeHeight, View.MeasureSpec.EXACTLY));
//        } else {
//            if(rotatedBitmap == null) {
//                //int hei = imgMain.getMeasuredHeight();//.getHeight();
//                //int wid = imgMain.getMeasuredWidth();
////            layout.measure(View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(newHeight, View.MeasureSpec.AT_MOST));
////            layout.measure(View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(newHeight, View.MeasureSpec.EXACTLY));
//                layout.measure(View.MeasureSpec.makeMeasureSpec(treeWidth, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(treeHeight, View.MeasureSpec.AT_MOST));
//            }else {
//                //int hei = imgMain.getHeight();
//                //int wid = imgMain.getWidth();
////              layout.measure(View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(hei, View.MeasureSpec.EXACTLY));
//                layout.measure(View.MeasureSpec.makeMeasureSpec(treeWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(treeHeight, View.MeasureSpec.EXACTLY));
//            }
//        }

        //layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());

        layout.buildDrawingCache(true);

        Bitmap map = layout.getDrawingCache();
        return map;
    }

    public void SuccessToast(String msg) {
        Toast toast = new Toast(getApplicationContext());
        View layout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.success_toast_layout, null, false);
        TextView text = (TextView) layout.findViewById(R.id.toastMessage);
        text.setText(msg);
        SuccessToastView successToastView = (SuccessToastView) layout.findViewById(R.id.successView);
        successToastView.startAnim();
        text.setBackgroundResource(R.drawable.success_toast);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        toast.setView(layout);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public void ErrorToast(String msg) {

        Toast toast = new Toast(getApplicationContext());
        View layout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.error_toast_layout, null, false);
        TextView text = (TextView) layout.findViewById(R.id.toastMessage);
        text.setText(msg);
        ErrorToastView errorToastView = (ErrorToastView) layout.findViewById(R.id.errorView);
        errorToastView.startAnim();
        text.setBackgroundResource(R.drawable.error_toast);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        toast.setView(layout);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    //---------------------Save in Cache---------------------------
//    SharedPreferences prefs;
//    SharedPreferences.Editor editor;

//    public class ImageAdapter extends BaseAdapter {
//
//        private Context mContext;
//        OnClickListener StickerClickListener;
//        LayoutInflater infalter;
//
//        // Constructor
//        public ImageAdapter(Context c, OnClickListener StickerClickListener) {
//            mContext = c;
//            this.StickerClickListener = StickerClickListener;
//            infalter = (LayoutInflater) mContext
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//        @Override
//        public int getCount() {
//            return NO_OF_STICKERS;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        Bitmap bitmap = null;
//        AssetManager assetManager;
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder view;
//
//            if (convertView == null) {
//                view = new ViewHolder();
//                convertView = infalter.inflate(R.layout.sticker_cell, null);
//                convertView.setTag(view);
//            } else {
//                view = (ViewHolder) convertView.getTag();
//            }
//
//            view.image_sticker = (ImageView) convertView.findViewById(R.id.Sticker_cell);
//            view.image_sticker.setOnClickListener(StickerClickListener);
//            assetManager = mContext.getAssets();
//            String counter = String.format("%02d", position + 1);
//
//            try {
//                bitmap = BitmapFactory.decodeStream(assetManager.open(counter + "_ico.png"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            view.image_sticker.setImageBitmap(bitmap);
//            view.image_sticker.setTag(counter + ".png");
//            return convertView;
//        }
//
//        public class ViewHolder {
//            ImageView image_sticker;
//        }
//    }

    boolean IscloseEditpage = false;
    public void Close_Dialog() {
        final Dialog dialog = new Dialog(ctx, R.style.Theme_Close_CustomDialog);
//        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.close_custom_dialog);

        Button btnReplace = (Button) dialog.findViewById(R.id.btnReplace);
        Button btnSaveAs = (Button) dialog.findViewById(R.id.btnSaveAs);
        Button btnDiscard = (Button) dialog.findViewById(R.id.btnDiscard);
        ImageView btnCancel = (ImageView) dialog.findViewById(R.id.btnCancel);

        if (SAVE == null) {
            btnSaveAs.setText("Save");
        }
        if (SAVE == "2") {
            btnReplace.setVisibility(View.VISIBLE);
        } else {
            btnReplace.setVisibility(View.GONE);
        }
        if (IsDiscardButoon) {
            btnDiscard.setVisibility(View.GONE);
            IscloseEditpage = false;
            IsDiscardButoon = false;
        }

        btnReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                File deletFile = new File(SharePath.getPath());
                deleteDir(deletFile);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, SharePath));
                SaveBitmapImage();
                if (IscloseEditpage) {
                    finish();
                    SAVE = null;
                } else {
                    SAVE = "1";
                }
            }
        });

        btnSaveAs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                HideKeyBoard(moveEdit);
                if (StickerOn) {
                    hideStickerLayout();
                }
                EditInvisible();
                RemoveStickerBorder();
                //if (SAVE == null || SAVE == "2") {
                EditInvisible();
                SaveBitmapImage();
//                    imgMain.setImageBitmap(finalBitmap);
                //}
                if (IscloseEditpage) {
                    finish();
                    SAVE = null;
                } else {
                    SAVE = "1";
                }
            }
        });

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
                SAVE = null;

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void RemoveStickerBorder() {
        if (LastSelectedView != null) {
            if (LastSelectedView.iv_border != null) {
                LastSelectedView.iv_border.setVisibility(View.GONE);
                LastSelectedView.iv_scale.setVisibility(View.GONE);
                LastSelectedView.iv_delete.setVisibility(View.GONE);
                LastSelectedView.iv_flip.setVisibility(View.GONE);
            }
        }
        if (EmojiLastSelectedView != null) {
            if (EmojiLastSelectedView.iv_border != null) {
                EmojiLastSelectedView.iv_border.setVisibility(View.GONE);
                EmojiLastSelectedView.iv_scale.setVisibility(View.GONE);
                EmojiLastSelectedView.iv_delete.setVisibility(View.GONE);
                EmojiLastSelectedView.iv_flip.setVisibility(View.GONE);
            }
        }
    }

//    private static final int SELECT_PICTURE = 100;
//    private static final String TAG = "Edit_Image";

    /* Choose an image from Gallery */
//    public void openImageChooser() {
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), SELECT_PICTURE);
//    }


//    private void alertSaveDialog() {
//        AlertDialog.Builder dialog = createAlert(ctx, "Love Sticker", "Do You Save This Image ?");
//        dialog.setCancelable(true);
//
//        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                SaveBitmapImage();
//                finish();
//                HideKeyBoard(moveEdit);
////                hideStickerLayout();
//                EditInvisible();
//                if (iv_sticker != null) {
//                    if (iv_sticker.iv_border != null) {
//                        iv_sticker.iv_border.setVisibility(View.GONE);
//                        iv_sticker.iv_scale.setVisibility(View.GONE);
//                        iv_sticker.iv_delete.setVisibility(View.GONE);
//                        iv_sticker.iv_flip.setVisibility(View.GONE);
//                    }
//                }
////                if (!IsSave) {
////                    EditInvisible();
////                    SaveBitmapImage();
////                }
//            }
//        });
//        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                try {
////                    if (CaptureURI != null) {
////                        File mypath = new File(String.valueOf(CaptureURI));
////                        deleteDir(mypath);
////                    }
////
////                } catch (Exception e) {
////                }
//                dialog.dismiss();
//                finish();
////                Intent intent = new Intent(ctx, MainActivity.class);
////                startActivity(intent);
//            }
//        });
//        dialog.show();
//    }

//    private String saveInCache() {
//
//        iv_sticker = new StickerImageView(ctx);
//        if (iv_sticker.iv_border != null) {
//            iv_sticker.iv_border = null;
//            iv_sticker.iv_scale.setVisibility(View.GONE);
//            iv_sticker.iv_delete.setVisibility(View.GONE);
//            iv_sticker.iv_flip.setVisibility(View.GONE);
//        }
//
//        Bitmap map = ConvertToBitmap(frameScreen);
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        // path to /data/data/yourapp/app_data/Love_Sticker
//        File directory = cw.getDir("Love_Sticker", Context.MODE_PRIVATE);
//        // Create imageDir
//        File mypath = new File(directory, currentTimeMillis() + "image.jpg");
////        SharePath = Uri.fromFile(new File(directory, currentTimeMillis() + "image.jpg"));
//
//        FileOutputStream fos = null;
//        try {
//            if (rotatedBitmap == null) {
//                fos = new FileOutputStream(mypath);
////                map.createScaledBitmap(map, wi, he, false);
//                map.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            } else {
//                Matrix rotateMatrix = new Matrix();
//                rotateMatrix.postRotate(-90);
//                Bitmap rotatedmap = Bitmap.createBitmap(map, 0, 0, map.getWidth(), map.getHeight(), rotateMatrix, false);
//                map.recycle();
//                fos = new FileOutputStream(mypath);
//                rotatedmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            }
//            Toast.makeText(ctx, "Save Succesfully", Toast.LENGTH_SHORT).show();
////            SharePath = Uri.fromFile(mypath);
////            IsSave = true;
//
//            ImageGetSet Product = new ImageGetSet();
//            Product.setSaveToLoveSticker(String.valueOf(mypath));
//            if (CheckActivity.equals("Edit")) {
//                Product.setIsLoveStickerNotifyAdapter(true);
//            }
//
//            this.getSharedPreferences("Love_Sticker", 0).edit().clear().commit();
//            prefs = this.getSharedPreferences("Love_Sticker", MODE_PRIVATE);
//            editor = prefs.edit();
//            editor.putString("RecentImage", String.valueOf(mypath));
//            editor.commit();
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return mypath.getAbsolutePath();
//    }

//    class dataListAdapter extends BaseAdapter {
//        String[] StickerPath;
//        Context context;
////        int[] imge;
//
//        dataListAdapter() {
//            StickerPath = null;
////            Detail = null;
////            imge=null;
//        }
//
//        public dataListAdapter(Context contexts, String[] path) {
//            this.StickerPath = path;
//            this.context = contexts;
//        }
//
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return StickerPath.length;
//        }
//
//        public Object getItem(int arg0) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return position;
//        }
//
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            LayoutInflater inflater = getLayoutInflater();
//            View row;
//            row = inflater.inflate(R.layout.sticker_cell, parent, false);
//            ImageView Sticker_cell;
//            Sticker_cell = (ImageView) row.findViewById(R.id.Sticker_cell);
//
//            InputStream is = null;
//
//            try {
//                is = assetManager.open(String.valueOf(StickerPath[position]));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Bitmap bitmap = BitmapFactory.decodeStream(is);
//
//            Sticker_cell.setImageBitmap(bitmap);
//
//            return (row);
//        }
//    }

//    private void Whatsapp_Share() {
//        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//        whatsappIntent.setType("image/jpeg");
//        whatsappIntent.putExtra(Intent.EXTRA_STREAM, SharePath);
//        whatsappIntent.setPackage("com.whatsapp");
////        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
//        try {
//            ctx.startActivity(whatsappIntent);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(ctx, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void Facebook_Share() {
//        Intent FacebookIntent = new Intent(Intent.ACTION_SEND);
//        FacebookIntent.setPackage("com.facebook.katana");
//        FacebookIntent.setType("image/*");
//        FacebookIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + SharePath));
//        startActivity(FacebookIntent);
//    }
//
//    private void Twitter_Share() {
//        Intent TwitterIntent = new Intent(Intent.ACTION_SEND);
//        TwitterIntent.setType("image/jpeg");
//        TwitterIntent.putExtra(Intent.EXTRA_STREAM, SharePath);
//        TwitterIntent.setPackage("com.twitter.android");
//        try {
//            ctx.startActivity(TwitterIntent);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(ctx, "Twitter have not been installed.", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void Mail_Share() {
//        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//        emailIntent.setType("image/jpeg");
//        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
//        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Love Sticker");
//        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
//        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + SharePath));
//        emailIntent.setPackage("com.google.android.gm");
//        startActivity(emailIntent);
//    }

//    private class getHtmlpageLinkTask extends AsyncTask<Void, Void, Void> {
//        String response = "";
//        ProgressDialog pDialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
////            showProgressDialog();
//            pDialog = new ProgressDialog(ctx);
//            pDialog.setMessage("Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            System.out.println("Group info api is call");
//
//            String ImagePath = FilePath.getPath(ctx, SharePath);
//
//            List<NameValuePair> parmas1 = new ArrayList<NameValuePair>();
//            List<NameValuePair> parmas2 = new ArrayList<NameValuePair>();
//            parmas1.add(new BasicNameValuePair("user", "benzatine"));
//            parmas1.add(new BasicNameValuePair("password", "benzatineinfotech"));
//            parmas2.add(new BasicNameValuePair("upn_image", ImagePath));
//
//            HttpLoader httpLoader = new HttpLoader();
//            try {
//                if (shareType != 2) {
//                    response = httpLoader.loadDataByPost(Utils.FACEB_TWITTER, parmas1, parmas2);
//                } else {
//                    response = httpLoader.loadDataByPost(Utils.WHATSAPP, parmas1, parmas2);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            if (response != null && response.length() > 0) {
//                try {
//                    JSONObject jobject1 = new JSONObject(response);
//                    String jStatus = jobject1.optString("data");
//                    String jStatusImage = jobject1.optString("image_url");
//                    if (!jStatus.equals("")) {
//                        //StringBuilder shareImage=new StringBuilder("<a href=\"https://play.google.com/store/apps/details?id=com.itflash.whatsappstickers\"><img src=\"" + jStatusImage + "\"/></a></body></html>");
//                        //createShareIntent(jStatus);
//                        if (shareType != 3) {
//                            shareDataFaceTwitt(jStatus);
//                        } else {
//                            shareDataFaceTwitt(jStatusImage);
//                        }
//                        Log.d("Html link for share --", jStatus);
//                    }
//                   /* if(jStatus==200) {
//                    }else {
//                        //Utils.setCustomToast(mcontext,jobject1.optString("msg"),false);
//                    }*/
//                } catch (Exception e) {
//                    System.out.println("Exception e:--" + e);
//                }
//            }
////            dismissProgressDialog();
//            if (pDialog != null) {
//                if (pDialog.isShowing()) {
//                    pDialog.dismiss();
////--------------------Delete in Gallery after 7 sec----------------------------------------------
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            File deletFile = new File(String.valueOf(SharePath));
//                            deleteDir(deletFile);
//                        }
//                    }, 7000);
//                }
//            }
//        }
//    }

//    int shareType = 0;

//    private void shareDataFaceTwitt(String image) {
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        switch (shareType) {
//            case 0:
//                shareIntent.setPackage("com.facebook.katana");
//                shareIntent.setType("text/plain");
//                shareIntent.putExtra(Intent.EXTRA_TEXT, image);
//                startActivity(shareIntent);
//                break;
//            case 1:
//                shareIntent.setPackage("com.twitter.android");
//                shareIntent.setType("text/plain");
//                shareIntent.putExtra(Intent.EXTRA_TEXT, image);
//                try {
//                    startActivity(shareIntent);
//                } catch (Exception e) {
//                    Toast.makeText(ctx, "Please install twitter first.", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case 2:
//                shareIntent.setPackage("com.whatsapp");
//                shareIntent.setType("text/plain");
//                shareIntent.putExtra(Intent.EXTRA_TEXT, image);
//                startActivity(shareIntent);
//                break;
//            case 3:
//
//                String playStoreLink = Utils.PLAY_STORE_LINK;
//
//                shareIntent.setPackage("com.google.android.gm");
//                //shareIntent1.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
//                shareIntent.setType("image/*");
//                shareIntent.putExtra(Intent.EXTRA_EMAIL, "test@gmail.com");
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, ctx.getResources().getString(R.string.app_name));
//                shareIntent.putExtra(Intent.EXTRA_TEXT, playStoreLink);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(ctx, Save_Bit));
//                startActivity(shareIntent);
//
//                break;
//        }
//    }

//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }

}

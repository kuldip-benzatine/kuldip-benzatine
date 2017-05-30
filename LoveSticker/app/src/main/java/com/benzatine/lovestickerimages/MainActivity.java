package com.benzatine.lovestickerimages;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.benzatine.lovestickerimages.Data.ImageGetSet;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private ImageView flipCamera, flashCameraButton, captureImage, btnMemories;
    ImageView imgClose, imgMenu;
    LinearLayout Animation_Layout, Close_Layout, llContact, llShare, llRate, llMoreApp, llHome;
    private int cameraId;
    private boolean flashmode = false;
    private int rotation;
    Context ctx;
    int CameraCheckId;

    AdView mAdView;
    AdRequest adRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAdd();
        ctx = this;
        mAdView = (AdView) findViewById(R.id.adView);
        if (Utils.isOnline(this)) {
            adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        init();
        // camera surface view created
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        init_action();

        if (Camera.getNumberOfCameras() > 1) {
            flipCamera.setVisibility(View.VISIBLE);
        }
        if (!getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            flashCameraButton.setVisibility(View.GONE);
        }

        showFullAdd();
    }

    private InterstitialAd mInterstitialAd;
    public void initAdd(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.key_admob_interstitials));
        if(Utils.isOnline(this)) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    public void showFullAdd() {
        showAdd = true;
        if (Utils.isOnline(this)) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
                showAdd = true;
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        }
    }

    Boolean showAdd = false;
    public void reloadFullAdd(){
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(showAdd) {
                    showFullAdd();
                    showAdd = false;
                }
            }
        });
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void init() {
        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

        flipCamera = (ImageView) findViewById(R.id.flipCamera);
        flashCameraButton = (ImageView) findViewById(R.id.flash);
        captureImage = (ImageView) findViewById(R.id.captureImage);
        btnMemories = (ImageView) findViewById(R.id.btnMemories);

        imgMenu = (ImageView) findViewById(R.id.imgMenu);
        imgClose = (ImageView) findViewById(R.id.imgClose);

        llHome = (LinearLayout) findViewById(R.id.llHome);
        llContact = (LinearLayout) findViewById(R.id.llContact);
        llShare = (LinearLayout) findViewById(R.id.llShare);
        llRate = (LinearLayout) findViewById(R.id.llRate);
        llMoreApp = (LinearLayout) findViewById(R.id.llMoreApp);

        Animation_Layout = (LinearLayout) findViewById(R.id.Animation_Layout);
        Close_Layout = (LinearLayout) findViewById(R.id.Close_Layout);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
    }

    public void init_action() {
        flipCamera.setOnClickListener(this);
        captureImage.setOnClickListener(this);
        flashCameraButton.setOnClickListener(this);
        btnMemories.setOnClickListener(this);

        imgMenu.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        llHome.setOnClickListener(this);
        llContact.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llRate.setOnClickListener(this);
        llMoreApp.setOnClickListener(this);
        reloadFullAdd();
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private void start_camera() {
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            Log.e("tag", "init_camera: " + e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();
        //modify parameter

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        param.setPreviewFrameRate(20);
        param.setPreviewSize(height, width);
        camera.setParameters(param);
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            //camera.takePicture(shutter, raw, jpeg)
        } catch (Exception e) {
            Log.e("tag", "init_camera: " + e);
            return;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (checkPermission()) {

            if (!openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)) {
                alertCameraDialog();
            }
//            start_camera();

        } else {
            requestPermission();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (flashmode) {
            flashOnButton();
        }
        releaseCamera();
        finish();
    }

    private boolean openCamera(int id) {
        boolean result = false;
        cameraId = id;
        releaseCamera();
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();
//            openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if (camera != null) {
            try {
                setUpCamera(camera);
                camera.setErrorCallback(new Camera.ErrorCallback() {
                    @Override
                    public void onError(int error, Camera camera) {

                    }
                });
                Camera.Parameters param;
                param = camera.getParameters();
                //modify parameter

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                int width = displayMetrics.heightPixels;
                int height = displayMetrics.heightPixels;
//                int height = displayMetrics.widthPixels;
                int width = displayMetrics.widthPixels;

                param.setPreviewFrameRate(20);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
                    Camera.Size previewSize = previewSizes.get(1);
                    param.setPreviewSize(previewSize.width, previewSize.height);
//                    param.setPreviewSize(2048, 1232);
                } else {
                    param.setPreviewSize(height, width);
                }

                List<String> focusModes = param.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    param.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }

                param.set("jpeg-quality", 100);
                param.setPictureFormat(PixelFormat.JPEG);
//                param.setPictureSize(2048, 1232);
                param.setRotation(90);

                camera.setParameters(param);
//                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }

    private void setUpCamera(Camera c) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
            default:
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 330;
            rotation = (360 - rotation) % 360;
        } else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360;
        }
        c.setDisplayOrientation(rotation);
        Camera.Parameters params = c.getParameters();

        showFlashButton(params);

        List<String> focusModes = params.getSupportedFlashModes();
        if (focusModes != null) {
            if (focusModes
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        params.setRotation(rotation);
    }

    private void showFlashButton(Camera.Parameters params) {
        boolean showFlash = (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH) && params.getFlashMode() != null)
                && params.getSupportedFlashModes() != null
                && params.getSupportedFocusModes().size() > 1;

        flashCameraButton.setVisibility(showFlash ? View.VISIBLE : View.INVISIBLE);
    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flash:
                flashOnButton();
                break;
            case R.id.flipCamera:
                flipCamera();
                break;
            case R.id.captureImage:
                takeImage();
                break;
            case R.id.btnMemories:
//                Intent i = new Intent(MainActivity.this, Edit_Image.class);
//                i.putExtra("CheckActivity", "GalleryImage");
//                startActivity(i);
                openImageChooser();
                break;
            case R.id.imgMenu:
                OpenMenu();
                break;
            case R.id.imgClose:
                CloseMenu();
                break;
            case R.id.llHome:
               /*  finish();
                Intent HomeIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(HomeIntent);  */
                CloseMenu();
                break;
            case R.id.llContact:
                CloseMenu();
                ContactUs();
                break;
            case R.id.llShare:
                CloseMenu();
                Share_Dialog();
                break;
            case R.id.llRate:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case R.id.llMoreApp:
                Uri uri = Uri.parse("https://play.google.com/store/apps/developer?id=ITFlash%20Software&hl=en");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void OpenMenu() {
        Close_Layout.setVisibility(View.VISIBLE);
        imgMenu.setVisibility(View.GONE);
        Animation_Layout.setVisibility(View.VISIBLE);
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menu_down);
        Animation_Layout.startAnimation(slide_up);
    }

    public void CloseMenu() {
        Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menu_up);
        Animation_Layout.startAnimation(slide_down);
        Close_Layout.setVisibility(View.GONE);
        imgMenu.setVisibility(View.VISIBLE);
        Animation_Layout.setVisibility(View.GONE);
    }

    public void ContactUs() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("image/jpeg");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"itflashsoftware@gmail.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Love Sticker");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        emailIntent.putExtra(Intent.EXTRA_STREAM, "");
        emailIntent.setPackage("com.google.android.gm");
        startActivity(emailIntent);
    }

    public void Share_Dialog() {
        final Dialog dialog = new Dialog(ctx, R.style.Theme_CustomDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.share_custom_dialog);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ImageView imgFB = (ImageView) dialog.findViewById(R.id.imgFB);
        ImageView imgTwitter = (ImageView) dialog.findViewById(R.id.imgTwitter);
        ImageView imgWhatsapp = (ImageView) dialog.findViewById(R.id.imgWhatsapp);
        ImageView imgMail = (ImageView) dialog.findViewById(R.id.imgMail);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnShareCancel);


        imgFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnline(MainActivity.this)) {
                    Facebook_Share();
                } else {
                    Toast.makeText(ctx, "Check Your Internet", Toast.LENGTH_LONG).show();
                }
            }
        });

        imgTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Twitter_Share();
            }
        });

        imgWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Whatsapp_Share();
            }
        });

        imgMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mail_Share();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
    }

    private void Whatsapp_Share() {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
//        whatsappIntent.putExtra(Intent.EXTRA_STREAM, SharePath);
        whatsappIntent.putExtra(android.content.Intent.EXTRA_TEXT, Utils.App_Share_Whatsapp);
        whatsappIntent.setPackage("com.whatsapp");
//        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
        try {
            ctx.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ctx, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void Facebook_Share() {
        Intent FacebookIntent = new Intent(Intent.ACTION_SEND);
        FacebookIntent.setPackage("com.facebook.katana");
        FacebookIntent.setType("text/plain");
//        FacebookIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Utils.App_Share_Facebook));
        FacebookIntent.putExtra(Intent.EXTRA_TEXT, Utils.App_Share_Facebook_Twitter);
        startActivity(FacebookIntent);
    }

    private void Twitter_Share() {
        Intent TwitterIntent = new Intent(Intent.ACTION_SEND);
        TwitterIntent.setType("text/plain");
//        TwitterIntent.putExtra(Intent.EXTRA_STREAM, SharePath);
        TwitterIntent.putExtra(Intent.EXTRA_TEXT, Utils.App_Share_Facebook_Twitter);
        TwitterIntent.setPackage("com.twitter.android");
        try {
            ctx.startActivity(TwitterIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ctx, "Twitter have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void Mail_Share() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Love Sticker");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Utils.PLAY_STORE_LINK);
        emailIntent.setPackage("com.google.android.gm");
        startActivity(emailIntent);
    }

    private void takeImage() {

        if (CameraCheckId == 0) {

//            if (camera != null) {
//                if (flashmode) {
////                    Camera.Parameters param = camera.getParameters();
////                    param.setFlashMode(!flashmode ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_ON);
////                    camera.setParameters(param);
//                    flashmode = false;
//                }
//            }

            camera.takePicture(null, null, new Camera.PictureCallback() {

//                private File imageFile;
//                byte[] byteArray;
//                String SaveinCache;

                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    ImageGetSet Product = new ImageGetSet();
                    Product.setImage_byteArray(data);
                    Intent intent = new Intent(MainActivity.this, Edit_Image.class);
                    intent.putExtra("CheckActivity", "Main");
                    intent.putExtra("CameraId", "MainCamera");
                    startActivityForResult(intent, 1);

                    if (flashmode) {
                        Camera.Parameters param = camera.getParameters();
                        param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(param);
                        flashmode = false;
                    }
                }
            });
        } else {
            camera.takePicture(null, null, new Camera.PictureCallback() {

                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    ImageGetSet Product = new ImageGetSet();
                    Product.setImage_byteArray(data);
                    Intent intent = new Intent(MainActivity.this, Edit_Image.class);
                    intent.putExtra("CheckActivity", "Main");
                    intent.putExtra("CameraId", "FontCamera");
                    startActivityForResult(intent, 1);
                }
            });
        }
    }

//    class SavePhotoTask extends AsyncTask<byte[], String, String> {
//        @Override
//        protected String doInBackground(byte[]... jpeg) {
//
//            File photo = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
//
//            if (photo.exists()) {
//                photo.delete();
//            }
//
//            try {
//                FileOutputStream fos = new FileOutputStream(photo.getPath());
//
//                fos.write(jpeg[0]);
//                fos.close();
//            } catch (java.io.IOException e) {
//                Log.e("PictureDemo", "Exception in photoCallback", e);
//            }
//
//            Intent intent = new Intent(MainActivity.this, Edit_Image.class);
//            intent.putExtra("EditImage", photo.getPath());
//            intent.putExtra("CheckActivity", "Main");
//            intent.putExtra("CameraId", "MainCamera");
//            startActivityForResult(intent, 1);
//            return (null);
//        }
//    }

    int SELECT_PICTURE = 100;

    public void openImageChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageUri);
                    Log.i("select image", "Image Path : " + path);

                    Intent i = new Intent(MainActivity.this, Edit_Image.class);
                    i.putExtra("CheckActivity", "GalleryImage");
                    i.putExtra("path", path);
                    startActivityForResult(i, 2);
                }
            }
        } else if (requestCode == 1) {
            CameraCheckId = 0;
            flashCameraButton.setImageResource(R.drawable.ic_flash_off);
            flashmode = false;
        }
    }

    public String getPathFromURI(Uri uri) {
        // just some safety built in
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

//    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize val8ue that is a power of 2 and
//            // keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//        // A power of two value is calculated because the decoder uses a final value by rounding down to the nearest
//        // power of two, as per the inSampleSize documentation.
//        return inSampleSize;
//    }


    //-------------------Save in Cache---------------------------
//    private String saveToInternalStorage(Bitmap bitmapImage) {
//        java.util.Date date = new java.util.Date();
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        // path to /data/data/yourapp/app_data/Love_Sticker
//        File directory = cw.getDir("Love_Sticker", Context.MODE_PRIVATE);
//        // Create imageDir
//        File mypath = new File(directory, currentTimeMillis() + "image.jpg");
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(mypath);
//            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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

//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }

    private void flipCamera() {
        CameraCheckId = (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK);
        if (!openCamera(CameraCheckId)) {
            alertCameraDialog();
        }

    }

    private void alertCameraDialog() {
        AlertDialog.Builder dialog = createAlert(MainActivity.this, "Camera info", "error to open camera");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private AlertDialog.Builder createAlert(Context context, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light_Dialog));
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }

    private void flashOnButton() {
        if (camera != null) {
            try {
                if (flashmode) {
                    Camera.Parameters param = camera.getParameters();
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(param);
                    flashCameraButton.setImageResource(R.drawable.ic_flash_off);
                    flashmode = false;
                } else {
                    Camera.Parameters param = camera.getParameters();
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    camera.setParameters(param);
                    flashCameraButton.setImageResource(R.drawable.ic_flash_on);
                    flashmode = true;
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    //-------------------MarshmallowPremission---------------------------
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 11:
                if (grantResults.length > 0) {
                    boolean location = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean camera = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean Read = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (location && camera && Read) {
                        if (!openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)) {
                            alertCameraDialog();
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                                showMessageOKCancel("Check",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{android.Manifest.permission.CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 11);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 11);
    }

}


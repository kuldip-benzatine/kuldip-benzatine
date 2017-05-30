package com.benzatine.lovestickerimages;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.benzatine.lovestickerimages.Data.ImageGetSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

public class Image_Viewer extends FragmentActivity {

    ViewPager mViewPager;
    static String path;
    static ArrayList<String> mylist;
    static int SelectPosition = 0;
    Context ctx;
    Bitmap bitmap;
    static String imagePath;
    private static int CurrentPosition;
    FullScreenImageAdapter adapterView;
    Dialog dialog;
    static String DeleteImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);

        ctx = this;

        /*if (CheckInterNet()) {
            AdView mAdView = (AdView) findViewById(R.id.adView);
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }*/

        mylist = new ArrayList<String>();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            path = extras.getString("path");
            SelectPosition = extras.getInt("selectPosition", 0);
            mylist = extras.getStringArrayList("arr");
        }

        mViewPager = (ViewPager) findViewById(R.id.viewPage);
        adapterView = new FullScreenImageAdapter(this, mylist);
        mViewPager.setAdapter(adapterView);
        mViewPager.setCurrentItem(SelectPosition);
        CurrentPosition = SelectPosition;
    }

    public class FullScreenImageAdapter extends PagerAdapter {

        private Activity _activity;
        private ArrayList<String> _imagePaths;
        private LayoutInflater inflater;

        public FullScreenImageAdapter(Activity activity, ArrayList<String> imagePaths) {
            this._activity = activity;
            this._imagePaths = imagePaths;
        }

        @Override
        public int getCount() {
            return this._imagePaths.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imgDisplay, imgClose_Viewer, imgOption;

            inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewLayout = inflater.inflate(R.layout.image_viewer_cell, container, false);

            imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgViewer);
            imgClose_Viewer = (ImageView) viewLayout.findViewById(R.id.imgClose_Viewer);
            imgOption = (ImageView) viewLayout.findViewById(R.id.imgOption);

            String uri = _imagePaths.get(position);
            imagePath = uri.replace("[", "").replace("]", "");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(imagePath, options);
            imgDisplay.setImageBitmap(bitmap);

            imgClose_Viewer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _activity.finish();
                }
            });
            imgOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dialog();
                }
            });

            ((ViewPager) container).addView(viewLayout);
            mViewPager.getCurrentItem();
            PageListener pageListener = new PageListener();
            mViewPager.setOnPageChangeListener(pageListener);
            return viewLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((RelativeLayout) object);
        }

        public void refresh() {
            adapterView.notifyDataSetChanged();
        }
    }

    private static class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            Log.i("TAG", "page selected " + position);
            CurrentPosition = position;
        }
    }

    public void Dialog() {
        dialog = new Dialog(ctx, R.style.Theme_CustomDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.custom_dialog);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ImageView dialog_SaveGallery = (ImageView) dialog.findViewById(R.id.dialog_SaveGallery);
        ImageView dialog_Share = (ImageView) dialog.findViewById(R.id.dialog_Share);
        ImageView dialog_Edit = (ImageView) dialog.findViewById(R.id.dialog_Edit);
        ImageView dialog_Delete = (ImageView) dialog.findViewById(R.id.dialog_Delete);

        dialog_SaveGallery.setImageResource(R.drawable.btn_save_gallery);
        dialog_Share.setImageResource(R.drawable.btn_share_viewer);
        dialog_Edit.setImageResource(R.drawable.btn_edit);
        dialog_Delete.setImageResource(R.drawable.btn_delete);

        if (imagePath.contains("data")) {
            dialog_SaveGallery.setVisibility(View.VISIBLE);
        }

        dialog_SaveGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SaveGallery = mylist.get(CurrentPosition).replace("[", "").replace("]", "");
                dialog.dismiss();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap SaveBitmap = BitmapFactory.decodeFile(SaveGallery, options);
                saveImageIntoGallery(SaveBitmap);


            }
        });

        dialog_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Share_Dialog();
            }
        });

        dialog_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mylist.get(CurrentPosition).replace("[", "").replace("]", "");
                dialog.dismiss();
                Intent i = new Intent(ctx, Edit_Image.class);
                i.putExtra("EditImageViewer", s);
                i.putExtra("CheckActivity", "Edit");
                startActivity(i);
            }
        });

        dialog_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                alertCameraDialog();
            }
        });
        dialog.show();
    }

    public void Share_Dialog() {
        final Dialog dialog = new Dialog(ctx, R.style.Theme_CustomDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.share_custom_dialog);

        ImageView imgFB = (ImageView) dialog.findViewById(R.id.imgFB);
        ImageView imgTwitter = (ImageView) dialog.findViewById(R.id.imgTwitter);
        ImageView imgWhatsapp = (ImageView) dialog.findViewById(R.id.imgWhatsapp);
        ImageView imgMail = (ImageView) dialog.findViewById(R.id.imgMail);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnShareCancel);

        imgFB.setImageResource(R.drawable.ic_facebook);
        imgTwitter.setImageResource(R.drawable.ic_twitter);
        imgWhatsapp.setImageResource(R.drawable.ic_whatsapp);
        imgMail.setImageResource(R.drawable.ic_gmail);

        imgFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Facebook_Share();
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

    public void DeleteInGallery(String s) {
        File fdelete = new File(s);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + s);
                Toast.makeText(ctx, "Delete Succesfully", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println("file not Deleted :" + s);
                Toast.makeText(ctx, "Delete Not Succesfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
    }

    public static void DeleteInCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            boolean success = deleteDir(new File(DeleteImage));
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

    private void alertCameraDialog() {
        AlertDialog.Builder dialog = createAlert(ctx, "Love Sticker", "Are Sure Delete Image ?");

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                DeleteImage = mylist.get(CurrentPosition).replace("[", "").replace("]", "");

                if (DeleteImage.contains("data")) {
                    DeleteInCache(ctx);

                    mylist.remove(mylist.get(CurrentPosition));
                    adapterView = new FullScreenImageAdapter(Image_Viewer.this, mylist);
                    mViewPager.setAdapter(adapterView);
                    adapterView.notifyDataSetChanged();
                    mViewPager.setCurrentItem(SelectPosition);

                    Intent intentMessage = new Intent();
                    intentMessage.putStringArrayListExtra("arr", mylist);
                    setResult(1, intentMessage);
                    finish();

                } else {
//                    DeleteInGallery(DeleteImage);
                    File mFile = new File(DeleteImage);
                    deleteFileFromMediaStore(getContentResolver(), mFile);

                    String s = mylist.get(CurrentPosition);
                    mylist.remove(s);
                    adapterView = new FullScreenImageAdapter(Image_Viewer.this, mylist);
                    mViewPager.setAdapter(adapterView);
                    adapterView.notifyDataSetChanged();
                    mViewPager.setCurrentItem(SelectPosition);

                    Intent intentMessage = new Intent();
                    intentMessage.putStringArrayListExtra("arr", mylist);
                    setResult(1, intentMessage);
                    finish();

                }
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private AlertDialog.Builder createAlert(Context context, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(
                new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog));
//        dialog.setIcon(R.drawable.ic_launcher);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }

//    private void Whatsapp_Share() {
//        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//        whatsappIntent.setType("image/jpeg");
//        whatsappIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Str_CurrentPosition));
//        whatsappIntent.setPackage("com.whatsapp");
////        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
//        try {
//            ctx.startActivity(whatsappIntent);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(ctx, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void Facebook_Share() {
//        Intent FacebookIntent = new Intent(Intent.ACTION_SEND);
//        FacebookIntent.setPackage("com.facebook.katana");
//        FacebookIntent.setType("image/*");
//        FacebookIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + Str_CurrentPosition));
//        startActivity(FacebookIntent);
//    }
//
//    private void Twitter_Share() {
//        String s = mylist.get(CurrentPosition).replace("[", "").replace("]", "");
//        Intent TwitterIntent = new Intent(Intent.ACTION_SEND);
//        TwitterIntent.setType("image/jpeg");
//        TwitterIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(s));
//        TwitterIntent.setPackage("com.twitter.android");
//        try {
//            ctx.startActivity(TwitterIntent);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(ctx, "Twitter have not been installed.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void Mail_Share() {
//        String s = mylist.get(CurrentPosition).replace("[", "").replace("]", "");
//        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//        emailIntent.setType("image/jpeg");
//        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
//        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Love Sticker");
//        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
//        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + s));
//        emailIntent.setPackage("com.google.android.gm");
//        startActivity(emailIntent);
//    }

    private void saveImageIntoGallery(Bitmap bitmap) {
        FileOutputStream fos = null;
        try {

            File folder = new File(Environment.getExternalStorageDirectory() + "/Love Sticker");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {
                // Do something on success
                final File mypath = new File(Environment.getExternalStorageDirectory() + "/Love Sticker/" + currentTimeMillis() + "image.jpg");
                fos = new FileOutputStream(mypath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Toast.makeText(getBaseContext(), "Save Succesfully", Toast.LENGTH_SHORT).show();

                ImageGetSet Product = new ImageGetSet();
                Product.setSaveToGallery(String.valueOf(mypath));
                Product.setIsGalleryNotifyAdapter(true);
//------------------------Refresh Gallery--------------------------------------
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mypath)));
            } else {
                // Do something else on failure
                Toast.makeText(getBaseContext(), "Image not Save", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void Whatsapp_Share() {
        String s = mylist.get(CurrentPosition).replace("[", "").replace("]", "");
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("image/jpeg");
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(s));
        whatsappIntent.setPackage("com.whatsapp");
//        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
        try {
            ctx.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ctx, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void Facebook_Share() {
        String s = mylist.get(CurrentPosition).replace("[", "").replace("]", "");
        Intent FacebookIntent = new Intent(Intent.ACTION_SEND);
        FacebookIntent.setPackage("com.facebook.katana");
        FacebookIntent.setType("image/*");
        FacebookIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + s));
        startActivity(FacebookIntent);
    }

    private void Twitter_Share() {
        String s = mylist.get(CurrentPosition).replace("[", "").replace("]", "");
        Intent TwitterIntent = new Intent(Intent.ACTION_SEND);
        TwitterIntent.setType("image/jpeg");
        TwitterIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(s));
        TwitterIntent.setPackage("com.twitter.android");
        try {
            ctx.startActivity(TwitterIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ctx, "Twitter have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void Mail_Share() {
        String s = mylist.get(CurrentPosition).replace("[", "").replace("]", "");
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("image/jpeg");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Love Sticker");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + s));
        emailIntent.setPackage("com.google.android.gm");
        startActivity(emailIntent);
    }
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
//            Str_CurrentPosition = "";
//            Str_CurrentPosition = mylist.get(CurrentPosition).replace("[", "").replace("]", "");
//
//            List<NameValuePair> parmas1 = new ArrayList<NameValuePair>();
//            List<NameValuePair> parmas2 = new ArrayList<NameValuePair>();
//            parmas1.add(new BasicNameValuePair("user", "benzatine"));
//            parmas1.add(new BasicNameValuePair("password", "benzatineinfotech"));
//            parmas2.add(new BasicNameValuePair("upn_image", Str_CurrentPosition));
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
//                    if(jStatus==200) {
//                    }else {
//                        //Utils.setCustomToast(mcontext,jobject1.optString("msg"),false);
//                    }
//                } catch (Exception e) {
//                    System.out.println("Exception e:--" + e);
//                }
//            }
////            dismissProgressDialog();
//            if (pDialog != null) {
//                if (pDialog.isShowing()) {
//                    pDialog.dismiss();
//                }
//            }
//        }
//    }
//
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
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                Bitmap SaveBitmap = BitmapFactory.decodeFile(Str_CurrentPosition, options);
//
//                String playStoreLink = Utils.PLAY_STORE_LINK;
//
//                shareIntent.setPackage("com.google.android.gm");
//                //shareIntent1.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
//                shareIntent.setType("image");
//                shareIntent.putExtra(Intent.EXTRA_EMAIL, "test@gmail.com");
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, ctx.getResources().getString(R.string.app_name));
//                shareIntent.putExtra(Intent.EXTRA_TEXT, playStoreLink);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(ctx, SaveBitmap));
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
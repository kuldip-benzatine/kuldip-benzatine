package com.benzatine.lovestickerimages;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.benzatine.lovestickerimages.Data.ImageGetSet;
import com.bumptech.glide.Glide;
//import com.fivehundredpx.greedolayout.GreedoLayoutManager;
//import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;
//import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ADMIN on 03-Apr-17.
 */

/*
public class LoveSticker extends Fragment {

    GridView gvLoveSticker;
    ImageView iv;
    RecyclerView recyclerView;
    PhotosAdapter recyclerAdapter;
    ArrayList<String> myList = new ArrayList<String>();// list of file paths
    File[] listFile;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.lovesticker, container, false);

        LoveSticke_Images();

//        gvLoveSticker = (GridView) root.findViewById(R.id.gvLoveSticker);
//        gvLoveSticker.setAdapter(new ImageAdapter(getContext()));
//
//        gvLoveSticker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                String s = myList.get(position);
//
//                int selectPosition = position;
//
//                Intent i = new Intent(getContext(), Image_Viewer.class);
//                i.putExtra("path", s);
//                i.putExtra("selectPosition", selectPosition);
//                i.putStringArrayListExtra("arr", myList);
//                startActivity(i);
//
//            }
//        });
        CollectionSortByDate(myList);
        recyclerAdapter = new PhotosAdapter(getContext(), myList);
        GreedoLayoutManager layoutManager = new GreedoLayoutManager(recyclerAdapter);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        layoutManager.setMaxRowHeight(500);

        int spacing = MeasUtils.dpToPx(2, getContext());
        recyclerView.addItemDecoration(new GreedoSpacingItemDecoration(spacing));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String s = myList.get(position);

                        int selectPosition = position;
                        Intent i = new Intent(getContext(), Image_Viewer.class);
                        i.putExtra("path", s);
                        i.putExtra("selectPosition", selectPosition);
                        i.putStringArrayListExtra("arr", myList);
                        startActivityForResult(i, 1);
                    }
                })
        );


        if (myList.size() > 0) {
            getContext().getSharedPreferences("Love_Sticker", 0).edit().clear().commit();
            String LastImagePath = myList.get(0).toString();
            prefs = getActivity().getSharedPreferences("Love_Sticker", MODE_PRIVATE);
            editor = prefs.edit();
            editor.putString("RecentImage", LastImagePath);
            editor.commit();
        }
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        ImageGetSet Product = new ImageGetSet();

        if (Product.isLoveStickerNotifyAdapter()) {

            String LastSaveToLoveStickerImage = Product.getSaveToLoveSticker();
            Product.setSaveToGallery("");
            if (LastSaveToLoveStickerImage != null && !LastSaveToLoveStickerImage.equals("")) {
                myList.add(LastSaveToLoveStickerImage);
            }
            recyclerAdapter.notifyDataSetChanged();
            CollectionSortByDate(myList);
            recyclerAdapter = new PhotosAdapter(getContext(), myList);
            recyclerView.setAdapter(recyclerAdapter);
            Product.setIsLoveStickerNotifyAdapter(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (null != data) {
                //fetch the message String
                String s = data.getStringExtra("SaveToGallery");
                ArrayList<String> newlist = data.getStringArrayListExtra("arr");
                recyclerAdapter.notifyDataSetChanged();
                CollectionSortByDate(myList);
                recyclerAdapter = new PhotosAdapter(getContext(), newlist);
                recyclerView.setAdapter(recyclerAdapter);
            }
        }
    }

    public void CollectionSortByDate(ArrayList<String> _list) {

        Collections.sort(_list, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                File f1 = new File(lhs);
                File f2 = new File(rhs);

                if (f1.lastModified() > (f2.lastModified())) {
                    return -1;
                } else if (f1.lastModified() < (f2.lastModified())) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });
    }

    //----------------Recyclerview Click Event-----------------------------------------
    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

        private RecyclerItemClickListener.OnItemClickListener mListener;

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, RecyclerItemClickListener.OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    //----------------Get all image in cache-----------------------------------------
    public void LoveSticke_Images() {

        ContextWrapper cw = new ContextWrapper(getActivity());
        // path to /data/data/yourapp/app_data/Love_Sticker
        File directory = cw.getDir("Love_Sticker", MODE_PRIVATE);

//        File directory = new File(Environment.getExternalStorageDirectory() + "/.Love Sticker");
        if (directory.isDirectory()) {
            listFile = directory.listFiles();

            for (int i = 0; i < listFile.length; i++) {

                myList.add(listFile[i].getAbsolutePath());

            }
        }
    }

    public static class MeasUtils {
        public static int pxToDp(int px, Context context) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,
                    context.getResources().getDisplayMetrics());
        }

        public static int dpToPx(float dp, Context context) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                    context.getResources().getDisplayMetrics());
        }
    }

    public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> implements GreedoLayoutSizeCalculator.SizeCalculatorDelegate {
        private static final int IMAGE_COUNT = 100; // number of images adapter will show

        //        private final int[] mImageResIds = Constants.IMAGES;
//        private final double[] mImageAspectRatios = new double[Constants.IMAGES.length];
        private final double[] mImageAspectRatios = new double[myList.size()];

        private Context mContext;
        private ArrayList<String> mList;

        @Override
        public double aspectRatioForIndex(int index) {
            // Precaution, have better handling for this in greedo-layout
            if (index >= getItemCount() - index)
                return 1.0;
            return mImageAspectRatios[getLoopedIndex(index)];

        }

        public class PhotoViewHolder extends RecyclerView.ViewHolder {
            private ImageView mImageView;

            public PhotoViewHolder(ImageView imageView) {
                super(imageView);
                mImageView = imageView;
            }
        }

        public PhotosAdapter(Context context, ArrayList<String> _list) {
            mContext = context;
            mList = _list;
            calculateImageAspectRatios();
        }

        @Override
        public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            return new PhotoViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(PhotoViewHolder holder, int position) {
            String s = mList.get(position).replace("[", "").replace("]", "");
            Glide.with(mContext)
                    .load(s)
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
//            return IMAGE_COUNT;
            return mList.size();
        }

        private void calculateImageAspectRatios() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            for (int i = 0; i < mList.size(); i++) {
                String s = mList.get(i).replace("[", "").replace("]", "");
                BitmapFactory.decodeFile(s, options);
                mImageAspectRatios[i] = options.outWidth / (double) options.outHeight * 2;
            }
        }

        // Index gets wrapped around <code>Constants.IMAGES.length</code> so we can loop content.
        private int getLoopedIndex(int index) {
            return index % mList.size(); // wrap around
        }
    }

//    class ImageAdapter extends BaseAdapter {
//
//        private Context context;
//
//
//        public ImageAdapter(Context c) {
//            context = c;
//        }
//
//        @Override
//        public int getCount() {
//            return myList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            // TODO Auto-generated method stub
//            return myList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View v = convertView;
//            if (v == null) {
//                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                v = mInflater.inflate(R.layout.grid_cell, null);
//            }
//            iv = (ImageView) v.findViewById(R.id.imgLoveSticker);
//            ImageView imgCameraRoll = (ImageView) v.findViewById(R.id.imageView);
//
//            imgCameraRoll.setVisibility(View.GONE);
//
//            String s = String.valueOf(getItem(position));
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap SaveBitmap = BitmapFactory.decodeFile(s, options);
//
//            int h = SaveBitmap.getHeight();
//            int w = SaveBitmap.getWidth();
//
//            if (h < w) {
//                iv.setLayoutParams(new RelativeLayout.LayoutParams(h, w));
//            } else {
//                iv.setLayoutParams(new RelativeLayout.LayoutParams(h, w));
//            }
//
//
//            iv.setImageBitmap(SaveBitmap);
//
////            Glide.with(context)
////                    .load(getItem(position))
////                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
////                    .into(iv);
//
//
//            return v;
//        }
//
//    }

}
*/

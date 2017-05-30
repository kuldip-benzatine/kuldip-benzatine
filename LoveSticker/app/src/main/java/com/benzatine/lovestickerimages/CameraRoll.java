package com.benzatine.lovestickerimages;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.benzatine.lovestickerimages.Data.ImageGetSet;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by ADMIN on 03-Apr-17.
 */
/*

public class CameraRoll extends Fragment {


    GridView gvCameraRoll;
    ArrayList<File> list;
    ArrayList<String> myList;
    ImageAdapter mAdapter;
    private Cursor cc = null;
    private String[] strUrls;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.cameraroll, container, false);

        gvCameraRoll = (GridView) root.findViewById(R.id.girdCameraRoll);

//        list = imageReader(Environment.getExternalStorageDirectory());
//        myList = new ArrayList<String>();
//        for (int i = 0; i < list.size(); i++) {
//            myList.add(String.valueOf(Arrays.<String>asList(String.valueOf(list.get(i)))));
//        }

//        Collections.sort(myList, Collections.reverseOrder());
//        Collections.sort(myList);

        myList = getAllImageInGallery();

//        Collections.sort(myList);
        CollectionSortByDate(myList);

        mAdapter = new ImageAdapter(getContext(), myList);
        gvCameraRoll.setAdapter(mAdapter);
        gvCameraRoll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String s = String.valueOf(myList.get(position));
                int selectPosition = position;
                Intent i = new Intent(getContext(), Image_Viewer.class);
                i.putExtra("path", s);
                i.putExtra("selectPosition", selectPosition);
                i.putStringArrayListExtra("arr", myList);
                startActivityForResult(i, 1);
            }
        });

//        gvCameraRoll.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                Toast.makeText(getContext(), scrollState+"", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
////                Toast.makeText(getContext(), "" + visibleItemCount, Toast.LENGTH_SHORT).show();
//            }
//        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        ImageGetSet Product = new ImageGetSet();
        if (Product.isGalleryNotifyAdapter()) {

            String LastSaveToGalleryImage = Product.getSaveToGallery();
            Product.setSaveToGallery("");
            if (LastSaveToGalleryImage != null && !LastSaveToGalleryImage.equals("")) {
                myList.add(LastSaveToGalleryImage);
            }
            mAdapter.notifyDataSetChanged();
            CollectionSortByDate(myList);
            mAdapter = new ImageAdapter(getContext(), myList);
            Product.setIsGalleryNotifyAdapter(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (null != data) {
                //fetch the message String
                ArrayList<String> newlist = data.getStringArrayListExtra("arr");
                myList = new ArrayList<String>();
                myList.addAll(newlist);
                mAdapter.notifyDataSetChanged();
                CollectionSortByDate(myList);
                mAdapter = new ImageAdapter(getContext(), myList);
                gvCameraRoll.setAdapter(mAdapter);
            }
        }
    }

    public ArrayList<String> getAllImageInGallery() {
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";
        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<String>();

        ArrayList<String> resultIAV = new ArrayList<String>();

        String[] directories = null;
        if (u != null) {
            c = getContext().getContentResolver().query(u, projection, null, null, orderBy);
        }
        if ((c != null) && (c.moveToFirst())) {
            do {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try {
                    dirList.add(tempDir);
//                    resultIAV.add(tempDir);
                } catch (Exception e) {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);
        }

        for (int i = 0; i < dirList.size(); i++) {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();

            if (imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if (imagePath.isDirectory()) {
                        imageList = imagePath.listFiles();
                    }
                    if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                            ) {
                        String path = imagePath.getAbsolutePath();
                        resultIAV.add(path);


                    }
                }
                //  }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return resultIAV;
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

    class ImageAdapter extends BaseAdapter {

        private Context context;
        ArrayList<String> _list;

        public ImageAdapter(Context c, ArrayList<String> My_List) {
            context = c;
            _list = My_List;
        }

        @Override
        public int getCount() {
            return _list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return _list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.grid_cell, null);
            }

            ImageView iv = (ImageView) v.findViewById(R.id.imageView);
            ImageView imgLoveSticker = (ImageView) v.findViewById(R.id.imgLoveSticker);
            imgLoveSticker.setVisibility(View.GONE);

//            String s = String.valueOf(getItem(position));
//            iv.setImageURI(Uri.parse(s));

            String ImagePath = String.valueOf(getItem(position)).replace("[", "").replace("]", "");
            Glide.with(context)
                    .load(ImagePath)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(iv);

            return v;
        }
    }

    ArrayList<File> imageReader(File root) {
        ArrayList<File> a = new ArrayList<>();

        File[] files = root.listFiles();
        if (files.length != 0) {
            for (int i = 0; files.length > i; i++) {

                if (files[i].isDirectory()) {
                    a.addAll(imageReader(files[i]));
                } else {
                    if (files[i].getName().endsWith(".jpg")) {
                        a.add(files[i]);
                    }
                }
            }
        }
        return a;
    }

}
*/

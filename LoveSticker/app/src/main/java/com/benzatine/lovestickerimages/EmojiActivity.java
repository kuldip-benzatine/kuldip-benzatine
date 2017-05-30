package com.benzatine.lovestickerimages;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import com.benzatine.lovestickerimages.StickerImageView.StickerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.benzatine.lovestickerimages.Edit_Image.hideStickerLayout;
import static com.benzatine.lovestickerimages.Edit_Image.isAnimation;
import static com.benzatine.lovestickerimages.Utils.CountSticker;

public class EmojiActivity extends Fragment implements AdapterView.OnItemClickListener{

    AssetManager assetManager;
    String[] imgPath;

    public static Bitmap EmojiBitmap = null;

    ArrayList<String> stickerDir;
    View root;

    ArrayList<String> smily;
    ArrayList<String> animal;
    ArrayList<String> food;
    ArrayList<String> activity;
    ArrayList<String> travel;
    ArrayList<String> object;
    ArrayList<String> symbo;
    ArrayList<String> flags;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.emoji_activity, container, false);
        isAnimation = false;

        init();
        init_adapter();


        init_action();
        return root;
    }

    GridView gvEmojiSmily;
    GridView gvEmojiAnimals;
    GridView gvEmojiFood;
    GridView gvEmojiActivity;
    GridView gvEmojiTravels;
    GridView gvEmojiObject;
    GridView gvEmojiSymbols;
    GridView gvEmojiFlags;
    public void init(){
        gvEmojiSmily = (GridView) root.findViewById(R.id.gvEmojiSmily);
        gvEmojiAnimals = (GridView) root.findViewById(R.id.gvEmojiAnimals);
        gvEmojiFood = (GridView) root.findViewById(R.id.gvEmojiFood);
        gvEmojiActivity = (GridView) root.findViewById(R.id.gvEmojiActivity);
        gvEmojiTravels = (GridView) root.findViewById(R.id.gvEmojiTravels);
        gvEmojiObject = (GridView) root.findViewById(R.id.gvEmojiObject);
        gvEmojiSymbols = (GridView) root.findViewById(R.id.gvEmojiSymbols);
        gvEmojiFlags = (GridView) root.findViewById(R.id.gvEmojiFlags);
    }

    public void init_action(){

//        setListViewHeightBasedOnChildren(gvEmojiActivity);
//        setListViewHeightBasedOnChildren(gvEmojiTravels);
//        setListViewHeightBasedOnChildren(gvEmojiObject);
//        setListViewHeightBasedOnChildren(gvEmojiSymbols);
//        setListViewHeightBasedOnChildren(gvEmojiFlags);

        //gvEmojiSmily.setOnItemClickListener(this);

//        gvEmojiActivity.setOnItemClickListener(this);
//        gvEmojiTravels.setOnItemClickListener(this);
//        gvEmojiObject.setOnItemClickListener(this);
//        gvEmojiSymbols.setOnItemClickListener(this);
//        gvEmojiFlags.setOnItemClickListener(this);
    }

    public void init_adapter(){
        assetManager = getActivity().getAssets();

        smily = new ArrayList<String>();
        animal = new ArrayList<String>();
        food = new ArrayList<String>();
        activity = new ArrayList<String>();
        travel = new ArrayList<String>();
        object = new ArrayList<String>();
        symbo = new ArrayList<String>();
        flags = new ArrayList<String>();

        stickerDir = new ArrayList<String>();
        stickerDir.add("emoji/Smileys_People");
        stickerDir.add("emoji/Animals_Nature");
        stickerDir.add("emoji/Food_Drink");
        stickerDir.add("emoji/Activit");
        stickerDir.add("emoji/Travel_Places");
        stickerDir.add("emoji/Objects");
        stickerDir.add("emoji/Symbo");
        stickerDir.add("emoji/Flags");

        stkPathName = new ArrayList<String>();
        stkPathName.add("/Smileys_");
        stkPathName.add("/animals_");
        stkPathName.add("/food_");
        stkPathName.add("/activity_");
        stkPathName.add("/travels_");
        stkPathName.add("/object_");
        stkPathName.add("/symbols_");
        stkPathName.add("/flg_");

        gvEmojiSmily.setAdapter(new EmojiAdapter(getContext(),smily = listFiles(stickerDir.get(0),stkPathName.get(0))));
        setListViewHeightBasedOnChildren(gvEmojiSmily);
        gvEmojiSmily.setOnItemClickListener(EmojiActivity.this);
        isFirst = false;

//        gvEmojiSmily.setAdapter(new EmojiAdapter(getContext(),smily = listFiles(stickerDir.get(0),stkPathName.get(0))));
        new task().execute();
    }

    Integer adap = 0;
    Boolean isFirst = true;
    class task extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                adap++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch (adap) {
                //case 0:

                    //new task().execute();
                    //break;
                case 1:
                    gvEmojiAnimals.setAdapter(new EmojiAdapter(getContext(), animal = listFiles(stickerDir.get(1), stkPathName.get(1))));
                    setListViewHeightBasedOnChildren(gvEmojiAnimals);
                    gvEmojiAnimals.setOnItemClickListener(EmojiActivity.this);
                    new task().execute();
                    break;
                case 2:
                    gvEmojiFood.setAdapter(new EmojiAdapter(getContext(), food = listFiles(stickerDir.get(2), stkPathName.get(2))));
                    setListViewHeightBasedOnChildren(gvEmojiFood);
                    gvEmojiFood.setOnItemClickListener(EmojiActivity.this);
                    new task().execute();
                    break;
                case 3:
                    gvEmojiActivity.setAdapter(new EmojiAdapter(getContext(), activity = listFiles(stickerDir.get(3), stkPathName.get(3))));
                    setListViewHeightBasedOnChildren(gvEmojiActivity);
                    gvEmojiActivity.setOnItemClickListener(EmojiActivity.this);
                    new task().execute();
                    break;
                case 4:
                    gvEmojiTravels.setAdapter(new EmojiAdapter(getContext(), travel = listFiles(stickerDir.get(4), stkPathName.get(4))));
                    setListViewHeightBasedOnChildren(gvEmojiTravels);
                    gvEmojiTravels.setOnItemClickListener(EmojiActivity.this);
                    new task().execute();
                    break;
                case 5:
                    gvEmojiObject.setAdapter(new EmojiAdapter(getContext(), object = listFiles(stickerDir.get(5), stkPathName.get(5))));
                    setListViewHeightBasedOnChildren(gvEmojiObject);
                    gvEmojiObject.setOnItemClickListener(EmojiActivity.this);
                    new task().execute();
                    break;
                case 6:
                    gvEmojiSymbols.setAdapter(new EmojiAdapter(getContext(), symbo = listFiles(stickerDir.get(6), stkPathName.get(6))));
                    setListViewHeightBasedOnChildren(gvEmojiSymbols);
                    gvEmojiSymbols.setOnItemClickListener(EmojiActivity.this);
                    new task().execute();
                    break;
                case 7 :
                    gvEmojiFlags.setAdapter(new EmojiAdapter(getContext(), flags = listFiles(stickerDir.get(7), stkPathName.get(7))));
                    setListViewHeightBasedOnChildren(gvEmojiFlags);
                    gvEmojiFlags.setOnItemClickListener(EmojiActivity.this);
                    new task().execute();
                    break;
                default:
                    adap = 0;
                    isFirst = true;
                    break;
            }
        }
    }

    ArrayList<String> stkPath;
    ArrayList<String> stkPathName;
    private ArrayList<String> listFiles(String dirFrom,String pathName) {
        imgPath = null;
        Resources res = getResources(); //if you are in an activity
        AssetManager am = res.getAssets();
        imgPath = new String[0];
        stkPath = new ArrayList<String>();
        //for(int i=0; i<dirFrom.size(); i++) {
            try {
                imgPath = am.list(dirFrom);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imgPath != null) {
                for (int a = 0; a < imgPath.length; a++) {
                    Log.d("", imgPath[a]);
                    stkPath.add("///android_asset/"+dirFrom+pathName+(a+1)+".png");
                }
                imgPath = new String[0];
            }
       // }
        return stkPath;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InputStream is = null;
        try {
            if(view.getParent() == gvEmojiSmily){
                is = assetManager.open(smily.get(position).replace("///android_asset/",""));//assetManager.open("emoji/" + imgPath[position]);
            }else if(view.getParent() == gvEmojiAnimals){
                is = assetManager.open(animal.get(position).replace("///android_asset/",""));//assetManager.open("emoji/" + imgPath[position]);
            }else if(view.getParent() == gvEmojiFood){
                is = assetManager.open(food.get(position).replace("///android_asset/",""));//assetManager.open("emoji/" + imgPath[position]);
            }else if(view.getParent() == gvEmojiActivity){
                is = assetManager.open(activity.get(position).replace("///android_asset/",""));//assetManager.open("emoji/" + imgPath[position]);
            }else if(view.getParent() == gvEmojiTravels){
                is = assetManager.open(travel.get(position).replace("///android_asset/",""));//assetManager.open("emoji/" + imgPath[position]);
            }else if(view.getParent() == gvEmojiObject){
                is = assetManager.open(object.get(position).replace("///android_asset/",""));//assetManager.open("emoji/" + imgPath[position]);
            }else if(view.getParent() == gvEmojiSymbols){
                is = assetManager.open(symbo.get(position).replace("///android_asset/",""));//assetManager.open("emoji/" + imgPath[position]);
            }else if(view.getParent() == gvEmojiFlags){
                is = assetManager.open(flags.get(position).replace("///android_asset/",""));//assetManager.open("emoji/" + imgPath[position]);
            }
//            is = assetManager.open(stkPath.get(position).replace("///android_asset/",""));//assetManager.open("emoji/" + imgPath[position]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CountSticker();
        isAnimation = true;

        EmojiBitmap = BitmapFactory.decodeStream(is);

        Edit_Image obj = new Edit_Image();
        obj.iv_sticker = new StickerImageView(getContext());
        obj.iv_sticker.setImageBitmap(EmojiBitmap);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(250, 250);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        obj.iv_sticker.setLayoutParams(params);

        obj.LastSelectedView = obj.iv_sticker;
        obj.llSticker_root.addView(obj.iv_sticker);

        obj.rlGridView.setVisibility(View.GONE);
        Edit_Image.imgClose.setImageResource(R.drawable.ic_cancel);
        hideStickerLayout();
    }

    class EmojiAdapter extends BaseAdapter {

        Context context;
        ArrayList<String> stickerPath;
        public EmojiAdapter(Context contexts, ArrayList<String> path) {
            stickerPath = path;
            this.context = contexts;
        }

        public int getCount() {
            return stickerPath.size();
        }

        public String getItem(int position) {
            return stickerPath.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.emoji_cell, null);
            }

            ImageView Sticker_cell;
            Sticker_cell = (ImageView) v.findViewById(R.id.Sticker_cell);

            String path = getItem(position); //"///android_asset/emoji/" + StickerPath[position];

            Glide.with(context)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(Sticker_cell);

            return (v);
        }
    }

    public static void setListViewHeightBasedOnChildren(GridView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

//        int desiredWidth = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.UNSPECIFIED);
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i+=7) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, GridLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (GridView.getDividerHeight() * (listAdapter.getCount() - 1));
        params.height = totalHeight + (listView.getVerticalSpacing() * listAdapter.getCount() - 1);
        listView.setLayoutParams(params);
    }
}

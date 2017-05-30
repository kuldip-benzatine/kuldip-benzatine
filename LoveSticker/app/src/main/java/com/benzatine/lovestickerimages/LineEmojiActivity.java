package com.benzatine.lovestickerimages;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.benzatine.lovestickerimages.StickerImageView.StickerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.benzatine.lovestickerimages.Edit_Image.isAnimation;
import static com.benzatine.lovestickerimages.Utils.CountSticker;

/**
 * Created by ADMIN on 27-May-17.
 */

public class LineEmojiActivity extends Fragment {

    static final String ASSET_SELECT_FOLDER = "line_emoji";
    GridView gvLineEmoji;
    ArrayList<String> arrLineSticker = new ArrayList<String>();
    AssetManager assetManager;
    Bitmap bitmap;
    String[] imgPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.line_emoji_activity, container, false);

        isAnimation = false;


        gvLineEmoji = (GridView) root.findViewById(R.id.gvLineEmoji);
        arrLineSticker = new ArrayList<String>();

        assetManager = getActivity().getAssets();


        listFiles(ASSET_SELECT_FOLDER);


        gvLineEmoji.setAdapter(new LineStickerAdapter(getActivity(), arrLineSticker));

        gvLineEmoji.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountSticker();
                isAnimation = true;

                InputStream is = null;
                try {
                    is = assetManager.open(ASSET_SELECT_FOLDER + "/" + arrLineSticker.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bitmap = BitmapFactory.decodeStream(is);

                Edit_Image obj = new Edit_Image();
                obj.iv_sticker = new StickerImageView(getContext());
                obj.iv_sticker.setImageBitmap(bitmap);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                obj.iv_sticker.setLayoutParams(params);

                obj.LastSelectedView = obj.iv_sticker;
                obj.llSticker_root.addView(obj.iv_sticker);

                obj.rlGridView.setVisibility(View.GONE);
                Edit_Image.imgClose.setImageResource(R.drawable.ic_cancel);
                Edit_Image.StickerOn = false;

            }
        });
        return root;
    }


    private void listFiles(String dirFrom) {
        Resources res = getResources(); //if you are in an activity
        AssetManager am = res.getAssets();
        imgPath = new String[0];
        try {
            imgPath = am.list(dirFrom);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (imgPath != null) {
            for (int i = 0; i < imgPath.length; i++) {
                arrLineSticker.add(imgPath[i]);
            }

        }
    }

    class LineStickerAdapter extends BaseAdapter {
        ArrayList<String> StickerPath;
        Context context;
//        int[] imge;

        LineStickerAdapter() {
            StickerPath = null;
//            Detail = null;
//            imge=null;
        }

        public LineStickerAdapter(Context contexts, ArrayList<String> path) {
            this.StickerPath = path;
            this.context = contexts;
        }

        public int getCount() {
            return StickerPath.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.sticker_cell, null);
            }
            ImageView Sticker_cell;
            Sticker_cell = (ImageView) v.findViewById(R.id.Sticker_cell);

            /*InputStream is = null;
            try {
                is = assetManager.open("emoji/" + String.valueOf(StickerPath[position]));
            } catch (IOException e) {
                e.printStackTrace();
            }*/

//            String path = "///android_asset/emojis/Activity/" + StickerPath.get(position);
            String path = "///android_asset/" + ASSET_SELECT_FOLDER + "/" + StickerPath.get(position);

            Glide.with(context)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(Sticker_cell);

//            Bitmap bitmap = BitmapFactory.decodeStream(is);
//            Sticker_cell.setImageBitmap(bitmap);

            return (v);
        }
    }
}

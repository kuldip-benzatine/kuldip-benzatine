package com.benzatine.lovestickerimages;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.benzatine.lovestickerimages.StickerImageView.StickerImageView;

import junit.framework.Assert;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.benzatine.lovestickerimages.Edit_Image.StickerOn;
import static com.benzatine.lovestickerimages.Edit_Image.hideStickerLayout;
import static com.benzatine.lovestickerimages.Edit_Image.isAnimation;
import static com.benzatine.lovestickerimages.Utils.CountSticker;

/**
 * Created by ADMIN on 19-May-17.
 */

public class StickerActivity extends Fragment {

    static GridView gvSticker;
    int cntList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sticker_activity, container, false);

        gvSticker = (GridView) root.findViewById(R.id.gvSticker);
        if(isAnimation) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up);
            animation.setDuration(500);
            gvSticker.setAnimation(animation);
            gvSticker.animate();
            animation.start();
            Edit_Image.StickerOn = true;
            isAnimation = false;
        }

        Document doc = null;
        try {
            doc = getXMLDocument(getActivity().getAssets().open("sticker/listdata.xml"));
        } catch (IOException e1) {
            e1.printStackTrace();
//            finish();
        }
        Element documentElement = doc.getDocumentElement();
        NodeList Nodes = documentElement.getElementsByTagName("sticker");
        cntList = Nodes.getLength();
        gvSticker.setAdapter(new ImageAdapter(getContext(), StickerOnClick));

        return root;
    }


    public Document getXMLDocument(InputStream stream) {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            try {
                document = builder.parse(stream);
            } catch (SAXException e) {
                e.printStackTrace();
            } finally {
                if (stream != null)
                    stream.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        document.getDocumentElement().normalize();
        return document;

    }

    AssetManager assetManager;
    View.OnClickListener StickerOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            hideStickerLayout();
            CountSticker();
            isAnimation = true;

            ImageView im = (ImageView) v;
            String tag = (String) im.getTag();
            Edit_Image obj = new Edit_Image();
            assetManager = getActivity().getAssets();
            obj.iv_sticker = new StickerImageView(getContext());
            try {
                Bitmap b = BitmapFactory.decodeStream(assetManager.open("sticker/" + tag));
                obj.iv_sticker.setImageBitmap(b);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (b.getWidth() * 1.5), (int) (b.getHeight() * 1.5));
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
//                params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                obj.iv_sticker.setLayoutParams(params);

            } catch (IOException e) {
                e.printStackTrace();
            }
            obj.LastSelectedView = obj.iv_sticker;
            obj.llSticker_root.addView(obj.iv_sticker);

            obj.rlGridView.setVisibility(View.GONE);
            hideStickerLayout();
            Edit_Image.imgClose.setImageResource(R.drawable.ic_cancel);
        }
    };

    static int NO_OF_STICKERS = 211;
    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        View.OnClickListener StickerClickListener;
        LayoutInflater infalter;

        // Constructor
        public ImageAdapter(Context c, View.OnClickListener StickerClickListener) {
            mContext = c;
            this.StickerClickListener = StickerClickListener;
            infalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return NO_OF_STICKERS;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        Bitmap bitmap = null;
        AssetManager assetManager;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder view;

            if (convertView == null) {
                view = new ViewHolder();
                convertView = infalter.inflate(R.layout.sticker_cell, null);
                convertView.setTag(view);
            } else {
                view = (ViewHolder) convertView.getTag();
            }

            view.image_sticker = (ImageView) convertView.findViewById(R.id.Sticker_cell);
            view.image_sticker.setOnClickListener(StickerClickListener);
            assetManager = mContext.getAssets();
            String counter = String.format("%02d", position + 1);

            try {
                bitmap = BitmapFactory.decodeStream(assetManager.open("sticker/" + counter + "_ico.png"));

            } catch (IOException e) {
                e.printStackTrace();
            }
            view.image_sticker.setImageBitmap(bitmap);
            view.image_sticker.setTag(counter + ".png");
            return convertView;
        }

        public class ViewHolder {
            ImageView image_sticker;
        }
    }


}
package com.benzatine.lovestickerimages.Data;

/**
 * Created by ADMIN on 29-Apr-17.
 */

public class ImageGetSet {
    static String SaveToGallery;
    static String SaveToLoveSticker;
    static byte[] Image_byteArray;
    static boolean IsGalleryNotifyAdapter;
    static boolean IsLoveStickerNotifyAdapter;


    public static boolean isGalleryNotifyAdapter() {
        return IsGalleryNotifyAdapter;
    }

    public static void setIsGalleryNotifyAdapter(boolean isGalleryNotifyAdapter) {
        IsGalleryNotifyAdapter = isGalleryNotifyAdapter;
    }


    public static boolean isLoveStickerNotifyAdapter() {
        return IsLoveStickerNotifyAdapter;
    }

    public static void setIsLoveStickerNotifyAdapter(boolean isLoveStickerNotifyAdapter) {
        IsLoveStickerNotifyAdapter = isLoveStickerNotifyAdapter;
    }


    public static String getSaveToLoveSticker() {
        return SaveToLoveSticker;
    }

    public static void setSaveToLoveSticker(String saveToLoveSticker) {
        SaveToLoveSticker = saveToLoveSticker;
    }

    public static byte[] getImage_byteArray() {
        return Image_byteArray;
    }

    public static void setImage_byteArray(byte[] image_byteArray) {
        Image_byteArray = image_byteArray;
    }

    public static String getSaveToGallery() {
        return SaveToGallery;
    }

    public static void setSaveToGallery(String saveToGallery) {
        SaveToGallery = saveToGallery;
    }

}

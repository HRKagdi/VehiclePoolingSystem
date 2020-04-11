package util;

import android.app.ProgressDialog;
import android.content.Context;

import org.bson.Document;

import java.security.MessageDigest;
import java.util.ArrayList;

public class Hashing {

    public static String convertToMd5(final String md5){
        StringBuffer sb = null;
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] array = md.digest(md5.getBytes("UTF-8"));
            sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static ArrayList<Document> mergeCollection(){
        ArrayList<Document> finalData=new ArrayList<>();
        return finalData;
    }
    public static ProgressDialog getProgressDialog(Context c,String msg){
        ProgressDialog pd=new ProgressDialog(c);
        pd.setMessage(msg);
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return pd;
    }
}

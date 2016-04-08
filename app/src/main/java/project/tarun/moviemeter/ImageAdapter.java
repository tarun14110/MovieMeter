package project.tarun.moviemeter;

import android.content.Context;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by mafia on 6/4/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] imageURL;

    public ImageAdapter(Context c, String[] imageURL) {
        mContext = c;
        this.imageURL=imageURL;
    }

    public int getCount() {
        return imageURL.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }


        //IF Image_url array contain null then assign it a default poster.
        if(imageURL[position]=="empty"){
            Picasso.with(mContext).load(R.drawable.no_image_available).into(imageView);
            Log.d("Image Adaptor", "empty");
        }
        else{
            Picasso.with(mContext).load(imageURL[position]).into(imageView);
            Log.d("Image Adaptor", imageURL[position]);
        }


        return imageView;

    }
}


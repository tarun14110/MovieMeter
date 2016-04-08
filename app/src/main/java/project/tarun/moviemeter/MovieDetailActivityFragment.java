package project.tarun.moviemeter;

/**
 * Created by tarun on 7/4/16.
 */
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    private Date ConvertToDate(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Bundle b = getActivity().getIntent().getExtras();
        MovieDetails movie = b.getParcelable("MOVIE");

        int backdropWidth = CustomUtil.getScreenWidth(getActivity());
        int backdropHeight = getResources().getDimensionPixelSize(R.dimen.details_backdrop_height);
        ImageView view_Backdrop = (ImageView) rootView.findViewById(R.id.backdrop_image);
        if(movie.getBackdrop_path().equals("empty"))
            Picasso.with(getActivity()).load(R.drawable.no_image_available).into(view_Backdrop);
        else {
            Picasso.with(getActivity()).load(movie.getBackdrop_path()).resize(backdropWidth, backdropHeight).centerCrop().into(view_Backdrop);
            Log.v("Movie Detail", "backdrop: " + movie.getBackdrop_path() + " " + backdropWidth + " " + backdropHeight);
            Log.v("Movie Detail", "Others: " + movie.getTitle()+ "- "+ movie.getOriginal_language() + "- " + movie.getPoster_path());

        }
        int posterWidth = getResources().getDimensionPixelSize(R.dimen.details_poster_width);
        int posterHeight = getResources().getDimensionPixelSize(R.dimen.details_poster_height);
        ImageView view_Poster = (ImageView) rootView.findViewById(R.id.poster_image);
        if(movie.getPoster_path().equals("empty"))
            Picasso.with(getActivity()).load(R.drawable.no_image_available).into(view_Backdrop);
        else
            Picasso.with(getActivity()).load(movie.getPoster_path()).resize(posterWidth ,posterHeight).centerCrop().into(view_Poster);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ConvertToDate(movie.getRelease_date()));
        ((TextView) rootView.findViewById(R.id.realease_date))
                .setText(String.valueOf(calendar.get(Calendar.YEAR)));;

        ((TextView) rootView.findViewById(R.id.description))
                .setText(movie.getOverview());

        ((TextView) rootView.findViewById(R.id.movie_title))
                .setText(movie.getTitle());

        ((TextView) rootView.findViewById(R.id.movie_rating))
                .setText(movie.getVote_average());

        return rootView;
    }
}

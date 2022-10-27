package es.uniovi.eii.favmovies.gui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import es.uniovi.eii.favmovies.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {

    public static final String RELEASE_DATE = "release_date";
    public static final String DURATION = "duration";
    public static final String  COVER= "cover";

    private String release_date;
    private String duration;
    private String cover;



    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(String param1, String param2, String param3) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(RELEASE_DATE, param1);
        args.putString(DURATION, param2);
        args.putString(COVER, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            release_date = getArguments().getString(RELEASE_DATE);
            duration = getArguments().getString(DURATION);
            cover = getArguments().getString(COVER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_info, container, false);

        final TextView tArgument = root.findViewById(R.id.argument_fragment_txt);
        final TextView tRealseDate = root.findViewById(R.id.info_fragment_date);
        final TextView tDuration = root.findViewById(R.id.info_fragment_duration_txt);
        ImageView Icover = root.findViewById(R.id.info_fragment_cover);

        tRealseDate.setText(release_date);
        tDuration.setText(duration);
        Picasso.get().load(cover).into(Icover);

        return root;
    }
}
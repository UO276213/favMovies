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
 * Use the {@link ArgumentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArgumentFragment extends Fragment {

    public static final String ARGUMENT = "argument";
    private String argument;

    public ArgumentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArgumentFragment newInstance(String param1) {
        ArgumentFragment fragment = new ArgumentFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argument = getArguments().getString(ARGUMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_argument, container, false);
        final TextView tArgument = root.findViewById(R.id.argument_fragment_txt);
        tArgument.setText(argument);
        return root;
    }
}
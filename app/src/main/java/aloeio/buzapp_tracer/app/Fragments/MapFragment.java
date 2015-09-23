package aloeio.buzapp_tracer.app.Fragments;

import aloeio.buzapp_tracer.app.R;
import aloeio.buzapp_tracer.app.Services.BackgroundService;
import aloeio.buzapp_tracer.app.Services.MapManagerService;
import aloeio.buzapp_tracer.app.Utils.Utils;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.osmdroid.views.MapView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment
        extends Fragment {

    public MapManagerService mapManagerService;
    private Utils utils;
    private MapView map;
    private LinearLayout loadingLinearLayout;
    private TextView loadingTextView;
    private View rootView;
    private MapView mapView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        setMainActivityDefaults();

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {

        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart(){

        super.onStart();
        if(this.mapView == null) {
            this.mapView = (MapView) MapFragment.this.getActivity().findViewById(R.id.home_mapview);
            startServices();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void startServices(){

        mapManagerService = new MapManagerService(MapFragment.this);
        utils = new Utils(MapFragment.this.getActivity());
        if(utils.checkGPS(MapFragment.this.getActivity())) {
            mapManagerService.drawUserLocation();
        }
    }

    private void setMainActivityDefaults() {

        loadingLinearLayout = (LinearLayout) rootView.findViewById(R.id.loading_template);
        loadingTextView = (TextView) rootView.findViewById(R.id.loading_text);
        loadingTextView.setText(R.string.home_txt_loading);
        this.setDefaultClickListener();
    }

    private void setDefaultClickListener() {
    }

    private void toggleLoadingScreen(boolean toggle){

        loadingLinearLayout.setVisibility((toggle) ? View.VISIBLE : View.INVISIBLE);
        Log.d("LOADING: ", "DONE");
    }

}

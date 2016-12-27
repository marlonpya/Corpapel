package com.littletemplate.corpapel.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.littletemplate.corpapel.MainActivity;
import com.littletemplate.corpapel.R;

import java.util.ArrayList;
import java.util.List;

import Clases.Producto;
import Clases.RVAdapter;

public class ProductosFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button buttonBurger;
    Context thiscontext;

    private List<Producto> productos;

    private OnFragmentInteractionListener mListener;

    public ProductosFragment() {
        // Required empty public constructor
    }

    private void initializeData() {
        productos = new ArrayList<>();
        productos.add(new Producto("Rollo de papel", "CP WALTZ 12", 145));
        productos.add(new Producto("Rollo de papel", "CP WALTZ 12", 145));
        productos.add(new Producto("Rollo de papel", "CP WALTZ 12", 145));
        productos.add(new Producto("Rollo de papel", "CP WALTZ 12", 145));
        productos.add(new Producto("Rollo de papel", "CP WALTZ 12", 145));
        productos.add(new Producto("Rollo de papel", "CP WALTZ 12", 145));
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductosFragment newInstance(String param1, String param2) {
        ProductosFragment fragment = new ProductosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_productos, container, false);

        buttonBurger = (Button) rootView.findViewById(R.id.buttonburger);

        buttonBurger.setOnClickListener(this);

        thiscontext = container.getContext();


        initializeData();

        recycler = (RecyclerView) rootView.findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

// Usar un administrador para LinearLayout
        lManager = new GridLayoutManager(thiscontext, 2);
        recycler.setLayoutManager(lManager);
        adapter = new RVAdapter(productos);
        recycler.setAdapter(adapter);


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonburger:
                ((MainActivity) getActivity()).OpenDrawer();
                break;
            case R.id.presslayout:
                ((MainActivity) getActivity()).SetFragmentProductos();
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

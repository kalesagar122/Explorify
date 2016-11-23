package com.explorify.companyname.explorify;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.explorify.companyname.explorify.Constants.Actual_Url;
import static com.explorify.companyname.explorify.Constants.Base_Url;


public class NewsFragment extends Fragment {

    private static final String TAG = NewsFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM4 = "param4";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam4;

    //private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    View loadMoreView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // Movies json url
    private static String URL = "http:url&page=1";
    private ProgressDialog pDialog;
    private List<JobDItem> jobsList;
    private ListView listView;
    private NewsListAdapter adapter;
    int current_page = 0;
    int mPreLast;

    // Session Manager Class
    SessionManager session;

    // TODO: Rename and change types of parameters
    public static NewsFragment newInstance(String param1,String param4) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam4 = getArguments().getString(ARG_PARAM4);
        }

        /*// TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        jobsList = new ArrayList<JobDItem>();

        // Session class instance
        session = new SessionManager(getActivity());

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

       /* // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);*/

        listView = (ListView) rootView.findViewById(android.R.id.list);
        //listView.setOnItemClickListener(this);

        loadMoreView = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
        listView.addFooterView(loadMoreView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                JobDItem item = (JobDItem) adapter.getItem(position);
                Intent intent = new Intent(rootView.getContext(), NewsDetailsActivity.class);
                intent.putExtra("Id", item.getId());
                startActivity(intent);
                //Toast.makeText(getActivity(), "Item clicked : " + position, Toast.LENGTH_SHORT).show();
            }
        });

        /*listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public int currentScrollState;
            public int currentFirstVisibleItem;
            public int currentVisibleItemCount;
            public boolean isLoading = false;
            *//*@Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }*//*

            *//*@Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {
                    if (mPreLast != lastItem) {
                        mPreLast = lastItem;
                        //onStart();

                    } else {
                        onStart();
                    }
                }
            }*//*

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            private void isScrollCompleted() {
                if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
                    *//*** In this way I detect if there's been a scroll which has completed ***//*
                    *//*** do the work for load more date! ***//*
                    if (!isLoading) {
                        isLoading = true;
                        onStart();
                    }
                }
            }
        });*/


        listView.setOnScrollListener(new EndlessScrollListener(10) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                onStartList(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });
        onStartList(1);



        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /*@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            NewsItem item = (NewsItem) adapter.getItem(position);
            Toast.makeText(getActivity(), "Item clicked : " + position, Toast.LENGTH_SHORT).show();
            //mListener.onFragmentInteraction(item.getId());
        }
    }*/

    //@Override
    public void onStartList(int page){
        //super.onStart();
        // calling adapter changes here, just
        // to avoid getactivity()null
        // increment current page
        //current_page += 1;

        // Next page request
        URL = Base_Url+ "Jobs?" + "categoryId=" + mParam1 + "&PageNo=" + page;
        //adapter = new CustomListAdapter(this, movieList);

        // changing action bar color
        //getActivity().getActionBar().setBackground(
        //new ColorDrawable(Color.parseColor("#1b1b1b")));

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d(TAG, response.toString());
                        hidePDialog();


                        if (response != null)
                        {
                            /*View footerView = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_news, null, false);
                            listView.removeFooterView(footerView);*/

                                // Parsing json
                                for (int i = 0; i < response.length(); i++) {
                                    try {

                                        JSONObject obj = response.getJSONObject(i);
                                        JobDItem movie = new JobDItem();
                                        movie.setJobTitle(obj.getString("JobTitle"));
                                        movie.setId(obj.getString("Id"));
                                        movie.setCompanyJobId(obj.getString("CompanyJobId"));
                                        movie.setYearExpereince(obj.getString("YearExpereince"));
                                        movie.setMonthExperience(obj.getString("MonthExperience"));
                                        movie.setJobDetails(obj.getString("JobDetails"));
                                        movie.setCompanyName(obj.getString("CompanyName"));
                                        movie.setWebsite(obj.getString("Website"));
                                        movie.setCompanyAddress(obj.getString("CompanyAddress"));
                                        movie.setExpireDate(obj.getString("ExpireDate"));
                                        movie.setPosteddate(obj.getString("PostedDate"));

                                        // Image might be null sometimes
                                        String image = obj.isNull("Img") ? null : Actual_Url + obj.getString("Img");
                                        movie.setImage(image);

                                        jobsList.add(movie);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            // save index and top position
                            int index = listView.getFirstVisiblePosition();
                            View v = listView.getChildAt(0);
                            int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());

                            adapter = new NewsListAdapter(getActivity(), jobsList);
                            adapter.notifyDataSetChanged();

                            listView.setAdapter(adapter);

                            listView.setSelectionFromTop(index, top);

                            if(response.length() == 0)
                            {
                                listView.removeFooterView(loadMoreView);
                            }

                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Connect error")
                            .setMessage("Either requested url is timeout or notwork is disconnected.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                                    //.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    //public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    //}
                                    //})
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Authorization Failure")
                            .setMessage("Requested url need authentication!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                                    //.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    //public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    //}
                                    //})
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else if (error instanceof ServerError) {
                    //TODO
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Server Error")
                            .setMessage("Please try again!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                                    //.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    //public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    //}
                                    //})
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else if (error instanceof NetworkError) {
                    //TODO
                    new AlertDialog.Builder(getActivity())
                            .setTitle("No Connectivity ")
                            .setMessage("Please check your internet connectivity!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                                    //.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    //public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    //}
                                    //})
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else if (error instanceof ParseError) {
                    //TODO
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Parse Error")
                            .setMessage("Error in response!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                                    //.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    //public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    //}
                                    //})
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                hidePDialog();

            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + mParam4);
                return headers;
            }
        };
        movieReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(movieReq);
        //listView.setAdapter(adapter);
        /*int currentPosition = listView.getFirstVisiblePosition();
        //Set the new position
        listView.setSelectionFromTop(currentPosition + 1, 0);*/

    }

    /*private View getActionBar() {
        // TODO Auto-generated method stub
        return null;
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    /*public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
   /* public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }*/

}

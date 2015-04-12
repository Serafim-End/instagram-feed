package com.nikitaend.instafeed.sola.instagram.util;

import com.nikitaend.instafeed.sola.instagram.io.GetMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class PaginationIterator<E> implements Iterator<E> {
	protected String nextUri; 
	protected ArrayList<E> list;
	protected int index = 0;
    protected int count = 0;
	
	public PaginationIterator(ArrayList<E> list, String nextUri) throws Exception {
		this.list = list;
		this.nextUri = nextUri;
		if(list.size() == 0) fetch();
	}
    
    public PaginationIterator(ArrayList<E> list, String nextUri, int count) throws Exception {
        this.list = list;
        this.count = count;
        this.nextUri = nextUri;
        if(list.size() == 0) fetch();        
    }


	public boolean hasNext() {
		return index < list.size() || !paginationComplete(); 
	}

	public E next() {
		if(index >= list.size() - 1) {	
			try {
				fetch();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		return list.get(index++);
	}

	public void remove() {
		// not implenting this	
	}

	public void fetch() throws Exception {
		if(paginationComplete()) return;
		JSONObject object = (new GetMethod()
                .setMethodURI(nextUri))
				.call()
                .getJSON();
		JSONObject pagination = object.optJSONObject("pagination");
		nextUri = pagination == null ? null : pagination.optString("next_url");
		JSONArray data = object.optJSONArray("data");
		if(data != null) {
			handleLoad(data);
		}
	}

	public void handleLoad(JSONArray data) throws JSONException {
		
	}

	public boolean paginationComplete() {
        if (count != 0) {
            return this.nextUri == null || this.nextUri == "" || index >= count;
        }
		return this.nextUri == null || this.nextUri == "";
	}
	
	public PaginationIterator<E> reset() {
		this.index = 0;
		return this;
	}
}

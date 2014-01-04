package com.mapzen.entity;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapzen.MapzenApplication;
import com.mapzen.osrm.Route;
import com.mapzen.util.RouteLayer;

import org.json.JSONObject;
import org.oscim.core.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class RouteInstruction {
    private String url;
    private Route route;
    private RouteLayer layer;

    public void fetchRoute(Context context, Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
               successListener, errorListener);
        MapzenApplication.getApp(context).enqueueApiRequest(jsonObjectRequest);
    }

    private String urlTemplate = "http://api-osrm-test.mapzen.com/viaroute?z=%d"
            + "&output=json"
            + "&loc=%.6f,%.6f"
            + "&loc=%.6f,%.6f"
            + "&instructions=true";

    public RouteInstruction(ArrayList<GeoPoint> points, double zoomLevel) {
        // currently only support two points
        GeoPoint to = points.get(0);
        GeoPoint from = points.get(1);
        this.route = null;
        this.url = String.format(urlTemplate, (int) Math.floor(zoomLevel), to.getLatitude(),
                to.getLongitude(), from.getLatitude(), from.getLongitude());
    }

    public void setLayer(RouteLayer layer) {
        this.layer = layer;
    }

    public void draw(Context context) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                getSuccessListener(), getErrorListener());
        MapzenApplication.getApp(context).enqueueApiRequest(jsonObjectRequest);
    }


    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        };
    }

    private Response.Listener<JSONObject> getSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                layer.clear();
                route = new Route(jsonObject);
                for(double[] pair : route.getGeometry()) {
                    layer.addPoint(new GeoPoint(pair[0], pair[1]));
                }
                layer.updateMap();
            }
        };
    }
}

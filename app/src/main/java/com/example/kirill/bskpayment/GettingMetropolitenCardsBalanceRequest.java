package com.example.kirill.bskpayment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class GettingMetropolitenCardsBalanceRequest extends StringRequest {

    private static final String URL = "https://kirill-kotovich1.000webhostapp.com/GettingMetropolitenCardsBalance.php";
    private Map<String, String> params;

    public GettingMetropolitenCardsBalanceRequest(String cardNum, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("MT_CARDNUM", cardNum);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.roma.simplebjclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

/**
 *
 * @author ko3a4ok
 */
public class ClientController {
    private Socket socket;
    private PrintWriter pw;
    private Scanner sc;
    private OnReceiveListener listener;
    public void connect() {
        try {
            socket = new Socket("localhost", 1488);
            pw = new PrintWriter(socket.getOutputStream(), true);
            sc = new Scanner(socket.getInputStream());
            new Thread(new Runnable() {

                public void run() {
                    while (socket.isConnected()) {
                        if (sc.hasNext()){
                            String str = sc.nextLine();
                            receive(str);
                        } else {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }

            }).start();
        } catch (IOException ex) {
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void send(String s) {
        pw.println(s);
    }
    
    public void receive(String s) {
        if (listener != null) {
            listener.onReceive(s);
            JSONObject o = JSONObject.fromObject(s);
            String action = o.getString("action");
            JSONObject data = o.getJSONObject("data");
            if ("connect".equals(action)) performConnect(data);
            else if ("roundStart".equals(action)) performRoundStart(data);
            else if ("betsConfirm".equals(action)) performBetsConfirm(data);
            else if ("dealCards".equals(action)) performDealCards(data);
            else if ("roundFinish".equals(action)) performRoundFinish(data);
            else if ("actions".equals(action)) performActions(data);
                    
        }
    }
    public void setOnReceiveListener(OnReceiveListener listener) {
        this.listener = listener;
    }

    private void performConnect(JSONObject data) {
        listener.connect(data.getInt("position"));
    }

    private void performRoundStart(JSONObject data) {
        listener.roundStart(data.getLong("timer"));
    }

    private void performBetsConfirm(JSONObject data) {
        listener.betsConfirm();
    }

    private void performDealCards(JSONObject data) {
        listener.dealCards(Card.parseCard(data.getJSONObject("card")), data.getInt("position"), data.getInt("points"));
    }

    private void performRoundFinish(JSONObject data) {
        listener.roundFinish(data.getInt("winAmount"));
    }

    private void performActions(JSONObject data) {
        JSONArray acts = data.getJSONArray("actions");
        Set<BjAction> actions = new HashSet<BjAction>();
        for (int i = 0; i < acts.size(); i++)
            actions.add(BjAction.valueOf(acts.getString(i)));
        listener.actions(data.getLong("timer"), actions);
    }

    void makeBet(int bet) {
        JSONObject o = new JSONObject();
        o.put("action", "bet");
        o.put("amount", bet);
        send(o.toString());
    }
    
    void makeAction(BjAction act) {
        JSONObject o = new JSONObject();
        o.put("action", "action");
        o.put("value", act);
        send(o.toString());
    }
    
    static interface OnReceiveListener {
        public void onReceive(String s);
        public void connect(int position);
        public void roundStart(long timer);
        public void betsConfirm();
        public void roundFinish(int winAmount);
        public void dealCards(Card card, int position, int points);
        public void actions(long timer, Set<BjAction> action);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.roma.simplebjclient;

import net.sf.json.JSONObject;

/**
 *
 * @author ko3a4ok
 */
public class Card {

    static Card parseCard(JSONObject o) {
        Suit suit = Suit.valueOf(o.getString("suit"));
        String r = o.getString("rank");
        char ch = r.charAt(0);
        Rank rank = Character.isLetter(ch) ? Rank.valueOf(r) : Rank.valueOf("_"+r);
        return new Card(suit, rank);
        
    }
    enum Suit { H, D, C, S}
    enum Rank {A(11), K(10), Q(10), J(10), _10(10), _9(9), _8(8),
               _7(7), _6(6), _5(5), _4(4), _3(3), _2(2);
               int value;
               Rank(int value) {
                   this.value = value;
               }
    }
    
    private Suit suit;
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }
    
    
}

package com.achat.app.model;

public class DictObjectModel {
    String word, meaning;

    public DictObjectModel(String word, String meaning){

        this.word=word;
        this.meaning = meaning;


    }
    public  String getWord()
    {
        return word;
    }

    public  String getMeaning()
    {
        return meaning;
    }


}

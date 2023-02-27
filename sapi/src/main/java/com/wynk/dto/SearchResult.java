package com.wynk.dto;

import java.util.List;

public class SearchResult {

    private int total;

    private int num;

    private int pos;

    private List results;


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getPos() {
        return pos;
    }


    public void setPos(int pos) {
        this.pos = pos;
    }

    public List getResults() {
        return results;
    }

    public void setResults(List results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "{total:"+getTotal()+",pos:"+pos+",num:"+num+",results:"+results+"}";
    }
}

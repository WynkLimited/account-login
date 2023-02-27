package com.wynk.dto;

public class Pager {
    private int startIndex;

    private int endIndex;

    private int count;

    private int total;

    private int numPerPage;

    public Pager(int startIndex, int total, int numPerPage) {
        if (startIndex < 0) {
            startIndex = 0;
        }

        if (count < 0) {
            count = 0;
        }

        if (total < 0) {
            total = 0;
        }

        if (numPerPage < 0) {
            numPerPage = 0;
        }

        if((total - startIndex) < 0)
        {
            startIndex = 0;
            this.count = 0;
        }
        else
        {
            //count of items this page which is minimum of number per page or items left
            this.count = (numPerPage < (total - startIndex)) ? numPerPage : total - startIndex;
        }
        this.startIndex = startIndex;
        this.endIndex = startIndex + count - 1;
        this.total = total;
        this.numPerPage = numPerPage;

    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getCount() {
        return count;
    }

    public int getTotal() {
        return total;
    }

    public int getNumPerPage() {
        return numPerPage;
    }

}

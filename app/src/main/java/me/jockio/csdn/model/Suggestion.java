package me.jockio.csdn.model;

/**
 * Created by jockio on 16/7/30.
 */

public class Suggestion implements Comparable{
    private String keyword;
    private int times;

    public Suggestion() {
    }

    public Suggestion(String keyword, int times) {
        this.keyword = keyword;
        this.times = times;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "keyword='" + keyword + '\'' +
                ", times=" + times +
                '}';
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }


    @Override
    public int compareTo(Object another) {
        if(!(another instanceof Suggestion)){
            throw new RuntimeException("This is not an instance of Suggestion");
        }

        if(((Suggestion)another).getTimes() > this.getTimes()){
            return -1;
        }else if(((Suggestion)another).getTimes() > this.getTimes()){
            return 1;
        }
        return 0;
    }
}

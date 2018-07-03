package com.lcz.service.search;

/**
 * Created by codingchaozhang.
 */
public class HouseSuggest {
    private String input;
    private int weight = 10; // 默认权重

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}

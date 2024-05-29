package com.qatools.reporter.slack;

public class Step {
    String name;
    String keyword;
    Result result;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return String.format("  %s %s", keyword.trim(), name.trim());
    }
}

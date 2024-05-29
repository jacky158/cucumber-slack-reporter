package com.qatools.reporter.slack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Element {
    String type;

    String name;

    String description;

    List<Step> steps;
    List<Tag> tags = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String status() {
        if (getSteps().stream().anyMatch(x -> x.getName().equals("failed"))) {
            return "failed";
        }
        if (getSteps().stream().anyMatch(x -> x.getName().equals("skipped"))) {
            return "skipped";
        }
        return "passed";
    }

    public String getHeader() {
        return String.format("%s *%s*", Icon.of(status()), getName());
    }

    public String getMarkdown() {
        String tags = getTags().stream().map(x -> "_" + x.getName() + "_")
                .collect(Collectors.joining(" "));
        return String.join("\n", getHeader(), tags,
                "```",
                "Scenario: " + name,
                getSteps().stream().map(Step::toString).collect(Collectors.joining("\n")), "```");
    }
}

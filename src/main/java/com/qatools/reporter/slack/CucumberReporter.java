package com.qatools.reporter.slack;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.slack.api.Slack;
import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.webhook.Payload;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CucumberReporter {
    private final String SLACK_WEBHOOK_URL = System.getenv("SLACK_WEBHOOK_URL");
    public static List<LayoutBlock> blockList = new ArrayList<>();

    public CucumberReporter() {
    }

    public static void main(String[] args) {
        CucumberReporter reporter = new CucumberReporter();

        try {
            reporter.buildTestNGCucumberMessage();
            reporter.sendNotification(blockList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void buildTestNGCucumberMessage() throws IOException {
        File jsonFile = Paths.get(System.getProperty("user.dir"), "target/cucumber-report/cucumber.json").toFile();
        if (!jsonFile.exists()) {
            System.out.print("File not found target/cucumber-report/cucumber.json");
            return;
        }


        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Feature[] features = mapper.readValue(jsonFile, Feature[].class);

        for (Feature feature : features) {
            for (Element element : feature.getElements()) {
                blockList.add(buildMarkDownSection(element.getMarkdown()));
                blockList.add(buildDivider());
            }
        }
        blockList.add(buildMarkDownSection(getSummary()));
    }

    public String getSummary() {
        try {
            File file = new File("test-output/testng-results.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            org.w3c.dom.Element root = document.getDocumentElement();

            // ignored="0" total="2" passed="2" failed="0" skipped="0"
            String total = root.getAttribute("total");
            String passed = root.getAttribute("passed");
            String failed = root.getAttribute("failed");
            String skipped = root.getAttribute("skipped");


            return String.join(", ", "*Total*: " + total, "*Passed*: " + passed, "*Failed*: " + failed, "*Skipped*: " + skipped);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";

    }

    public void sendNotification(List<LayoutBlock> blocksList) {
        Slack slack = Slack.getInstance();
        if (Objects.nonNull(SLACK_WEBHOOK_URL) && SLACK_WEBHOOK_URL.isEmpty()) {
            System.out.println("Missing SLACK_WEBHOOK_URL");
        }
        splitBlocksInToChunks(blocksList).forEach(block -> {
            Payload payload = Payload.builder().blocks(block).build();
            try {
                slack.send(SLACK_WEBHOOK_URL, payload);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private LayoutBlock buildDivider() {
        return Blocks.divider();
    }

    private LayoutBlock buildMarkDownSection(String markDownText) {
        return Blocks.section(sectionBlockBuilder -> SectionBlock.builder().text(MarkdownTextObject.builder().text(markDownText).build()));
    }

    private List<List<LayoutBlock>> splitBlocksInToChunks(List<LayoutBlock> blocksList) {
        return Lists.partition(blocksList, 50);
    }
}

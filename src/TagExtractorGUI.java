import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class TagExtractorGUI extends JFrame {
    private JButton selectTextButton, selectStopButton, extractButton, saveButton;
    private JTextArea textArea;
    private File textFile;
    private File stopFile;
    private Map<String, Integer> tagMap;

    public TagExtractorGUI() {
        super("Tag/Keyword Extractor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        selectTextButton = new JButton("Select Text File");
        selectStopButton = new JButton("Select Stop Words File");
        extractButton = new JButton("Extract Tags");
        saveButton = new JButton("Save Tags");

        topPanel.add(selectTextButton);
        topPanel.add(selectStopButton);
        topPanel.add(extractButton);
        topPanel.add(saveButton);
        add(topPanel, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        selectTextButton.addActionListener(e -> chooseTextFile());
        selectStopButton.addActionListener(e -> chooseStopFile());
        extractButton.addActionListener(e -> runExtraction());
        saveButton.addActionListener(e -> saveTags());

        setVisible(true);
    }

    private void chooseTextFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            textFile = chooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Selected text file: " + textFile.getName());
        }
    }

    private void chooseStopFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            stopFile = chooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Selected stop words file: " + stopFile.getName());
        }
    }

    private void runExtraction() {
        if (textFile == null || stopFile == null) {
            JOptionPane.showMessageDialog(this, "Please select both text and stop word files.");
            return;
        }
        try {
            Set<String> stopWords = loadStopWords(stopFile);
            tagMap = extractTags(textFile, stopWords);
            displayTags(tagMap);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading files.");
        }
    }

    private Set<String> loadStopWords(File file) throws IOException {
        Set<String> stopWords = new TreeSet<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            stopWords.add(line.trim().toLowerCase());
        }
        br.close();
        return stopWords;
    }

    private Map<String, Integer> extractTags(File file, Set<String> stopWords) throws IOException {
        Map<String, Integer> map = new TreeMap<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase();
            String[] words = line.split("\\s+");
            for (String word : words) {
                if (word.isEmpty() || stopWords.contains(word)) continue;
                map.put(word, map.getOrDefault(word, 0) + 1);
            }
        }
        br.close();
        return map;
    }

    private void displayTags(Map<String, Integer> map) {
        textArea.setText("");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    private void saveTags() {
        if (tagMap == null || tagMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tags to save.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File outFile = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(outFile)) {
                for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
                    pw.println(entry.getKey() + ": " + entry.getValue());
                }
                JOptionPane.showMessageDialog(this, "Tags saved to " + outFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving file.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TagExtractorGUI::new);
    }
}

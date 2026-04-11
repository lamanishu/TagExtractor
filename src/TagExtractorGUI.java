import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class TagExtractorGUI extends JFrame {

    private JButton selectTextBtn, selectStopBtn, extractBtn, saveBtn;
    private JTextArea textArea;

    private File textFile;
    private File stopFile;

    private Map<String, Integer> tagMap = new TreeMap<>();

    public TagExtractorGUI() {

        super("Tag/Keyword Extractor");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();

        selectTextBtn = new JButton("Select Text File");
        selectStopBtn = new JButton("Select Stop Words File");
        extractBtn = new JButton("Extract Tags");
        saveBtn = new JButton("Save Tags");

        panel.add(selectTextBtn);
        panel.add(selectStopBtn);
        panel.add(extractBtn);
        panel.add(saveBtn);

        add(panel, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        selectTextBtn.addActionListener(e -> chooseTextFile());
        selectStopBtn.addActionListener(e -> chooseStopFile());
        extractBtn.addActionListener(e -> extractTags());
        saveBtn.addActionListener(e -> saveTags());

        setVisible(true);
    }

    private void chooseTextFile() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            textFile = chooser.getSelectedFile();

            JOptionPane.showMessageDialog(this,
                    "Selected Text File: " + textFile.getName());

            setTitle("Tag Extractor - " + textFile.getName());
        }
    }

    private void chooseStopFile() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            stopFile = chooser.getSelectedFile();

            JOptionPane.showMessageDialog(this,
                    "Selected Stop Words File: " + stopFile.getName());
        }
    }

    private void extractTags() {

        if (textFile == null || stopFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select both files first!");
            return;
        }

        tagMap.clear();

        try {
            Set<String> stopWords = new HashSet<>();

            BufferedReader brStop = new BufferedReader(new FileReader(stopFile));
            String line;

            while ((line = brStop.readLine()) != null) {
                stopWords.add(line.trim().toLowerCase());
            }
            brStop.close();

            BufferedReader brText = new BufferedReader(new FileReader(textFile));

            while ((line = brText.readLine()) != null) {

                line = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase();
                String[] words = line.split("\\s+");

                for (String word : words) {

                    if (!word.isEmpty() && !stopWords.contains(word)) {
                        tagMap.put(word, tagMap.getOrDefault(word, 0) + 1);
                    }
                }
            }

            brText.close();

            displayTags();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error reading files.");
        }
    }

    private void displayTags() {

        textArea.setText("");

        for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
            textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    private void saveTags() {

        if (tagMap.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No tags to save!");
            return;
        }

        File outFile = new File("traveling_tags.txt");

        try (PrintWriter pw = new PrintWriter(outFile)) {

            for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
                pw.println(entry.getKey() + ": " + entry.getValue());
            }

            JOptionPane.showMessageDialog(this,
                    "Saved successfully to:\n" + outFile.getAbsolutePath());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving file.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TagExtractorGUI::new);
    }
}
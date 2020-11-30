package com.company;

import com.json5Parser.TnJson;
import com.json5Parser.TnJsonBuilder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class Main {


    static final String designButton = "<html><style>body{color:#5BADE7}</style>";
    static Font font;

    static {
        try {
            font = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File(
                            "src/com/company/Fonts/JetBrainsMono-Regular.ttf"
                    )
            ).deriveFont(Font.PLAIN,12f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        ge.registerFont(font);

    }



    static private String themeName = "{}";
    static JFrame frame = new JFrame("SyntaxHiglighter");
    static JTextPane lines = new JTextPane();
    static JTextPane textPane = new JTextPane();
    static JScrollPane scrollerText = new JScrollPane(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
    );



    static Color colorBackground = new Color(0x444455);
    static Color colorForeground = new Color(0xEEEEEE);
    static Color colorBackgroundPressed = new Color(0x333344);
    static JButtonCustom btnLoadFile = new JButtonCustom(designButton + "<body>Load file</body>");
    static JButtonCustom btnSaveFile = new JButtonCustom(designButton + "<body>Save file</body>");
    static JButtonCustom btnSetTheme = new JButtonCustom(designButton + "<body>Set theme</body>");
    static final JFileChooser fc = new JFileChooser("c:/Users/User/Desktop");
    static final SyntaxHighlighter syntaxHighlighter = new SyntaxHighlighter(textPane);
    static String text="";

    static Map<String, Object> mapTheme = new HashMap<>();

    private static class FileLoader implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int returnVal = fc.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    text = Files.readString(Paths.get(file.getAbsolutePath()));
                    syntaxHighlighter.setText(text);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static class FileSaver implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int returnVal = fc.showSaveDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    text = syntaxHighlighter.getText();
                    Files.writeString(Paths.get(file.getAbsolutePath()), text);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static class ThemeChanger implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JFileChooser fc = new JFileChooser(new FileSystemView() {
                    @Override
                    public File createNewFolder(File containingDir) {
                        return null;
                    }

                    @Override
                    public File getParentDirectory(File dir) {
                        return null;
                    }

                    @Override
                    public File getDefaultDirectory() {
                        return null;
                    }

                    @Override
                    public File getHomeDirectory() {
                        return null;
                    }
                });
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")+"/src/Themes"));
                int returnVal = fc.showDialog(frame,"Set theme");
                File file = fc.getSelectedFile();
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    text = Files.readString(Paths.get(file.getAbsolutePath()));
                    loadTheme(text);
                }

                File settingsFile = new File(System.getProperty("user.dir")+"/src/.settings.json5");
                String settingsText = Files.readString(Paths.get(settingsFile.getAbsolutePath()));
                Map<String, Object> settings = TnJson.parse(settingsText);
                settings.put("theme",file.getName().replaceAll("\\..*",""));
                TnJsonBuilder tnJsonBuilder = TnJson.builder();
                Files.writeString(Paths.get(settingsFile.getAbsolutePath()),tnJsonBuilder.buildJson(settings));
            } catch (Exception ex) {}
        }
    }


    public static void loadTheme(Map<String, Object> mapTheme){
        if(mapTheme.containsKey("Background")) {
            colorBackground = new Color(Integer.parseInt(mapTheme.get("Background").toString()));
        }
        if(mapTheme.containsKey("BackgroundPressed")) {
            colorBackgroundPressed = new Color(Integer.parseInt(mapTheme.get("BackgroundPressed").toString()));
        }
        if(mapTheme.containsKey("Foreground")) {
            colorForeground = new Color(Integer.parseInt(mapTheme.get("Foreground").toString()));
        }
        initializeElements();
    }

    public static void loadThemeFromThemeFile(String themeName){
        String stringTheme = "{}";
        File file = new File(System.getProperty("user.dir")+"/src/Themes/"+themeName+".json5");
        try{
            stringTheme = Files.readString(Paths.get(file.getAbsolutePath()));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        loadTheme(stringTheme);
    }

    /**
     * @param stringTheme {stringified json object}
     */
    public static void loadTheme(String stringTheme){
        mapTheme = TnJson.parse(stringTheme);
        Main.loadTheme(mapTheme);
        syntaxHighlighter.loadTheme(mapTheme);
    }


    public static void initializeElements(){
        textPane.setFocusTraversalKeysEnabled(false);
        textPane.getDocument().addDocumentListener(new DocumentListener() {
            public String getText() {
                int caretPosition = textPane.getDocument().getLength();
                Element root = textPane.getDocument().getDefaultRootElement();
                StringBuilder text = new StringBuilder("1" + System.getProperty("line.separator"));
                for(int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
                    text.append(i).append(System.getProperty("line.separator"));
                }
                return text.toString();
            }
            @Override
            public void changedUpdate(DocumentEvent de) {
                lines.setText(getText());
            }
            @Override
            public void insertUpdate(DocumentEvent de) {
                lines.setText(getText());
            }
            @Override
            public void removeUpdate(DocumentEvent de) {
                lines.setText(getText());
            }
        });
        lines.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent de) {
            }
            @Override
            public void insertUpdate(DocumentEvent de) {
            }
            @Override
            public void removeUpdate(DocumentEvent de) {
            }
        });
        scrollerText.setRowHeaderView(lines);
        scrollerText.getViewport().add(textPane);


        // UI elements
        frame.setFont(font);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        lines.setEditable(false);
        lines.setFont(font);
        textPane.setFont(font);
        btnLoadFile.setFont(font);
        btnLoadFile.addActionListener(new FileLoader());
        btnSaveFile.setFont(font);
        btnSaveFile.addActionListener(new FileSaver());
        btnSetTheme.setFont(font);
        btnSetTheme.addActionListener(new ThemeChanger());
        colorizeElements();
        packElements();
    }

    public static void colorizeElements(){
        lines.setBackground(colorBackground);
        lines.setForeground(colorForeground);
        textPane.setBackground(colorBackground);
        textPane.setForeground(colorForeground);
        btnLoadFile.setBackground(colorBackground);
        btnLoadFile.setHoverBackground(colorBackground);
        btnLoadFile.setPressedBackground(colorBackgroundPressed);
        btnSaveFile.setBackground(colorBackground);
        btnSaveFile.setHoverBackground(colorBackground);
        btnSaveFile.setPressedBackground(colorBackgroundPressed);
        btnSetTheme.setBackground(colorBackground);
        btnSetTheme.setHoverBackground(colorBackground);
        btnSetTheme.setPressedBackground(colorBackgroundPressed);
        scrollerText.setBackground(colorBackground);
        scrollerText.setForeground(colorForeground);
        scrollerText.getViewport().setBackground(colorBackground);
        scrollerText.getViewport().setForeground(colorForeground);
    }

    public static void packElements(){
        JPanel panelMain = new JPanel();
        panelMain.setBackground(colorBackground);
        panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.LINE_AXIS));
        panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollerText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        frame.getContentPane().removeAll();
        panelMain.removeAll();
        var rows = Box.createVerticalBox();
        {
            var row = Box.createHorizontalBox();
            row.add(btnLoadFile);
            row.add(btnSaveFile);
            rows.add(row);
        }{
            var row = Box.createHorizontalBox();
            row.add(scrollerText);
            rows.add(row);
        }{
            var row = Box.createHorizontalBox();
            row.add(btnSetTheme);
            rows.add(row);
        }
        panelMain.add(rows);


        // Final
        frame.getContentPane().add(panelMain);
        frame.setVisible(true);
    }


    private static void applySettings() {
        File settingsFile = new File(System.getProperty("user.dir")+"/src/.settings.json5");
        try {
            String settingsText = Files.readString(Paths.get(settingsFile.getAbsolutePath()));

            Map<String, Object> settings = TnJson.parse(settingsText);
            if (settings.containsKey("theme")) {
                themeName = settings.get("theme").toString();
            } else {
                themeName = "Dracula";
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        applySettings();
        loadThemeFromThemeFile(themeName);
        initializeElements();
    }

}

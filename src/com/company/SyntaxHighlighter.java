package com.company;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SyntaxHighlighter {
    private Map<String, Object> colors = new HashMap<>();
    private Map<String, Style> styles = new HashMap<>();
    private final StyleContext sc = new StyleContext();

    private int tabSize = 4;
    private String tabString = "    ";

    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
        this.tabString = " ".repeat(tabSize);
    }

    public int getTabSize() {
        return tabSize;
    }

    private final JTextPane textPane;
    private final Caret caret;

    public SyntaxHighlighter(JTextPane textPane) {
        for(String item: colorItems){
            sc.addStyle(item, null);
        }
        this.textPane = textPane;
        textPane.setCaretColor(new Color(0xBBCCCC));
        this.caret = this.textPane.getCaret();
        textPane.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int caretPosition = textPane.getCaretPosition();
                textPane.setText(textSimplify(textPane.getText()));
                text = textPane.getText();
                highlight();

                try {
                    if (e.getKeyChar() == KeyEvent.VK_TAB) {
                        textPane.setCaretPosition(caretPosition + (tabSize - 1));
                    } else {
                        textPane.setCaretPosition(caretPosition);
                    }
                } catch (IllegalArgumentException illegalArgumentException) {

                }
            }
        });
        doc = textPane.getDocument();
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {}
            @Override
            public void removeUpdate(DocumentEvent e) {}
            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        styleizeColors();
    }

    private String[] colorItems = new String[]{
            "Text","Comment","CommentEscape","String","StringEscape",
            "Boolean","Number","Function","Type","BuiltinType",
            "Variable","Keyword","Preprocessor",
    };

    String[] endings = new String[]{"Escape"};

    protected void styleizeColors(){
        for(String item : colorItems) {
            if(colors.containsKey(item)){
                sc.getStyle(item).addAttribute(StyleConstants.Foreground, colors.get(item));
                styles.put(item,sc.getStyle(item));
            }else if(item.equals("Text")){
                sc.getStyle(item).addAttribute(StyleConstants.Foreground, Main.colorForeground);
                styles.put(item, sc.getStyle(item));
            }else{
                for (String ending : endings) {
                    if (item.endsWith(ending)) {
                        styles.put(item, sc.addStyle(
                                item,
                                styles.get(item.substring(0, item.length() - ending.length()))
                        ));
                    }
                }
            }
        }
    }

    private int pos;
    private int partLenght;
    private String text = "";
    private final Document doc;

    public void loadTheme(Map<String, Object> mapTheme){
        for(String item: colorItems) {
            if (mapTheme.containsKey(item)) {
                colors.put(item, new Color(Integer.parseInt(mapTheme.get(item).toString())));
            } else {
                boolean isPuted = false;
                for (String ending:endings) {
                    if (item.endsWith(ending)) {
                        colors.put(item, colors.get(
                                item.substring(0, item.length() - ending.length())
                        ));
                        isPuted = true;
                        break;
                    }
                }
                if(!isPuted){
                    colors.put(item, Main.colorForeground);
                }
            }
        }
        styleizeColors();
        try {
            this.doc.remove(0, this.doc.getLength());
            this.highlight();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String textSimplify(String text){
        text = text.replaceAll("\\r\\n", "\n");
        text = text.replaceAll("\\r", "\n");
        text = text.replaceAll("\\t", this.tabString);
        return text;
    }

    public void setText(String text) {
        text = textSimplify(text);
        this.text = text;
        this.highlight();
    }


    public String getText() {
        return this.text;
    }

    public static String insert(String bag, String marble, int index) {
        String bagBegin = bag.substring(0, index);
        String bagEnd = bag.substring(index);
        return bagBegin + marble + bagEnd;
    }

    private final String[] slisKeywords = new String[]{
            "if", "else", "for", "in", "of", "while",
            "let", "var", "function",  "static", "yield",
            "try", "catch", "finally", "throw", "return",
            "this", "switch", "case", "default", "step",
            // boolean
            "false", "true", "undefined",
            // classes
            "class", "namespace", "new",
    };

    private final String[] slisBoolValues = new String[]{
            "false", "true", "undefined",
    };

    private final String[] slisClassDeterminators = new String[]{
            "class", "namespace", "new",
    };

    public void insert(String insertText) {
        this.text = insert(this.text, insertText, pos);
        pos += insertText.length();
    }

    private void documentInsertNextChar(Style style) throws BadLocationException {
        doc.insertString(
                doc.getLength(),
                String.valueOf(this.text.charAt(pos)),
                style);
        pos++;
    }


    private void documentInsertNextString(Style style) throws BadLocationException {
        doc.insertString(
                doc.getLength(),
                this.text.substring(pos, pos + partLenght),
                style);
        pos += partLenght;
    }

    private int position(){
        return pos + partLenght;
    }

    public void toClipboardWithSyntaxHiglighting() {
        StringSelection selection = new StringSelection(this.textPane.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        textPane.getStyledDocument();
        clipboard.setContents(selection, selection);
    }

    public void highlight(){
        highlight(0, text.length());
    }

    public void highlight(int offset, int length) {
        try {
            doc.remove(offset, length);
        }catch (BadLocationException badLocationException){}


        /*
         TODO: edit string with "`" as delimiter, behaves like javascript string with same delimiter.
        */
        try {


            pos = offset;
            testValues:
            while ( pos < offset + length) {

                // strings
                // TODO: If editing this, edit in {strings alternative}
                if (this.text.charAt(pos) == '"') {
                    documentInsertNextChar(styles.get("String"));
                    while ((this.text.charAt(pos) != '"')) {
                        while (this.text.charAt(pos) == '\\') {
                            documentInsertNextChar(styles.get("StringEscape"));
                            documentInsertNextChar(styles.get("StringEscape"));
                        }
                        if (this.text.charAt(pos) != '"') {
                            documentInsertNextChar(styles.get("String"));
                        }
                    }
                    documentInsertNextChar(styles.get("String"));
                    continue testValues;
                }
                // strings alternative
                if (this.text.charAt(pos) == '`') {
                    documentInsertNextChar(styles.get("String"));
                    while ((this.text.charAt(pos) != '`')) {
                        while ((this.text.charAt(pos) == '\\')
                                || (this.text.charAt(pos) == '$')
                                && (this.text.charAt(pos + 1) == '{')) {
                            if (this.text.charAt(pos) == '\\') {
                                partLenght = 2;
                                documentInsertNextString(styles.get("StringEscape"));
                            }
                            if (this.text.charAt(pos) == '$') {
                                partLenght = 1;
                                while (this.text.charAt(position()) != '}') {
                                    partLenght++;
                                }
                                partLenght++;
                                documentInsertNextString(styles.get("Variable"));
                            }
                            if (this.text.charAt(pos) == '`') {
                                documentInsertNextChar(styles.get("String"));
                                continue testValues;
                            }
                        }
                        if (this.text.charAt(pos) != '`') {
                            documentInsertNextChar(styles.get("String"));
                        }

                    }
                    documentInsertNextChar(styles.get("String"));
                    continue testValues;
                }

                // comments
                if ((this.text.charAt(pos) == '/') && (this.text.charAt(pos + 1) == '*')) {
                    partLenght = 1;
                    while (!((this.text.charAt(position()-1) == '*') && (this.text.charAt(position()) == '/'))){
                        partLenght ++;
                    }
                    partLenght++;
                    documentInsertNextString(styles.get("Comment"));
                    continue testValues;
                }
                if ((this.text.charAt(pos) == '/') && (this.text.charAt(pos + 1) == '/')) {

                    partLenght = 1;
                    while (this.text.charAt(position()) != '\n') {
                        partLenght++;
                    }
                    documentInsertNextString(styles.get("Comment"));
                    continue testValues;
                }
                // keywords
                for (var item : slisKeywords) {
                    if (this.text.startsWith(item, pos)) {
                        char afterEnd = this.text.charAt(pos + item.length());
                        if (!Character.isLetter(afterEnd) && !Character.isDigit(afterEnd) &&
                                afterEnd != '_' && afterEnd != '\'') {
                            if (Arrays.asList(slisBoolValues).contains(item)) {
                                doc.insertString(pos, item, styles.get("Boolean"));
                            }else {
                                doc.insertString(pos, item, styles.get("Keyword"));
                            }
                            pos += item.length();
                            if (Arrays.asList(slisClassDeterminators).contains(item)) {

                                while ((Character.isLetter(this.text.charAt(pos)))
                                        || Character.isDigit(this.text.charAt(pos))
                                        || (this.text.charAt(pos) == '_')
                                        || (this.text.charAt(pos) == ' ')
                                        || (this.text.charAt(pos) == '\'')) {
                                    documentInsertNextChar(styles.get("Type"));
                                }
                            }
                        }
                    }
                }
                // numbers

                if (Character.isDigit(this.text.charAt(pos))) {
                    partLenght = 1;
                    if (String.valueOf(this.text.charAt(position())).matches("[0-9_'.e]")) {
                        // base 10
                        while (String.valueOf(this.text.charAt(position())).matches("[0-9_']")) {
                            partLenght++;
                        }
                        if (this.text.charAt(position()) == '.') {
                            if (Character.isDigit(this.text.charAt(position() + 1))) {
                                partLenght++;
                                while (String.valueOf(this.text.charAt(position())).matches("[0-9_']")) {
                                    partLenght++;
                                }
                            }
                        }
                        if (this.text.charAt(position()) == 'e') {
                            partLenght++;
                            while (String.valueOf(this.text.charAt(position())).matches("[0-9_']")) {
                                partLenght++;
                            }
                        }
                    } else if (this.text.charAt(position()) == 'x') {

                        // // base 16
                        partLenght=2;
                        while (String.valueOf(this.text.charAt(position())).matches("[0-9A-F_']")) {
                            partLenght++;
                        }
                        if (this.text.charAt(position()) == '.') {
                            if (Character.isDigit(this.text.charAt(position() + 1))) {
                                partLenght++;
                                while (String.valueOf(this.text.charAt(position())).matches("[0-9A-F_']")) {
                                    partLenght++;
                                }
                            }
                        }
                        if (this.text.charAt(position()) == 'e') {
                            partLenght++;
                            while (String.valueOf(this.text.charAt(position())).matches("[0-9_']")) {
                                partLenght++;
                            }
                        } else if (this.text.charAt(position()) == 'p') {
                            partLenght++;
                            while (String.valueOf(this.text.charAt(position())).matches("[0-9A-F_']")) {
                                partLenght++;
                            }
                        }
                    } else if (this.text.charAt(position()) == 'o') {
                        // // base 8
                        partLenght=2;

                        while (String.valueOf(this.text.charAt(position())).matches("[0-7_']")) {
                            partLenght++;
                        }
                        if (this.text.charAt(position()) == '.') {
                            if (Character.isDigit(this.text.charAt(position() + 1))) {
                                partLenght++;
                                while (String.valueOf(this.text.charAt(position())).matches("[0-7_']")) {
                                    partLenght++;
                                }
                            }
                        }
                        if (this.text.charAt(position()) == 'e') {
                            partLenght++;
                            while (String.valueOf(this.text.charAt(position())).matches("[0-9_']")) {
                                partLenght++;
                            }
                        } else if (this.text.charAt(position()) == 'p') {
                            partLenght++;
                            while (String.valueOf(this.text.charAt(position())).matches("[0-7_']")) {
                                partLenght++;
                            }
                        }
                    }
                    documentInsertNextString(styles.get("Number"));
                    continue testValues;
                }

                {
                    // variables
                    Style tempStyle;
                    if (((Character.isLetter(this.text.charAt(pos)))
                            || Character.isDigit(this.text.charAt(pos))
                            || (this.text.charAt(pos) == '_') || (this.text.charAt(pos) == '\''))) {
                        {
                            partLenght = 0;
                            while ((Character.isLetter(this.text.charAt(position())))
                                    || Character.isDigit(this.text.charAt(position()))
                                    || (this.text.charAt(position()) == '_')
                                    || (this.text.charAt(position()) == '\'')
                                    || (this.text.charAt(position()) == ' ')) {
                                partLenght++;
                            }
                            if (this.text.charAt(position()) == '(') {
                                tempStyle = styles.get("Function");
                            } else {
                                tempStyle = styles.get("Variable");
                            }
                        }
                        documentInsertNextString(tempStyle);
                        continue testValues;
                    }
                    documentInsertNextChar(styles.get("Text"));
                }
            }
        } catch (Exception exception) {
            try {

                while ( pos < this.text.length()) {
                    documentInsertNextChar(styles.get("Text"));
                }
            } catch (Exception ignoredException){}
        }
    }
}

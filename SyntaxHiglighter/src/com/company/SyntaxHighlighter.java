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
import java.util.Map;

public class SyntaxHighlighter {
    private static final String formatText = "<html><style>body{color:#EEEEEE;margin-left: 10px}  .comment {\n    color: #888888;\n}\n\n.escape {\n    color: #7766FF;\n}\n\n.string {\n    color: #44DD44;\n}\n\n\n\n\n\n.number {\n    color: #EE8800;\n}\n\n.boolean {\n    color: #EE8800;\n}\n\n.builtin-type {\n    color: #CCCC44;\n}\n\n.type {\n    color: #CCCC44;\n}\n\n.variable {\n    color: #DD5555;\n}\n\n.function {\n    color: #22AAFF;\n}\n\n.keyword {\n    color: #9955CC;\n}\n\n</style><body>";

    private Color colorText = new Color(0xEEEEEE);
    private Color colorComment = new Color(0x888888);
    private Color colorCommentEscape = new Color(0x7766FF);
    private Color colorString = new Color(0x44DD44);
    private Color colorStringEscape = new Color(0x7766FF);
    private Color colorNumber = new Color(0xEE8800);
    private Color colorBoolean = new Color(0xEE8800);
    private Color colorBuiltinType = new Color(0xCCCC44);
    private Color colorType = new Color(0xCCCC44);
    private Color colorVariable = new Color(0xDD5555);
    private Color colorFunction = new Color(0x22AAFF);
    private Color colorKeyword = new Color(0x9955FF);
    private Color colorPreprocessor = new Color(0x9955FF);

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

    private final Style styleText = sc.addStyle("text", null);
    private final Style styleComment = sc.addStyle("comment", styleText);
    private final Style styleCommentEscape = sc.addStyle("commentEscape", styleComment);
    private final Style styleString = sc.addStyle("string", styleText);
    private final Style styleStringEscape = sc.addStyle("stringEscape", styleString);
    private final Style styleNumber = sc.addStyle("number", styleText);
    private final Style styleBoolean = sc.addStyle("boolean", styleText);
    private final Style styleType = sc.addStyle("type", styleText);
    private final Style styleBuiltinType = sc.addStyle("builtinType", styleText);
    private final Style styleVariable = sc.addStyle("variable", styleText);
    private final Style styleFunction = sc.addStyle("function", styleText);
    private final Style styleKeyword = sc.addStyle("keyword", styleText);
    private final Style stylePreprocessor = sc.addStyle("preprocessor", styleText);





    private final JTextPane textPane;
    private final Caret caret;

    public SyntaxHighlighter(JTextPane textPane) {
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
                setText(textPane.getText());
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

    protected void styleizeColors(){
        this.styleText.addAttribute(StyleConstants.Foreground, this.colorText);
        this.styleComment.addAttribute(StyleConstants.Foreground, this.colorComment);
        this.styleCommentEscape.addAttribute(StyleConstants.Foreground, this.colorCommentEscape);
        this.styleString.addAttribute(StyleConstants.Foreground, this.colorString);
        this.styleStringEscape.addAttribute(StyleConstants.Foreground, this.colorStringEscape);
        this.styleBoolean.addAttribute(StyleConstants.Foreground, this.colorBoolean);
        this.styleNumber.addAttribute(StyleConstants.Foreground, this.colorNumber);
        this.styleFunction.addAttribute(StyleConstants.Foreground, this.colorFunction);
        this.styleType.addAttribute(StyleConstants.Foreground, this.colorType);
        this.styleBuiltinType.addAttribute(StyleConstants.Foreground, this.colorBuiltinType);
        this.styleVariable.addAttribute(StyleConstants.Foreground, this.colorVariable);
        this.styleKeyword.addAttribute(StyleConstants.Foreground, this.colorKeyword);
        this.stylePreprocessor.addAttribute(StyleConstants.Foreground, this.colorPreprocessor);
    }

    private int pos;
    private String text = "";
    private final Document doc;

    public void loadTheme(Map<String, Object> mapTheme){
        if(mapTheme.containsKey("colorText")){
            colorText = new Color(Integer.parseInt(mapTheme.get("colorText").toString()));
        }else{
            colorText = Main.colorForeground;
        }
        if(mapTheme.containsKey("colorComment")){
            colorComment = new Color(Integer.parseInt(mapTheme.get("colorComment").toString()));
        }else{
            colorComment = colorText;
        }
        if(mapTheme.containsKey("colorCommentEscape")){
            colorCommentEscape = new Color(Integer.parseInt(mapTheme.get("colorCommentEscape").toString()));
        }else{
            colorCommentEscape = colorComment;
        }
        if(mapTheme.containsKey("colorString")){
            colorString = new Color(Integer.parseInt(mapTheme.get("colorString").toString()));
        }else{
            colorString = colorText;
        }
        if(mapTheme.containsKey("colorStringEscape")){
            colorStringEscape = new Color(Integer.parseInt(mapTheme.get("colorStringEscape").toString()));
        }else{
            colorStringEscape = colorString;
        }
        if(mapTheme.containsKey("colorNumber")){
            colorNumber = new Color(Integer.parseInt(mapTheme.get("colorNumber").toString()));
        }else{
            colorNumber = colorText;
        }
        if(mapTheme.containsKey("colorBoolean")){
            colorBoolean = new Color(Integer.parseInt(mapTheme.get("colorBoolean").toString()));
        }else{
            colorBoolean = colorText;
        }
        if(mapTheme.containsKey("colorType")){
            colorType = new Color(Integer.parseInt(mapTheme.get("colorType").toString()));
        }else{
            colorType = colorText;
        }
        if(mapTheme.containsKey("colorBuiltinType")){
            colorBuiltinType = new Color(Integer.parseInt(mapTheme.get("colorBuiltinType").toString()));
        }else{
            colorBuiltinType = colorType;
        }
        if(mapTheme.containsKey("colorVariable")){
            colorVariable = new Color(Integer.parseInt(mapTheme.get("colorVariable").toString()));
        }else{
            colorVariable = colorText;
        }
        if(mapTheme.containsKey("colorFunction")){
            colorFunction = new Color(Integer.parseInt(mapTheme.get("colorFunction").toString()));
        }else{
            colorFunction = colorText;
        }
        if(mapTheme.containsKey("colorKeyword")){
            colorKeyword = new Color(Integer.parseInt(mapTheme.get("colorKeyword").toString()));
        }else{
            colorKeyword = colorText;
        }
        if(mapTheme.containsKey("colorPreprocessor")){
            colorPreprocessor = new Color(Integer.parseInt(mapTheme.get("colorPreprocessor").toString()));
        }else{
            colorPreprocessor = colorText;
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
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ble) {
            System.out.println("Documents are weird!");
        }
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



    public void toClipboardWithSyntaxHiglighting() {
        StringSelection selection = new StringSelection(this.textPane.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        textPane.getStyledDocument();
        clipboard.setContents(selection, selection);
    }

    public void highlight(){
        text = textSimplify(text);
        highlight(0, text.length());
    }

    public void highlight(int offset, int length) {


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
                    documentInsertNextChar(styleString);
                    while ((this.text.charAt(pos) != '"')) {
                        while (this.text.charAt(pos) == '\\') {
                            documentInsertNextChar(styleStringEscape);
                            documentInsertNextChar(styleStringEscape);
                        }
                        if (this.text.charAt(pos) != '"') {
                            documentInsertNextChar(styleString);
                        }
                    }
                    documentInsertNextChar(styleString);
                    continue testValues;
                }
                // strings alternative
                if (this.text.charAt(pos) == '`') {
                    documentInsertNextChar(styleString);
                    while ((this.text.charAt(pos) != '`')) {
                        while ((this.text.charAt(pos) == '\\')
                                || (this.text.charAt(pos) == '$')
                                && (this.text.charAt(pos + 1) == '{')) {
                            if (this.text.charAt(pos) == '\\') {
                                documentInsertNextChar(styleStringEscape);
                                documentInsertNextChar(styleStringEscape);
                            }
                            if (this.text.charAt(pos) == '$') {

                                while (this.text.charAt(pos) != '}') {

                                    documentInsertNextChar(styleVariable);
                                }
                                documentInsertNextChar(styleVariable);
                            }
                            if (this.text.charAt(pos) == '`') {
                                documentInsertNextChar(styleString);
                                continue testValues;
                            }
                        }
                        if (this.text.charAt(pos) != '`') {
                            documentInsertNextChar(styleString);
                        }

                    }
                    documentInsertNextChar(styleString);
                    continue testValues;
                }

                // comments
                if ((this.text.charAt(pos) == '/') && (this.text.charAt(pos + 1) == '*')) {

                    documentInsertNextChar(styleComment);
                    while (!((this.text.charAt(pos - 1) == '*') && (this.text.charAt(pos) == '/'))) {

                        documentInsertNextChar(styleComment);
                    }
                    documentInsertNextChar(styleComment);
                    continue testValues;
                }
                if ((this.text.charAt(pos) == '/') && (this.text.charAt(pos + 1) == '/')) {

                    documentInsertNextChar(styleComment);
                    while (this.text.charAt(pos) != '\n') {
                        documentInsertNextChar(styleComment);
                    }
                    continue testValues;
                }
                // keywords
                for (var item : slisKeywords) {
                    if (this.text.startsWith(item, pos)) {
                        char afterEnd = this.text.charAt(pos + item.length());
                        if (!Character.isLetter(afterEnd) && !Character.isDigit(afterEnd) &&
                                afterEnd != '_' && afterEnd != '\'') {
                            if (Arrays.asList(slisBoolValues).contains(item)) {
                                doc.insertString(pos, item, styleBoolean);
                            }else {
                                doc.insertString(pos, item, styleKeyword);
                            }
                            pos += item.length();
                            if (Arrays.asList(slisClassDeterminators).contains(item)) {

                                while ((Character.isLetter(this.text.charAt(pos)))
                                        || Character.isDigit(this.text.charAt(pos))
                                        || (this.text.charAt(pos) == '_')
                                        || (this.text.charAt(pos) == ' ')
                                        || (this.text.charAt(pos) == '\'')) {
                                    documentInsertNextChar(styleType);
                                }
                            }
                        }
                    }
                }
                // numbers

                if (Character.isDigit(this.text.charAt(pos))) {
                    documentInsertNextChar(styleNumber);
                    if (Character.isDigit(this.text.charAt(pos)) || this.text.charAt(pos) == '.') {
                        // base 10
                        while (String.valueOf(this.text.charAt(pos)).matches("[0-9_']")) {
                            documentInsertNextChar(styleNumber);
                        }

                        if (this.text.charAt(pos) == '.') {
                            if (Character.isDigit(this.text.charAt(pos + 1))) {
                                documentInsertNextChar(styleNumber);
                                while (String.valueOf(this.text.charAt(pos)).matches("[0-9_']")) {
                                    documentInsertNextChar(styleNumber);
                                }
                            }
                        }
                        if (this.text.charAt(pos) == 'e') {
                            documentInsertNextChar(styleNumber);
                            while (String.valueOf(this.text.charAt(pos)).matches("[0-9_']")) {
                                documentInsertNextChar(styleNumber);
                            }
                        }
                        continue testValues;
                    } else if (this.text.charAt(pos) == 'x') {

                        // // base 16
                        documentInsertNextChar(styleNumber);
                        while (String.valueOf(this.text.charAt(pos)).matches("[0-9A-F_']")) {

                            documentInsertNextChar(styleNumber);
                        }
                        if (this.text.charAt(pos) == '.') {
                            if (Character.isDigit(this.text.charAt(pos + 1))) {

                                documentInsertNextChar(styleNumber);
                                while (String.valueOf(this.text.charAt(pos)).matches("[0-9A-F_']")) {

                                    documentInsertNextChar(styleNumber);
                                }
                            }
                        }
                        if (this.text.charAt(pos) == 'e') {

                            documentInsertNextChar(styleNumber);
                            while (String.valueOf(this.text.charAt(pos)).matches("[0-9_']")) {

                                documentInsertNextChar(styleNumber);
                            }
                        } else if (this.text.charAt(pos) == 'p') {

                            documentInsertNextChar(styleNumber);
                            while (String.valueOf(this.text.charAt(pos)).matches("[0-9A-F_']")) {

                                documentInsertNextChar(styleNumber);
                            }
                        }
                        continue testValues;
                    } else if (this.text.charAt(pos) == 'o') {
                        // // base 8
                        documentInsertNextChar(styleNumber);

                        while (String.valueOf(this.text.charAt(pos)).matches("[0-7_']")) {

                            documentInsertNextChar(styleNumber);
                        }
                        if (this.text.charAt(pos) == '.') {
                            if (Character.isDigit(this.text.charAt(pos + 1))) {

                                documentInsertNextChar(styleNumber);
                                while (String.valueOf(this.text.charAt(pos)).matches("[0-7_']")) {
                                    documentInsertNextChar(styleNumber);
                                }
                            }
                        }
                        if (this.text.charAt(pos) == 'e') {
                            documentInsertNextChar(styleNumber);
                            while (String.valueOf(this.text.charAt(pos)).matches("[0-9_']")) {
                                documentInsertNextChar(styleNumber);
                            }
                        } else if (this.text.charAt(pos) == 'p') {
                            documentInsertNextChar(styleNumber);
                            while (String.valueOf(this.text.charAt(pos)).matches("[0-7_']")) {
                                documentInsertNextChar(styleNumber);
                            }
                        }
                        continue testValues;
                    }
                }
                {
                    // variables
                    Style style_temp;
                    if (((Character.isLetter(this.text.charAt(pos)))
                            || Character.isDigit(this.text.charAt(pos))
                            || (this.text.charAt(pos) == '_') || (this.text.charAt(pos) == '\''))) {
                        {
                            int x = 0;
                            while ((Character.isLetter(this.text.charAt(pos + x)))
                                    || Character.isDigit(this.text.charAt(pos + x))
                                    || (this.text.charAt(pos + x) == '_')
                                    || (this.text.charAt(pos + x) == '\'')
                                    || (this.text.charAt(pos + x) == ' ')) {
                                x++;
                            }
                            if (this.text.charAt(pos + x) == '(') {
                                style_temp = styleFunction;
                            } else {
                                style_temp = styleVariable;
                            }
                        }
                        while ((Character.isLetter(this.text.charAt(pos)))
                                || Character.isDigit(this.text.charAt(pos))
                                || (this.text.charAt(pos) == '_') || (this.text.charAt(pos) == '\'')) {

                            documentInsertNextChar(style_temp);
                        }
                        continue testValues;
                    }
                    documentInsertNextChar(styleText);
                }
            }
        } catch (Exception exception) {
            try {

                while ( pos < this.text.length()) {
                    documentInsertNextChar(styleText);
                }
            } catch (Exception ignoredException){}
        }
    }
}

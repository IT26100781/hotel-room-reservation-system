package com.hotel.util;

import com.hotel.config.DataConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Shared read/write helpers for the pipe-delimited data files.
 *
 * Usage pattern in a DAO:
 *   read:  FileHelper.readLines(FILE)  -> for each line: Model.fromFileString(line)
 *   write: build a list of model.toFileString() -> FileHelper.writeLines(FILE, lines)
 */
public final class FileHelper {

    private FileHelper() {
    }

    /** Reads every non-blank line of a data file. Returns an empty list if the file does not exist yet. */
    public static List<String> readLines(String fileName) throws IOException {
        List<String> lines = new ArrayList<>();
        File file = getFile(fileName);
        if (!file.exists()) {
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    /** Overwrites the whole file with the given lines (used for update and delete). */
    public static void writeLines(String fileName, List<String> lines) throws IOException {
        File file = getFile(fileName);
        ensureDataDir(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /** Adds one line to the end of the file (used for create). */
    public static void appendLine(String fileName, String line) throws IOException {
        File file = getFile(fileName);
        ensureDataDir(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(line);
            writer.newLine();
        }
    }

    /** Splits a line on the delimiter, keeping empty trailing fields. */
    public static String[] split(String line) {
        return line.split(Pattern.quote(DataConfig.DELIMITER), -1);
    }

    /** Joins fields with the delimiter (the reverse of split). */
    public static String join(Object... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(DataConfig.DELIMITER);
            }
            sb.append(fields[i]);
        }
        return sb.toString();
    }

    private static File getFile(String fileName) {
        return new File(DataConfig.DATA_DIR, fileName);
    }

    private static void ensureDataDir(File file) {
        File dir = file.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
    }
}

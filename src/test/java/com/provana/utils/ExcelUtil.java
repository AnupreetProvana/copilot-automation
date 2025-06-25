package com.provana.utils;

import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public final class ExcelUtil {

    private static final String EXCEL_PATH = "/config-and-locators.xlsx";

    private static final Map<String,String> CONFIG      = new HashMap<>();
    private static final Map<String,Locator> LOCATORS    = new HashMap<>();

    /* static init – load once */
    static {
        try (InputStream is =
                 ExcelUtil.class.getResourceAsStream(EXCEL_PATH);
             Workbook wb = WorkbookFactory.create(is)) {

            /* sheet 0 -> key/value config */
            Sheet cfg = wb.getSheetAt(0);
            cfg.forEach(r -> CONFIG.put(
                cell(r,0), cell(r,1)));

            /* sheet 1 -> locator table */
            Sheet loc = wb.getSheetAt(1);
            loc.forEach(r -> LOCATORS.put(
                cell(r,0).trim(),
                new Locator(cell(r,1).trim(), cell(r,2).trim())));

            System.out.println("[ExcelUtil] Loaded "
                    + CONFIG.size()   + " config keys & "
                    + LOCATORS.size() + " locators from "
                    + EXCEL_PATH);

        } catch (Exception e) {
            throw new RuntimeException("Cannot load excel: " + EXCEL_PATH, e);
        }
    }

    /* helpers */
    private static String cell(Row r,int i) {
        Cell c = r.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return c.toString();
    }

    public static String cfg(String key) { return CONFIG.get(key); }

    public static Locator loc(String name) { return LOCATORS.get(name); }

    /* tiny DTO */
    public record Locator(String by, String value) { }
    private ExcelUtil() {}
}

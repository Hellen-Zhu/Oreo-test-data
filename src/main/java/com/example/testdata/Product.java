package com.example.testdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * One product of the suite: a composition, one per trade type. The suite's registry ({@link ProductRegistry}) lists
 * them; the name is the {@code Examples} row, the product part of a case ID and the folder of its dat files. An enum
 * implements it naturally: {@code name()} is the constant's name.
 */
public interface Product {

    /** The classpath folder of the dat files, one folder per product. */
    String DAT_DIR = "data/dat/";

    /** The composition ID ({@code basic.compositionId}): {@code FX_CO}. */
    String name();

    /** The end of the trade type, after {@code Tradeable.}: {@code PayoffScript.Ctrl.CorrOption}. */
    String tradeType();

    /** The product ID the cases send for this product ({@code basic.productId}): {@code FX_PSCRIPT}. */
    String productId();

    /** The capabilities it supports, as the names of their {@code @capability#} tags. */
    Set<String> capabilityTags();

    /** The name of the product's default dat file, {@code <C>.dat}. */
    default String defaultDatFile() {
        return name() + ".dat";
    }

    /** Classpath path of the product's default dat file, {@code data/dat/<C>/<C>.dat}. */
    default String path() {
        return path(null);
    }

    /**
     * Classpath path of a dat file of this product, {@code data/dat/<C>/<datFile>}: a variant chosen by the case's
     * test data ({@code datFile}), or the default file when {@code datFile} is {@code null} or blank.
     */
    default String path(String datFile) {
        String file = datFile == null || datFile.isBlank() ? defaultDatFile() : datFile;
        checkFileName(file);
        return DAT_DIR + name() + "/" + file;
    }

    /** Fails with an {@link IllegalArgumentException} unless {@code datFile} is a file name ending in {@code .dat}. */
    static void checkFileName(String datFile) {
        if (datFile == null || !Pattern.matches("[A-Za-z0-9][A-Za-z0-9_.-]*\\.dat", datFile)) {
            throw new IllegalArgumentException("'" + datFile + "' is not a dat file name: a file of the product's"
                    + " folder " + DAT_DIR + "<product>/, such as FX_TRF.dat, without a folder");
        }
    }

    /** Whether {@code fullTradeType} is this product's trade type: equal to, or ending with {@code .<tradeType>}. */
    default boolean isTradeType(String fullTradeType) {
        String own = tradeType();
        return fullTradeType != null && (fullTradeType.equals(own) || fullTradeType.endsWith("." + own));
    }

    /** The report line of a case: {@code composition FX_CO: trade type PayoffScript.Ctrl.CorrOption, product ID FX_PSCRIPT}. */
    default String describe() {
        return "composition " + name() + ": trade type " + tradeType() + ", product ID " + productId();
    }

    /**
     * The problems of a trade the backend returned for this product: its {@code basic.compositionId} is not this
     * one, or its {@code basic.tradeType} not this one's trade type; empty when both agree. A trade without a
     * composition ID (a backend or recording older than composition IDs) has none: there is nothing to compare.
     */
    default List<String> mismatches(String compositionId, String fullTradeType) {
        if (compositionId == null || compositionId.isBlank()) return List.of();
        List<String> problems = new ArrayList<>();
        if (!compositionId.equals(name())) {
            problems.add("compositionId is '" + compositionId + "', not '" + name() + "'");
        }
        if (!isTradeType(fullTradeType)) {
            problems.add("tradeType '" + fullTradeType + "' does not end with '" + tradeType() + "'");
        }
        return problems;
    }
}

package com.example.testdata;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** The registry of this library's own tests: four products with every capability, in two groups as a suite has them. */
public class TestProducts implements ProductRegistry {

    private static final List<String> LIFECYCLE = List.of("create", "stepInFull", "stepInPartial", "earlyTermination",
            "novationRemaining", "cancellation", "allocation", "amendment", "partialTermination",
            "partialNovationRemaining", "stepOutFull", "stepOutPartial", "portfolioReassignment");
    private static final List<String> FEATURES = List.of("fixings", "cortexFormula", "productUpload");

    record TestProduct(String name, String tradeType, String productId, Set<String> capabilityTags)
            implements Product {}

    @Override
    public List<? extends Product> products() {
        Set<String> all = Set.copyOf(ProductRegistry.capabilityTags());
        return List.of(
                new TestProduct("FX_TRF", "TRF", "FX_TRF", all),
                new TestProduct("FX_CO", "PayoffScript.Ctrl.CorrOption", "FX_PSCRIPT", all),
                new TestProduct("FX_FSB", "PayoffScript.Ctrl.FloatingStrikeBarrier", "FX_PSCRIPT_FSKO", all),
                new TestProduct("FX_PSCRIPT_TRF", "PayoffScript.Ctrl.TRFGeneric", "FX_PSCRIPT_TRF", all));
    }

    @Override
    public Map<String, List<String>> capabilityGroups() {
        Map<String, List<String>> groups = new LinkedHashMap<>();
        groups.put("Trade lifecycle", LIFECYCLE);
        groups.put("Product features", FEATURES);
        return groups;
    }
}

package org.example.life;

import java.util.*;

/** Reference demo of honest life-loop safety semantics. Fictional data only. */
public final class LifeLoopDemo {
    enum Status { PLAN_READY, DAY_COMPLETED }
    enum Activity { PLANNED, COMPLETED, CANCELLED }
    enum ReceiptPhase { REQUESTED, UNKNOWN, RESULT_RECORDED, APPLIED }
    record Budget(long reservedMicroUsd, long actualMicroUsd, int calls, boolean unknownRetained) {}
    record Receipt(String id, ReceiptPhase phase, int effects) {}
    record Day(String date, Status status, List<Activity> activities, String reflection, String memoryRef) {}

    static final class Engine {
        Status status = Status.PLAN_READY;
        List<Activity> activities = new ArrayList<>(List.of(Activity.PLANNED, Activity.PLANNED));
        String reflection = "";
        String memoryRef = "";
        Budget budget = new Budget(0, 0, 0, false);
        Receipt receipt = new Receipt("r1", ReceiptPhase.REQUESTED, 0);
        final Set<String> settled = new HashSet<>();

        void adjustBeforeReflection() { require(status == Status.PLAN_READY, "ADJUST_AFTER_DAY_END"); }
        void executeAndVerify(int index) {
            require(status == Status.PLAN_READY, "EXEC_AFTER_DAY_END");
            require(index >= 0 && index < activities.size(), "BAD_ACTIVITY");
            require(activities.get(index) == Activity.PLANNED, "DUPLICATE_EXECUTION");
            activities.set(index, Activity.COMPLETED);
        }
        void cancelRemaining() {
            require(status == Status.PLAN_READY, "CANCEL_AFTER_DAY_END");
            activities.replaceAll(a -> a == Activity.PLANNED ? Activity.CANCELLED : a);
        }
        void reflect(String text) {
            require(status == Status.PLAN_READY, "REFLECT_AFTER_DAY_END");
            require(activities.stream().noneMatch(a -> a == Activity.PLANNED), "UNVERIFIED_ACTIVITY");
            require(!text.isBlank(), "EMPTY_REFLECTION");
            reflection = text;
            memoryRef = "DAY_MEMORY:" + text.hashCode();
        }
        void completeDay() {
            require(status == Status.PLAN_READY, "DUPLICATE_DAY_END");
            require(!reflection.isBlank(), "REFLECT_REQUIRED");
            activities.replaceAll(a -> a == Activity.PLANNED ? Activity.CANCELLED : a);
            status = Status.DAY_COMPLETED;
        }
        Budget reserve(long worstCase, int callLimit, long usdLimitMicros) {
            require(!budget.unknownRetained(), "UNKNOWN_USAGE_BLOCKED");
            require(budget.calls() < callLimit, "CALL_LIMIT");
            require(budget.reservedMicroUsd() + worstCase <= usdLimitMicros, "USD_LIMIT");
            budget = new Budget(budget.reservedMicroUsd() + worstCase, budget.actualMicroUsd(),
                budget.calls() + 1, false);
            return budget;
        }
        void settle(String reservationId, long actualMicros) {
            require(!settled.contains(reservationId), "DUPLICATE_SETTLEMENT");
            settled.add(reservationId);
            budget = new Budget(budget.reservedMicroUsd(), budget.actualMicroUsd() + actualMicros,
                budget.calls(), budget.unknownRetained());
        }
        void unknownUsage() {
            budget = new Budget(budget.reservedMicroUsd(), budget.actualMicroUsd(),
                budget.calls(), true);
        }
        Receipt dispatch(boolean effectHappens, boolean crashBeforeAck) {
            require(receipt.phase() == ReceiptPhase.REQUESTED, "ALREADY_DISPATCHED");
            int effects = effectHappens ? 1 : 0;
            if (crashBeforeAck) {
                receipt = new Receipt(receipt.id(), ReceiptPhase.UNKNOWN, effects);
                return receipt;
            }
            require(!effectHappens || effects == 1, "EFFECT_NOT_CONFIRMED");
            receipt = new Receipt(receipt.id(), ReceiptPhase.RESULT_RECORDED, effects);
            return receipt;
        }
        void recoverUnknown() {
            require(receipt.phase() == ReceiptPhase.UNKNOWN, "NOT_UNKNOWN");
            // Do not replay; require explicit external reconciliation.
            receipt = new Receipt(receipt.id(), ReceiptPhase.UNKNOWN, receipt.effects());
        }
        void applyVerified() {
            require(receipt.phase() == ReceiptPhase.RESULT_RECORDED, "UNVERIFIED_RESULT");
            receipt = new Receipt(receipt.id(), ReceiptPhase.APPLIED, receipt.effects());
        }
    }
    static void require(boolean ok, String code) { if (!ok) throw new IllegalStateException(code); }
    static void expectReject(Runnable r, String code, List<String> results) {
        try { r.run(); results.add("FAIL expected=" + code); }
        catch (IllegalStateException e) { results.add((code.equals(e.getMessage()) ? "PASS " : "FAIL ") + code + " -> " + e.getMessage()); }
    }

    public static void main(String[] args) {
        List<String> out = new ArrayList<>();
        Engine d1 = new Engine();
        d1.executeAndVerify(0); d1.cancelRemaining(); d1.adjustBeforeReflection(); d1.reflect("Day 1: completed reading; cancelled walk was not claimed."); d1.completeDay();
        out.add("DAY1 status=" + d1.status + " reflection=" + d1.reflection + " memory=" + d1.memoryRef);
        Engine d2 = new Engine(); d2.executeAndVerify(0); d2.cancelRemaining(); d2.reflect("verified"); d2.completeDay();
        expectReject(() -> d2.executeAndVerify(1), "EXEC_AFTER_DAY_END", out);
        expectReject(d2::completeDay, "DUPLICATE_DAY_END", out);
        Engine ref = new Engine();
        ref.executeAndVerify(0);
        expectReject(() -> ref.reflect("not verified"), "UNVERIFIED_ACTIVITY", out);
        Engine budget = new Engine();
        for (int i = 0; i < 10; i++) budget.reserve(7200, 10, 100_000);
        expectReject(() -> budget.reserve(7200, 10, 100_000), "CALL_LIMIT", out);
        budget.settle("res1", 420);
        expectReject(() -> budget.settle("res1", 420), "DUPLICATE_SETTLEMENT", out);
        Engine unknown = new Engine();
        unknown.reserve(7200, 10, 100_000); unknown.unknownUsage();
        expectReject(() -> unknown.reserve(7200, 10, 100_000), "UNKNOWN_USAGE_BLOCKED", out);
        Engine receipt = new Engine();
        receipt.dispatch(true, true); receipt.recoverUnknown();
        if (receipt.receipt.phase() != ReceiptPhase.UNKNOWN || receipt.receipt.effects() != 1)
            throw new IllegalStateException("UNKNOWN_NOT_RETAINED");
        out.add("RECEIPT effects=" + receipt.receipt.effects() + " phase=" + receipt.receipt.phase());
        Engine done = new Engine();
        done.executeAndVerify(0); done.cancelRemaining(); done.reflect("verified"); done.completeDay();
        expectReject(() -> done.completeDay(), "DUPLICATE_DAY_END", out);
        boolean pass = out.stream().noneMatch(s -> s.startsWith("FAIL"));
        out.forEach(System.out::println);
        System.out.println("DEMO_RESULT=" + (pass ? "PASS" : "FAIL"));
        if (!pass) System.exit(1);
    }
}
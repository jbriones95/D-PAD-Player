package com.example.dpadplayer;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000<\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u001a\u000e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u000e\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u000e\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u000e\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\bH\u0002\u001a \u0010\f\u001a\u0014\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00010\r2\u0006\u0010\u000f\u001a\u00020\u0010\u001a\u0016\u0010\u0011\u001a\u00020\u00122\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\u0012\u001a6\u0010\u0014\u001a\u00020\u0001*\u00020\u00032\u001c\b\u0002\u0010\u0015\u001a\u0016\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u0001\u0018\u00010\r2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u0017\u00a8\u0006\u0018"}, d2 = {"applyItemFocusBackground", "", "view", "Landroid/view/View;", "applyMiniPlayerFocusBackground", "applyPlayerControlFocusBackground", "applyTopBarTabFocusBackground", "dp", "", "context", "Landroid/content/Context;", "value", "materialButtonFocusChangeHandler", "Lkotlin/Function2;", "", "button", "Lcom/google/android/material/button/MaterialButton;", "resolveColor", "", "attrRes", "setupDpadItem", "onFocusChanged", "onClick", "Lkotlin/Function0;", "app_debug"})
public final class ItemFocusUtilsKt {
    
    /**
     * Applies a D-pad-friendly focus background to a list item view.
     *
     * Layers (bottom → top):
     *  1. Focused state: high-contrast outline with a very light tint
     *  2. Activated state: subtle now-playing tint
     *  3. RippleDrawable for touch feedback
     *
     * Call this once per ViewHolder in its `init` block.
     */
    public static final void applyItemFocusBackground(@org.jetbrains.annotations.NotNull
    android.view.View view) {
    }
    
    public static final void applyMiniPlayerFocusBackground(@org.jetbrains.annotations.NotNull
    android.view.View view) {
    }
    
    public static final void applyPlayerControlFocusBackground(@org.jetbrains.annotations.NotNull
    android.view.View view) {
    }
    
    public static final void applyTopBarTabFocusBackground(@org.jetbrains.annotations.NotNull
    android.view.View view) {
    }
    
    @org.jetbrains.annotations.NotNull
    public static final kotlin.jvm.functions.Function2<android.view.View, java.lang.Boolean, kotlin.Unit> materialButtonFocusChangeHandler(@org.jetbrains.annotations.NotNull
    com.google.android.material.button.MaterialButton button) {
        return null;
    }
    
    private static final float dp(android.content.Context context, float value) {
        return 0.0F;
    }
    
    /**
     * Resolves a theme color attribute to an ARGB int.
     */
    public static final int resolveColor(@org.jetbrains.annotations.NotNull
    android.content.Context context, int attrRes) {
        return 0;
    }
    
    /**
     * Sets up standard D-pad behaviour on a RecyclerView item view:
     * - onFocusChangeListener: sets isSelected = hasFocus (drives focus highlight)
     * - onKeyListener: fires onClick when DPAD_CENTER or ENTER is pressed
     */
    public static final void setupDpadItem(@org.jetbrains.annotations.NotNull
    android.view.View $this$setupDpadItem, @org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function2<? super android.view.View, ? super java.lang.Boolean, kotlin.Unit> onFocusChanged, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}
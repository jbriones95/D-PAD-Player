package com.example.dpadplayer;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\"\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u001a\u000e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005\u001a\u0018\u0010\t\u001a\u00020\u0001*\u00020\u00032\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000b\u00a8\u0006\f"}, d2 = {"applyItemFocusBackground", "", "view", "Landroid/view/View;", "resolveColor", "", "context", "Landroid/content/Context;", "attrRes", "setupDpadItem", "onClick", "Lkotlin/Function0;", "app_debug"})
public final class ItemFocusUtilsKt {
    
    /**
     * Applies a D-pad-friendly focus background to a list item view.
     *
     * Layers (bottom → top):
     *  1. Transparent base
     *  2. State-list overlay: colorPrimary@18% when focused (isSelected), colorPrimary@10% when activated
     *  3. RippleDrawable for touch feedback
     *
     * Call this once per ViewHolder in its `init` block.
     */
    public static final void applyItemFocusBackground(@org.jetbrains.annotations.NotNull
    android.view.View view) {
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
    android.view.View $this$setupDpadItem, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}
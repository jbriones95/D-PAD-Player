package com.example.dpadplayer;

/**
 * LinearLayoutManager with reliable D-pad focus behaviour for Android TV / D-pad remotes.
 *
 * What it does:
 * 1. onRequestChildFocus — scrolls the focused item into view smoothly.
 * 2. onInterceptFocusSearch — when navigating UP/DOWN, finds the next item's
 *    clickable_item overlay (or the item root) and returns it, so the framework
 *    moves focus there directly instead of doing its own unpredictable search.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0012\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0002J\u001a\u0010\t\u001a\u0004\u0018\u00010\b2\u0006\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u0016J*\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\b2\b\u0010\n\u001a\u0004\u0018\u00010\bH\u0016\u00a8\u0006\u0013"}, d2 = {"Lcom/example/dpadplayer/FocusLinearLayoutManager;", "Landroidx/recyclerview/widget/LinearLayoutManager;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "findOwnerRecyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "view", "Landroid/view/View;", "onInterceptFocusSearch", "focused", "direction", "", "onRequestChildFocus", "", "parent", "state", "Landroidx/recyclerview/widget/RecyclerView$State;", "child", "app_debug"})
public final class FocusLinearLayoutManager extends androidx.recyclerview.widget.LinearLayoutManager {
    
    public FocusLinearLayoutManager(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super(null);
    }
    
    @java.lang.Override
    public boolean onRequestChildFocus(@org.jetbrains.annotations.NotNull
    androidx.recyclerview.widget.RecyclerView parent, @org.jetbrains.annotations.NotNull
    androidx.recyclerview.widget.RecyclerView.State state, @org.jetbrains.annotations.NotNull
    android.view.View child, @org.jetbrains.annotations.Nullable
    android.view.View focused) {
        return false;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.Nullable
    public android.view.View onInterceptFocusSearch(@org.jetbrains.annotations.NotNull
    android.view.View focused, int direction) {
        return null;
    }
    
    /**
     * Walks up the view hierarchy to find the RecyclerView that owns this layout manager.
     */
    private final androidx.recyclerview.widget.RecyclerView findOwnerRecyclerView(android.view.View view) {
        return null;
    }
}
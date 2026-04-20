package com.example.dpadplayer;

/**
 * Scrolls to keep the D-pad focused item visible automatically.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J*\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\fH\u0016\u00a8\u0006\u000e"}, d2 = {"Lcom/example/dpadplayer/FocusLinearLayoutManager;", "Landroidx/recyclerview/widget/LinearLayoutManager;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "onRequestChildFocus", "", "parent", "Landroidx/recyclerview/widget/RecyclerView;", "state", "Landroidx/recyclerview/widget/RecyclerView$State;", "child", "Landroid/view/View;", "focused", "app_debug"})
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
}